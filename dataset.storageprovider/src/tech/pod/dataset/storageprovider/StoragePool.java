package tech.pod.dataset.storageprovider;

import java.util.ArrayList;
//Basic StoragePool interface
public interface StoragePool{
//public String getStripe( int stripe);//Not sure what this does
public void addStripe(String[] stripeDaemons, int tier);//adds a heterogenous pool stripe
public void addStripe(String[] stripeDaemons);//adds a homogenous pool stripe
public void addRepLayer(String[] stripeDaemons);//ads a single layer of daemons to every stripe
public void remove(int stripe);//deletes a stripe
public ArrayList<String> getAllDaemons();//retrieves all daemons on the current increment level of every stripe
}