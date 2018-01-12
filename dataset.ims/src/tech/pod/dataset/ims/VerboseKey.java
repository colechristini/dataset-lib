import tech.pod.dataset.ims.IndexKey;

//VerboseKey is an immutable shortened version of an IndexKey used to replace an IndexKey flushed to disk

public class VerboseKey implements IndexKey{
    static final long serialVersionUID=(long)0L;
    String title;
    String path;
    UUID uuid;
    String hash;
    String selfHash;
    VerboseKey(String title, String path,UUID uuid,String hash) {
        this.title = title;
        this.path = path;
        this.uuid=uuid;
        this.hash=hash;
        selfHash=Integer.toHexString(this.hashCode());
    }
    public String getTitle(){
        return title;
    }
    public String getPath(){
        return path;
    }
    public UUID getUUID(){
        return uuid;
    }
    public String getHashCode(){
        return hash;
    }
    public String getKeyHash(){
        return selfHash;
    }
}