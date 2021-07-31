package me.DMan16.POPUtils.Classes;

import java.util.Objects;
import java.util.function.Function;

public class Trio<F,S,T> {
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
		return Objects.equals(this.first,other.first) && Objects.equals(this.second,other.second) && Objects.equals(this.third,other.third);
	}
	
	public int hashCode() {
		return com.google.common.base.Objects.hashCode(this.first,this.second,this.third);
	}
	
	public <F2> Trio<F2,S,T> mapFirst(Function<? super F,? extends F2> function) {
		return of(function.apply(this.first),this.second,this.third);
	}
	
	public <S2> Trio<F,S2,T> mapSecond(Function<? super S,? extends S2> function) {
		return of(this.first,function.apply(this.second),this.third);
	}
	
	public <T2> Trio<F,S,T2> mapThird(Function<? super T,? extends T2> function) {
		return of(this.first,this.second,function.apply(this.third));
	}
	
	public static <F,S,T> Trio<F,S,T> of(F first, S second, T third) {
		return new Trio<F,S,T>(first,second,third);
	}
}