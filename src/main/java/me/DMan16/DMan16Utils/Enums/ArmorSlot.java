package me.DMan16.DMan16Utils.Enums;

import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ArmorSlot {
	HELMET(EquipSlot.HELMET),
	CHESTPLATE(EquipSlot.CHESTPLATE),
	LEGGINGS(EquipSlot.LEGGINGS),
	BOOTS(EquipSlot.BOOTS);
	
	public final @NotNull EquipSlot equipSlot;
	
	ArmorSlot(@NotNull EquipSlot slot) {
		this.equipSlot = slot;
	}
	
	@Nullable
	public static ArmorSlot get(@NotNull EquipmentSlot slot) {
		return switch (slot) {
			case HEAD -> HELMET;
			case CHEST -> CHESTPLATE;
			case LEGS -> LEGGINGS;
			case FEET -> BOOTS;
			default -> null;
		};
	}
	
	@Nullable
	public static ArmorSlot get(String name) {
		EquipSlot slot = EquipSlot.get(name);
		return slot == null ? null : switch (slot) {
			case HELMET -> HELMET;
			case CHESTPLATE -> CHESTPLATE;
			case LEGGINGS -> LEGGINGS;
			case BOOTS -> BOOTS;
			default -> null;
		};
	}
}