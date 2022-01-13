package me.DMan16.POPUtils.NMSWrappers.Entities;

import org.jetbrains.annotations.NotNull;

public sealed class PlayerWrapper permits PlayerWrapper.Safe {
	/**
	 * !null -> safe to cast to {@link net.minecraft.server.level.ServerPlayer}!</>
	 */
	protected final Object player;
	
	public PlayerWrapper(@NotNull Object obj) {
		player = (obj instanceof net.minecraft.server.level.ServerPlayer) ? obj : null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.server.level.ServerPlayer}!</>
	 */
	public Object player() {
		return player;
	}
	
	public boolean isPlayer() {
		return player != null;
	}
	
	public static final class Safe extends PlayerWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isPlayer()) throw new IllegalArgumentException();
		}
	}
}