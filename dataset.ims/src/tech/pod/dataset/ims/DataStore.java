package tech.pod.dataset.ims;
//Basic DataStore interface implemented by every DataStore
public interface DataStore{
    public void put(IndexKey i, String s);

    public String get(IndexKey i);
 
    public String get(int i) ;

    public void start();
}