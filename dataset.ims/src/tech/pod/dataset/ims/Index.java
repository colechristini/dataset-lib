package tech.pod.dataset.ims;

import java.util.ArrayList;
//Basic Index interface implemented by every Index
public interface Index{
public IndexKey get(int location);
public Object getFromStore(IndexKey i);
public void add(IndexKey i, String content);
public void remove(int location);
public void set(IndexKey i,String content);
public void replace(int index, IndexKey element);
public ArrayList<IndexKey> search(String query);
public ArrayList<Object> stats();
}