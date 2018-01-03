package tech.pod.dataset.ims;

public interface DataStore{
    public void put(IndexKey i, String s);

    public String get(IndexKey i);
 
    public String get(int i) ;

    public void start();
}