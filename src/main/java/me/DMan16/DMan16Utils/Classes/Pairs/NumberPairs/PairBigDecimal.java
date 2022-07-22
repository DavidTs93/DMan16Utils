package me.DMan16.DMan16Utils.Classes.Pairs.NumberPairs;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class PairBigDecimal extends PairNumber<BigDecimal> {
	public PairBigDecimal(@NotNull BigDecimal first,@NotNull BigDecimal second) {
		super(first,second);
	}
	
	@NotNull
	@Contract(value = "_,_ -> new",pure = true)
	public PairBigDecimal add(@NotNull BigDecimal first,@NotNull BigDecimal second) {
		return new PairBigDecimal(first().add(first),second().add(second));
	}
	
	@NotNull
	@Contract(value = "_,_ -> new",pure = true)
	public PairBigDecimal subtract(@NotNull BigDecimal first,@NotNull BigDecimal second) {
		return new PairBigDecimal(first().subtract(first),second().subtract(second));
	}
	
	@NotNull
	@Contract(value = "_,_ -> new",pure = true)
	public static PairBigDecimal of(@NotNull BigDecimal first,@NotNull BigDecimal second) {
		return new PairBigDecimal(first,second);
	}
}