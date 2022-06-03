package me.DMan16.DMan16Utils.Interfaces;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Copyable<V extends Copyable<V>> {
	@NotNull @Contract(pure = true) V copy();
}