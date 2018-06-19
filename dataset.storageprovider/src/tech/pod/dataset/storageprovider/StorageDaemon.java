package tech.pod.dataset.storageprovider;

import java.nio.*;
import java.nio.channels.SocketChannel;
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
    ConcurrentHashMap<String,String> fileNames=new ConcurrentHashMap<String>();
    boolean isActive;
    SocketChannel socket=SocketChannel.open();;
    SocketAddress daemonIP;
    Socket
    StorageDaemon(boolean isActive,SocketAddress daemonIP){
        this.isActive=isActive;
        this.daemonIP=daemonIP;
    }
    public void start(){
        socket.bind(daemonIP);
    }
    public void recieve(){
        Runnable reciever=()->{
            
        };
    }
}