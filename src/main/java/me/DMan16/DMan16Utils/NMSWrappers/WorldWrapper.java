package me.DMan16.DMan16Utils.NMSWrappers;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
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
	@MonotonicNonNull
	public final Object world() {
		return world;
	}
	
	public final boolean isWorld() {
		return world != null;
	}
	
	public static final class Safe extends WorldWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isWorld()) throw new IllegalArgumentException();
		}
	}
}