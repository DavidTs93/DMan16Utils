package me.DMan16.DMan16Utils.Classes;

import me.DMan16.DMan16Utils.Interfaces.Copyable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SoftTrio<V,T,U> implements Copyable<SoftTrio<V,T,U>> {
	protected V first;
	protected T second;
	protected U third;
	
	public SoftTrio(V first,T second,U third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}
	
	public V first() {
		return this.first;
	}
	
	public T second() {
		return this.second;
	}
	
	public U third() {
		return this.third;
	}
	
	public SoftTrio<V,T,U> first(V val) {
		this.first = val;
		return this;
	}
	
	public SoftTrio<V,T,U> second(T val) {
		this.second = val;
		return this;
	}
	
	public SoftTrio<V,T,U> third(U val) {
		this.third = val;
		return this;
	}
	
	@NotNull
	@Contract(pure = true)
	public String toString() {
		return "(" + this.first + ", " + this.second + ", " + this.third + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SoftTrio<?,?,?> other)) return false;
		return other == this || (Objects.equals(this.first,other.first) && Objects.equals(this.second,other.second) && Objects.equals(this.third,other.third));
	}
	
	public int hashCode() {
		return com.google.common.base.Objects.hashCode(this.first,this.second,this.third);
	}
	
	@NotNull
	@Contract("_ -> new")
	public <F2> SoftTrio<F2,T,U> mapFirst(@NotNull Function<? super V,? extends F2> function) {
		return of(function.apply(this.first),this.second,this.third);
	}
	
	@NotNull
	@Contract("_ -> new")
	public <S2> SoftTrio<V,S2,U> mapSecond(@NotNull Function<? super T,? extends S2> function) {
		return of(this.first,function.apply(this.second),this.third);
	}
	
	@NotNull
	@Contract("_ -> new")
	public <T2> SoftTrio<V,T,T2> mapThird(@NotNull Function<? super U,? extends T2> function) {
		return of(this.first,this.second,function.apply(this.third));
	}
	
	@NotNull
	public SoftTrio<V,T,U> copy() {
		return SoftTrio.of(first,second,third);
	}
	
	@NotNull
	@Contract(pure = true)
	public <E> MapEntry<V,E> toEntry(@NotNull BiFunction<T,U,E> valueFunction) {
		return MapEntry.of(first,valueFunction.apply(second,third));
	}
	
	@Contract(value = "_,_,_ -> new", pure = true)
	public static <V,T,U> @NotNull SoftTrio<V,T,U> of(V first,T second,U third) {
		return new SoftTrio<>(first,second,third);
	}
}