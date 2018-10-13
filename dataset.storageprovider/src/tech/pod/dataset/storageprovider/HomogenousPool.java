package tech.pod.dataset.storageprovider;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//StoragePool whose servers contain multiple storage tiers in each server,
//instead of per stripe tiering

public class HomogenousPool implements StoragePoolInterface {
    List < List < SocketAddress > > storageDaemons = new ArrayList < ArrayList < SocketAddress >> ();
    List < Integer > replicationLayers = new ArrayList < Integer > ();
    HomogenousPool() {

    }
    public List<SocketAddress> getStripe(int stripe) {
        return storageDaemons.get(stripe);
    }
    public SocketAddress getDaemon(Integer stripe){
        return storageDaemons.get(stripe).get(replicationLayers.get(stripe));
    }
    public void addStripe(String[] stripeDaemons, int tier) {
        throw new UnsupportedOperationException();
    }
    public void addStripe(SocketAddress[] stripeDaemons) {
        storageDaemons.add(Arrays.asList(stripeDaemons));
        replicationLayers.add((Integer)0);
    }
    public void addRepLayer(SocketAddress[] stripeDaemons) {
        for(int i=0;i<storageDaemons.size();i++){
            storageDaemons.get(i).add(stripeDaemons[i]);
        }
    }
    public void remove(int stripe) {
        storageDaemons.remove(stripe);
        replicationLayers.remove(stripe);
    }
    public void replace(int stripe, int repLayer, SocketAddress newDaemon) {
        storageDaemons.get(stripe).set(repLayer, newDaemon);
    }
    public ArrayList<SocketAddress> getAllDaemons(){
        ArrayList<SocketAddress> output=new ArrayList<SocketAddress>();
        for(int i=0;i<storageDaemons.size();i++){
            output.add(storageDaemons.get(i).get(replicationLayers.get(i)));
        }
        return output;
    }
    public void incrementRepLayer(int stripe) {
        replicationLayers.set(stripe, replicationLayers.get(stripe) + 1);
    }
}