package tech.pod.dataset.storageprovider;

import java.util.ArrayList;

public interface StoragePool{
public String get( int stripe);
public void addStripe(String[] stripeDisks, int tier);
public void addStripe(String[] stripeDisks);
public void addRepLayer(String[] stripeDisks);
public void remove(int stripe);
public ArrayList<String> getAllDaemons();
}