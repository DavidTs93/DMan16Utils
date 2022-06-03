package me.DMan16.DMan16Utils.Enums;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public enum Tags {
	SWORDS(List.of("WOODEN_SWORD","STONE_SWORD","IRON_SWORD","DIAMOND_SWORD","GOLDEN_SWORD","NETHERITE_SWORD"),EquipmentSlot.HAND),
	TRIDENT(List.of("TRIDENT"),EquipmentSlot.HAND),
	AXES(List.of("WOODEN_AXE","STONE_AXE","IRON_AXE","DIAMOND_AXE","GOLDEN_AXE","NETHERITE_AXE"),EquipmentSlot.HAND),
	PICKAXES(List.of("WOODEN_PICKAXE","STONE_PICKAXE","IRON_PICKAXE","DIAMOND_PICKAXE","GOLDEN_PICKAXE","NETHERITE_PICKAXE"),EquipmentSlot.HAND),
	SHOVELS(List.of("WOODEN_SHOVEL","STONE_SHOVEL","IRON_SHOVEL","DIAMOND_SHOVEL","GOLDEN_SHOVEL","NETHERITE_SHOVEL"),EquipmentSlot.HAND),
	HOES(List.of("WOODEN_HOE","STONE_HOE","IRON_HOE","DIAMOND_HOE","GOLDEN_HOE","NETHERITE_HOE"),EquipmentSlot.HAND),
	CHESTPLATES(List.of("LEATHER_CHESTPLATE","CHAINMAIL_CHESTPLATE","IRON_CHESTPLATE","DIAMOND_CHESTPLATE","GOLDEN_CHESTPLATE","NETHERITE_CHESTPLATE"),EquipmentSlot.CHEST),
	LEGGINGS(List.of("LEATHER_LEGGINGS","CHAINMAIL_LEGGINGS","IRON_LEGGINGS","DIAMOND_LEGGINGS","GOLDEN_LEGGINGS","NETHERITE_LEGGINGS"),EquipmentSlot.LEGS),
	BOOTS(List.of("LEATHER_BOOTS","CHAINMAIL_BOOTS","IRON_BOOTS","DIAMOND_BOOTS","GOLDEN_BOOTS","NETHERITE_BOOTS"),EquipmentSlot.FEET),
	HELMETS(List.of("LEATHER_HELMET","CHAINMAIL_HELMET","IRON_HELMET","DIAMOND_HELMET","GOLDEN_HELMET","NETHERITE_HELMET","TURTLE_HELMET"),EquipmentSlot.HEAD),
	SHIELD(List.of("SHIELD"),EquipmentSlot.OFF_HAND),
	EXTRA_TOOLS(List.of("CARROT_ON_A_STICK","WARPED_FUNGUS_ON_A_STICK","SHEARS","FLINT_AND_STEEL","FISHING_ROD"),null),
	EXTRA_ARMORY(List.of("ELYTRA"),EquipmentSlot.CHEST),
	BOWS(List.of("BOW","CROSSBOW"),EquipmentSlot.HAND),
	WOODEN(List.of("WOODEN_SWORD","WOODEN_AXE","WOODEN_PICKAXE","WOODEN_SHOVEL","WOODEN_HOE"),null),
	LEATHER(List.of("LEATHER_CHESTPLATE","LEATHER_LEGGINGS","LEATHER_BOOTS","LEATHER_HELMET"),null),
	STONE(List.of("STONE_SWORD","STONE_AXE","STONE_PICKAXE","STONE_SHOVEL","STONE_HOE"),null),
	GOLD(List.of("GOLDEN_SWORD","GOLDEN_AXE","GOLDEN_PICKAXE","GOLDEN_SHOVEL","GOLDEN_HOE","GOLDEN_CHESTPLATE","GOLDEN_LEGGINGS","GOLDEN_BOOTS","GOLDEN_HELMET"),null),
	CHAINMAIL(List.of("CHAINMAIL_CHESTPLATE","CHAINMAIL_LEGGINGS","CHAINMAIL_BOOTS","CHAINMAIL_HELMET"),null),
	IRON(List.of("IRON_SWORD","IRON_AXE","IRON_PICKAXE","IRON_SHOVEL","IRON_HOE","IRON_CHESTPLATE","IRON_LEGGINGS","IRON_BOOTS","IRON_HELMET"),null),
	DIAMOND(List.of("DIAMOND_SWORD","DIAMOND_AXE","DIAMOND_PICKAXE","DIAMOND_SHOVEL","DIAMOND_HOE","DIAMOND_CHESTPLATE","DIAMOND_LEGGINGS","DIAMOND_BOOTS","DIAMOND_HELMET"),null),
	NETHERITE(List.of("NETHERITE_SWORD","NETHERITE_AXE","NETHERITE_PICKAXE","NETHERITE_SHOVEL","NETHERITE_HOE","NETHERITE_CHESTPLATE","NETHERITE_LEGGINGS","NETHERITE_BOOTS",
			"NETHERITE_HELMET"),null),
	RAW_ORES(List.of("RAW_COPPER","RAW_GOLD","RAW_IRON"),null),
	NON_BLOCK_DROPS(List.of("GOLD_NUGGET","RAW_COPPER","RAW_GOLD","RAW_IRON","COAL","NETHERITE_SCRAP","DIAMOND","EMERALD","REDSTONE","QUARTZ","AMETHYST_SHARD","LAPIS_BLOCK",
			"FLINT","GLOWSTONE_DUST","PRISMARINE_CRYSTALS","CLAY_BALL"),null);
	
	public final @NotNull @Unmodifiable List<Material> materials;
	public final @Nullable EquipmentSlot slot;
	
	Tags(@NotNull List<@NotNull String> names, @Nullable EquipmentSlot slot) {
		this.materials = names.stream().map(Material::getMaterial).filter(Objects::nonNull).toList();
		this.slot = slot;
	}
	
	@Contract("null -> false")
	public boolean contains(@Nullable Material material) {
		return material != null && materials.contains(material);
	}
	
	@Contract("null -> false")
	public boolean contains(@Nullable ItemStack item) {
		return item != null && contains(item.getType());
	}
	
	public static boolean isArmor(@NotNull Material material) {
		return BOOTS.materials.contains(material) || CHESTPLATES.materials.contains(material) || HELMETS.materials.contains(material) || LEGGINGS.materials.contains(material);
	}
	
	public static boolean isArmor(@NotNull ItemStack item) {
		return isArmor(item.getType());
	}
	
	public static boolean isArmorExtra(@NotNull Material material) {
		return EXTRA_ARMORY.materials.contains(material) || SHIELD.materials.contains(material);
	}
	
	public static boolean isArmorExtra(@NotNull ItemStack item) {
		return isArmorExtra(item.getType());
	}
	
	public static boolean isTool(@NotNull Material material, boolean includeAxe) {
		return (includeAxe && AXES.materials.contains(material)) || HOES.materials.contains(material) || PICKAXES.materials.contains(material) || SHOVELS.materials.contains(material);
	}
	
	/**
	 * Includes Axes
	 */
	public static boolean isTool(@NotNull Material material) {
		return isTool(material,true);
	}
	
	public static boolean isTool(@NotNull ItemStack item, boolean includeAxe) {
		return isTool(item.getType(),includeAxe);
	}
	
	/**
	 * Includes Axes
	 */
	public static boolean isTool(@NotNull ItemStack item) {
		return isTool(item.getType());
	}
	
	public static boolean isToolExtra(@NotNull Material material) {
		return EXTRA_TOOLS.contains(material);
	}
	
	public static boolean isToolExtra(@NotNull ItemStack item) {
		return isToolExtra(item.getType());
	}
	
	public static boolean isWeapon(@NotNull Material material, boolean includeAxe) {
		return SWORDS.materials.contains(material) || TRIDENT.materials.contains(material) || BOWS.materials.contains(material) || (includeAxe && AXES.materials.contains(material));
	}
	
	/**
	 * Includes Axes
	 */
	public static boolean isWeapon(@NotNull Material material) {
		return isWeapon(material,true);
	}
	
	public static boolean isWeapon(@NotNull ItemStack item, boolean includeAxe) {
		return isWeapon(item.getType(),includeAxe);
	}
	
	/**
	 * Includes Axes
	 */
	public static boolean isWeapon(@NotNull ItemStack item) {
		return isWeapon(item.getType());
	}
	
	public static boolean isExtra(@NotNull Material material) {
		return isToolExtra(material) || isArmorExtra(material);
	}
	
	public static boolean isExtra(@NotNull ItemStack item) {
		return isExtra(item.getType());
	}
	
	@NotNull
	public static List<Tags> get(@NotNull ItemStack item) {
		return get(item.getType());
	}
	
	@NotNull
	public static List<Tags> get(@NotNull Material material) {
		List<Tags> tags = new ArrayList<>();
		for (Tags tag : values()) if (tag.materials.contains(material)) tags.add(tag);
		return tags;
	}
	
	@NotNull
	public List<Material> getMaterials() {
		return materials;
	}
}