package me.DMan16.DMan16Utils.NMSWrappers.Entities;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;

public sealed class EntityWrapper permits EntityWrapper.Safe {
	/**
	 * !null -> safe to cast to {@link net.minecraft.world.entity.Entity}!</>
	 */
	protected final Object entity;
	
	public EntityWrapper(@NotNull Object obj) {
		entity = (obj instanceof net.minecraft.world.entity.Entity) ? obj : null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.world.entity.Entity}!</>
	 */
	@MonotonicNonNull
	public final Object entity() {
		return entity;
	}
	
	public final boolean isEntity() {
		return entity != null;
	}
	
	public static final class Safe extends EntityWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isEntity()) throw new IllegalArgumentException();
		}
	}
}