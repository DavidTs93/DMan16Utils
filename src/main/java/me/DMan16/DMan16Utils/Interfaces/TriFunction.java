package me.DMan16.DMan16Utils.Interfaces;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

/**
 * See {@link java.util.function.Function}
 */
@FunctionalInterface
public interface TriFunction<S,T,U,R> {
	R apply(S s,T t,U u);
	
	@NotNull
	@Contract(pure = true)
	default <V> TriFunction<S,T,U,V> andThen(@NotNull Function<? super R,? extends V> after) {
		Objects.requireNonNull(after);
		return (S s,T t,U u) -> after.apply(apply(s,t,u));
	}
}