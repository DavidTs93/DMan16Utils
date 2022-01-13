package me.DMan16.POPUtils.NMSWrappers.Entities;

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
	public Object mob() {
		return mob;
	}
	
	public boolean isMob() {
		return mob != null;
	}
	
	public static final class Safe extends MobWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isMob()) throw new IllegalArgumentException();
		}
	}
}