package tech.pod.dataset.ims;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        IndexKeyStore = Collections.synchronizedList(SortedList(millisTimeInterval));
        IndexKeyStore.start();
        this.d = d;
        this.cleanInterval = cleanInterval;
        this.b = b;
        this.maxIndexStorage = maxIndexStorage;
    }
    public long calcMemory() {
        return (long) 358 * IndexKeyStore.length;
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
    public List < IndexKey > search(String[] query) {
        final String[] querySet = query;
        final List < ? extends IndexKey > tempList = IndexKeyStore;
        List < IndexKey > returnList = new ArrayList < IndexKey > ();
        Callable < List < IndexKey > > searchCall = () -> {
            if (Arrays.asList(querySet).indexOf("from:") != -1 && querySet[Arrays.asList(querySet).indexOf("from:")].indexOf("any") == -1) {
                String[] timeQuery=list[Arrays.asList(querySet).indexOf("to:")].split(":");
                timeQuery=timeQuery[1].split("&");
                Date[] date=new Date[timeQuery.length];
                DateFormat format = new SimpleDateFormat("   yyyy.MM.dd  HH:mm:ss z", Locale.ENGLISH);
                for(int i=0;i<timeQuery.length;i++){
                    date[i]=format.parse(timeQuery[i].split("-")[1]);
                }
                if(Arrays.asList(timeQuery).indexOf("AccessTime")!=-1)
                {for (int i=0;i<returnList.length;i++) {
                  
                    if (tempList.get(i).getLastAccessTime().before(date[Arrays.asList(timeQuery).indexOf("AccessTime")])) {
                        returnList.add(tempList.get(i));
                    } else {
                        continue;
                    }
                }}
                if(Arrays.asList(timeQuery).indexOf("ImportTime")!=-1)
                {for (int i=0;i<returnList.length;i++) {
                    
                    if (tempList.get(i).getImportTime().before(date[Arrays.asList(timeQuery).indexOf("ImportTime")])) {
                        returnList.add(tempList.get(i));
                    } else {
                        continue;
                    }
                    
                }
            }
            if(Arrays.asList(timeQuery).indexOf("CreationTime")!=-1)
            {for (int i=0;i<returnList.length;i++) {
                
                if (tempList.get(i).getImportTime().before(date[Arrays.asList(timeQuery).indexOf("CreationTime")])) {
                    returnList.add(tempList.get(i));
                } else {
                    continue;
                }
                
            }
        }
            }
            else if (Arrays.asList(querySet).indexOf("from:") != -1 && querySet[Arrays.asList(querySet).indexOf("from:")].indexOf("any") != -1) {
                returnList=tempList;
            }
            if (Arrays.asList(querySet).indexOf("to:") != -1 && querySet[Arrays.asList(querySet).indexOf("to:")].indexOf("any") == -1) {
                String[] timeQuery=list[Arrays.asList(querySet).indexOf("to:")].split(":");
                
                timeQuery=timeQuery[1].split("&");
                Date[] date=new Date[timeQuery.length];
                DateFormat format = new SimpleDateFormat("   yyyy.MM.dd  HH:mm:ss z", Locale.ENGLISH);
                for(int i=0;i<timeQuery.length;i++){
                    date[i]=format.parse(timeQuery[i].split("-")[1]);
                }
                if(Arrays.asList(timeQuery).indexOf("AccessTime")!=-1)
                {for (int i=0;i<returnList.length;i++) {
                  
                    if (tempList.get(i).getLastAccessTime().before(date[Arrays.asList(timeQuery).indexOf("AccessTime")])) {
                        returnList.add(tempList.get(i));
                    } else {
                        continue;
                    }
                }}
                if(Arrays.asList(timeQuery).indexOf("ImportTime")!=-1)
                {for (int i=0;i<returnList.length;i++) {
                    
                    if (tempList.get(i).getImportTime().before(date[Arrays.asList(timeQuery).indexOf("ImportTime")])) {
                        returnList.add(tempList.get(i));
                    } else {
                        continue;
                    }
                    
                }
            }
            if(Arrays.asList(timeQuery).indexOf("CreationTime")!=-1)
            {for (int i=0;i<returnList.length;i++) {
                
                if (tempList.get(i).getImportTime().before(date[Arrays.asList(timeQuery).indexOf("CreationTime")])) {
                    returnList.add(tempList.get(i));
                } else {
                    continue;
                }
                
            }
        } 
    }
        
        return returnList;
    };


}}