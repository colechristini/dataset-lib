package tech.pod.dataset.storageprovider;

import java.io.Serializable;
import java.util.UUID;

import tech.pod.dataset.ims.IndexKey;

public class StorageKey implements Serializable{
    int pool,stripe;
    String hash;
    String name;
    static final UUID uuid=UUID.randomUUID();
    static final long serialVersionUID=uuid;
    StorageKey(int pool, int stripe, String name,String hash){
        this.pool=pool;
        this.stripe=stripe;
        this.name=name;
        this.hash=hash;
    }
    public int[] getPath(){
        int[] list={pool,stripe};
        return list;
    }
    public String getHash(){
        return hash;
    }
    public UUID getUUID(){
        return uuid;
    }
}