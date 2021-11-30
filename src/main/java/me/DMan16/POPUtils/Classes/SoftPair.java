package me.DMan16.POPUtils.Classes;

import me.DMan16.POPUtils.Interfaces.Copyable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

public class SoftPair<F,S> implements Copyable<SoftPair<F,S>> {
	private F first;
	private S second;
	
	protected SoftPair(F first, S second) {
		this.first = first;
		this.second = second;
	}
	
	public F first() {
		return this.first;
	}
	
	public S second() {
		return this.second;
	}
	
	@NotNull
	public SoftPair<F,S> first(F val) {
		this.first = val;
		return this;
	}
	
	@NotNull
	public SoftPair<F,S> second(S val) {
		this.second = val;
		return this;
	}
	
	@NotNull
	@Contract(value = " -> new", pure = true)
	public SoftPair<S,F> swapped() {
		return of(this.second,this.first);
	}
	
	@NotNull
	@Contract(pure = true)
	public String toString() {
		return "(" + this.first + ", " + this.second + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SoftPair<?,?> other)) return false;
		return other == this || (Objects.equals(this.first,other.first) && Objects.equals(this.second,other.second));
	}
	
	public int hashCode() {
		return com.google.common.base.Objects.hashCode(this.first,this.second);
	}
	
	@NotNull
	@Contract("_ -> new")
	public <F2> SoftPair<F2,S> mapFirst(@NotNull Function<? super F,? extends F2> function) {
		return of(function.apply(this.first),this.second);
	}
	
	@NotNull
	@Contract("_ -> new")
	public <S2> SoftPair<F,S2> mapSecond(@NotNull Function<? super S,? extends S2> function) {
		return of(this.first,function.apply(this.second));
	}
	
	@NotNull
	public SoftPair<F,S> copy() {
		return SoftPair.of(first,second);
	}
	
	@Contract(value = "_,_ -> new", pure = true)
	public static <F,S> @NotNull SoftPair<F,S> of(F first, S second) {
		return new SoftPair<>(first,second);
	}
	
	@NotNull
	@SafeVarargs
	public static <F,S> Map<F,S> toMap(SoftPair<F,S>... pairs) {
		Map<F,S> map = new HashMap<>();
		for (SoftPair<F,S> pair : pairs) if (pair != null) map.put(pair.first,pair.second);
		return map;
	}
	
	@Nullable
	@Contract("null -> null; !null -> !null")
	public static <F,S> List<SoftPair<F,S>> fromMap(Map<F,S> map) {
		if (map == null) return null;
		List<SoftPair<F,S>> list = new ArrayList<>();
		for (Entry<F,S> entry : map.entrySet()) list.add(of(entry.getKey(),entry.getValue()));
		return list;
	}
}