package tech.pod.dataset.ims;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
public class DataStore implements Serializable, Runnable {
    private static final long serialVersionUID = DataStore.hashcode();
    ConcurrentSkipListMap < IndexKey, String > dataStore = new ConcurrentSkipListMap < IndexKey, String > ();
    Index index;
    List < File > tierList = new ArrayList < File > ();
    String[] storageTiering;
    int[] thresholds;
    int[] size;
    int checkInterval;
    int[] storageTierCount;
    TimeUnit unit;
    DataStore(Index a, String[] storageTiering, int[] thresholds, int[] size, int checkInterval, String tempDirectory,TimeUnit unit) {
        index = a;
        this.thresholds = thresholds;
        this.size = size;
        this.storageTiering = storageTiering;
        this.checkInterval = checkInterval;
        this.unit=unit;
    }

    public void put(IndexKey i, String s) {
        dataStore.put(i, s);
        index.get(i.getLocation()).incrementCounter();
        index.get(i.getLocation()).update();
    }

    public String get(IndexKey i) {
        try {
            return dataStore.get(i);
        } catch (NullPointerException n) {
            i.incrementCounter();
            i.update();
            String str = new String(Files.readAllBytes(Paths.get(i.getPath())));
            dataStore.put(i, str);
            return str;
        }
    }
 
    public String get(int i) {

        return dataStore.get(a.get(i));
    }
    /* public void start(){

     }*/
public void start(){
    StringBuilder b;
    Path p;
    File[] storageLocations = new File[storageTiering.length];
    for (int i = 0; i < storageTiering.length; i++) {
        storageLocations[i] = new File(storageTiering[i]);
    }  
    Runnable clean=()->{
        for (int i = 0; i < index.length(); i++) {
            for (int z = 0; z < storageTiering.length; z++) {
                if (a.get(i).getAccessAverage() < thresholds[z]) {
                    b.append(storageTiering[i]);
                    b.append("/");
                    if (a.get(i).getLocationTier() != 0) {
                        String str = new String(Files.readAllBytes( Paths.get( index.get(i).getPath())));
                        dataStore.put(a.get(i), str);
                        File file=new File(index.get(a).getPath());
                        file.delete();
                    }
                    b.append(a.get(i).getTitle());
                    b.append(".txt");
                    p.get(b.toString());
                    Files.write(p, dataStore.get(a.get(i)).getBytes());
                    index.get(i).setPath(b.toString());
                    b.setLength(0);
                    index.get(i).setLocationTier(z);
                    }
                }
            }
        };
    ScheduledExecutorService service=Executors.newScheduledThreadPool(1);
    ScheduledFuture future=service.schedule(clean, (long)checkInterval,unit);
    }
}