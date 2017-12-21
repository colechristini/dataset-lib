package tech.pod.dataset.ims;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class Index implements Serializable, Callable {
    private static final long serialVersionUID = DataStore.hashcode();
    List < IndexKey > IndexKeyStore;
    DataStore d;
    int cleanInterval;
    boolean b;
    long maxIndexStorage;
    int ListMemory;
    IndexKeyStore(DataStore d, int cleanInterval, long millisTimeInterval, boolean b, long maxIndexStorage) {
        IndexKeyStore = new SortedList(millisTimeInterval);
        this.d = d;
        this.cleanInterval = cleanInterval;
        this.b = b;
        this.maxIndexStorage = maxIndexStorage;
    }
    public long calcMemory() {
        return (long) 322 * IndexKeyStore.length;
    }
    public int length() {
        return IndexKeyStore.length;
    }
    public IndexKey get(int location) {
        IndexKeyStore.get(location).incrementCounter();
        IndexKeyStore.get(location).update();
        return IndexKeyStore.get(location);
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
        IndexKeyStore.add(i);
        d.add(i, s);
    }
    public void start() {
        b = true;
        ExecutorService exec = Executors.newFixedThreadPool(threadPoolSize);
        Callable < Object > thread = new SortedList < Object > (millisTimeInterval);
        Future < Object > Futures = new Future < Object > ();
        Future < Object > future = exec.submit(thread);
        Futures = future;
    }
    public void stop() {

    }
    @Override
    public Object call() throws Exception {
        String oldTitle;
        IndexKey ik;
        int checkCode;
        String newTitle;
        long time;
        long tempTime;
        while (b) {
            time++;
            if (time == millisTimeInterval || calcMemory() == maxIndexStorage) {
                tempTime=calcMemory();
                for (int a = 0; a < IndexKeyStore.length; a++) {
                    IndexKeyStore.get(a).setLocation(a);
                    ik = IndexKeyStore.get(a);
                    for (int i = 0; i < IndexKeyStore.length; a++) {
                        checkCode = ik.duplicateCheck(IndexKeyStore.get(a));
                        if (checkCode == 1) {
                            newTitle = IndexKeyStore.get(i).getTitle() + "1";

                            IndexKeyStore.get(i).setTitle(newTitle);
                        } else if (checkCode == 2) {

                            newTitle = IndexKeyStore.get(a).getTitle() + " or " + IndexKeyStore.get(i).getTitle();


                            IndexKeyStore.get(a).setTitle(newTitle);
                            IndexKeyStore.remove(i);

                        } else if (checkCode == 3) {
                            IndexKeyStore.remove(i);

                        }
                    }
                }
                time = 0;
            }
            while(tempTime==calcMemory()){
                Thread.sleep(1);
            }
            Thread.sleep(1);
        }
        return null;
    }
}