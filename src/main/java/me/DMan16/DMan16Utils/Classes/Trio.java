package me.DMan16.DMan16Utils.Classes;

import me.DMan16.DMan16Utils.Interfaces.Copyable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Trio<V,T,U> implements Copyable<Trio<V,T,U>> {
	public final V first;
	public final T second;
	public final U third;
	
	public Trio(V first, T second, U third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}
	
	public final V first() {
		return this.first;
	}
	
	public final T second() {
		return this.second;
	}
	
	public final U third() {
		return this.third;
	}
	
	@NotNull
	@Contract(pure = true)
	public String toString() {
		return "(" + this.first + ", " + this.second + ", " + this.third + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Trio<?,?,?> other)) return false;
		return other == this || (Objects.equals(this.first,other.first) && Objects.equals(this.second,other.second) && Objects.equals(this.third,other.third));
	}
	
	public int hashCode() {
		return com.google.common.base.Objects.hashCode(this.first,this.second,this.third);
	}
	
	@NotNull
	@Contract("_ -> new")
	public <F2> Trio<F2,T,U> mapFirst(@NotNull Function<? super V,? extends F2> function) {
		return of(function.apply(this.first),this.second,this.third);
	}
	
	@NotNull
	@Contract("_ -> new")
	public <S2> Trio<V,S2,U> mapSecond(@NotNull Function<? super T,? extends S2> function) {
		return of(this.first,function.apply(this.second),this.third);
	}
	
	@NotNull
	@Contract("_ -> new")
	public <T2> Trio<V,T,T2> mapThird(@NotNull Function<? super U,? extends T2> function) {
		return of(this.first,this.second,function.apply(this.third));
	}
	
	@NotNull
	public Trio<V,T,U> copy() {
		return Trio.of(first,second,third);
	}
	
	@NotNull
	@Contract(pure = true)
	public <E> MapEntry<V,E> toEntry(@NotNull BiFunction<T,U,E> valueFunction) {
		return MapEntry.of(first,valueFunction.apply(second,third));
	}
	
	@Contract(value = "_,_,_ -> new", pure = true)
	public static <V,T,U> @NotNull Trio<V,T,U> of(V first, T second, U third) {
		return new Trio<>(first,second,third);
	}
}