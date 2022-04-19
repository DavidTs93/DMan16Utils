package me.DMan16.POPUtils.Classes;

import java.util.*;
import java.util.function.Function;

import me.DMan16.POPUtils.Items.Socketable;
import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.EquipmentSlot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public final class Engraving extends CustomEnchantment {
	private static final @NotNull HashMap<@NotNull Engraving,@NotNull List<@NotNull Function<@NotNull Socketable<?>,@Nullable Integer>>> EXTRA_SCORES = new HashMap<>();
	
	public final char symbol;
	private final @NotNull @Unmodifiable Set<@NotNull EnchantmentTarget> targets;
	private final @NotNull @Unmodifiable Set<@NotNull Material> materials;
	
	public Engraving(@NotNull String key, char symbol, @Nullable Collection<@NotNull EnchantmentTarget> targets, @Nullable Collection<@NotNull Material> materials) {
		super(key);
		this.symbol = symbol;
		this.targets = targets == null ? Set.of() : Set.copyOf(targets);
		this.materials = materials == null ? Set.of() : Set.copyOf(materials);
	}
	
	public static void registerExtraEngravingScore(@NotNull Engraving engraving,@NotNull Function<@NotNull Socketable<?>,@Nullable Integer> scoreFunction) {
		EXTRA_SCORES.computeIfAbsent(engraving,l -> new ArrayList<>()).add(scoreFunction);
	}
	
	@Nullable
	public static Integer getEngravingExtraScore(@NotNull Socketable<?> socketable) {
		List<@NotNull Function<@NotNull Socketable<?>,@Nullable Integer>> list = Utils.applyNotNull(socketable.getEngraving(),EXTRA_SCORES::get);
		if (list == null || list.isEmpty()) return null;
		List<Integer> scores = list.stream().map(function -> function.apply(socketable)).filter(Objects::nonNull).toList();
		return scores.isEmpty() ? null : scores.stream().reduce(0,Integer::sum);
	}
	
	@NotNull
	@Override
	public Set<EquipmentSlot> getActiveSlots() {
		Set<EnchantmentTarget> targets = new HashSet<>(this.targets);
		Set<EquipmentSlot> slots = new HashSet<>();
		if (targets.remove(EnchantmentTarget.ARMOR)) {
			slots.add(EquipmentSlot.HEAD);
			slots.add(EquipmentSlot.CHEST);
			slots.add(EquipmentSlot.LEGS);
			slots.add(EquipmentSlot.FEET);
		}
		if (targets.remove(EnchantmentTarget.ARMOR_HEAD)) slots.add(EquipmentSlot.HEAD);
		if (targets.remove(EnchantmentTarget.ARMOR_TORSO)) slots.add(EquipmentSlot.CHEST);
		if (targets.remove(EnchantmentTarget.ARMOR_LEGS)) slots.add(EquipmentSlot.LEGS);
		if (targets.remove(EnchantmentTarget.ARMOR_FEET)) slots.add(EquipmentSlot.FEET);
		if (!targets.isEmpty()) slots.add(EquipmentSlot.HAND);
		materials.forEach(material -> slots.add(material.getEquipmentSlot()));
		return slots;
	}
	
	@Override
	public boolean conflictsWith(@NotNull Enchantment enchantment) {
		return (enchantment instanceof Engraving) || super.conflictsWith(enchantment);
	}
	
	@Override
	public boolean includes(Material material) {
		if (Utils.isNull(material)) return false;
		if (materials.contains(material)) return true;
		for (EnchantmentTarget target : targets) if (target.includes(material)) return true;
		return false;
	}
}