package me.DMan16.POPUtils.Interfaces;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Amountable<V extends Amountable<V>> extends Copyable<V> {
	
	@NotNull @Contract(pure = true) V copy(@Positive int amount);
	
	@NotNull
	@Contract(pure = true)
	default V copyMaxStackSize() {
		return copy(maxStackSize());
	}
	
	@NotNull
	@Contract(pure = true)
	default V copyIncrement(@NonNegative int amount) {
		if (amount == 0) return copy();
		long newAmount = ((long) amount()) + amount;
		return copy(newAmount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) newAmount);
	}
	
	@NotNull
	@Contract(pure = true)
	default V copyDecrement(@NonNegative int amount) {
		if (amount == 0) return copy();
		long newAmount = ((long) amount()) - amount;
		return copy(newAmount < 0 ? 1 : (int) newAmount);
	}
	
	@Positive int amount();
	
	int maxStackSize();
}