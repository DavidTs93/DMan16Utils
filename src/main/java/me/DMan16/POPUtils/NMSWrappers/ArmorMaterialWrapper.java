package me.DMan16.POPUtils.NMSWrappers;

import org.jetbrains.annotations.NotNull;

public sealed class ArmorMaterialWrapper permits ArmorMaterialWrapper.Safe {
	/**
	 * !null -> safe to cast to {@link net.minecraft.world.item.ArmorMaterial}!</>
	 */
	protected final Object item;
	
	public ArmorMaterialWrapper(@NotNull Object obj) {
		item = (obj instanceof net.minecraft.world.item.ArmorMaterial) ? obj : null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.world.item.ArmorMaterial}!</>
	 */
	public Object armorMaterial() {
		return item;
	}
	
	public boolean isArmorMaterial() {
		return item != null;
	}
	
	public static final class Safe extends ArmorMaterialWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isArmorMaterial()) throw new IllegalArgumentException();
		}
	}
}