package tech.pod.dataset.appserver;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
/*AppServer acts as a basic wrapper for a servable AppServlet, taking input over a user inputted port and calling the corresponding AppServlet method contained in 'methods' */
public class AppServer {
    boolean acceptingConnections;
    AppServlet servlet;
    AppServer(AppServlet servlet,boolean acceptingConnections){
        this.servlet=servlet;
        this.acceptingConnections=acceptingConnections;
    }
    @Hidden
    public void recieve(int port,int paramBufferSize,int maxFileSize) {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        ByteBuffer parameters = ByteBuffer.allocate(paramBufferSize);
        ByteBuffer content = ByteBuffer.allocate(maxFileSize);
        List < ScheduledFuture > futures = new ArrayList < ScheduledFuture > ();
        ExecutorService executorService = Executors.newFixedThreadPool(maxAgents);
        int currentConnections;
        Method[] methods=servlet.class.getDeclaredMethods();
        List<String> methodNames=new ArrayList<String>();
        ConcurrentHashMap<String,Method> hashMap=new ConcurrentHashMap<String,Method>();
        List < SocketChannel > socketChannel = new List < SocketChannel > ();
        int p;
        for(Method method:methods){
            Annotation[] annotations=method.getAnnotations();
            if(Arrays.asList(annotations).contains(Hidden.class)){
                continue;
            }
            else{
            hashMap.put(method.getName(),method);
            methodNames.add(method.getName());
            }
        }
        Runnable serverThread = () -> {
            int pos=p;
            SocketChannel channel = socketChannel.get(pos);
            channel.read(parameters);
            buffer=parameters.asCharBuffer();
            String params=buffer.toString();
            List<? extends Object> returns;
            if(params.contains("call")){
               channel.read(content);
                String[] strings=params.split(":");
                int position;
                for(int i=0;i<methodNames.size();i++){
                    if(methodNames.get(i)==strings[1]){
                        position=i;
                    }
                }
                if(content!=null){
                    String[] strings2=Arrays.copyOfRange(strings, 2,strings.length);
                    returns=methods[position].invoke(servlet, strings2,content);

                }
                else if(content==null){
                    String[] strings2=Arrays.copyOfRange(strings, 2,strings.length);
                    returns=methods[position].invoke(servlet, strings2);
                }
                if(returns!=null){
                    ByteArrayOutputStream stream1=new ByteArrayOutputStream();
                    ObjectOutputStream stream=new ObjectOutputStream(stream1);
                    stream.writeObject(returns);
                    byte[] array=stream1.toByteArray();
                    ByteBuffer temp=ByteBuffer.allocate(array.length);
                    temp.put(array);
                    temp.flip();
                    while(temp.hasRemaining()){
                        channel.write(temp);
                    }
                }
            }
            else{
                return;
            }
            socketChannel.remove(pos);
        };
        while (acceptingConnections) {
            socketChannel.add(serverSocketChannel.accept());
            p=socketChannel.size();
            if (socketChannel != null) {
                ScheduledFuture future = executorService.submit(serverThread);
                i = futures.size();
                futures.add(future);
            }
        }
    }
}