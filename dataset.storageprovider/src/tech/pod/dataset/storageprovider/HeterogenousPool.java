package tech.pod.dataset.storageprovider;

import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
//StoragePool with per-stripe tiering instead of a homogeneous pool where every server is the same, containing all tiers. Individual servers run StorageDaemons to manage and send data.
public class HeterogenousPool implements StoragePool {
    List < List < String >> storageDaemons = new ArrayList < ArrayList < String >> ();
    List < List < SocketAddress >> storageDaemonCommandAddresses = new ArrayList < ArrayList < SocketAddress >> ();
    List < Integer > tiers = new ArrayList < Integer > ();
    List < Integer > replicationLayers = new ArrayList < Integer > ();
    ConcurrentHashMap<Integer,Integer> tierSizes=new ConcurrentHashMap<Integer,Integer>();
    HeterogenousPool() {

    }
    public String get(int stripe) {
        return storageDaemons.get(stripe).get(replicationLayers.get(stripe));
    }
    public SocketAddress getDaemonCommandAddress(int stripe){
        return storageDaemonsCommandAddressses.get(stripe).get(replicationLayers.get(stripe));
    }
    public void addStripe(String[] stripeDaemons, Integer tier) {
        storageDaemons.add(Arrays.asList(stripeDaemons));
        tiers.add(tier);
       if(tierSizes.get(tier)==null){
           tierSizes.put(tier, (Integer)1);
       }
        replicationLayers.add((Integer)0);
    }
    public void addStripe(String[] stripeDaemons) {
        throw new UnsupportedOperationException();
    }
    public void addRepLayer(String[] stripeDaemons) {
        if (stripeDaemons.length == storageDaemons.size()) {
            for (int i = 0; i < storageDaemons.size(); i++) {
                storageDaemons.get(i).add(stripeDaemons[i]);
            }
        }
        else{
            throw UnsupportedOperationException;
        }
    }
    public void remove(int stripe) {
        storageDaemons.remove(stripe);
        replicationLayers.remove(stripe);
        tiers.remove(stripe);
        tierSizes.remove(tiers.get(stripe));
    }
    public void replace(int stripe, int repLayer, String newDaemon) {
        storageDaemons.get(stripe).set(repLayer, newDaemon);
    }
    public void incrementRepLayer(int stripe) {
        replicationLayers.set(stripe, replicationLayers.get(stripe) + 1);
    }
    public List<Integer> returnTiers(){
        return tiers;
    }
    public HashMap<String,Integer> returnTierSizes(){
        return tierSizes;
    }
    public ArrayList<String> getAllDaemons(){
        List<String> output=new ArrayList<String>();
        for(int i=0;i<storageDaemons.size();i++){
            output.add(storageDaemons.get(i).get(replicationLayers.get(i)));
        }
        return output;
    }
}