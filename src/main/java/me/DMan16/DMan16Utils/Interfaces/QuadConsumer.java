package me.DMan16.DMan16Utils.Interfaces;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * See {@link java.util.function.Consumer}
 */
@FunctionalInterface
public interface QuadConsumer<A,B,C,D> {
    void accept(A a,B b,C c,D d);
    
    default QuadConsumer<A,B,C,D> andThen(@NotNull QuadConsumer<? super A,? super B,? super C,? super D> after) {
        Objects.requireNonNull(after);
        return (a,b,c,d) -> {
            accept(a,b,c,d);
            after.accept(a,b,c,d);
        };
    }
}
