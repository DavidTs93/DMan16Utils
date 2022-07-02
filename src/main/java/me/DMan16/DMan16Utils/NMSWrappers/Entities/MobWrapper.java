package me.DMan16.DMan16Utils.NMSWrappers.Entities;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;

public sealed class MobWrapper permits MobWrapper.Safe {
	/**
	 * !null -> safe to cast to {@link net.minecraft.world.entity.Mob}!</>
	 */
	protected final Object mob;
	
	public MobWrapper(@NotNull Object obj) {
		mob = (obj instanceof net.minecraft.world.entity.Mob) ? obj : null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.world.entity.Mob}!</>
	 */
	@MonotonicNonNull
	public final Object mob() {
		return mob;
	}
	
	public final boolean isMob() {
		return mob != null;
	}
	
	public static final class Safe extends MobWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isMob()) throw new IllegalArgumentException();
		}
	}
}