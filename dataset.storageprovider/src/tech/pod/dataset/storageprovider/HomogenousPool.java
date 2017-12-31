package tech.pod.dataset.storageprovider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomogenousPool implements StoragePool {
    List < List < String >> storageDaemons = new ArrayList < ArrayList < String >> ();
    List < Integer > replicationLayers = new ArrayList < Integer > ();
    HomogenousPool() {

    }
    public String get(int stripe) {
        return storageDaemons.get(get(i));
    }
    public void addStripe(String[] stripeDisks, int tier) {
    throw new UnsupportedOperationException();
    }
    public void addStripe(String[] stripeDisks) {
        storageDaemons.add(Arrays.asList(stripeDisks));
        replicationLayers.add((Integer)0);
    }
    public void addRepLayer(String[] stripeDisks) {
        for(int i=0;i<storageDaemons.size();i++){
            storageDaemons.get(i).add(stripeDisks[i]);
        }
    }
    public void remove(int stripe) {
        storageDaemons.remove(stripe);
        replicationLayers.remove(stripe);
    }
    public void replace(int stripe, int repLayer, String newDaemon) {
        storageDaemons.get(stripe).set(repLayer, newDaemon);
    }
}