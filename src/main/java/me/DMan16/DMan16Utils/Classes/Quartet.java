package me.DMan16.DMan16Utils.Classes;

import me.DMan16.DMan16Utils.Interfaces.Copyable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public class Quartet<F,S,T,V> implements Copyable<Quartet<F,S,T,V>> {
	public final F first;
	public final S second;
	public final T third;
	public final V fourth;
	
	protected Quartet(F first, S second, T third, V fourth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
	}
	
	public F first() {
		return this.first;
	}
	
	public S second() {
		return this.second;
	}
	
	public T third() {
		return this.third;
	}
	
	public V fourth() {
		return this.fourth;
	}
	
	@NotNull
	@Contract(pure = true)
	public String toString() {
		return "(" + this.first + ", " + this.second + ", " + this.third + ", " + this.fourth + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Quartet<?,?,?,?> other)) return false;
		return other == this || (Objects.equals(this.first,other.first) && Objects.equals(this.second,other.second) && Objects.equals(this.third,other.third) &&
				Objects.equals(this.fourth,other.fourth));
	}
	
	public int hashCode() {
		return com.google.common.base.Objects.hashCode(this.first,this.second,this.third);
	}
	
	@NotNull
	@Contract("_ -> new")
	public <F2> Quartet<F2,S,T,V> mapFirst(@NotNull Function<? super F,? extends F2> function) {
		return of(function.apply(this.first),this.second,this.third,fourth);
	}
	
	@NotNull
	@Contract("_ -> new")
	public <S2> Quartet<F,S2,T,V> mapSecond(@NotNull Function<? super S,? extends S2> function) {
		return of(this.first,function.apply(this.second),this.third,this.fourth);
	}
	
	@NotNull
	@Contract("_ -> new")
	public <T2> Quartet<F,S,T2,V> mapThird(@NotNull Function<? super T,? extends T2> function) {
		return of(this.first,this.second,function.apply(this.third),fourth);
	}
	
	@NotNull
	@Contract("_ -> new")
	public <V2> Quartet<F,S,T,V2> mapFourth(@NotNull Function<? super V,? extends V2> function) {
		return of(this.first,this.second,this.third,function.apply(this.fourth));
	}
	
	@NotNull
	public Quartet<F,S,T,V> copy() {
		return Quartet.of(first,second,third,fourth);
	}
	
	@Contract(value = "_,_,_,_ -> new", pure = true)
	public static <F,S,T,V> @NotNull Quartet<F,S,T,V> of(F first, S second, T third, V fourth) {
		return new Quartet<>(first,second,third,fourth);
	}
}