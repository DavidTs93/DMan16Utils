package me.DMan16.POPUtils.Classes;

import me.DMan16.POPUtils.Interfaces.Copyable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public class Trio<F,S,T> implements Copyable<Trio<F,S,T>> {
	private final F first;
	private final S second;
	private final T third;
	
	protected Trio(F first, S second, T third) {
		this.first = first;
		this.second = second;
		this.third = third;
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
	
	public String toString() {
		return "(" + this.first + ", " + this.second + ", " + this.third + ")";
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof Trio<?,?,?> other)) return false;
		return other == this || (Objects.equals(this.first,other.first) && Objects.equals(this.second,other.second) && Objects.equals(this.third,other.third));
	}
	
	public int hashCode() {
		return com.google.common.base.Objects.hashCode(this.first,this.second,this.third);
	}
	
	public <F2> Trio<F2,S,T> mapFirst(@NotNull Function<? super F,? extends F2> function) {
		return of(function.apply(this.first),this.second,this.third);
	}
	
	public <S2> Trio<F,S2,T> mapSecond(@NotNull Function<? super S,? extends S2> function) {
		return of(this.first,function.apply(this.second),this.third);
	}
	
	public <T2> Trio<F,S,T2> mapThird(@NotNull Function<? super T,? extends T2> function) {
		return of(this.first,this.second,function.apply(this.third));
	}
	
	@Contract(value = "_,_,_ -> new", pure = true)
	public static <F,S,T> @NotNull Trio<F,S,T> of(F first, S second, T third) {
		return new Trio<>(first,second,third);
	}
	
	@NotNull
	public Trio<F,S,T> copy() {
		return Trio.of(first,second,third);
	}
}