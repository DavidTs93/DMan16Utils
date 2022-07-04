package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.Classes.Pair;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.Interfaces.ItemableAmountable;
import me.DMan16.DMan16Utils.NMSWrappers.TagWrapper;
import me.DMan16.DMan16Utils.Utils.ReflectionUtils;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.nbt.ByteTag;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Consumable<V extends Consumable<V>> implements ItemableAmountable<V> {
	private final @NotNull String key;
	private final @NotNull Material material;
	protected final boolean isDefault;
	private final int hunger;
	private final float saturation;
	/**
	 * Not empty
	 */
	private final @Nullable @Unmodifiable List<@NotNull Pair<@NotNull PotionEffect,@NotNull @Positive @Range(from = 1,to = 100) Integer>> effectsAdd;
	/**
	 * Not empty
	 */
	private final @Nullable @Unmodifiable Map<@NotNull PotionEffectType,@NotNull @Positive @Range(from = 1,to = 100) Integer> effectsRemove;
	private final @Nullable Itemable<?> leftover;
	private final @Nullable @Positive Integer model;
	private final @Nullable Component name;
	private final boolean enchanted;
	private @Positive int amount = 1;
	
	protected Consumable(@NotNull String key,@NotNull Material material,boolean isDefault,int hunger,float saturation,@Nullable Collection<@NotNull Pair<@NotNull PotionEffect,@NotNull Integer>> effectsAdd,@Nullable Map<@NotNull PotionEffectType,@NotNull Integer> effectsRemove,
						 @Nullable Itemable<
			?> leftover,Integer model,@Nullable Component name,boolean enchanted) {
		this.key = Objects.requireNonNull(Utils.fixKey(key));
		this.material = material;
		this.isDefault = isDefault;
		if ((!this.isDefault && name == null) || isIllegal(this.key,this.material,this.isDefault)) throw new IllegalArgumentException();
		this.hunger = hunger;
		this.saturation = saturation;
		HashMap<PotionEffectType,Integer> effectsRemoveMap = effectsRemove == null || effectsRemove.isEmpty() ? null : new HashMap<>(effectsRemove);
		if (effectsRemoveMap != null) {
			Set<PotionEffectType> remove = effectsRemoveMap.entrySet().stream().filter(entry -> entry.getValue() > 0).map(Map.Entry::getKey).collect(Collectors.toSet());
			remove.forEach(effectsRemoveMap::remove);
			this.effectsRemove = Collections.unmodifiableMap(effectsRemoveMap);
		} else this.effectsRemove = null;
		if (effectsAdd != null && !effectsAdd.isEmpty()) {
			effectsAdd = effectsAdd.stream().filter(pair -> pair.first().getAmplifier() >= 0 && pair.first().getDuration() > 0 && pair.second() > 0).map(pair -> Pair.of(pair.first(),Math.min(pair.second(),100))).toList();
			this.effectsAdd = effectsAdd.isEmpty() ? null : List.copyOf(effectsAdd);
		} else this.effectsAdd = null;
		this.leftover = leftover;
		this.model = model == null || model <= 0 ? null : model;
		this.name = name;
		this.enchanted = enchanted;
	}
	
	protected Consumable(@NotNull String key,@NotNull Material material,boolean isDefault,@NotNull ConsumableInfo info,Integer model,@Nullable Component name,boolean enchanted) {
		this(key,material,isDefault,info.hunger(),info.saturation(),info.effectsAdd(),info.effectsRemove(),info.leftover(),model,name,enchanted);
	}
	
	protected boolean isIllegal(@NotNull String key,@NotNull Material material,boolean isDefault) {
		return false;
	}
	
	@NotNull
	public final String key() {
		return key;
	}
	
	/**
	 * Edible
	 */
	@NotNull
	public final Material material() {
		return material;
	}
	
	public final int hunger() {
		return hunger;
	}
	
	public final float saturation() {
		return saturation;
	}
	
	@NotNull
	@Unmodifiable
	public List<@NotNull Pair<@NotNull PotionEffect,@NotNull @Positive @Range(from = 1,to = 100) Integer>> effectsAdd() {
		return effectsAdd == null ? List.of() : effectsAdd;
	}
	
	@NotNull
	@Unmodifiable
	public Map<@NotNull PotionEffectType,@NotNull @Positive @Range(from = 1,to = 100) Integer> effectsRemove() {
		return effectsRemove == null ? Map.of() : effectsRemove;
	}
	
	protected final boolean effectsRemoveContains(@NotNull PotionEffectType type) {
		return effectsRemove != null && effectsRemove.containsKey(type);
	}
	
	@Nullable
	@Positive
	public final Integer model() {
		return model;
	}
	
	@Nullable
	public final Component name() {
		return name;
	}
	
	public final boolean looksEnchanted() {
		return enchanted;
	}
	
	@NotNull
	public Component displayName() {
		Component name = name();
		if (!isDefault && name == null) throw new IllegalArgumentException();
		return name == null ? Component.translatable(material().translationKey(),NamedTextColor.WHITE) : name;
	}
	
	@Nullable
	public Itemable<?> leftover() {
		return leftover;
	}
	
	@Positive
	public int amount() {
		return amount;
	}
	
	@NotNull
	public ItemStack asItem() {
		return Utils.applyOrOriginalIf(enchanted ? Utils.addEnchantment(Utils.makeItem(material,displayName(),model,ItemFlag.HIDE_ENCHANTS),Enchantment.DURABILITY,1) : Utils.makeItem(material,displayName(),model),item -> ReflectionUtils.addNBTTag(item,"Original",new TagWrapper.Safe(ByteTag.ONE)),isDefault);
	}
	
	@NotNull
	public Component giveComponent() {
		return displayName().append(Component.text(" x" + amount(),NamedTextColor.WHITE));
	}
	
	@NotNull
	public Map<@NotNull String,Object> toMap() {
		return new HashMap<>() {{
			put("Key",key);
			if (amount > 1) put("Amount",amount);
		}};
	}
	
	@NotNull
	public V copy(@Positive int amount) {
		return copy().amount(amount);
	}
	
	@NotNull
	@SuppressWarnings("unchecked")
	public V amount(@Positive int amount) {
		this.amount = Math.min(amount,maxStackSize());
		return (V) this;
	}
	
	public boolean onConsume(@NotNull Player player) {
		return true;
	}
	
	public record ConsumableInfo(int hunger,float saturation,@Nullable Collection<@NotNull Pair<@NotNull PotionEffect,@NotNull Integer>> effectsAdd,@Nullable Map<@NotNull PotionEffectType,@NotNull Integer> effectsRemove,@Nullable Itemable<?> leftover) {}
}