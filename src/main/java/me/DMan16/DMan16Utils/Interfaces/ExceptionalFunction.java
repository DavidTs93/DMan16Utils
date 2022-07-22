package me.DMan16.DMan16Utils.Interfaces;

import java.util.Objects;

/**
 * See {@link java.util.function.Function}
 */
@FunctionalInterface
public interface ExceptionalFunction<V,T> {
    
    T apply(V v) throws Exception;
    
    default <U> ExceptionalFunction<U,T> compose(ExceptionalFunction<? super U,? extends V> before) {
        Objects.requireNonNull(before);
        return (U u) -> apply(before.apply(u));
    }
    
    default <U> ExceptionalFunction<V,U> andThen(ExceptionalFunction<? super T,? extends U> after) {
        Objects.requireNonNull(after);
        return (V v) -> after.apply(apply(v));
    }
    
    static <V> ExceptionalFunction<V,V> identity() {
        return v -> v;
    }
}
