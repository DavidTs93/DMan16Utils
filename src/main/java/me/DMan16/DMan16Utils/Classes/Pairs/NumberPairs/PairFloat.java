package me.DMan16.DMan16Utils.Classes.Pairs.NumberPairs;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PairFloat extends PairNumber<Float> {
	public PairFloat(@NotNull Float first,@NotNull Float second) {
		super(first,second);
	}
	
	@NotNull
	@Contract(value = "_,_ -> new",pure = true)
	public PairFloat add(@NotNull Float first,@NotNull Float second) {
		return new PairFloat(first() + first,second() + second);
	}
	
	@NotNull
	@Contract(value = "_,_ -> new",pure = true)
	public PairFloat subtract(@NotNull Float first,@NotNull Float second) {
		return new PairFloat(first() - first,second() - second);
	}
	
	@NotNull
	@Contract(value = "_,_ -> new",pure = true)
	public static PairFloat of(@NotNull Float first,@NotNull Float second) {
		return new PairFloat(first,second);
	}
}