package me.DMan16.POPUtils.Interfaces;

import me.DMan16.POPUtils.Classes.AttributesInfo;
import me.DMan16.POPUtils.Enums.Tags;
import me.DMan16.POPUtils.Items.ItemableStack;
import me.DMan16.POPUtils.POPUtilsMain;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public abstract class Enchantable<V extends Enchantable<V> & Itemable<V>> implements Itemable<V> {
	public static final NamespacedKey DAMAGE = new NamespacedKey(POPUtilsMain.getInstance(),"prisonpop_item_damage");
	public static int CUSTOM_FIX_DIVIDE = 10;
	
	protected final @NotNull HashMap<@NotNull Enchantment,@NotNull Integer> enchantments = new HashMap<>();
	protected @NonNegative int damage = 0;
	public final @NotNull AttributesInfo info;
	protected final boolean isDefault;
	
	protected Enchantable(@NotNull AttributesInfo info,boolean isDefault) {
		this.info = info;
		this.isDefault = isDefault;
	}
	
	public abstract @Positive int model();
	
	public boolean canEnchant(@NotNull Enchantment enchantment, int level) {
		if (level < enchantment.getStartLevel()) return false;
		Integer existing = enchantments.get(enchantment);
		if (existing != null) return level > existing || (level == existing && level < enchantment.getMaxLevel());
		if (Tags.AXES.contains(material())) {
			if (enchantment == Enchantment.DIG_SPEED || enchantment == Enchantment.LOOT_BONUS_BLOCKS || enchantment == Enchantment.SILK_TOUCH) return false;
			if (enchantment != Enchantment.LOOT_BONUS_MOBS && enchantment != Enchantment.KNOCKBACK && enchantment != Enchantment.FIRE_ASPECT &&
					!enchantment.canEnchantItem(new ItemStack(material()))) return false;
		} else if (!enchantment.canEnchantItem(new ItemStack(material()))) return false;
		return enchantments.keySet().stream().noneMatch(ench -> Utils.conflictsNotEquals(ench,enchantment));
	}
	
	public boolean addEnchant(@NotNull Enchantment enchantment, int level) {
		if (!canEnchant(enchantment,level)) return false;
		Integer existing = enchantments.get(enchantment);
		enchantments.put(enchantment,existing != null && existing == level ? level + 1 : level);
		return true;
	}
	
	@MonotonicNonNull
	public abstract ItemableStack repairItem();
	
	@NonNegative
	public final int damage() {
		return damage;
	}
	
	/**
	 * @return leftover amount
	 */
	@NonNegative
	public final int fix(@NonNegative int amount) {
		if (damage == 0) return amount;
		int damage = this.damage, per = maxDurability() / CUSTOM_FIX_DIVIDE;
		while (damage > 0 && amount > 0) {
			damage -= per;
			amount--;
		}
		if (damage < 0) damage = 0;
		this.damage = damage;
		return amount;
	}
	
	@NotNull public abstract String key();
	
	@Positive public abstract int maxDurability();
	
	public final boolean shouldBreak() {
		return damage >= maxDurability();
	}
	
	@NotNull
	@SuppressWarnings("unchecked")
	public final V addDamage(@NonNegative int amount) {
		damage += amount;
		return (V) this;
	}
	
	@NotNull
	@SuppressWarnings("unchecked")
	public final V reduceDamage(@NonNegative int amount) {
		damage = Math.max(damage - amount,0);
		return (V) this;
	}
	
	@Nullable
	protected List<Component> getExtraLoreItemNoAttributes() {
		return null;
	}
	
	@NotNull
	protected abstract ItemStack makeItemNoAttributes(@NotNull Material material, @NotNull List<Component> lore);
	
	@NotNull public abstract EquipmentSlot equipSlot();
	
	@NotNull protected abstract Component displayName();
	
	@NotNull
	protected final ItemStack asItemNoAttributes() {
		if (shouldBreak()) return new ItemStack(Material.AIR);
		Material material = material();
		List<Component> lore = info.lore();
		lore.add(0,Utils.noItalic(Component.translatable("item.modifiers." + equipSlot().name().toLowerCase(),NamedTextColor.WHITE)));
		lore.add(0,Component.empty());
		Utils.applyNotNull(getExtraLoreItemNoAttributes(),lore::addAll);
		if (!enchantments.isEmpty()) {
			lore.add(0,Component.empty());
			lore.addAll(0,ItemableStack.enchantmentsLore(enchantments));
		}
		ItemStack item = makeItemNoAttributes(material,lore);
		int damageItemStack = damageItemStack(item.getType());
		if (damageItemStack > 0) item = Utils.setKeyPersistentDataContainer(Utils.setDamage(item,damageItemStack),DAMAGE,PersistentDataType.INTEGER,damage);
		return Utils.addEnchantments(item,enchantments);
	}
	
	@NonNegative
	public int damageItemStack(@NotNull Material material) {
		return damage <= 0 ? 0 : (int) Math.max(1,Math.floor(((float) damage) / maxDurability() * material.getMaxDurability()));
	}
	
	@NotNull
	public final ItemStack asItem() {
		return Utils.addDurabilityLore(info.addAttributes(asItemNoAttributes(),key(),equipSlot()),maxDurability(),damage,false);
	}
	
	@NotNull
	public final Component giveComponent() {
		return displayName().hoverEvent(asItemNoAttributes().asHoverEvent());
	}
	
	@NotNull
	protected final HashMap<@NotNull String,Object> baseMap() {
		HashMap<String,Object> map = new HashMap<>();
		if (!enchantments.isEmpty()) map.put("Enchantments",ItemableStack.getEnchantments(enchantments));
		if (damage > 0) map.put("Damage",damage);
		return map;
	}
	
	public abstract int hashCode();
}