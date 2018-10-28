package tech.pod.dataset.storageprovider;

import java.io.Serializable;
import java.util.UUID;


public class StorageKey implements Serializable{
    int pool,stripe;
    String hash;
    String name;
    static final UUID uuid=UUID.randomUUID();
    static Integer temp=Integer.parseInt(uuid.toString());
    static int temp1=temp;
    static long temp2=temp1;
    static final long serialVersionUID=temp2;
    StorageKey(int pool, int stripe, String name, String hash){
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