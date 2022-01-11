package me.DMan16.POPUtils.Enums;

import net.minecraft.world.entity.EnumItemSlot;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ArmorSlot {
	HELMET(EquipSlot.HELMET),
	CHESTPLATE(EquipSlot.CHESTPLATE),
	LEGGINGS(EquipSlot.LEGGINGS),
	BOOTS(EquipSlot.BOOTS);
	
	public final @NotNull String key;
	public final @NotNull EquipmentSlot equipSlot;
	public final @NotNull EnumItemSlot enumSlot;
	
	ArmorSlot(@NotNull EquipSlot slot) {
		this.key = slot.key;
		this.equipSlot = slot.equipSlot;
		this.enumSlot = slot.enumSlot;
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