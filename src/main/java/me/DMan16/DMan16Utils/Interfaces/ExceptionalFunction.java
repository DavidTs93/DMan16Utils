package me.DMan16.DMan16Utils.Interfaces;

import java.util.Objects;

@FunctionalInterface
public interface ExceptionalFunction<V, T> {

    /**
     * Applies this function to the given argument.
     *
     * @param v the function argument
     * @return the function result
     */
    T apply(V v) throws Exception;

    /**
     * Returns a composed function that first applies the {@code before}
     * function to its input, and then applies this function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param <U> the type of input to the {@code before} function, and to the
     *           composed function
     * @param before the function to apply before this function is applied
     * @return a composed function that first applies the {@code before}
     * function and then applies this function
     * @throws NullPointerException if before is null
     *
     * @see #andThen(ExceptionalFunction)
     */
    default <U> ExceptionalFunction<U, T> compose(ExceptionalFunction<? super U, ? extends V> before) {
        Objects.requireNonNull(before);
        return (U u) -> apply(before.apply(u));
    }

    /**
     * Returns a composed function that first applies this function to
     * its input, and then applies the {@code after} function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param <U> the type of output of the {@code after} function, and of the
     *           composed function
     * @param after the function to apply after this function is applied
     * @return a composed function that first applies this function and then
     * applies the {@code after} function
     * @throws NullPointerException if after is null
     *
     * @see #compose(ExceptionalFunction)
     */
    default <U> ExceptionalFunction<V, U> andThen(ExceptionalFunction<? super T, ? extends U> after) {
        Objects.requireNonNull(after);
        return (V v) -> after.apply(apply(v));
    }

    /**
     * Returns a function that always returns its input argument.
     *
     * @param <V> the type of the input and output objects to the function
     * @return a function that always returns its input argument
     */
    static <V> ExceptionalFunction<V, V> identity() {
        return v -> v;
    }
}
