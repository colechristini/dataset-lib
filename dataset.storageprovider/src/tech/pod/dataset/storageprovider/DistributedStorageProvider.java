package tech.pod.dataset.storageprovider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
//import tech.pod.dataset.appserver.*;
//DistributedStorageProvider acts as a more advanced on-premises StorageProvider, saving, getting, and removing objects from a set of distributed StoragePools

public class DistributedStorageProvider implements StorageProvider {
    ConcurrentHashMap < String, Boolean > heartbeatMap = new ConcurrentHashMap < String, Boolean > ();
    List < StoragePool > storagePools = new ArrayList < StoragePool > ();
    ConcurrentHashMap < String, StorageKey > files = new  ConcurrentHashMap < String, StorageKey > ();
    String ip;
    DistributedStorageProvider(String ip){
        this.ip=ip;
    }

    public void addPool(String mode){
        if(mode=="homogenous"){
            storagePools.add(new HomogenousPool());
        }
        else if(mode=="heterogenous"){
            storagePools.add(new HeterogenousPool());
        }
    }
    
    public void addPool(String mode,List<List<String>> presuppliedDaemons,List<Integer> tiers){
        if(mode=="homogenous"){
            storagePools.add(new HomogenousPool());
        }
        else if(mode=="heterogenous"){
            storagePools.add(new HeterogenousPool());
        }
        if(storagePools.get(storagePools.size()-1) instanceof HomogenousPool){
            for(List i:presuppliedDaemons){
            storagePools.get(storagePools.size()-1).addStripe((String[])i.toArray());
                for(int inc=0;inc<i.size();i++){
                    heartbeatMap.put((String)i.get(inc), (Boolean)true);
                }
            }
        }
        else if(storagePools.get(storagePools.size()-1) instanceof HeterogenousPool){
            for(int i=0;i< presuppliedDaemons.size();i++){
            storagePools.get(storagePools.size()-1).addStripe((String[])presuppliedDaemons.get(i).toArray(),(int)tiers.get(i));
                for(int inc=0;inc<presuppliedDaemons.get(i).size();i++){
                    heartbeatMap.put((String)presuppliedDaemons.get(i).get(inc), (Boolean)true);
                }
            }
        }
    }
    @Hidden
    public void startHeartbeat(int port,int acceptablePing,long heartbeatTimer,TimeUnit unit){
        Runnable heartbeat=()->{
            Socket s;
      
            Runnable reciever=()->{
                int currentPing=0;
                BufferedReader reader=new BufferedReader(new InputStreamReader(s.getInputStream())); 
                while(currentPing<acceptablePing){
                    int pool=i;
                    int stripe=a;
                    String str=reader.readLine();
                    if(str!=null){
                        return;
                    }
                    else{
                        continue;
                    }
                }
                storagePools.get(pool).incrementRepLayer(stripe);
                heartbeatMap.replace(storagePools.get(pool).get(stripe), false);
            };
            List<List<String>> temp=new ArrayList<ArrayList<String>>(heartbeatMap.size());
            for(int i=0;i<storagePools.size();i++){
                temp.add(storagePools.get(i).getAllDaemons());
            }
            for(int i=0;i<storagePools.size();i++){
                for(int a=0;a<storagePools.get(i).size();a++){
                    s=new Socket(storagePools.get(i).get(a),port);
                    PrintWriter writer=new PrintWriter(s.getOutputStream(), true);
                    writer.write("ping");
                    ScheduledExecutorService service=Executors.newSingleThreadExecutor();
                    ScheduledFuture f=service.submit(reciever);
                }
            }
        };
        ScheduledExecutorService service=Executors.newScheduledThreadPool(1);
        ScheduledFuture future=service.scheduleWithFixedDelay(heartbeat, heartbeatTimer, heartbeatTimer, unit);
    }
    public void recieve(String port){
        ServerSocket command=new Socket(new InetSocketAddress(InetAddress.getByName(ip), 9999));
        Runnable recieve=()->{

        };
    }
    public void put(Object[] o) {
        
    }

    public Object get(Object[] o) {
        return null;
    }

    public void remove(Object[] o) {
        
    }
}