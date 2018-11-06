package tech.pod.dataset.storageprovider;

import java.net.InetAddress;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

//StoragePool with per-stripe tiering instead of a homogeneous pool, wherein every server is the same, containing all tiers. Individual servers run StorageDaemons to manage and send data.
public class HeterogenousPool implements StoragePoolInterface {
    ArrayList<ArrayList<InetSocketAddress>> storageDaemons = new ArrayList<ArrayList<InetSocketAddress>>();
    HashMap<Integer, List<Integer>> tiers = new HashMap<Integer, List<Integer>>();
    List<Integer> replicationLayers = new ArrayList<Integer>();
    ConcurrentHashMap<Integer, Integer> tierSizes = new ConcurrentHashMap<Integer, Integer>();
    List<Integer> tierCycles = new ArrayList<Integer>();

    HeterogenousPool() {
    }

    public InetSocketAddress getDaemon(int stripe) {
        return storageDaemons.get(stripe).get(replicationLayers.get(stripe));
    }

    public List<InetSocketAddress> getStripe(int stripe) { // gets all daemons in stripe
        return storageDaemons.get(stripe);
    }

    public void addStripe(InetSocketAddress[] stripeDaemons, int tier) { // adds a homogenous stripe
        ArrayList<InetSocketAddress> temp = new ArrayList<InetSocketAddress>();
        temp.addAll(Arrays.asList(stripeDaemons));
        storageDaemons.add(temp);
        if (tiers.containsKey(new Integer(tier))) {
            tiers.get(new Integer(tier)).add(new Integer(storageDaemons.size() - 1));
        } else {
            List<Integer> tempList = new ArrayList<Integer>();
            tempList.add(new Integer(storageDaemons.size() - 1));
            tiers.put(new Integer(tier), tempList);
        }
        if (tierSizes.get(tier) == null) { // checks to see if the tier is in the tierSizes hashmap already
            tierSizes.put(tier, new Integer(stripeDaemons.length)); // if not, it adds an entry to the hashmap with the
                                                                    // key as the tier and the value as the length of
                                                                    // the array
        } else {
            tierSizes.replace(tier, Integer.sum((int) tierSizes.get(tier), stripeDaemons.length));
        }
        replicationLayers.add(new Integer(0));
    }

    public void addStripe(InetSocketAddress[] stripeDaemons) { // adds a homogenous stripe, not supported
        throw new UnsupportedOperationException();
    }

    public void addRepLayer(InetSocketAddress[] stripeDaemons) { // adds a single daemon to every stripe
        if (stripeDaemons.length == storageDaemons.size()) {
            for (int i = 0; i < storageDaemons.size(); i++) {
                storageDaemons.get(i).add(stripeDaemons[i]);
            }
        } else {
            // throw UnsupportedOperationException;
        }
    }

    public void addRepDaemon(int stripe, InetSocketAddress daemon) { // adds a single daemon to one stripe
        storageDaemons.get(stripe).add(daemon);
    }

    public void remove(int stripe) { // deletes a stripe
        storageDaemons.remove(stripe);
        replicationLayers.remove(stripe);
        tiers.remove(stripe);
        tierSizes.remove(tiers.get(stripe));
    }

    public void replace(int stripe, int repLayer, InetSocketAddress newDaemon) {
        storageDaemons.get(stripe).set(repLayer, newDaemon);
    }

    public void incrementRepLayer(int stripe) {
        replicationLayers.set(stripe, replicationLayers.get(stripe) + 1);
    }

    public List<Integer> returnTiers() {
        return tiers;
    }

    public ConcurrentHashMap<Integer, Integer> returnTierSizes() {
        return tierSizes;
    }

    public ArrayList<InetSocketAddress> getAllDaemons() {
        ArrayList<InetSocketAddress> output = new ArrayList<InetSocketAddress>();
        for (int i = 0; i < storageDaemons.size(); i++) {
            output.add(storageDaemons.get(i).get(replicationLayers.get(i)));
        }
        return output;
    }

    public int getStripeCount() {
        return storageDaemons.size();
    }

    public InetSocketAddress getDaemonByTier(int tier) {
        int temp = tierCycles.get(tier).intValue();
        temp++;
        if (temp > tierSizes.get(new Integer(tier))) {
            temp = 0;
        }
        tierCycles.set(tier, new Integer(temp));
        return storageDaemons.get(tierSizes.get(tier).intValue())
                .get(replicationLayers.get(tierSizes.get(tier).intValue()));
    }
}