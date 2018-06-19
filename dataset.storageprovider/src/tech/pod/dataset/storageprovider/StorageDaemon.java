package tech.pod.dataset.storageprovider;

import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
//StorageDaemons run on individual servers, managing the files on the server and replicationg them to all servers within the stripe.
public class StorageDaemon{
    ArrayList<Integer,InetAddress> stripeIPs=new ArrayList<Integer,Integer>();
    ConcurrentHashMap<String,String> fileLocs=new ConcurrentHashMap<String,String>();
    ConcurrentHashMap<String,Integer> fileSizes=new ConcurrentHashMap<String,Integer>();
    boolean isActive;
    InetSocketAddress daemonIP;
    Socket command=Socket.open();
    InetSocketAddress commandIP;
    int defaultBufferSize;
    String saveLocation;
    boolean running=false;
    StorageDaemon(boolean isActive,InetSocketAddress daemonIP,InetSocketAddress commandIP,int defaultBufferSize,String saveLocation){
        this.isActive=isActive;
        this.daemonIP=daemonIP;
        this.commandIP=commandIP;
        this.defaultBufferSize=defaultBufferSize;
        this.saveLocation=saveLocation;
    }
    public void start(){
        command.bind(commandIP);
        running=true;
    }
    public void pause(){
        running=false;
    }
    public void unpause(){
        running=true;
    }
    public void recieve(){
        PrintWriter out=new PrintWriter(command.getOutputStream(),true);
        String commands=out.toString();
        Runnable recieve=()->{
            String commandString=commands;
                if(commandString==get){
                    String[] components=commandString.split(":");
                    RandomAccessFile file=new RandomAccessFile(fileNames.get(components[1]),"r");
                    ByteBuffer buffer=ByteBuffer.allocate(fileSizes.get(components[1]));
                    FileChannel fileChannel=file.getChannel();
                    int bytesRead=fileChannel.read(bb);
                    buffer.flip();
                    InetSocketAddress remote=new InetSocketAddress(InetAddress.getByName(components[2]));
                    SocketChannel socket=SocketChannel.open();
                    socket.bind(daemonIP);
                    socket.connect(remote);
                    socket.finishConnect();
                    socket.write(buffer);
                }
                else if(commandString==write){
                    String[] components=commandString.split(":");
                    RandomAccessFile file=new RandomAccessFile(saveLocation="/"+components[2],"w");
                    fileLocs.put(components[1], components[2]);
                    ByteBuffer buffer=ByteBuffer.allocate(defaultBufferSize);
                    InetSocketAddress remote=new InetSocketAddress(InetAddress.getByName(components[3]));
                    SocketChannel socket=SocketChannel.open();
                    socket.bind(daemonIP);
                    socket.connect(remote);
                    socket.read(buffer);
                    buffer.flip(); 
                    FileChannel channel=file.getChannel();
                    channel.write(buffer);
                    if(isActive){
                        for(int i=1;i<stripeIps.size();i++){
                            Socket commandSenderSocket=new Socket(stripeIPs.get(i),10000);
                            OutputStream stream=commandSenderSocket.getOutputStream();
                            PrintWriter writer=new PrintWriter(stream);
                            writer.write(commandString);
                            socket.connect(stripeIPs.get(i));
                            socket.write(buffer);
                        }
                    }
                }
        };
       while(running){
           if(commands){
            ExecutorService executorService=Executors.newSingleThreadExecutor();
            executorService.execute(recieve);
           }
       }
    }
}