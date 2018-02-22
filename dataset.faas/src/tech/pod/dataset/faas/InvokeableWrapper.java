package tech.pod.dataset.faas;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class InvokeableWrapper {
    InvokeableFunction function;
    ConcurrentHashMap < String, Integer > keys = new ConcurrentHashMap < String, Integer > ();
    List<String> keyList=new ArrayList<String>();
    InvokeableWrapper(InvokeableFunction function, String startingKey) {
        this.function = function;
        keys.put(startingKey, 0);
        keyList.add(startingKey);
    }
    public void putKey(String key) {
        keys.put(key, 0);
        keyList.add(key);
    }
    public void removeKey(String key){
        keys.remove(key);
        keyList.remove(key);
    }
    public Integer getKeyValue(String key){
        return keys.get(key);
    }
    public Integer getKeyLocation(String key){
        for(int i=0;i<keyList.size();i++){
            if(key==keyList.get(i)){
                Integer r=i;
                return r;
            }
        }
        return null;
    }
    @SafeVarargs
    public final <T> List<? extends Object> invoke(String key, T parameter, T... parameters) throws KeyNotAuthorizedException{
        if(keys.containsKey(key)){
            keys.replace(key, keys.get(key)+1);
            return function.invoke(parameter, parameters);

        }
        else{
            throw new KeyNotAuthorizedException(key);
        }
    }
}