package tech.pod.dataset.storageprovider;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//StoragePool whose servers contain multiple storage tiers in each server,
//instead of per stripe tiering

public class HomogenousPool implements StoragePoolInterface {
    ArrayList <ArrayList < InetSocketAddress > > storageDaemons = new ArrayList < ArrayList < InetSocketAddress >> ();
    List < Integer > replicationLayers = new ArrayList < Integer > ();
    HomogenousPool() {

    }
    public List<InetSocketAddress> getStripe(int stripe) {
        return storageDaemons.get(stripe);
    }
    public InetSocketAddress getDaemon(int stripe) {
        return storageDaemons.get(stripe).get(replicationLayers.get(stripe));
    }
    public void addStripe(InetSocketAddress[] stripeDaemons, int tier) {
        throw new UnsupportedOperationException();
    }
    public void addStripe(InetSocketAddress[] stripeDaemons) {
        ArrayList<InetSocketAddress> temp=new ArrayList<InetSocketAddress>();
        temp.addAll(Arrays.asList(stripeDaemons));
        storageDaemons.add(temp);
        replicationLayers.add((Integer)0);
    }
    public void addRepLayer(InetSocketAddress[] stripeDaemons) {
        for(int i=0;i<storageDaemons.size();i++){
            storageDaemons.get(i).add(stripeDaemons[i]);
        }
    }
    public void remove(int stripe) {
        storageDaemons.remove(stripe);
        replicationLayers.remove(stripe);
    }
    public void replace(int stripe, int repLayer, InetSocketAddress newDaemon) {
        storageDaemons.get(stripe).set(repLayer, newDaemon);
    }
    public ArrayList<InetSocketAddress> getAllDaemons(){
        ArrayList<InetSocketAddress> output=new ArrayList<InetSocketAddress>();
        for(int i=0;i<storageDaemons.size();i++){
            output.add(storageDaemons.get(i).get(replicationLayers.get(i)));
        }
        return output;
    }
    public void incrementRepLayer(int stripe) {
        replicationLayers.set(stripe, replicationLayers.get(stripe) + 1);
    }
    public int getStripeCount(){
        return storageDaemons.size();
    }
}