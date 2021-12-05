package me.DMan16.POPUtils.Interfaces;

import org.jetbrains.annotations.NotNull;

public interface Copyable<V extends Copyable<V>> {
	@NotNull V copy();
}