package me.DMan16.DMan16Utils.Classes;

import io.papermc.paper.enchantments.EnchantmentRarity;
import me.DMan16.DMan16Utils.Items.ItemUtils;
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

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

public abstract class CustomEnchantment extends Enchantment implements Listener {
	private static final @NotNull Field ACCEPTING_NEW = Objects.requireNonNull(acceptingNew());
	private static final @NotNull Field BY_KEY = Objects.requireNonNull(byKey());
	private static final @NotNull Field BY_NAME = Objects.requireNonNull(byName());
	
	private final @NotNull Set<NamespacedKey> conflicts;
	private boolean registered = false;
	
	protected CustomEnchantment(@NotNull String key, @Nullable Collection<@NotNull NamespacedKey> conflicts) {
		super(NamespacedKey.minecraft(Objects.requireNonNull(Utils.fixKey(key))));
		this.conflicts = new HashSet<>();
		if (conflicts != null) addConflicts(conflicts);
	}
	
	protected CustomEnchantment(@NotNull String key, @NotNull NamespacedKey ... conflicts) {
		this(key,Arrays.asList(conflicts));
	}
	
	protected void addConflicts(@NotNull Collection<@NotNull NamespacedKey> conflicts) {
		this.conflicts.addAll(conflicts);
	}
	
	protected void addConflicts(NamespacedKey @NotNull ... conflicts) {
		this.conflicts.addAll(Arrays.asList(conflicts));
	}
	
	protected void removeConflicts(@NotNull Collection<@NotNull NamespacedKey> conflicts) {
		conflicts.forEach(this.conflicts::remove);
	}
	
	protected void removeConflicts(NamespacedKey @NotNull ... conflicts) {
		Arrays.asList(conflicts).forEach(this.conflicts::remove);
	}
	
	public boolean isRegistered() {
		return registered;
	}
	
	public void register() throws IllegalAccessException {
		ACCEPTING_NEW.setAccessible(true);
		ACCEPTING_NEW.set(null,true);
		Enchantment.registerEnchantment(this);
		registered = true;
	}
	
	@SuppressWarnings("unchecked")
	public void unregister() throws IllegalAccessException {
		BY_KEY.setAccessible(true);
		((HashMap<NamespacedKey,Enchantment>) BY_KEY.get(null)).remove(getKey());
		BY_NAME.setAccessible(true);
		((HashMap<String,Enchantment>) BY_NAME.get(null)).remove(getKey().getKey());
		registered = false;
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
		if (Utils.isNull(item) || (!includes(item) && item.getType() != Material.ENCHANTED_BOOK)) return false;
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
	
	@Contract("null -> false")
	public boolean hasEnchantmentNotEnchantedBook(ItemStack item) {
		return Utils.notNull(item) && item.getType() != Material.ENCHANTED_BOOK && item.getEnchantments().containsKey(this);
	}
	
	@NotNull
	public ItemStack getBook(@Positive int lvl) {
		return ItemUtils.ofOrSubstituteOrHolder(Utils.addEnchantment(new ItemStack(Material.ENCHANTED_BOOK),this,lvl)).asItem();
	}
	
	public abstract boolean includes(Material material);
	// return Utils.notNull(material) && getItemTarget().includes(material);
	
	public boolean includes(ItemStack item) {
		return Utils.notNull(item) && includes(item.getType());
	}
	
	@Nullable
	@Contract(pure = true)
	private static Field acceptingNew() {
		try {
			return Enchantment.class.getDeclaredField("acceptingNew");
		} catch (Exception e) {}
		return null;
	}
	
	@Nullable
	@Contract(pure = true)
	private static Field byKey() {
		try {
			return Enchantment.class.getDeclaredField("byKey");
		} catch (Exception e) {}
		return null;
	}
	
	@Nullable
	@Contract(pure = true)
	private static Field byName() {
		try {
			return Enchantment.class.getDeclaredField("byName");
		} catch (Exception e) {}
		return null;
	}
}