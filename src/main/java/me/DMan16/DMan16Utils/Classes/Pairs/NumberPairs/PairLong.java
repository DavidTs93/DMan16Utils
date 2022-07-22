package me.DMan16.DMan16Utils.Classes.Pairs.NumberPairs;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PairLong extends PairNumber<Long> {
	public PairLong(@NotNull Long first,@NotNull Long second) {
		super(first,second);
	}
	
	@NotNull
	@Contract(value = "_,_ -> new",pure = true)
	public PairLong add(@NotNull Long first,@NotNull Long second) {
		return new PairLong(first() + first,second() + second);
	}
	
	@NotNull
	@Contract(value = "_,_ -> new",pure = true)
	public PairLong subtract(@NotNull Long first,@NotNull Long second) {
		return new PairLong(first() - first,second() - second);
	}
	
	@NotNull
	@Contract(value = "_,_ -> new",pure = true)
	public static PairLong of(@NotNull Long first,@NotNull Long second) {
		return new PairLong(first,second);
	}
}