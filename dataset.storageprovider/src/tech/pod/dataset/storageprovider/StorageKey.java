package tech.pod.dataset.storageprovider;

import java.io.Serializable;
import java.util.UUID;

public class StorageKey implements Serializable {
    int pool, stripe;
    String hash;
    String name;
    static final UUID uuid = UUID.randomUUID();
    static final long serialVersionUID = Integer.parseInt(uuid.toString());

    StorageKey(int pool, int stripe, String name) {
        this.pool = pool;
        this.stripe = stripe;
        this.name = name;
        hash = Integer.toHexString(this.hashCode());
    }

    public int[] getPath() {
        int[] list = { pool, stripe };
        return list;
    }

    public String getHash() {
        return hash;
    }

    public UUID getUUID() {
        return uuid;
    }
}