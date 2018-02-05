package tech.pod.dataset.faas;

import java.util.concurrent.ConcurrentHashMap;

public class InvokeableWrapper<T> {
    InvokeableFunction function;
    ConcurrentHashMap < String, Integer > keys = new ConcurrentHashMap < String, Integer > ();

    InvokeableWrapper(InvokeableFunction
        function, String startingKey) {
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
    public final T invoke(String key, T parameter, T... parameters) throws KeyNotAuthorizedException{
        if(keys.containsKey(key)){
            return function.invoke(parameter, parameters);
        }
        else{
            throw new KeyNotAuthorizedException(key);
        }
    }
}