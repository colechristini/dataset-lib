package tech.pod.dataset.storageprovider;
// basic StorageProvider interface.
public interface StorageProviderInterface {
    public void put(String objectName, Object object);
    public Object get(String objectName);
    public void remove(String objectName);
}