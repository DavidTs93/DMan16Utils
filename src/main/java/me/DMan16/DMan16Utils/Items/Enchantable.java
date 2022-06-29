package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.Classes.AttributesInfo;
import me.DMan16.DMan16Utils.Classes.Engraving;
import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Enums.Tags;
import me.DMan16.DMan16Utils.Interfaces.EnchantmentsHolder;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.Interfaces.Repairable;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Enchantable<V extends Enchantable<V> & Itemable<V>> implements Itemable<V>,EnchantmentsHolder,Repairable {
	protected static final HashMap<@NotNull Enchantment,@NotNull Integer> EXTRA_MAX_LEVELS = new HashMap<>();
	protected static final NamespacedKey DAMAGE_KEY = new NamespacedKey(DMan16UtilsMain.getInstance(),"item_damage");
	protected static final NamespacedKey MAX_ENCHANTMENTS_KEY = new NamespacedKey(DMan16UtilsMain.getInstance(),"max_enchantments");
	public static final int DEFAULT_CUSTOM_FIX_DIVIDE = 10;
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
		this.maxEnchantments = 1;
	}
	
	public static int getMaxLevel(@NotNull Enchantment enchantment) {
		Integer extra = EXTRA_MAX_LEVELS.get(enchantment);
		if (((enchantment instanceof Engraving engraving) && extra != null) || Objects.equals(extra,0)) {
			EXTRA_MAX_LEVELS.remove(enchantment);
			extra = null;
		}
		return Math.max(enchantment.getMaxLevel() + Utils.thisOrThatOrNull(extra,0),enchantment.getStartLevel());
	}
	
	public static void addExtraMaxLevels(@NotNull Enchantment enchantment,int amount) {
		if (amount == 0) return;
		amount += Utils.thisOrThatOrNull(EXTRA_MAX_LEVELS.get(enchantment),0);
		if (amount == 0) EXTRA_MAX_LEVELS.remove(enchantment);
		else EXTRA_MAX_LEVELS.put(enchantment,amount);
	}
	
	public static int getExtraMaxLevels(@NotNull Enchantment enchantment,int amount) {
		return Utils.thisOrThatOrNull(EXTRA_MAX_LEVELS.get(enchantment),0);
	}
	
	@Positive
	public int fixDivide() {
		return DEFAULT_CUSTOM_FIX_DIVIDE;
	}
	
	@Positive
	public int maxEnchantments() {
		return maxEnchantments;
	}
	
	@NonNegative
	public int enchantmentsAmount() {
		return enchantments.size();
	}
	
	public abstract @NonNegative int model();
	
	@Nullable
	@Positive
	public Integer getEnchantmentLevel(@NotNull Enchantment enchantment) {
		return (enchantment instanceof Engraving engraving) ? (hasEngraving(engraving) ? engraving.getMaxLevel() : null) : enchantments.get(enchantment);
	}
	
	public boolean hasEnchantment(@NotNull Enchantment enchantment) {
		return getEnchantmentLevel(enchantment) != null;
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
		if (keys.size() > this.maxEnchantments) {
			List<Enchantment> keysList = new ArrayList<>(keys);
			while (keys.size() > this.maxEnchantments) removeEnchantment(keysList.remove(keysList.size() - 1));
		}
		return (V) this;
	}
	
	@Positive
	protected int randomMaxEnchantments(@Positive int limit) {
		return ThreadLocalRandom.current().nextInt(0,limit) + 1;
	}
	
	@NotNull
	protected V setRandomMaxEnchantments(@Positive int limit) {
		return setMaxEnchantmentSlots(randomMaxEnchantments(limit));
	}
	
	protected V setEnchantments(@NotNull Map<@NotNull Enchantment,@NotNull @Positive Integer> enchantments) {
		Engraving engraving = null;
		for (Enchantment enchantment : enchantments.keySet()) if (enchantment instanceof Engraving e) {
			engraving = e;
			break;
		}
		return setEnchantmentsEngraving(enchantments,engraving);
	}
	
	protected final V setEnchantmentsEngraving(@NotNull Map<@NotNull Enchantment,@NotNull @Positive Integer> enchantments,@Nullable Engraving engraving) {
		this.engraving = engraving;
		this.enchantments.clear();
		for (Map.Entry<Enchantment,Integer> entry : enchantments.entrySet()) if (!(entry.getKey() instanceof Engraving)) this.enchantments.put(entry.getKey(),entry.getValue());
		return setMaxEnchantmentSlots(maxEnchantments);
	}
	
	public boolean canEnchant(@NotNull Enchantment enchantment,@Positive int level) {
		if (level < enchantment.getStartLevel()) return false;
		if (enchantment instanceof Engraving engraving) return this.engraving == null;
		Boolean existingCheck = canEnchantExisting(enchantment,level);
		if ((existingCheck == null && emptyEnchantmentSlots() <= 0) || Boolean.FALSE.equals(existingCheck)) return false;
		if (Tags.AXES.contains(material())) {
			if (enchantment == Enchantment.DIG_SPEED || enchantment == Enchantment.LOOT_BONUS_BLOCKS || enchantment == Enchantment.SILK_TOUCH) return false;
			if (enchantment != Enchantment.LOOT_BONUS_MOBS && enchantment != Enchantment.KNOCKBACK && enchantment != Enchantment.FIRE_ASPECT && !enchantment.canEnchantItem(new ItemStack(material()))) return false;
		} else if (!enchantment.canEnchantItem(new ItemStack(material()))) return false;
		return enchantments.keySet().stream().noneMatch(ench -> Utils.conflictsNotEquals(ench,enchantment));
	}
	
	/**
	 * @param enchantment NOT an {@link Engraving}!
	 * @param level >= start level
	 */
	@Nullable
	protected Boolean canEnchantExisting(@NotNull Enchantment enchantment,@Positive int level) {
		return Utils.applyNotNull(getEnchantmentLevel(enchantment),l -> level > l || (level == l && level < Enchantable.getMaxLevel(enchantment)));
	}
	
	@Nullable
	public Engraving getEngraving() {
		return engraving;
	}
	
	public boolean addEnchantment(@NotNull Enchantment enchantment,@Positive int level) {
		if (!canEnchant(enchantment,level)) return false;
		if (enchantment instanceof Engraving engraving) this.engraving = engraving;
		else {
			Integer existing = getEnchantmentLevel(enchantment);
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
		int damage = shouldBreak() ? maxDurability() : this.damage,per = maxDurability() / fixDivide();
		while (damage > 0 && amount > 0) {
			damage -= per;
			amount--;
		}
		this.damage = Math.max(damage,0);
		return amount;
	}
	
	@NotNull public abstract String key();
	
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
	protected abstract ItemStack makeItemNoAttributes(@NotNull Material material,@NotNull List<Component> lore);
	
	@NotNull public abstract EquipmentSlot equipSlot();
	
	@NotNull protected abstract Component displayName();
	
//	@NotNull
//	public ItemStack brokenItem() {
//		return new ItemStack(Material.AIR);
//	}
	
	@NotNull
	protected List<Component> enchantmentsLore(@NotNull HashMap<@NotNull Enchantment,@NotNull @Positive Integer> enchantmentsMap) {
		return Utils.enchantmentsLore(enchantmentsMap,engraving);
	}
	
	@NotNull
	protected ItemStack asItemNoAttributes() {
		Material material = material();
		List<Component> lore = new ArrayList<>();
		if (shouldBreak()) lore.add(Utils.noItalic(Component.translatable("menu.broken",NamedTextColor.RED)));
		lore.add(Component.empty());
		lore.addAll(enchantmentsLore(enchantments));
		lore.add(Component.empty());
		lore.add(Utils.noItalic(Component.translatable("item.modifiers." + ((equipSlot() == EquipmentSlot.HAND ? "main_" : "") + equipSlot().name().toLowerCase()).replace("_",""),NamedTextColor.WHITE)));
		lore.addAll(info.lore());
		Utils.applyNotNull(getExtraLoreItemNoAttributes(),lore::addAll);
		return Utils.setKeyPersistentDataContainer(damageItem(makeItemNoAttributes(material,lore),damageItemStack(material),shouldBreak()),MAX_ENCHANTMENTS_KEY,PersistentDataType.INTEGER,maxEnchantments);
	}
	
	@Nullable
	@Contract("null,_,_ -> null; !null,_,_ -> !null")
	public static ItemStack damageItem(ItemStack item,int damage,boolean shouldBreak) {
		return damage > 0 ? Utils.setKeyPersistentDataContainer(!shouldBreak ? Utils.setDamage(item,damage) : item,DAMAGE_KEY,PersistentDataType.INTEGER,damage,true) : item;
	}
	
	@NotNull
	protected final ItemStack finalizeItem(@NotNull ItemStack item) {
		return finalizeItem(item,info);
	}
	
	@NotNull
	protected final ItemStack finalizeItem(@NotNull ItemStack item,@Nullable AttributesInfo info) {
		return finalizeItem(item,info,key(),equipSlot(),enchantments,shouldBreak(),maxDurability(),damage());
	}
	
	@NotNull
	protected static ItemStack finalizeItem(@NotNull ItemStack item,@Nullable AttributesInfo info,@NotNull String key,@NotNull EquipmentSlot slot,@Nullable HashMap<Enchantment,Integer> enchantments,boolean shouldBreak,@Positive int maxDurability,@NonNegative int damage) {
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
	
	@NotNull
	public final Component giveComponent() {
		return displayName().hoverEvent(asItemNoAttributes().asHoverEvent());
	}
	
	@NotNull
	@Unmodifiable
	public Map<@NotNull Enchantment,@NotNull Integer> getEnchantments() {
		return Collections.unmodifiableMap(enchantments);
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