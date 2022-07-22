package me.DMan16.DMan16Utils.Interfaces;

import java.util.Objects;

/**
 * See {@link java.util.function.Consumer}
 */
@FunctionalInterface
public interface ExceptionalConsumer<V> {
    void accept(V v) throws Exception;
    
    default ExceptionalConsumer<V> andThen(ExceptionalConsumer<? super V> after) {
        Objects.requireNonNull(after);
        return (V v) -> { accept(v); after.accept(v); };
    }
}
