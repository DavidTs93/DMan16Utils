package me.DMan16.DMan16Utils.Interfaces;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

/**
 * See {@link java.util.function.Function}
 */
@FunctionalInterface
public interface QuadFunction<S,T,U,V,R> {
	R apply(S s,T t,U u,V v);
	
	@NotNull
	@Contract(pure = true)
	default <W> QuadFunction<S,T,U,V,W> andThen(@NotNull Function<? super R,? extends W> after) {
		Objects.requireNonNull(after);
		return (S s,T t,U u,V v) -> after.apply(apply(s,t,u,v));
	}
}