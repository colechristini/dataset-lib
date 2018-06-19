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
//StorageDaemons run on individual servers, managing the files on the server and replicationg them to all servers within the stripe
public class StorageDaemon{
    ArrayList<Integer,Integer> stripeIPs=new ArrayList<Integer,Integer>();
    ConcurrentHashMap<String,String> fileLocs=new ConcurrentHashMap<String,String>();
    ConcurrentHashMap<String,Integer> fileSizes=new ConcurrentHashMap<String,Integer>();
    boolean isActive;
    InetSocketAddress daemonIP;
    Socket command=Socket.open();
    InetSocketAddress commandIP;
    StorageDaemon(boolean isActive,InetSocketAddress daemonIP,InetSocketAddress commandIP){
        this.isActive=isActive;
        this.daemonIP=daemonIP;
        this.commandIP=commandIP;
    }
    public void start(){
        command.bind(commandIP);
    }
    public void recieve(){
        Runnable reciever=()->{
            PrintWriter out=new PrintWriter(command.getOutputStream(),true);
            String commandString=out.toString();
            if(command){
                if(command==get){
                    String[] components=commandString.split(":");
                    RandomAccessFile file=new RandomAccessFile(fileNames.get(components[1],"r"));
                    ByteBuffer bb=ByteBuffer.allocate(fileSizes.get(components[1]));
                    FileChannel fileChannel=file.getChannel();
                    int bytesRead=fileChannel.read(bb);
                    bb.flip();
                    InetSocketAddress remote=new InetSocketAddress(InetAddress.getByName(components[2]));
                    SocketChannel socket=SocketChannel.open();
                    socket.bind(daemonIP);
                    socket.connect(remote);
                    socket.finishConnect();
                    socket.write(bb);
                    bb.flip();
                }
            }
        };
    }
}