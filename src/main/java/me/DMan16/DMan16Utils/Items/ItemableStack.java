package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.Classes.Engraving;
import me.DMan16.DMan16Utils.Classes.Pair;
import me.DMan16.DMan16Utils.Enums.Tags;
import me.DMan16.DMan16Utils.Interfaces.EnchantmentsHolder;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.Interfaces.ItemableAmountable;
import me.DMan16.DMan16Utils.Restrictions.Restrictions;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class ItemableStack implements ItemableAmountable<ItemableStack>,EnchantmentsHolder {
	private static final Color DEFAULT_LEATHER_COLOR = ((LeatherArmorMeta) new ItemStack(Material.LEATHER_HELMET).getItemMeta()).getColor();
	private static final HashMap<@NotNull Material,@Nullable Supplier<Itemable<?>>> DISABLED_MATERIALS = new HashMap<>();
	private static final List<@NotNull Function<@NotNull ItemStack,@Nullable Itemable<?>>> DISABLED_ITEMS = new ArrayList<>();
	private static final @NotNull Set<@NotNull Material> NO_ENCHANTMENTS_MATERIALS = new HashSet<>();
	
	private final ItemStack item;
	
	public static void addDisabledMaterials(@NotNull Material ... materials) {
		for (Material material : materials) DISABLED_MATERIALS.put(material,null);
	}
	
	public static void addDisabledMaterials(@NotNull Collection<@NotNull Material> materials) {
		for (Material material : materials) DISABLED_MATERIALS.put(material,null);
	}
	
	public static void addDisabledMaterialsSubstitutes(@NotNull Function<@NotNull Material,Itemable<?>> get,@NotNull Material ... materials) {
		for (Material material : materials) DISABLED_MATERIALS.put(material,() -> get.apply(material));
	}
	
	public static void addDisabledMaterialsSubstitutes(@NotNull Function<@NotNull Material,Itemable<?>> get,@NotNull Collection<@NotNull Material> materials) {
		for (Material material : materials) DISABLED_MATERIALS.put(material,() -> get.apply(material));
	}
	
	public static void addDisabledItemSubstitute(@NotNull Function<@NotNull ItemStack,@Nullable Itemable<?>> substitute) {
		DISABLED_ITEMS.add(substitute);
	}
	
	@SafeVarargs
	public static void addDisabledMaterialsSubstitutes(@NotNull Pair<@NotNull Material,@Nullable Supplier<Itemable<?>>> @NotNull ... infos) {
		for (Pair<@NotNull Material,@Nullable Supplier<Itemable<?>>> info : infos) DISABLED_MATERIALS.put(info.first,info.second);
	}
	
	public static void addDisabledMaterialsSubstitutes(@NotNull Collection<@NotNull Pair<@NotNull Material,@Nullable Supplier<Itemable<?>>>> infos) {
		for (Pair<@NotNull Material,@Nullable Supplier<Itemable<?>>> info : infos) DISABLED_MATERIALS.put(info.first,info.second);
	}
	
	public static void addNoEnchantmentsMaterials(@NotNull Collection<@NotNull Material> materials) {
		NO_ENCHANTMENTS_MATERIALS.addAll(materials);
	}
	
	public static void addNoEnchantmentsMaterials(@NotNull Material ... materials) {
		NO_ENCHANTMENTS_MATERIALS.addAll(Arrays.asList(materials));
	}
	
	private ItemableStack(@NotNull ItemStack item) {
		this.item = item.clone();
	}
	
	@NotNull
	@Unmodifiable
	public Map<@NotNull Enchantment,@NotNull Integer> getEnchantments() {
		return Utils.thisOrThatOrNull(Utils.getStoredEnchants(item),item.getEnchantments());
	}
	
	@NotNull
	public static List<Component> enchantmentsLore(@NotNull Map<@NotNull Enchantment,@NotNull @Positive Integer> enchantments) {
		Engraving engraving = null;
		for (Map.Entry<Enchantment,Integer> ench : enchantments.entrySet()) if (ench.getKey() instanceof Engraving e) {
			engraving = e;
			break;
		}
		return Utils.enchantmentsLore(enchantments,engraving);
	}
	
	@NotNull
	public ItemStack asItem() {
		return material() == Material.ENCHANTED_BOOK ? item.clone() : Utils.addDurabilityLore(item.clone(),material().getMaxDurability(),0,true);
	}
	
	@NotNull
	public Material material() {
		return item.getType();
	}
	
	@Contract("null -> false")
	private static boolean legalBut(Material material) {
		return Utils.notNull(material) && material.isItem();
//		return Utils.notNull(material) && material.isItem() && (material.getEquipmentSlot() == EquipmentSlot.HAND || material.getEquipmentSlot() == EquipmentSlot.OFF_HAND);
	}
	
	@Contract("null -> false")
	private static boolean legal(Material material) {
		return legalBut(material) && !DISABLED_MATERIALS.containsKey(material);
	}
	
	@Nullable
	private static ItemableStack getLegalItemableStack(@NotNull ItemStack item) {
		if (item.getType().getMaxDurability() > 0) try {
			if (((Damageable) item.getItemMeta()).getDamage() <= 0) item = Utils.setLore(item,null);
		} catch (Exception e) {}
		if (NO_ENCHANTMENTS_MATERIALS.contains(item.getType())) {
			item = item.clone();
			new HashSet<>(item.getEnchantments().keySet()).forEach(item::removeEnchantment);
		}
		ItemableStack stack = new ItemableStack(item);
		ItemableStack fromMap = of(stack.toMap());
		return fromMap == null || !Utils.sameItem(item,fromMap.item) ? null : stack;
	}
	
	@Nullable
	@Contract("null -> null")
	public static ItemableStack of(ItemStack item) {
		return item == null || !legal(item.getType()) ? null : getLegalItemableStack(item);
	}
	
	@Nullable
	@Contract("null -> null")
	public static Itemable<?> ofOrSubstitute(ItemStack item) {
		if (item == null || !legalBut(item.getType())) return null;
		Itemable<?> result;
		for (Function<ItemStack,Itemable<?>> check : DISABLED_ITEMS) if ((result = check.apply(item)) != null) return result;
		Supplier<Itemable<?>> info = DISABLED_MATERIALS.get(item.getType());
		return info != null ? info.get() : getLegalItemableStack(item);
	}
	
	@Nullable
	@Contract("null -> null")
	public static Itemable<?> ofOrSubstitute(Material material) {
		if (!legalBut(material)) return null;
		Supplier<Itemable<?>> info = DISABLED_MATERIALS.get(material);
		return info != null ? info.get() : getLegalItemableStack(new ItemStack(material));
	}
	
	@Nullable
	@Contract("null -> null")
	public static ItemableStack of(Material material) {
		return legal(material) ? new ItemableStack(new ItemStack(material)) : null;
	}
	
	@NotNull
	public static Map<@NotNull String,@NotNull Integer> getEnchantments(@NotNull Map<@NotNull Enchantment,@NotNull Integer> enchantments) {
		Map<String,Integer> enchants = new HashMap<>();
		for (Map.Entry<Enchantment,Integer> entry : enchantments.entrySet()) if (entry.getValue() > 0) enchants.put(entry.getKey().getKey().getKey(),entry.getValue());
		return enchants;
	}
	
	@NotNull
	public static Map<@NotNull Enchantment,@NotNull Integer> getEnchantments(Object obj) {
		HashMap<Enchantment,Integer> enchants = new HashMap<>();
		if (obj != null) try {
			List<?> enchantments = (List<?>) obj;
			for (Object o : enchantments) Utils.runNoException(() ->  {
				Map<?,?> enchantment = (Map<?,?>) o;
				Enchantment enchant = Utils.getEnchantment(Utils.getString(enchantment.get("id")));
				Integer level = Utils.getInteger(enchantment.get("lvl"));
				if (enchant != null && level != null && level >= enchant.getStartLevel()) enchants.putIfAbsent(enchant,level);
			});
		} catch (Exception e) {
			Utils.runNoException(() -> {
				Map<?,?> enchantments = (Map<?,?>) obj;
				for (Map.Entry<?,?> entry : enchantments.entrySet()) Utils.runNoException(() ->  {
					String name = Objects.requireNonNull(Utils.getString(entry.getKey()));
					Integer level = Utils.getInteger(entry.getValue());
					Enchantment enchant = Objects.requireNonNull(Enchantment.getByKey(NamespacedKey.minecraft(name)));
					if (level != null && level >= enchant.getStartLevel()) enchants.putIfAbsent(enchant,level);
				});
			});
		}
		return enchants;
	}
	
	@NotNull
	public static List<@NotNull Map<@NotNull String,@NotNull String>> getPatterns(@NotNull List<@NotNull Pattern> patterns) {
		return patterns.stream().map(pattern -> Map.of("Pattern",pattern.getPattern().name(),"Color",pattern.getColor().name())).toList();
	}
	
	@NotNull
	public static List<@NotNull Pattern> getPatterns(Object obj) {
		List<@NotNull Pattern> patterns = new ArrayList<>();
		if (obj != null) Utils.runNoException(() -> {
			List<?> list = (List<?>) obj;
			for (Object o : list) Utils.runNoException(() ->  {
				Map<?,?> map = (Map<?,?>) o;
				patterns.add(new Pattern(DyeColor.valueOf(Utils.applyNotNull(Utils.fixKey(Utils.getString(map.get("Color"))),String::toUpperCase)),PatternType.valueOf(Utils.applyNotNull(Utils.fixKey(Utils.getString(map.get("Pattern"))),String::toUpperCase))));
			});
		});
		return patterns;
	}
	
	@NotNull
	public static ItemFlag @NotNull [] getFlags(int num) {
		List<@NotNull Boolean> bits = new ArrayList<>();
		for (int i = 0; i <= 7; i++) bits.add(((num >> i) & 1) == 1);
		ItemFlag[] itemFlags = ItemFlag.values();
		List<ItemFlag> flags = new ArrayList<>();
		for (int i = 0; i < bits.size() && i < itemFlags.length; i++) if (bits.get(i)) flags.add(itemFlags[i]);
		return flags.toArray(new ItemFlag[0]);
	}
	
	@Nullable
	@Contract("null,_ -> null")
	public static Itemable<?> ofOrSubstitute(Material material,@Nullable Map<String,?> arguments) {
		Supplier<Itemable<?>> info = DISABLED_MATERIALS.get(material);
		return info != null ? info.get() : of(material,arguments);
	}
	
	@Nullable
	@Contract("null,_ -> null")
	public static ItemableStack of(Material material,@Nullable Map<String,?> arguments) {
		if (!legal(material)) return null;
		ItemStack item = new ItemStack(material);
		if (arguments == null) return new ItemableStack(item);
		if (material == Material.ENCHANTED_BOOK) return new ItemableStack(Utils.addEnchantments(item,getEnchantments(arguments.get("Enchantments"))));
		ItemMeta meta = item.getItemMeta();
		Integer damage = Utils.getInteger(arguments.get("Damage"));
		if (damage != null) {
			if (damage >= material.getMaxDurability()) return new ItemableStack(item);
			else if (damage >= 0) Utils.runNoException(() -> ((Damageable) meta).setDamage(damage));
		}
		Utils.runNotNull(Utils.mapToComponent(arguments.get("Name")),name -> meta.displayName(Utils.noItalic(name)));
		Utils.runNotNull(Utils.mapToListComponent(arguments.get("Lore")),meta::lore);
		Utils.runNotNull(Utils.getBoolean(arguments.get("Unbreakable")),meta::setUnbreakable);
		Utils.runNotNullIf(Utils.getInteger(arguments.get("Model")),meta::setCustomModelData,model -> model > 0);
		String skin = Utils.getString(arguments.get("Skin"));
		if (skin != null && material == Material.PLAYER_HEAD) Utils.setSkin((SkullMeta) meta,skin,null);
		meta.addItemFlags(getFlags(Utils.thisOrThatOrNull(Utils.getInteger(arguments.get("HideFlags")),0)));
		if (Tags.LEATHER.contains(material)) ((LeatherArmorMeta) meta).setColor(Utils.getColor(Utils.getString(arguments.get("Color"))));
		else if (material == Material.SHIELD || material.name().endsWith("_BANNER")) Utils.setPatterns(meta,getPatterns(arguments.get("Patterns")));
		item.setItemMeta(meta);
		item.setAmount(Math.max(1,Utils.thisOrThatOrNull(Utils.getInteger(arguments.get("Amount")),1)));
		item = Utils.addEnchantments(item,getEnchantments(arguments.get("Enchantments")));
		if (arguments.get("Restrictions") instanceof List<?> restrictions) Restrictions.addRestrictions(item,restrictions.stream().map(Utils::getString).map(Restrictions::byName).filter(Objects::nonNull).toList());
		return new ItemableStack(item);
	}
	
	@Nullable
	@Contract("null -> null")
	public static ItemableStack of(@Nullable Map<String,?> arguments) {
		if (arguments == null) return null;
		String material = Utils.getString(arguments.get("Material"));
		return material == null ? null : of(Utils.getMaterial(material),arguments);
	}
	
	@Nullable
	@Contract("null -> null")
	public static Itemable<?> ofOrSubstitute(@Nullable Map<String,?> arguments) {
		if (arguments == null) return null;
		String material = Utils.getString(arguments.get("Material"));
		return material == null ? null : ofOrSubstitute(Utils.getMaterial(material),arguments);
	}
	
	@Override
	public @NotNull Map<@NotNull String,Object> toMap() {
		HashMap<String,Object> map = new HashMap<>();
		map.put("Material",item.getType().name());
		if (item.getType() == Material.ENCHANTED_BOOK) {
			Utils.runNotNullIf(Utils.applyNotNull(Utils.getStoredEnchants(item),ItemableStack::getEnchantments),enchants -> map.put("Enchantments",enchants),enchants -> !enchants.isEmpty());
			return map;
		}
		ItemMeta meta = item.getItemMeta();
		Utils.runNotNull(Utils.mapComponent(meta.displayName()),name -> map.put("Name",name));
		if (item.getType().getMaxDurability() > 0) Utils.runNoException(() ->  {
			int damage = ((Damageable) meta).getDamage();
			if (damage > 0) map.put("Damage",damage);
		});
		else Utils.runNotNullIf(Utils.applyNotNull(meta.lore(),lore -> lore.stream().map(Utils::mapComponent).toList()),lore -> map.put("Lore",lore),lore -> !lore.isEmpty());
//		List<Component> originalLore = meta.lore();
//		if (originalLore != null) {
//			originalLore = new ArrayList<>(originalLore);
//			Component line;
//			Integer j = null;
//			for (int i = 0; i < originalLore.size() - 1; i++) {
//				line = originalLore.get(i);
//				if (!(line instanceof TextComponent text) || !text.content().isEmpty()) continue;
//				line = originalLore.get(i + 1);
//				if (!(line instanceof TranslatableComponent translate) || !translate.key().equalsIgnoreCase("item.durability")) continue;
//				j = i;
//				break;
//			}
//			if (j != null) {
//				originalLore.remove(j + 1);
//				originalLore.remove((int) j);
//			}
//		}
		if (item.getType() == Material.PLAYER_HEAD) Utils.runNoException(() -> map.put("Skin",Objects.requireNonNull(Utils.getSkin(Objects.requireNonNull(Utils.getProfile((SkullMeta) meta)))).first()));
		if (meta.isUnbreakable()) map.put("Unbreakable",true);
		int model;
		if (meta.hasCustomModelData() && (model = meta.getCustomModelData()) > 0) map.put("Model",model);
		Color color;
		if (Tags.LEATHER.contains(item.getType())) if ((color = ((LeatherArmorMeta) meta).getColor()) != DEFAULT_LEATHER_COLOR) map.put("Color",color.asRGB());
		if (item.getMaxStackSize() > 1 && amount() > 1) map.put("Amount",amount());
		Utils.runNotNullIf(meta.getItemFlags().stream().map(flag -> Math.pow(2,flag.ordinal())).mapToInt(Double::intValue).sum(),sum -> map.put("HideFlags",sum.shortValue()),sum -> sum > 0);
		Utils.runNotNullIf(getEnchantments(item.getEnchantments()),enchants -> map.put("Enchantments",enchants),enchants -> !enchants.isEmpty());
		Utils.runNotNullIf(Utils.applyNotNull(Utils.getPatterns(item),ItemableStack::getPatterns),patterns -> map.put("Patterns",patterns),patterns -> !patterns.isEmpty());
		Utils.runNotNullIf(Restrictions.getRestrictions(meta).stream().map(Restrictions.Restriction::name).toList(),restrict -> map.put("Restrictions",restrict),restrict -> !restrict.isEmpty());
		return map;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof ItemableStack item) && canPassAsThis(item);
	}
	
	@Override
	public boolean canPassAsThis(@NotNull Itemable<?> item) {
		return item.material() == material() && Utils.sameItem(asItem(),item.asItem());
	}
	
	public boolean similar(Object obj,boolean ignoreDurability,boolean ignoreFlags) {
		return (obj instanceof ItemableStack other) && material() == other.material() && Utils.similarItem(item,other.item,ignoreDurability,ignoreFlags);
	}
	
	public boolean similar(Object obj) {
		return similar(obj,true,true);
	}
	
	@NotNull
	public String mappableKey() {
		return "item";
	}
	
	@NotNull
	public ItemableStack copy() {
		return new ItemableStack(item);
	}
	
	@NotNull
	@Contract(value = "_ -> new",pure = true)
	public ItemableStack copy(@Positive int amount) {
		return Utils.runGetOriginal(copy(),copy -> copy.item.setAmount(Math.min(amount,maxStackSize())));
	}
	
	@Nullable
	public ItemableStack copy(Material material) {
		return legal(material) ? Utils.runGetOriginal(new ItemableStack(item),item -> item.item.setType(material)) : null;
	}
	
	@NotNull
	public Component giveComponent() {
		Component displayName = item.getItemMeta().displayName();
		return (displayName == null ? Utils.noItalic(Component.translatable(item.getType().translationKey(),NamedTextColor.WHITE)) : displayName).
				append(Utils.noItalic(Component.text(" x" + amount(),NamedTextColor.WHITE)));
	}
	
	@Positive
	public int amount() {
		return item.getAmount();
	}
}