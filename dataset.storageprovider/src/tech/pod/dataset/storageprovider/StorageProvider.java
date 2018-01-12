package tech.pod.dataset.storageprovider;
// basic StorageProvider interface.
public interface StorageProvider{
public void put(Object[] o);
public Object get(Object[] o);
public void remove(Object[] o);
}