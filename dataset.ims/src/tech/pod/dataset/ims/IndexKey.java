package tech.pod.dataset.ims;
import java.util.UUID;
import java.io.Serializable;

public interface IndexKey extends Comparable, Serializable{
    public String getPath();
    public UUID getUUID();
    public String getHashCode();
    public String getKeyHash();
}