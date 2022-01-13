package me.DMan16.POPUtils.NMSWrappers.Entities;

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
	public Object human() {
		return human;
	}
	
	public boolean isHuman() {
		return human != null;
	}
	
	public static final class Safe extends HumanWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isHuman()) throw new IllegalArgumentException();
		}
	}
}