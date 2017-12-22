package tech.pod.dataset.ims;

import java.io.Serializable;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

public class SortedList < T > extends ArrayList implements Serializable, Callable, Cloneable {
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
        List<?> copy;
        while (b) {
            copy=(SortedList<?>) super.clone();
            Collections.sort(copy);
            super.SortedList=copy;
            Thread.sleep(millisTimeInterval);
        }

        return null;
    }
    @Override
    public List<T> clone(){
        List<T> clone=new SortedList<>(millisTimeInterval);
        clone=super.SortedList;
        return clone;
    }
  public  void stop() {
        b = false;
    }
}