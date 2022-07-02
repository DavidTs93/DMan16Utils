package me.DMan16.DMan16Utils.Interfaces;

import org.checkerframework.checker.index.qual.Positive;

public interface ItemableAmountable<V extends Itemable<V> & Amountable<V>> extends Itemable<V>,Amountable<V> {
	@Override
	@Positive
	default int maxStackSize() {
		return material().getMaxStackSize();
	}
}