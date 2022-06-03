package me.DMan16.DMan16Utils.Classes;

import io.papermc.paper.enchantments.EnchantmentRarity;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;

public abstract class CustomEnchantment extends Enchantment implements Listener {
	private final @NotNull Set<NamespacedKey> conflicts;
	
	protected CustomEnchantment(@NotNull String key, @Nullable Collection<@NotNull NamespacedKey> conflicts) {
		super(NamespacedKey.minecraft(Objects.requireNonNull(Utils.fixKey(key))));
		this.conflicts = new HashSet<>();
		if (conflicts != null) this.conflicts.addAll(conflicts);
	}
	
	protected CustomEnchantment(@NotNull String key, @NotNull NamespacedKey ... conflicts) {
		this(key,Arrays.asList(conflicts));
	}
	
	@NotNull
	@Deprecated
	public String getName() {
		return name();
	}
	
	@NotNull
	public String name() {
		return getKey().getKey().toUpperCase();
	}
	
	public final boolean canEnchantItem(@NotNull ItemStack item) {
		if (Utils.isNull(item) || (!includes(item) && item.getType() != Material.ENCHANTED_BOOK) || hasEnchantment(item)) return false;
		for (Entry<Enchantment,Integer> ench : Utils.thisOrThatOrNull(Utils.getStoredEnchants(item),item.getEnchantments()).entrySet()) if (conflictsWith(ench.getKey())) return false;
		return true;
	}
	
	@NotNull
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.BREAKABLE;
	}
	
	public float getDamageIncrease(int level,@NotNull EntityCategory entityCategory) {
		return 0;
	}
	
	@NotNull
	public String translationKey() {
		return defaultTranslationKey();
	}
	
	public boolean conflictsWith(@NotNull Enchantment enchantment) {
		return conflicts.contains(enchantment.getKey());
	}
	
	public boolean isCursed() {
		return false;
	}
	
	public boolean isTreasure() {
		return false;
	}
	
	@Positive
	public final int getStartLevel() {
		return 1;
	}
	
	@Positive
	public int getMaxLevel() {
		return 1;
	}
	
	public boolean isTradeable() {
		return false;
	}
	
	public boolean isDiscoverable() {
		return false;
	}
	
	@NotNull
	public EnchantmentRarity getRarity() {
		return EnchantmentRarity.VERY_RARE;
	}
	
	@NotNull
	public Component displayName(int level) {
		return defaultDisplayName(level);
	}
	
	@NotNull
	public Set<EquipmentSlot> getActiveSlots() {
		Set<EquipmentSlot> slots = new HashSet<>();
		switch (getItemTarget()) {
			case ARMOR -> slots.addAll(List.of(EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET));
			case ARMOR_HEAD -> slots.add(EquipmentSlot.HEAD);
			case ARMOR_TORSO -> slots.add(EquipmentSlot.CHEST);
			case ARMOR_LEGS -> slots.add(EquipmentSlot.LEGS);
			case ARMOR_FEET -> slots.add(EquipmentSlot.FEET);
			default -> slots.add(EquipmentSlot.HAND);
		}
		return slots;
	}
	
	@NotNull
	protected final TranslatableComponent defaultDisplayName(int level) {
		TranslatableComponent name = Component.translatable(translationKey()).color(isCursed() ? NamedTextColor.RED : NamedTextColor.GRAY);
		if (getMaxLevel() != 1) name = name.append(Component.space()).append(Component.translatable("enchantment.level." + level));
		return Utils.noItalic(name);
	}
	
	@NotNull
	protected String defaultTranslationKey() {
		return "enchantment.minecraft." + name().toLowerCase();
	}
	
//	protected boolean addConflict(Enchantment enchantment) {
//		return enchantment != null && !equals(enchantment) && conflicts.add(enchantment.getKey());
//	}
	
	@Contract("null -> false")
	public boolean hasEnchantment(ItemStack item) {
		return Utils.notNull(item) && Utils.thisOrThatOrNull(Utils.getStoredEnchants(item),item.getEnchantments()).containsKey(this);
	}
	
	@NotNull
	public ItemStack getBook(@Positive int lvl) {
		return Utils.addEnchantment(new ItemStack(Material.ENCHANTED_BOOK),this,lvl);
	}
	
	public abstract boolean includes(Material material);
	// return Utils.notNull(material) && getItemTarget().includes(material);
	
	public boolean includes(ItemStack item) {
		return Utils.notNull(item) && includes(item.getType());
	}
}