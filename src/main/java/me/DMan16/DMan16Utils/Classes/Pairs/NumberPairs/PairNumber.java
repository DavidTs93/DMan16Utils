package me.DMan16.DMan16Utils.Classes.Pairs.NumberPairs;

import me.DMan16.DMan16Utils.Classes.Pairs.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class PairNumber<V extends Number> extends Pair<V,V> {
	protected PairNumber(@NotNull V first,@NotNull V second) {
		super(first,second);
	}
	
	@NotNull @Contract(value = "_,_ -> new",pure = true) public abstract PairNumber<V> add(@NotNull V first,@NotNull V second);
	@NotNull @Contract(value = "_,_ -> new",pure = true) public abstract PairNumber<V> subtract(@NotNull V first,@NotNull V second);
	
	@NotNull
	@Contract(value = "_ -> new",pure = true)
	public final PairNumber<V> add(@NotNull Pair<V,V> pair) {
		return add(pair.first(),pair.second());
	}
	
	@NotNull
	@Contract(value = "_ -> new",pure = true)
	public final PairNumber<V> subtract(@NotNull Pair<V,V> pair) {
		return subtract(pair.first(),pair.second());
	}
}