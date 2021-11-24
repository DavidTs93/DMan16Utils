package me.DMan16.POPUtils.Interfaces;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Represents an operation that accepts two input arguments and returns no
 * result.  This is the two-arity specialization of {@link Consumer}.
 * Unlike most other functional interfaces, {@code BiConsumer} is expected
 * to operate via side-effects.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #accept(Object,Object,Object,Object,Object,Object)}.
 *
 * @param <A> the type of the first argument to the operation
 * @param <B> the type of the second argument to the operation
 * @param <C> the type of the third argument to the operation
 * @param <D> the type of the fourth argument to the operation
 * @param <E> the type of the fifth argument to the operation
 * @param <F> the type of the sixth argument to the operation
 *
 * @see Consumer
 * @since 1.8
 */
@FunctionalInterface
public interface HexaConsumer<A,B,C,D,E,F> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param a the first input argument
     * @param b the second input argument
     * @param c the third input argument
     * @param d the fourth input argument
     * @param e the fifth input argument
     * @param f the sixth input argument
     */
    void accept(A a, B b, C c, D d, E e, F f);

    /**
     * Returns a composed {@code BiConsumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code BiConsumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default HexaConsumer<A,B,C,D,E,F> andThen(@NotNull HexaConsumer<? super A, ? super B, ? super C, ? super D, ? super E, ? super F> after) {
        Objects.requireNonNull(after);
        return (a,b,c,d,e,f) -> {
            accept(a,b,c,d,e,f);
            after.accept(a,b,c,d,e,f);
        };
    }
}
