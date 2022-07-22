package me.DMan16.DMan16Utils.Classes.Pairs.NumberPairs;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public class PairBigInt extends PairNumber<BigInteger> {
	public PairBigInt(@NotNull BigInteger first,@NotNull BigInteger second) {
		super(first,second);
	}
	
	@NotNull
	@Contract(value = "_,_ -> new",pure = true)
	public PairBigInt add(@NotNull BigInteger first,@NotNull BigInteger second) {
		return new PairBigInt(first().add(first),second().add(second));
	}
	
	@NotNull
	@Contract(value = "_,_ -> new",pure = true)
	public PairBigInt subtract(@NotNull BigInteger first,@NotNull BigInteger second) {
		return new PairBigInt(first().subtract(first),second().subtract(second));
	}
	
	@NotNull
	@Contract(value = "_,_ -> new",pure = true)
	public static PairBigInt of(@NotNull BigInteger first,@NotNull BigInteger second) {
		return new PairBigInt(first,second);
	}
}