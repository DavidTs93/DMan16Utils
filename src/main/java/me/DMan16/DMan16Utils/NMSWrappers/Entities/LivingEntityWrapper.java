package me.DMan16.DMan16Utils.NMSWrappers.Entities;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
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
	@MonotonicNonNull
	public final Object livingEntity() {
		return entity;
	}
	
	public final boolean isLivingEntity() {
		return entity != null;
	}
	
	public static final class Safe extends LivingEntityWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isLivingEntity()) throw new IllegalArgumentException();
		}
	}
}