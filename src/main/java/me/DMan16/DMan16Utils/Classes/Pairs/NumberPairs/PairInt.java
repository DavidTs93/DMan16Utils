package me.DMan16.DMan16Utils.Classes.Pairs.NumberPairs;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PairInt extends PairNumber<Integer> {
	public PairInt(@NotNull Integer first,@NotNull Integer second) {
		super(first,second);
	}
	
	@NotNull
	@Contract(value = "_,_ -> new",pure = true)
	public PairInt add(@NotNull Integer first,@NotNull Integer second) {
		return new PairInt(first() + first,second() + second);
	}
	
	@NotNull
	@Contract(value = "_,_ -> new",pure = true)
	public PairInt subtract(@NotNull Integer first,@NotNull Integer second) {
		return new PairInt(first() - first,second() - second);
	}
	
	@NotNull
	@Contract(value = "_,_ -> new",pure = true)
	public static PairInt of(@NotNull Integer first,@NotNull Integer second) {
		return new PairInt(first,second);
	}
}