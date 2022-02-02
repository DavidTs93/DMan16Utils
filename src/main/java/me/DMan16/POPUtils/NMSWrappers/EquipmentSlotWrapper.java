package me.DMan16.POPUtils.NMSWrappers;

import org.jetbrains.annotations.NotNull;

public sealed class EquipmentSlotWrapper permits EquipmentSlotWrapper.Safe {
	/**
	 * !null -> safe to cast to {@link net.minecraft.world.entity.EquipmentSlot}!</>
	 */
	protected final Object component;
	
	public EquipmentSlotWrapper(@NotNull Object obj) {
		component = (obj instanceof net.minecraft.world.entity.EquipmentSlot) ? obj : null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.world.entity.EquipmentSlot}!</>
	 */
	public Object slot() {
		return component;
	}
	
	public boolean isSlot() {
		return component != null;
	}
	
	public static final class Safe extends EquipmentSlotWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isSlot()) throw new IllegalArgumentException();
		}
	}
}