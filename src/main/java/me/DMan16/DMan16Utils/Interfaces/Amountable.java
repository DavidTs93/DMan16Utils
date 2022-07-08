package me.DMan16.DMan16Utils.Interfaces;

import me.DMan16.DMan16Utils.Utils.Utils;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Amountable<V extends Amountable<V>> extends Copyable<V> {
	@Positive int amount();
	
	@Positive int maxSize();
	
	default boolean isMaxSize() {
		return maxSize() <= amount();
	}
	
	@NotNull @Contract(value = "_ -> new",pure = true) V copy(@Positive int amount);
	
	@NotNull
	@Contract(value = " -> new",pure = true)
	default V copySingle() {
		return copy(1);
	}
	
	@NotNull
	@Contract(value = " -> new",pure = true)
	default V copyMaxStack() {
		return copy(maxSize());
	}
	
	@NotNull
	@Contract(value = "_ -> new",pure = true)
	default V copyIncrement(@NonNegative int amount) {
		if (amount == 0) return copy();
		long newAmount = ((long) amount()) + amount;
		return copy(newAmount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) newAmount);
	}
	
	/**
	 * New amount is minimum 1
	 * See also: {@link Amountable#copyDecrementOrNull(int)}
	 */
	@NotNull
	@Contract(value = "_ -> new",pure = true)
	default V copyDecrement(@NonNegative int amount) {
		V copy = copyDecrementOrNull(amount);
		return copy == null ? copy(1) : copy;
	}
	
	@Nullable
	@Contract(pure = true)
	default V copyDecrementOrNull(@NonNegative int amount) {
		if (amount == 0) return copy();
		long newAmount = Utils.clamp(((long) amount()) - amount,Integer.MIN_VALUE,Integer.MAX_VALUE);
		return newAmount <= 0 ? null : copy((int) newAmount);
	}
}