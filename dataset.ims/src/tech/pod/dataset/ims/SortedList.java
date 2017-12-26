package tech.pod.dataset.ims;

import org.threadly.concurrent.collections.ConcurrentArrayList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

public class SortedList < T > extends ConcurrentArrayList implements Serializable, Callable, Cloneable {
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
        Future < Object > future = exec.submit(thread);
    }
    @Override
    public Object call() throws Exception {
        List<?> copy;
        while (b) {
            copy=(SortedList<?>) SortedList.this;
            Collections.sort(copy);
            super.SortedList=copy;
            Thread.sleep(millisTimeInterval);
        }

        return null;
    }
   /* @Override
    public List<T> clone(){
        List<T> clone=new SortedList<>(millisTimeInterval);
        clone=super.SortedList;
        return clone;
    }*/
  public  void stop() {
        b = false;
    }
    public void backup(String name,String path){
        try {
            SortedList<T> sortedList=this;
            FileOutputStream fs=new FileOutputStream(path+"/"+name+".ser");
            ObjectOutputStream out=new ObjectOutputStream(fs);
            out.writeObject(sortedList);
            out.close();
            fs.close(); 
        } catch (IOException e) {
            //TODO: handle exception
        }
      
    }
    public void restore(String name,String path,SortedList s){
        try {
            SortedList<T> sortedList=null;
            FileInputStream fs=new FileInputStream(path+"/"+name+".ser");
            ObjectInputStream in=new ObjectInputStream(fs);
            sortedList=in.readObject();
            s=sortedList;
        } catch (IOException e) {
            //TODO: handle exception
        }
    }
}

