package tech.pod.dataset.ims;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
public class DataStore extends ConcurrentSkipListMap implements Serializable,Callable{
    private static final long serialVersionUID=DataStore.hashcode();
    ConcurrentSkipListMap<IndexKey,String> dataStore=new ConcurrentSkipListMap<IndexKey,String>();
    Index a;
    List<File> tierList=new ArrayList<File>();
    String[] storageTiering;
    int[] thresholds;
    int[] size;
    int checkInterval;
    int[] storageTierCount;
    
    DataStore(Index a,String[] storageTiering,int[] thresholds,int[] size,int checkInterval,String tempDirectory){
        this.a=a;
        this.thresholds=thresholds;
        this.size=size;
        this.storageTiering=storageTiering;
        this.checkInterval=checkInterval;
    }
    @Override
    public void put(IndexKey i,String s){
        dataStore.put(i,s);
        a.get(i.getLocation()).incrementCounter();
        a.get(i.getLocation()).update();
    }
  @Override
    public String get(IndexKey i){
      return dataStore.get(i);
       
    }
    @Override
    public String get(int i){
      return  dataStore.get(a.get(i));
    }
   /* public void start(){

    }*/
    @Override
    public Object call() throws Exception {
        StringBuilder b;
        Path p;
        File[] storageLocations=new File[storageTiering.length];
        for(int i=0;i<storageTiering.length;i++){
            storageLocations[i]=new File(storageTiering[i]);
        }
        while(true){
            for(int i=0;i<a.length();i++){
                for(int z=0;z<storageTiering.length;z++){
                    if(a.get(i).getAccessAverage()<thresholds[z]){
                        b.append(storageTiering[i]);
                        b.append("/");
                        b.append(a.get(i).getTitle());
                        b.append(".txt");
                        p.get(b.toString());
                        Files.write(p,dataStore.get(a.get(i)).getBytes());
                        b.setLength(0);
                        a.get(i).setLocationTier(z);
                        break;
                    }
                }
            }
            Thread.sleep((long)checkInterval);
        }
        return null;
    }
}