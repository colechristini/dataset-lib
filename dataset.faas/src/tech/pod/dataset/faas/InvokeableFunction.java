package tech.pod.dataset.faas;
@FunctionalInterface
public interface InvokeableFunction<Q>{
public abstract<T> List<Q> invoke(T parameter, T... parameters);
}  