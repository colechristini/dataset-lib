package tech.pod.dataset.storageprovider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DistributedStorageProvider implements StorageProvider {
    ConcurrentHashMap < String, Boolean > heartbeatMap = new ConcurrentHashMap < String, Boolean > ();
    List < StoragePool > storagePools = new ArrayList < StoragePool > ();

    DistributedStorageProvider(){}

    public void addPool(String mode){
        if(mode=="homogenous"){
            storagePools.add(new HomogenousPool());
        }
        else if(mode=="heterogenous"){
            storagePools.add(new HeterogenousPool());
        }
    }
    
    public void startHeartbeat(int port,int acceptablePing){
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
                    ExecutorService service=Executors.newSingleThreadExecutor();
                    Future f=service.submit(reciever);
                }
            }
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