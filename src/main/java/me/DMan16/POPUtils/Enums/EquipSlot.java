package me.DMan16.POPUtils.Enums;

import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum EquipSlot {
	MAIN_HAND(EquipmentSlot.HAND,net.minecraft.world.entity.EquipmentSlot.MAINHAND),
	OFF_HAND(EquipmentSlot.OFF_HAND,net.minecraft.world.entity.EquipmentSlot.OFFHAND),
	HELMET(EquipmentSlot.HEAD,net.minecraft.world.entity.EquipmentSlot.HEAD),
	CHESTPLATE(EquipmentSlot.CHEST,net.minecraft.world.entity.EquipmentSlot.CHEST),
	LEGGINGS(EquipmentSlot.LEGS,net.minecraft.world.entity.EquipmentSlot.LEGS),
	BOOTS(EquipmentSlot.FEET,net.minecraft.world.entity.EquipmentSlot.FEET);
	
	public final @NotNull String key;
	public final @NotNull EquipmentSlot equipSlot;
	public final @NotNull net.minecraft.world.entity.EquipmentSlot enumSlot;
	
	EquipSlot(@NotNull EquipmentSlot slot, @NotNull net.minecraft.world.entity.EquipmentSlot enumSlot) {
		this.key = name().toLowerCase();
		this.equipSlot = slot;
		this.enumSlot = enumSlot;
	}
	
	@NotNull
	public static EquipSlot get(@NotNull EquipmentSlot slot) {
		return switch (slot) {
			case HEAD -> HELMET;
			case CHEST -> CHESTPLATE;
			case LEGS -> LEGGINGS;
			case FEET -> BOOTS;
			case HAND -> MAIN_HAND;
			case OFF_HAND -> OFF_HAND;
		};
	}
	
	@Nullable
	public static EquipSlot get(String name) {
		return name == null ? null : switch (name.toUpperCase()) {
			case "MAIN_HAND","HAND" -> MAIN_HAND;
			case "OFF_HAND" -> OFF_HAND;
			case "HELMET","HEAD" -> HELMET;
			case "CHESTPLATE","CHEST" -> CHESTPLATE;
			case "LEGGINGS","LEGS" -> LEGGINGS;
			case "BOOTS","FEET" -> BOOTS;
			default -> null;
		};
	}
}