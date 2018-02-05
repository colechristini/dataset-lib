package tech.pod.dataset.faas;
@FunctionalInterface
public interface InvokeableFunction<Q>{
public abstract<T> Q invoke(T parameter, T... parameters);
}