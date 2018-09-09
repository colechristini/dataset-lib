package tech.pod.dataset.storageprovider;

import java.net.SocketAddress;
import java.util.List;

public interface StoragePoolInterface {
    public List < SocketAddress > getStripe(int stripe); //retrieves all daemons in a stripe
    public SocketAddress getDaemon(int stripe);
    public void addStripe(String[] stripeDaemons, int tier); //adds a heterogenous pool stripe
    public void addStripe(String[] stripeDaemons); //adds a homogenous pool stripe
    public void addRepLayer(String[] stripeDaemons); //ads a single layer of daemons to every stripe
    public void remove(int stripe); //deletes a stripe
    public List < SocketAddress > getAllDaemons(); //retrieves all daemons on the current increment level of every stripe
}