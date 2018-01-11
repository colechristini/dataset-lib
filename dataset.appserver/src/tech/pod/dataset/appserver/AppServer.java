package tech.pod.dataset.appserver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

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
        CharBuffer buffer = CharBuffer.allocate(paramBufferSize);
        List < ScheduledFuture > futures = new ArrayList < ScheduledFuture > ();
        ExecutorService executorService = Executors.newFixedThreadPool(maxAgents);
        int currentConnections;
        Method[] methods=servlet.class.getDeclaredMethods();
        List<String> methodNames=new ArrayList<String>();
        ConcurrentHashMap<String,Method> hashMap=new ConcurrentHashMap<String,Method>();
        SocketChannel[] sections;
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
           /* socketChannel.read(parameters);
            if (params[0].equals("put")) {
                socketChannel.read(content);
                
            }
            buffer = buff.asCharBuffer();
            String str = buffer.toString();
            String[] params = str.split(":");
            if (params[0].equals("put")) {       
                java.nio.file.Path p = Paths.get(tempPath + params[2]);
                Files.write(p, content.array());
                this.put(params);
                Files.delete(p);
            } else if (params[0].equals("get")) {
                ByteBuffer b = this.get(params);
                socketChannel.write(b);
            } else if (params[0].equals("remove")) {
                this.remove(params);
            }
            futures.remove(i);*/
            
        };
        while (acceptingConnections) {
            socketChannel[0] = serverSocketChannel.accept();
            if (socketChannel != null) {
                ScheduledFuture future = executorService.submit(serverThread);
                i = futures.size();
                futures.add(future);
            }
        }
    }
}