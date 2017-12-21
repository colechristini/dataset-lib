package tech.pod.dataset.ims;

import java.io.Serializable;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class SortedList < T > extends ArrayList implements Serializable, Callable {
    static final long serialVersionUID = SortedList.hashcode();
    long millisTimeInterval;
    boolean b;
    SortedList(long millisTimeInterval) {
        this.millisTimeInterval = millisTimeInterval;
    }
    public void start() {
        b = true;
        ExecutorService exec = Executors.newFixedThreadPool(1);
        Callable < Object > thread = new SortedList < Object > (millisTimeInterval);
        Future < Object > Futures = new Future < Object > ();
        Future < Object > future = exec.submit(thread);
        Futures = future;
    }
    @Override
    public Object call() throws Exception {
        SortedList<T> copy=this.clone();
        while (b) {
            Collections.sort(SortedList);
            Thread.sleep(millisTimeInterval);
        }

        return null;
    }
    @Override
    public SortedList<T> clone(){
        SortedList<T> clone=new SortedList<>(millisTimeInterval);
        clone=
    }
  public  void stop() {
        b = false;
    }
}