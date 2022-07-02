package me.DMan16.DMan16Utils.NMSWrappers.Entities;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;

public sealed class HumanWrapper permits HumanWrapper.Safe {
	/**
	 * !null -> safe to cast to {@link net.minecraft.world.entity.player.Player}!</>
	 */
	protected final Object human;
	
	public HumanWrapper(@NotNull Object obj) {
		human = (obj instanceof net.minecraft.world.entity.player.Player) ? obj : null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.world.entity.player.Player}!</>
	 */
	@MonotonicNonNull
	public final Object human() {
		return human;
	}
	
	public final boolean isHuman() {
		return human != null;
	}
	
	public static final class Safe extends HumanWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isHuman()) throw new IllegalArgumentException();
		}
	}
}