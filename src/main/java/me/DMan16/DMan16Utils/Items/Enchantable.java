package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.Classes.AttributesInfo;
import me.DMan16.DMan16Utils.Classes.Engraving;
import me.DMan16.DMan16Utils.Enums.Tags;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Utils.Utils;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class Enchantable<V extends Enchantable<V> & Itemable<V>> implements Itemable<V> {
	protected static final NamespacedKey DAMAGE_KEY = new NamespacedKey(DMan16UtilsMain.getInstance(),"item_damage");
	protected static final NamespacedKey MAX_ENCHANTMENTS_KEY = new NamespacedKey(DMan16UtilsMain.getInstance(),"max_enchantments");
	public static final int CUSTOM_FIX_DIVIDE = 10;
	private static final int MAX_ENCHANTMENTS = 20;
	
	protected @Nullable Engraving engraving = null;
	protected final @NotNull HashMap<@NotNull Enchantment,@NotNull @Positive Integer> enchantments = new HashMap<>();
	protected @Positive int maxEnchantments;
	protected @NonNegative int damage = 0;
	public final @NotNull AttributesInfo info;
	protected final boolean isDefault;
	
	protected Enchantable(@NotNull AttributesInfo info,boolean isDefault) {
		this.info = info;
		this.isDefault = isDefault;
		this.maxEnchantments = initialMaxEnchantments();
	}
	
	@Positive
	protected int initialMaxEnchantments() {
		return 1;
	}
	
	@Positive
	public int maxEnchantments() {
		return maxEnchantments;
	}
	
	public abstract @NonNegative int model();
	
	@Nullable
	@Positive
	public Integer getEnchant(@NotNull Enchantment enchantment) {
		if (!(enchantment instanceof Engraving)) return enchantments.get(enchantment);
		return null;
	}
	
	public boolean hasEnchant(@NotNull Enchantment enchantment) {
		return getEnchant(enchantment) != null;
	}
	
	public boolean hasEngraving(@NotNull Engraving engraving) {
		return Objects.equals(engraving,this.engraving);
	}
	
	@NonNegative
	public int emptyEnchantmentSlots() {
		return maxEnchantments - enchantments.size();
	}
	
	@SuppressWarnings("unchecked")
	protected V setMaxEnchantmentSlots(@Positive int maxEnchantments) {
		if (maxEnchantments == this.maxEnchantments) return (V) this;
		this.maxEnchantments = Math.min(maxEnchantments,MAX_ENCHANTMENTS);
		Set<Enchantment> keys = enchantments.keySet();
		if (keys.size() > maxEnchantments) {
			List<Enchantment> keysList = new ArrayList<>(keys);
			while (keys.size() > maxEnchantments) keys.remove(keysList.remove(keysList.size() - 1));
		}
		return (V) this;
	}
	
	protected V setEnchantments(@NotNull Map<@NotNull Enchantment,@NotNull @Positive Integer> enchantments) {
		Engraving engraving = null;
		for (Enchantment enchantment : enchantments.keySet()) if (enchantment instanceof Engraving e) {
			engraving = e;
			break;
		}
		return setEnchantmentsEngraving(enchantments,engraving);
	}
	
	protected V setEnchantmentsEngraving(@NotNull Map<@NotNull Enchantment,@NotNull @Positive Integer> enchantments, @Nullable Engraving engraving) {
		this.engraving = engraving;
		this.enchantments.clear();
		for (Map.Entry<Enchantment,Integer> entry : enchantments.entrySet()) if (!(entry.getKey() instanceof Engraving)) this.enchantments.put(entry.getKey(),entry.getValue());
		return setMaxEnchantmentSlots(maxEnchantments);
	}
	
	public boolean canEnchant(@NotNull Enchantment enchantment, int level) {
		if (level <= 0 || level < enchantment.getStartLevel()) return false;
		if (enchantment instanceof Engraving engraving) {
			if (this.engraving != null) return false;
			this.engraving = engraving;
			return true;
		}
		Integer existing = getEnchant(enchantment);
		if (existing != null) return level > existing || (level == existing && level < enchantment.getMaxLevel());
		if (emptyEnchantmentSlots() <= 0) return false;
		if (Tags.AXES.contains(material())) {
			if (enchantment == Enchantment.DIG_SPEED || enchantment == Enchantment.LOOT_BONUS_BLOCKS || enchantment == Enchantment.SILK_TOUCH) return false;
			if (enchantment != Enchantment.LOOT_BONUS_MOBS && enchantment != Enchantment.KNOCKBACK && enchantment != Enchantment.FIRE_ASPECT && !enchantment.canEnchantItem(new ItemStack(material()))) return false;
		} else if (!enchantment.canEnchantItem(new ItemStack(material()))) return false;
		return enchantments.keySet().stream().noneMatch(ench -> Utils.conflictsNotEquals(ench,enchantment));
	}
	
	@Nullable
	public Engraving getEngraving() {
		return engraving;
	}
	
	public boolean addEnchant(@NotNull Enchantment enchantment, int level) {
		if (!canEnchant(enchantment,level)) return false;
		if (enchantment instanceof Engraving engraving) this.engraving = engraving;
		else {
			Integer existing = getEnchant(enchantment);
			enchantments.put(enchantment,existing != null && existing == level ? level + 1 : level);
		}
		return true;
	}
	
	@Nullable
	public Integer removeEnchantment(@NotNull Enchantment enchantment) {
		return (enchantment instanceof Engraving) ? null : enchantments.remove(enchantment);
	}
	
	@SuppressWarnings("unchecked")
	public V clearEnchantments() {
		enchantments.clear();
		return (V) this;
	}
	
	@SuppressWarnings("unchecked")
	public V removeEngraving() {
		engraving = null;
		return (V) this;
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
		int damage = shouldBreak() ? maxDurability() : this.damage, per = maxDurability() / CUSTOM_FIX_DIVIDE;
		while (damage > 0 && amount > 0) {
			damage -= per;
			amount--;
		}
		this.damage = Math.max(damage,0);
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
		damage = (int) Math.min(((long) damage) + amount,maxDurability());
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
	
	public boolean canRevive() {
		return false;
	}
	
//	@NotNull
//	public ItemStack brokenItem() {
//		return new ItemStack(Material.AIR);
//	}
	
	@NotNull
	protected List<Component> enchantmentsLore(@NotNull HashMap<@NotNull Enchantment,@NotNull @Positive Integer> enchantmentsMap) {
		return Utils.enchantmentsLore(enchantmentsMap,engraving);
	}
	
	@NotNull
	protected final ItemStack asItemNoAttributes() {
		Material material = material();
		List<Component> lore = info.lore();
		lore.add(0,Utils.noItalic(Component.translatable("item.modifiers." +
				((equipSlot() == EquipmentSlot.HAND ? "main_" : "") + equipSlot().name().toLowerCase()).replace("_",""),NamedTextColor.WHITE)));
		Utils.applyNotNull(getExtraLoreItemNoAttributes(),lore::addAll);
		if (!enchantments.isEmpty()) {
			lore.add(0,Component.empty());
			lore.addAll(0,enchantmentsLore(enchantments));
		}
		lore.add(0,Component.empty());
		if (shouldBreak()) lore.add(0,Utils.noItalic(Component.translatable("menu.broken",NamedTextColor.RED)));
		return Utils.setKeyPersistentDataContainer(damageItem(makeItemNoAttributes(material,lore),damageItemStack(material),shouldBreak()),MAX_ENCHANTMENTS_KEY,PersistentDataType.INTEGER,maxEnchantments);
	}
	
	@Nullable
	@Contract("null,_,_ -> null; !null,_,_ -> !null")
	public static ItemStack damageItem(ItemStack item, int damage, boolean shouldBreak) {
		return damage > 0 ? Utils.setKeyPersistentDataContainer(!shouldBreak ? Utils.setDamage(item,damage) : item,DAMAGE_KEY,PersistentDataType.INTEGER,damage,true) : item;
	}
	
	@NotNull
	protected final ItemStack finalizeItem(@NotNull ItemStack item) {
		return finalizeItem(item,info);
	}
	
	@NotNull
	protected final ItemStack finalizeItem(@NotNull ItemStack item, @Nullable AttributesInfo info) {
		return finalizeItem(item,info,key(),equipSlot(),enchantments,shouldBreak(),maxDurability(),damage());
	}
	
	@NotNull
	protected static ItemStack finalizeItem(@NotNull ItemStack item, @Nullable AttributesInfo info, @NotNull String key, @NotNull EquipmentSlot slot, @Nullable HashMap<Enchantment,Integer> enchantments, boolean shouldBreak, @Positive int maxDurability, @NonNegative int damage) {
		if (!shouldBreak) {
			if (enchantments != null) item = Utils.addEnchantments(item,enchantments);
			item = info == null ? AttributesInfo.addAttributesNull(item,key,slot) : info.addAttributes(item,key,slot);
		} else item = AttributesInfo.addAttributesNull(item,key,slot);
		return Utils.addDurabilityLore(item,maxDurability,damage,false);
	}
	
	@NotNull
	public final ItemStack asItem() {
		return finalizeItem(asItemNoAttributes(),info,key(),equipSlot(),enchantments,shouldBreak(),maxDurability(),damage());
	}
	
	@NonNegative
	public int damageItemStack() {
		return damageItemStack(material());
	}
	
	@NonNegative
	public int damageItemStack(@NotNull Material material) {
		return damage <= 0 ? 0 : (int) Math.max(1,Math.floor(((float) damage) / maxDurability() * material.getMaxDurability()));
	}
	
	@NotNull
	public final Component giveComponent() {
		return displayName().hoverEvent(asItemNoAttributes().asHoverEvent());
	}
	
	@NotNull
	protected final HashMap<@NotNull String,Object> baseMap() {
		HashMap<String,Object> map = new HashMap<>();
		if (!enchantments.isEmpty()) map.put("Enchantments",ItemableStack.getEnchantments(enchantments));
		if (engraving != null) map.put("Engraving",engraving.name());
		map.put("MaxEnchantments",maxEnchantments);
		if (damage > 0) map.put("Damage",damage);
		return map;
	}
	
	public abstract int hashCode();
}