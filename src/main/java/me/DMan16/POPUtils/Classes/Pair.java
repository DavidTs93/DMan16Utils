package me.DMan16.POPUtils.Classes;

import me.DMan16.POPUtils.Interfaces.Copyable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

public class Pair<F,S> implements Copyable<Pair<F,S>> {
	public final F first;
	public final S second;
	
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
	
	@NotNull
	@Contract(value = " -> new", pure = true)
	public Pair<S,F> swapped() {
		return of(this.second,this.first);
	}
	
	@NotNull
	@Contract(pure = true)
	public String toString() {
		return "(" + this.first + ", " + this.second + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Pair<?,?> other)) return false;
		return other == this || (Objects.equals(this.first,other.first) && Objects.equals(this.second,other.second));
	}
	
	public int hashCode() {
		return com.google.common.base.Objects.hashCode(this.first,this.second);
	}
	
	@NotNull
	@Contract("_ -> new")
	public <F2> Pair<F2,S> mapFirst(@NotNull Function<? super F,? extends F2> function) {
		return of(function.apply(this.first),this.second);
	}
	
	@NotNull
	@Contract("_ -> new")
	public <S2> Pair<F,S2> mapSecond(@NotNull Function<? super S,? extends S2> function) {
		return of(this.first,function.apply(this.second));
	}
	
	@NotNull
	public Pair<F,S> copy() {
		return Pair.of(first,second);
	}
	
	@Contract(value = "_,_ -> new", pure = true)
	public static <F,S> @NotNull Pair<F,S> of(F first, S second) {
		return new Pair<>(first,second);
	}
	
	@NotNull
	@SafeVarargs
	public static <F,S> Map<F,S> toMap(Pair<F,S> ... pairs) {
		Map<F,S> map = new HashMap<>();
		for (Pair<F,S> pair : pairs) if (pair != null) map.put(pair.first,pair.second);
		return map;
	}
	
	@Nullable
	@Contract("null -> null; !null -> !null")
	public static <F,S> List<Pair<F,S>> fromMap(Map<F,S> map) {
		if (map == null) return null;
		List<Pair<F,S>> list = new ArrayList<>();
		for (Entry<F,S> entry : map.entrySet()) list.add(of(entry.getKey(),entry.getValue()));
		return list;
	}
}