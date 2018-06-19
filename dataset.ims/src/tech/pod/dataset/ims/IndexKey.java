package tech.pod.dataset.ims;
import java.util.UUID;
import java.io.Serializable;
//Minimal IndexKey interface allowing for lightweight and verbose keys for optimization
public interface IndexKey extends Serializable{
    public String getPath();
    public UUID getUUID();
    public String getHashCode();
    public String getKeyHash();
}