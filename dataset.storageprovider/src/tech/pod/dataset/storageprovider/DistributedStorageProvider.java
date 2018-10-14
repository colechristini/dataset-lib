package tech.pod.dataset.storageprovider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
//DistributedStorageProvider acts as a more advanced on-premises StorageProvider, saving, getting, and removing objects from a set of distributed StoragePools

public class DistributedStorageProvider implements StorageProviderInterface {
    ConcurrentHashMap < String, Boolean > heartbeatMap = new ConcurrentHashMap < String, Boolean > ();
    List < StoragePoolInterface > storagePools = new ArrayList < StoragePoolInterface > ();
    ConcurrentHashMap < String, StorageKey > files = new  ConcurrentHashMap < String, StorageKey > ();
    SocketAddress ip;
    int timeOut;
    int threadMaxCompleteTime;
    int maxActiveThreads;
    ServerSocketChannel serverSocket;
    int maxHeartbeatThreads;
    DistributedStorageProvider(SocketAddress ip, int maxActiveThreads, int maxHeartbeatThreads, int timeOut, int threadMaxCompleteTime){
        this.ip=ip;
        this.maxActiveThreads = maxActiveThreads;
        this.maxHeartbeatThreads=maxHeartbeatThreads;
        try {
            serverSocket.bind(ip,9999);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.timeOut = timeOut;
        this.threadMaxCompleteTime=threadMaxCompleteTime;
    }
    DistributedStorageProvider(SocketAddress ip, int port, int timeOut, int maxActiveThreads, int maxHeartbeatThreads,  int threadMaxCompleteTime){
        this.ip=ip;
        this.maxActiveThreads = maxActiveThreads;
        try {
            serverSocket.bind(ip,port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.timeOut = timeOut;
        this.threadMaxCompleteTime=threadMaxCompleteTime;
    }

    public void addPool(String mode){
        if(mode=="homogenous"){
            storagePools.add(new HomogenousPool());
        }
        else if(mode=="heterogenous"){
            storagePools.add(new HeterogenousPool());
        }
    }
    
    public void addPool(String mode,List<List<SocketAddress>> presuppliedDaemons,List<Integer> tiers){
        if(mode=="homogenous"){
            storagePools.add(new HomogenousPool());
        }
        else if(mode=="heterogenous"){
            storagePools.add(new HeterogenousPool());
        }
        if(storagePools.get(storagePools.size()-1) instanceof HomogenousPool){
            int a=0;
            for(int i=0;i< presuppliedDaemons.size();i++){
                a++;
                storagePools.get(storagePools.size()-1).addStripe(presuppliedDaemons.get(i).toArray(new SocketAddress[presuppliedDaemons.get(i).size()]));
                    for(int inc=0;inc<presuppliedDaemons.get(i).size();inc++){
                        heartbeatMap.put(presuppliedDaemons.get(i).get(inc).toString(), (Boolean)true);
                    }
                }
            }
        else if(storagePools.get(storagePools.size()-1) instanceof HeterogenousPool){
            for(int i=0;i< presuppliedDaemons.size();i++){
                storagePools.get(storagePools.size()-1).addStripe(presuppliedDaemons.get(i).toArray(new SocketAddress[presuppliedDaemons.get(i).size()]),(int)tiers.get(i));
                    for(int inc=0;inc<presuppliedDaemons.get(i).size();i++){
                        heartbeatMap.put(presuppliedDaemons.get(i).get(inc).toString(), (Boolean)true);
                    }
                }
            }
    }
    public void startHeartbeat(int port,int acceptablePing,long heartbeatTimer,TimeUnit unit){
        Runnable heartbeat=()->{
            ConcurrentLinkedDeque<Socket> sockets=new ConcurrentLinkedDeque<Socket>();
            ConcurrentLinkedDeque<int[]> poolStripe=new ConcurrentLinkedDeque<int[]>();
            Runnable reciever=()->{
                int[] poolStripeArray=poolStripe.pollFirst();
                Socket socket=sockets.pollFirst();
                int currentPing=0;
                try{
                BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                int pool=poolStripeArray[0];
                int stripe=poolStripeArray[1];
                while(currentPing<acceptablePing){
                    String str=reader.readLine();
                    currentPing++;
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                       e.printStackTrace();
                    }
                    if(str!=null){
                        return;
                    }
                    else{
                        continue;
                    }
                }
                StoragePoolInterface storagePool=storagePools.get(pool);
                storagePool.incrementRepLayer(stripe);
                heartbeatMap.replace(storagePools.get(pool).getDaemon(stripe).toString(), false);
            }
                catch(IOException e){
                    e.printStackTrace();
                }
            };
            ArrayList<ArrayList<SocketAddress>> temp=new ArrayList<ArrayList<SocketAddress>>(heartbeatMap.size());
            for(int i=0;i<storagePools.size();i++){
                ArrayList<SocketAddress> converter=storagePools.get(i).getAllDaemons();
                temp.add(converter);
            }
            ExecutorService service=Executors.newFixedThreadPool(maxHeartbeatThreads);
            for(int i=0;i<storagePools.size();i++){
                final int pool1=i;
                for(int a=0;a<storagePools.get(i).getAllDaemons().size();a++){
                    final int stripe1=a;
                    Socket socket=new Socket();
                    try {
                        socket.bind(ip);
                        socket.connect(storagePools.get(i).getDaemon(a));
                        PrintWriter writer=new PrintWriter(socket.getOutputStream(), true);
                        writer.write("ping");
                        sockets.addFirst(socket);
                        int[] poolStripeArray={pool1,stripe1};
                        poolStripe.addLast(poolStripeArray);
                        socket.close(); 
                    } catch (IOException e) {
                       e.printStackTrace();
                    }
                    if(sockets.size()<maxHeartbeatThreads){
                    Future<?> f=service.submit(reciever, null);
                    }
                }
            }
        };
        ScheduledExecutorService service=Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> future=service.scheduleWithFixedDelay(heartbeat, heartbeatTimer, heartbeatTimer, unit);
    }
    public void startRecieve(String port) {
        RejectedExecutionHandlerImplementation rejectedExecutionHandlerImpl=new RejectedExecutionHandlerImplementation();
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(2, maxActiveThreads, threadMaxCompleteTime, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2), Executors.defaultThreadFactory(), rejectedExecutionHandlerImpl);
        Runnable recieve=()->{

        };
    }
    public void put(String objectName, Object object) {
        
    }

    public Object get(String objectName) {
        return null;
    }

    public void remove(String objectName) {
        
    }
    
}