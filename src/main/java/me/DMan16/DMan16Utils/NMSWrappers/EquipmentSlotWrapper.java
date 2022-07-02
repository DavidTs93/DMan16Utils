package me.DMan16.DMan16Utils.NMSWrappers;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;

public sealed class EquipmentSlotWrapper permits EquipmentSlotWrapper.Safe {
	/**
	 * !null -> safe to cast to {@link net.minecraft.world.entity.EquipmentSlot}!</>
	 */
	protected final Object slot;
	
	public EquipmentSlotWrapper(@NotNull Object obj) {
		slot = (obj instanceof net.minecraft.world.entity.EquipmentSlot) ? obj : null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.world.entity.EquipmentSlot}!</>
	 */
	@MonotonicNonNull
	public final Object slot() {
		return slot;
	}
	
	public final boolean isSlot() {
		return slot != null;
	}
	
	public static final class Safe extends EquipmentSlotWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isSlot()) throw new IllegalArgumentException();
		}
	}
}