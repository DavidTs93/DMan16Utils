package me.DMan16.POPUtils.NMSWrappers;

import org.jetbrains.annotations.NotNull;

public sealed class WorldWrapper permits WorldWrapper.Safe {
	/**
	 * !null -> safe to cast to {@link net.minecraft.server.level.ServerLevel}!</>
	 */
	protected final Object world;
	
	public WorldWrapper(@NotNull Object obj) {
		world = (obj instanceof net.minecraft.server.level.ServerLevel) ? obj : null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.server.level.ServerLevel}!</>
	 */
	public Object world() {
		return world;
	}
	
	public boolean isWorld() {
		return world != null;
	}
	
	public static final class Safe extends WorldWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isWorld()) throw new IllegalArgumentException();
		}
	}
}