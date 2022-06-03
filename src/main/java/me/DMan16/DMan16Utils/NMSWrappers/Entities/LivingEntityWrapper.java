package me.DMan16.DMan16Utils.NMSWrappers.Entities;

import org.jetbrains.annotations.NotNull;

public sealed class LivingEntityWrapper permits LivingEntityWrapper.Safe {
	/**
	 * !null -> safe to cast to {@link net.minecraft.world.entity.LivingEntity}!</>
	 */
	protected final Object entity;
	
	public LivingEntityWrapper(@NotNull Object obj) {
		entity = (obj instanceof net.minecraft.world.entity.LivingEntity) ? obj : null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.world.entity.LivingEntity}!</>
	 */
	public Object livingEntity() {
		return entity;
	}
	
	public boolean isLivingEntity() {
		return entity != null;
	}
	
	public static final class Safe extends LivingEntityWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isLivingEntity()) throw new IllegalArgumentException();
		}
	}
}