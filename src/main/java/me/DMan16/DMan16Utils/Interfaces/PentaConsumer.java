package me.DMan16.DMan16Utils.Interfaces;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * See {@link java.util.function.Consumer}
 */
@FunctionalInterface
public interface PentaConsumer<A,B,C,D,E> {
    void accept(A a,B b,C c,D d,E e);
    
    default PentaConsumer<A,B,C,D,E> andThen(@NotNull PentaConsumer<? super A,? super B,? super C,? super D,? super E> after) {
        Objects.requireNonNull(after);
        return (a,b,c,d,e) -> {
            accept(a,b,c,d,e);
            after.accept(a,b,c,d,e);
        };
    }
}
