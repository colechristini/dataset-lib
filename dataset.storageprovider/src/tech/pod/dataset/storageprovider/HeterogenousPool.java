package tech.pod.dataset.storageprovider;

import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
//StoragePool with per-stripe tiering instead of a homogeneous pool, wherein every server is the same, containing all tiers. Individual servers run StorageDaemons to manage and send data.
public class HeterogenousPool implements StoragePoolInterface {
    List < List < SocketAddress > > storageDaemons = new ArrayList < ArrayList < SocketAddress > > ();
    List < List < SocketAddress > > storageDaemonCommandAddresses = new ArrayList < ArrayList < SocketAddress > > ();
    List < Integer > tiers = new ArrayList < Integer > ();
    List < Integer > replicationLayers = new ArrayList < Integer > ();
    ConcurrentHashMap<Integer,Integer> tierSizes=new ConcurrentHashMap<Integer,Integer>();
    HeterogenousPool() {

    }
    public SocketAddress getDaemon(int stripe) {
        return storageDaemons.get(stripe).get(replicationLayers.get(stripe));
    }
    public List<SocketAddress> getStripe(int stripe) {//gets all daemons in stripe
        return storageDaemons.get(stripe);
    }
    public SocketAddress getDaemonCommandAddress(int stripe){
        return storageDaemonsCommandAddressses.get(stripe).get(replicationLayers.get(stripe));
    }
    public void addStripe(SocketAddress[] stripeDaemons, Integer tier) {//adds a homogenous stripe
        storageDaemons.add(Arrays.asList(stripeDaemons));
        tiers.add(tier);
       if(tierSizes.get(tier)==null){//checks to see if the tier is in the tierSizes hashmap already
           tierSizes.put(tier, new Integer(stripeDaemons.length));//if not, it adds an entry to the hashmap with the key as the tier and the alue as the length of the array
       }
       else{
           tierSizes.replace(tier, Integer.add(tierSizes.get(tier),new Integer(stripeDaemons.length)));
       }
        replicationLayers.add(new Integer(0));
    }
    public void addStripe(SocketAddress[] stripeDaemons) {//adds a heterogenous stripe, not supported
        throw new UnsupportedOperationException();
    }
    public void addRepLayer(SocketAddress[] stripeDaemons) {//adds a single daemon to every stripe
        if (stripeDaemons.length == storageDaemons.size()) {
            for (int i = 0; i < storageDaemons.size(); i++) {
                storageDaemons.get(i).add(stripeDaemons[i]);
            }
        }
        else{
            throw UnsupportedOperationException;
        }
    }
    public void addRepDaemon(int stripe, SocketAddress daemon){//adds a single daemon to one stripe
        storageDaemons.get(stripe).add(daemon);
    }
    public void remove(int stripe) {//deletes a stripe
        storageDaemons.remove(stripe);
        replicationLayers.remove(stripe);
        tiers.remove(stripe);
        tierSizes.remove(tiers.get(stripe));
    }
    public void replace(int stripe, int repLayer, SocketAddress newDaemon) {
        storageDaemons.get(stripe).set(repLayer, newDaemon);
    }
    public void incrementRepLayer(int stripe) {
        replicationLayers.set(stripe, replicationLayers.get(stripe) + 1);
    }
    public List<Integer> returnTiers(){
        return tiers;
    }
    public ConcurrentHashMap<Integer,Integer> returnTierSizes(){
        return tierSizes;
    }
    public ArrayList<SocketAddress> getAllDaemons(){
        ArrayList<SocketAddress> output=new ArrayList<SocketAddress>();
        for(int i=0;i<storageDaemons.size();i++){
            output.add(storageDaemons.get(i).get(replicationLayers.get(i)));
        }
        return output;
    }
}