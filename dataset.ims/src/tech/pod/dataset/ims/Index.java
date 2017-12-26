package tech.pod.dataset.ims;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;

import com.sun.jndi.url.iiopname.iiopnameURLContextFactory;

import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;

public class Index implements Serializable, Callable {
    private static final long serialVersionUID = DataStore.hashcode();
    List < IndexKey > IndexKeyStore;
    DataStore d;
    int cleanInterval;
    boolean b;
    long maxIndexStorage;
    int ListMemory;
    IndexKeyStore(DataStore d, int cleanInterval, long millisTimeInterval, boolean b, long maxIndexStorage) {
        IndexKeyStore = Collections.synchronizedList(SortedList(millisTimeInterval));
        IndexKeyStore.start();
        this.d = d;
        this.cleanInterval = cleanInterval;
        this.b = b;
        this.maxIndexStorage = maxIndexStorage;
    }
    public long calcMemory() {
        return (long) 422 * IndexKeyStore.length;
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
        Future < Object > future = exec.submit(thread);

    }
    @Override
    public void stop() {
        b = false;
    }
    @Override
    public Object call() throws Exception {

        String oldTitle;
        IndexKey ik;
        int checkCode;
        String newTitle;
        long time;
        long tempTime;
        long tempTime2;
        while (b) {

            time++;
            if (time == millisTimeInterval || calcMemory() == maxIndexStorage) {
                SortedList tempStore = IndexKeyStore;
                tempStore.stop();
                here: tempTime = calcMemory();
                for (int a = 0; a < IndexKeyStore.length; a++) {
                    tempStore.get(a).setLocation(a);
                    ik = tempStore.get(a);
                    for (int i = 0; i < IndexKeyStore.length; a++) {
                        checkCode = ik.duplicateCheck(IndexKeyStore.get(i));
                        switch (checkCode) {
                            case 1:
                                newTitle = IndexKeyStore.get(i).getTitle() + "1";
                                IndexKeyStore.get(i).setTitle(newTitle);
                                break;
                            case 2:

                                newTitle = IndexKeyStore.get(a).getTitle() + " or " + IndexKeyStore.get(i).getTitle();


                                IndexKeyStore.get(a).setTitle(newTitle);
                                IndexKeyStore.remove(i);
                                break;

                            case 3:
                                IndexKeyStore.remove(i);

                        }
                    }
                }
            }
            time = 0;
            tempStore.start();
            IndexKeyStore = tempStore;
        }
        while (tempTime == calcMemory()) {
            tempTime2 = calcMemory();
            Thread.sleep(1);
            if (tempTime != tempTime2) {
                break here;
            }
        }
        Thread.sleep(1);

        return null;
    }
    public List < IndexKey > searchOutput(String queryInput) {
        final String query = queryInput;
        List < IndexKey > output = new ArrayList < IndexKey > ();
        Callable < List < List < List < String >>> > c = () -> {
            String[] queries = query.split("|");
            List < List < String >> querygroup = new ArrayList < ArrayList < String >> ();
            for (int i = 0; i < queries.length; i++) {
                querygroup.get(i).addAll(Arrays.asList(queries[i].split(";")));
            }
            List < List < List < String >>> queryset = new ArrayList < ArrayList < ArrayList < String >>> ();
            for (int i = 0; i < queryset.length; i++) {
                for (int j = 0; j < queryset.get(i).length; j++) {
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
        for (int i = 0; i < queryBlock.length; i++) {
            searchAgents.add(new SearchAgent(queryBlock.get(i), IndexKeyStore));
            output.addAll(searchAgents.get(i).search());
        }
        return output;
    }
    public void backup(String name, String path) {
        try {
            Index index = this;
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
}