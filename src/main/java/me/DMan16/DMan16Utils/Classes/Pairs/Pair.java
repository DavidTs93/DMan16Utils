package me.DMan16.DMan16Utils.Classes.Pairs;

import me.DMan16.DMan16Utils.Classes.MapEntry;
import me.DMan16.DMan16Utils.Interfaces.Copyable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

public class Pair<V,T> implements Copyable<Pair<V,T>> {
	public final V first;
	public final T second;
	
	public Pair(V first,T second) {
		this.first = first;
		this.second = second;
	}
	
	public final V first() {
		return this.first;
	}
	
	public final T second() {
		return this.second;
	}
	
	@NotNull
	@Contract(value = " -> new",pure = true)
	public Pair<T,V> swapped() {
		return of(this.second,this.first);
	}
	
	@NotNull
	@Contract(pure = true)
	public String toString() {
		return "(" + this.first + "," + this.second + ")";
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
	public <V2> Pair<V2,T> mapFirst(@NotNull Function<? super V,? extends V2> function) {
		return of(function.apply(this.first),this.second);
	}
	
	@NotNull
	@Contract("_ -> new")
	public <T2> Pair<V,T2> mapSecond(@NotNull Function<? super T,? extends T2> function) {
		return of(this.first,function.apply(this.second));
	}
	
	@NotNull
	@Contract(pure = true)
	public Pair<V,T> copy() {
		return Pair.of(first,second);
	}
	
	@NotNull
	@Contract(pure = true)
	public MapEntry<V,T> toEntry() {
		return MapEntry.of(first,second);
	}
	
	@Contract(value = "_,_ -> new",pure = true)
	public static <V,T> @NotNull Pair<V,T> of(V first,T second) {
		return new Pair<>(first,second);
	}
	
	@NotNull
	@SafeVarargs
	public static <V,T> HashMap<V,T> toHashMap(Pair<V,T> @NotNull ... pairs) {
		HashMap<V,T> map = new HashMap<>();
		for (Pair<V,T> pair : pairs) if (pair != null && pair.first != null) map.put(pair.first,pair.second);
		return map;
	}
	
	@Nullable
	@Contract("null -> null; !null -> !null")
	public static <V,T> List<Pair<V,T>> fromMap(Map<V,T> map) {
		if (map == null) return null;
		List<Pair<V,T>> list = new ArrayList<>();
		for (Entry<V,T> entry : map.entrySet()) list.add(of(entry.getKey(),entry.getValue()));
		return list;
	}
}