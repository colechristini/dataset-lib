package tech.pod.dataset.storageprovider;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public interface StoragePoolInterface {
    public List < InetSocketAddress > getStripe(int stripe); //retrieves all daemons in a stripe
    public InetSocketAddress getDaemon(int stripe);
    public void addStripe(InetSocketAddress[] stripeDaemons, int tier); //adds a heterogenous pool stripe
    public void addStripe(InetSocketAddress[] stripeDaemons); //adds a homogenous pool stripe
    public void addRepLayer(InetSocketAddress[] stripeDaemons); //ads a single layer of daemons to every stripe
    public void remove(int stripe); //deletes a stripe
    public ArrayList < InetSocketAddress > getAllDaemons(); //retrieves all daemons on the current increment level of every stripe
    public void incrementRepLayer(int stripe); //increments the replication layer of the stripe
    public int getStripeCount();
}