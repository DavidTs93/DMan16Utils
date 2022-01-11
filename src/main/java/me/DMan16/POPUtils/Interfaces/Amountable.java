package me.DMan16.POPUtils.Interfaces;

import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Amountable<V extends Amountable<V>> extends Copyable<V> {
	
	@NotNull @Contract(pure = true) V copy(@Positive int amount);
	
	@Positive int amount();
	
	int maxStackSize();
}