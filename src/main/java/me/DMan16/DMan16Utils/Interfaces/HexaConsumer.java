package me.DMan16.DMan16Utils.Interfaces;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * See {@link java.util.function.Consumer}
 */
@FunctionalInterface
public interface HexaConsumer<A,B,C,D,E,F> {
    void accept(A a,B b,C c,D d,E e,F f);
    
    default HexaConsumer<A,B,C,D,E,F> andThen(@NotNull HexaConsumer<? super A,? super B,? super C,? super D,? super E,? super F> after) {
        Objects.requireNonNull(after);
        return (a,b,c,d,e,f) -> {
            accept(a,b,c,d,e,f);
            after.accept(a,b,c,d,e,f);
        };
    }
}
