package tech.pod.dataset.faas;

import java.util.concurrent.ConcurrentHashMap;

public class InvokeableWrapper<R> {
    InvokeableFunction function;
    ConcurrentHashMap < String, Integer > keys = new ConcurrentHashMap < String, Integer > ();
    String type;
    InvokeableWrapper(InvokeableFunction function, String startingKey,String type) {
        this.type=type;
        this.function = function;
        keys.put(startingKey, 0);
    }
    public void putKey(String key) {
        keys.put(key, 0);
    }
    public void removeKey(String key){
        keys.remove(key);
    }
    public Integer getKeyValue(String key){
        return keys.get(key);
    }
    @SafeVarargs
    public final <T> List<R> invoke(String key, T parameter, T... parameters) throws KeyNotAuthorizedException{
        if(keys.containsKey(key)){
            keys.replace(key, keys.get(key)+1);
            return function.invoke(parameter, parameters);

        }
        else{
            throw new KeyNotAuthorizedException(key);
        }
    }
}