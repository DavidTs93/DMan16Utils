package me.DMan16.DMan16Utils.Interfaces;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface QuadFunction<S,T,U,V,R> {
	
	/**
	 * Applies this function to the given arguments.
	 *
	 * @param s the first function argument
	 * @param t the second function argument
	 * @param u the third function argument
	 * @param v the fourth function argument
	 * @return the function result
	 */
	R apply(S s, T t, U u, V v);
	
	/**
	 * Returns a composed function that first applies this function to
	 * its input, and then applies the {@code after} function to the result.
	 * If evaluation of either function throws an exception, it is relayed to
	 * the caller of the composed function.
	 *
	 * @param <W> the type of output of the {@code after} function, and of the
	 *           composed function
	 * @param after the function to apply after this function is applied
	 * @return a composed function that first applies this function and then
	 * applies the {@code after} function
	 * @throws NullPointerException if after is null
	 */
	@NotNull
	@Contract(pure = true)
	default <W> QuadFunction<S,T,U,V,W> andThen(@NotNull Function<? super R,? extends W> after) {
		Objects.requireNonNull(after);
		return (S s, T t, U u, V v) -> after.apply(apply(s,t,u,v));
	}
}