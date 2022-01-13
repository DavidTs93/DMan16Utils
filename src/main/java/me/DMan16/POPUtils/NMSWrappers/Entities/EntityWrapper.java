package me.DMan16.POPUtils.NMSWrappers.Entities;

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
	public Object entity() {
		return entity;
	}
	
	public boolean isEntity() {
		return entity != null;
	}
	
	public static final class Safe extends EntityWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isEntity()) throw new IllegalArgumentException();
		}
	}
}