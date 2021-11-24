package me.DMan16.POPUtils.Interfaces;

import org.jetbrains.annotations.NotNull;

public interface Amountable<V> extends Copyable<V> {
	
	@NotNull V copy(int amount);
	
	int amount();
	
	int maxStackSize();
}