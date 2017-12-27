package tech.pod.dataset.ims;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;

public class Index implements Serializable, Callable {
    private static final long serialVersionUID = DataStore.hashcode();
    List < Object > IndexKeyStore;
    DataStore d;
    int cleanInterval;
    boolean b;
    long maxIndexStorage;
    int ListMemory;
    int keyEjectionLevel;
    String keySavePath;
    boolean isBuffered;
    List<IndexKey> keyBuffer=new ArrayList<IndexKey>();
    int bufferSize;
    IndexKeyStore(DataStore d, int cleanInterval, long millisTimeInterval, boolean b, long maxIndexStorage, int keyEjectionLevel, String keySavePath) {
        IndexKeyStore = Collections.synchronizedList(ArrayList());
        IndexKeyStore.start();
        this.d = d;
        this.cleanInterval = cleanInterval;
        this.b = b;
        this.maxIndexStorage = maxIndexStorage;
        this.keyEjectionLevel = keyEjectionLevel;
        this.keySavePath = keySavePath;
    }
    public long calcMemory() {
        return (long) 422 * IndexKeyStore.size();
    }
    public int length() {
        return IndexKeyStore.size();
    }
    public IndexKey get(int location) {

        if (IndexKeyStore.get(location) instanceof IndexKey) {
            IndexKeyStore.get(location).incrementCounter();
            IndexKeyStore.get(location).update();
            return IndexKeyStore.get(location);
        } else if (IndexKeyStore.get(location) instanceof VerboseKey) {
            IndexKey i;
            FileInputStream fs = new FileInputStream(keySavePath + "/" + ((VerboseKey) IndexKeyStore.get(location)).getTitle() + ".ser");
            ObjectInputStream in = new ObjectInputStream(fs);
            i = in .readObject();
            i.setLocation(location);
            IndexKeyStore.remove(location);
            IndexKeyStore.add(location, i);
            IndexKeyStore.get(location).incrementCounter();
            IndexKeyStore.get(location).update();
            return i;
        }

    }
    public String getFromStore(IndexKey i) {
        i.incrementCounter();
        i.update();
        return d.get(i.getLocation());

    }
    public void set(IndexKey i, String str) {
        d.replace(i, str);

    }
    public void add(IndexKey i, String str) {
        
        if(!isBuffered){ 
        i.setLocation(IndexKeyStore.size());
        IndexKeyStore.add(i);
        d.add(i, str);
        }
        else{
            bufferSize++;
            i.setLocation(IndexKeyStore.size()+bufferSize);
            keyBuffer.add(i);
            d.add(i, str);
        }
    }
    public void start(long cleanTime, TimeUnit cleanTimeUnit,long ejectCheckTime,TimeUnit ejectTimeCheckUnit) {
     
        Runnable duplicateCheck = () -> {
            isBuffered=true;
            String oldTitle;
            IndexKey ik;
            int checkCode;
            String newTitle;
            ArrayList < Object > tempStore = this.getList();
            for (int a = 0; a < IndexKeyStore.size(); a++) {
                if (tempStore.get(a) instanceof IndexKey) {
                    ik = tempStore.get(a);
                    for (int i = 0; i < IndexKeyStore.size(); a++) {
                        if(tempStore.get(i) instanceof IndexKey)
                        {
                            IndexKey key=tempStore.get(i);
                            checkCode = ik.duplicateCheck(key);
                        switch (checkCode) {
                            case 1:
                                newTitle = key.getTitle() + "1";
                                tempStore.setTitle(newTitle);
                                break;
                            case 2:
                                newTitle = IndexKeyStore.get(a).getTitle() + " or " + IndexKeyStore.get(i).getTitle();
                                IndexKeyStore.get(a).setTitle(newTitle);
                                IndexKeyStore.remove(i);
                                break;

                            case 3:
                                IndexKeyStore.remove(i);
                                break;
                            case 4:
                                break;
                        }}

                    }
                } else {
                    continue;
                }
            }
            IndexKeyStore=tempStore;
            IndexKeyStore.addAll(keyBuffer);
            keyBuffer.clear();
            isBuffered=false;
        };
        Runnable keyListEjectCheckAgent=() -> {
            isBuffered=true;
            ArrayList < Object > tempStore = this.getList();
            for(int i=0;i<tempStore.length;i++){
                if(tempStore.get(i) instanceof IndexKey){
                    if(tempStore.get(i).getAccessAverage()<keyEjectionLevel){
                        FileOutputStream fs = new FileOutputStream(keySavePath + "/" + tempStore.get(i).getTitle() + ".ser");
                        ObjectOutputStream out = new ObjectOutputStream(fs);
                        out.writeObject(temp.get(i));
                        out.close();
                        fs.close();
                        IndexKey key=tempStore.get(i);
                        tempStore.remove(i);
                        tempStore.add(i,new VerboseKey(key.getTitle(),keySavePath + "/" + tempStore.get(i).getTitle() + ".ser"));
                       
                        
                    }
                }
                else{
                    continue;
                }
            }
            IndexKeyStore=tempStore;
            IndexKeyStore.addAll(keyBuffer);
            keyBuffer.clear();
            isBuffered=false;
        };
        ScheduledExecutorService keyDuplicateCleanExecutorService = Executors.newScheduledThreadPool(1);
        ScheduledFuture keyDuplicateCleanFuture = keyDuplicateCleanExecutorService.scheduleAtFixedRate(duplicateCheck, cleanTime, cleanTime, cleanTimeUnit);
        ScheduledExecutorService keyEjectCheckExecutorService = Executors.newScheduledThreadPool(1);
        ScheduledFuture keyEjectCheckFuture = keyEjectCheckExecutorService.scheduleAtFixedRate(keyListEjectCheckAgent, ejectCheckTime, ejectCheckTime, ejectTimeCheckUnit);
    }

    @Override
    public void stop() {
        b = false;
    }

    public List < IndexKey > search(String queryInput) {
        final String query = queryInput;
        List < IndexKey > output = new ArrayList < IndexKey > ();
        Callable < List < List < List < String >>> > c = () -> {
            String[] queries = query.split("|");
            List < List < String >> querygroup = new ArrayList < ArrayList < String >> ();
            for (int i = 0; i < queries.length; i++) {
                querygroup.get(i).addAll(Arrays.asList(queries[i].split(";")));
            }
            List < List < List < String >>> queryset = new ArrayList < ArrayList < ArrayList < String >>> ();
            for (int i = 0; i < queryset.size(); i++) {
                for (int j = 0; j < queryset.get(i).size(); j++) {
                    queryset.get(i).get(j).addAll(Arrays.asList(Arrays.asList(querygroup.get(i).get(j).split(":")).get(1).split(",")));
                }
            }

        };
        ExecutorService service = Executors.newSingleThreadExecutor();
        ScheduledFuture < List < List < List < String >>> > scheduledFuture = service.submit(c);
        if (scheduledFuture.isDone()) {
            List < List < List < String >>> queryBlock = scheduledFuture.get();
        }
        List < SearchAgent > searchAgents = new ArrayList < SearchAgent > ();
        List < Callable < List < IndexKey > > > searchAgentsOutput = new ArrayList < Callable < List < IndexKey >>> ();
        List < ScheduledFuture < List < IndexKey >>> scheduledFutures = new ArrayList < ScheduledFuture < List < IndexKey >>> ();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < queryBlock.size(); i++) {
            searchAgents.add(new SearchAgent(queryBlock.get(i), IndexKeyStore));
            Callable<List<IndexKey>> tempCallable = () -> {
                return searchAgents.get(i).search();
            };
            searchAgentsOutput.add(tempCallable);
            scheduledFutures = executorService.submit(searchAgentsOutput.get(i));
        }
        int i = 0;
        while (i < searchAgents.size()) {
            for (int a = 0; searchAgents.size(); a++) {
                if (scheduledFutures.get(a).isDone()) {
                    output.addAll(scheduledFutures.get(i).get());
                    i++;
                }
            }
        }
        return output;
    }
    public void backup(String name, String path,Index index) {
        try {
            FileOutputStream fs = new FileOutputStream(path + "/" + name + ".ser");
            ObjectOutputStream out = new ObjectOutputStream(fs);
            out.writeObject(index);
            out.close();
            fs.close();
        } catch (IOException e) {
            //TODO: handle exception
        }

    }
    public void restore(String name, String path, Index i) {
        try {
            Index index = null;
            FileInputStream fs = new FileInputStream(path + "/" + name + ".ser");
            ObjectInputStream in = new ObjectInputStream(fs);
            index = in .readObject();
            i = index;
        } catch (IOException e) {
            //TODO: handle exception
        }
    }
    public List<Object> getList(){
        final List<Object> list=IndexKeyStore;
        return list;
    }
}