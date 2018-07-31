package tech.pod.dataset.storageprovider;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//StoragePool whose servers contain multiple storage tiers in each server, instead of per stripe tiering
public class HomogenousPool implements StoragePool {
    List < List < String >> storageDaemons = new ArrayList < ArrayList < String >> ();
    List < Integer > replicationLayers = new ArrayList < Integer > ();
    HomogenousPool() {

    }
    public String get(int stripe) {
        return storageDaemons.get(stripe));
    }
    public SocketAddress getDaemonCommandAddress(int stripe){
        return storageDaemonsCommandAddressses.get(stripe).get(replicationLayers.get(stripe));
    }
    public void addStripe(String[] stripeDaemons, int tier) {
        throw new UnsupportedOperationException();
    }
    public void addStripe(String[] stripeDaemons) {
        storageDaemons.add(Arrays.asList(stripeDaemons));
        replicationLayers.add((Integer)0);
    }
    public void addRepLayer(String[] stripeDaemons) {
        for(int i=0;i<storageDaemons.size();i++){
            storageDaemons.get(i).add(stripeDaemons[i]);
        }
    }
    public void remove(int stripe) {
        storageDaemons.remove(stripe);
        replicationLayers.remove(stripe);
    }
    public void replace(int stripe, int repLayer, String newDaemon) {
        storageDaemons.get(stripe).set(repLayer, newDaemon);
    }
    public ArrayList<String> getAllDaemons(){
        List<String> output=new ArrayList<String>();
        for(int i=0;i<storageDaemons.size();i++){
            output.add(storageDaemons.get(i).get(replicationLayers.get(i)));
        }
        return output;
    }
    public void incrementRepLayer(int stripe) {
        replicationLayers.set(stripe, replicationLayers.get(stripe) + 1);
    }
}