package tech.pod.dataset.appserver;

public interface AppServlet<T>{
public T get(String file);
public void put(T buffer);
public void remove(String file);
}