package me.DMan16.DMan16Utils.Classes.Pairs.NumberPairs;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PairDouble extends PairNumber<Double> {
	public PairDouble(@NotNull Double first,@NotNull Double second) {
		super(first,second);
	}
	
	@NotNull
	@Contract(value = "_,_ -> new",pure = true)
	public PairDouble add(@NotNull Double first,@NotNull Double second) {
		return new PairDouble(first() + first,second() + second);
	}
	
	@NotNull
	@Contract(value = "_,_ -> new",pure = true)
	public PairDouble subtract(@NotNull Double first,@NotNull Double second) {
		return new PairDouble(first() - first,second() - second);
	}
	
	@NotNull
	@Contract(value = "_,_ -> new",pure = true)
	public static PairDouble of(@NotNull Double first,@NotNull Double second) {
		return new PairDouble(first,second);
	}
}