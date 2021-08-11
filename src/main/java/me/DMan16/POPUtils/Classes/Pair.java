package me.DMan16.POPUtils.Classes;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

public class Pair<F,S> {
	private final F first;
	private final S second;
	
	protected Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}
	
	public F first() {
		return this.first;
	}
	
	public S second() {
		return this.second;
	}
	
	public Pair<S,F> swapped() {
		return of(this.second,this.first);
	}
	
	public String toString() {
		return "(" + this.first + ", " + this.second + ")";
	}
	
	public Pair<F,S> copy() {
		return Pair.of(first,second);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Pair<?,?> other)) return false;
		return Objects.equals(this.first,other.first) && Objects.equals(this.second,other.second);
	}
	
	public <F2> Pair<F2,S> mapFirst(Function<? super F,? extends F2> function) {
		return of(function.apply(this.first),this.second);
	}
	
	public <S2> Pair<F,S2> mapSecond(Function<? super S,? extends S2> function) {
		return of(this.first,function.apply(this.second));
	}
	
	public static <F,S> Pair<F,S> of(F first, S second) {
		return new Pair<>(first,second);
	}
	
	@SafeVarargs
	public static <F,S> Map<F,S> toMap(Pair<F,S> ... pairs) {
		Map<F,S> map = new HashMap<>();
		for (Pair<F,S> pair : pairs) map.put(pair.first,pair.second);
		return map;
	}
	
	public static <F,S> List<Pair<F,S>> fromMap(Map<F,S> map) {
		List<Pair<F,S>> list = new ArrayList<>();
		if (map != null) for (Entry<F,S> entry : map.entrySet()) list.add(of(entry.getKey(),entry.getValue()));
		return list;
	}
	
	public int hashCode() {
		return com.google.common.base.Objects.hashCode(this.first,this.second);
	}
}