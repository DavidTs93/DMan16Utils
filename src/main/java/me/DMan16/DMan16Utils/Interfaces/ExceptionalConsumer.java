package me.DMan16.DMan16Utils.Interfaces;

import java.util.Objects;

@FunctionalInterface
public interface ExceptionalConsumer<V> {

    /**
     * Performs this operation on the given argument.
     *
     * @param v the input argument
     */
    void accept(V v) throws Exception;

    /**
     * Returns a composed {@code Consumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code Consumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default ExceptionalConsumer<V> andThen(ExceptionalConsumer<? super V> after) {
        Objects.requireNonNull(after);
        return (V v) -> { accept(v); after.accept(v); };
    }
}
