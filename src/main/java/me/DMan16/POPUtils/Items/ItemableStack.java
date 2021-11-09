package me.DMan16.POPUtils.Items;

import me.DMan16.POPUtils.Enums.Tags;
import me.DMan16.POPUtils.Interfaces.Itemable;
import me.DMan16.POPUtils.Restrictions.Restrictions;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ItemableStack implements Itemable<ItemableStack> {
	private static final Color DEFAULT_LEATHER_COLOR = ((LeatherArmorMeta) new ItemStack(Material.LEATHER_HELMET).getItemMeta()).getColor();
	
	private final ItemStack item;
	
	private ItemableStack(@NotNull ItemStack item) {
		this.item = item;
	}
	
	@NotNull
	public ItemStack asItem() {
		return item.clone();
	}
	
	@Nullable
	@Contract("null -> null")
	public static ItemableStack of(ItemStack item) {
		if (Utils.isNull(item) || !item.getType().isItem()) return null;
		ItemableStack stack = new ItemableStack(item);
		return Utils.sameItem(item,stack.item) ? stack : null;
	}
	
	@NotNull
	@SuppressWarnings("deprecation")
	private static Map<@NotNull Enchantment,@NotNull Integer> getEnchantments(Object obj) {
		HashMap<Enchantment,Integer> enchants = new HashMap<>();
		Enchantment enchant;
		String key;
		Integer level;
		if (obj != null) try {
			List<?> enchantments = (List<?>) obj;
			Map<?,?> enchantment;
			for (Object o : enchantments) try {
				enchantment = (Map<?,?>) o;
				enchant = Enchantment.getByName(Utils.getString(enchantment.get("id")));
				level = Utils.getInteger(enchantment.get("lvl"));
				if (enchant != null && level != null && level > 0 && level >= enchant.getStartLevel()) enchants.putIfAbsent(enchant,level);
			} catch (Exception e1) {}
		} catch (Exception e) {
			Map<?,?> enchantments = (Map<?,?>) obj;
			for (Map.Entry<?,?> entry : enchantments.entrySet()) try {
				enchant = Enchantment.getByName(Utils.getString(entry.getKey()));
				level = Utils.getInteger(entry.getValue());
				if (enchant != null && level != null && level > 0 && level >= enchant.getStartLevel()) enchants.putIfAbsent(enchant,level);
			} catch (Exception e2) {}
		}
		return enchants;
	}
	
	@NotNull
	private static ItemFlag @NotNull [] getFlags(int num) {
		List<@NotNull Boolean> bits = new ArrayList<>();
		for (int i = 0; i <= 7; i++) bits.add(((num >> i) & 1) == 1);
		ItemFlag[] itemFlags = ItemFlag.values();
		List<ItemFlag> flags = new ArrayList<>();
		for (int i = 0; i < bits.size() && i < itemFlags.length; i++) if (bits.get(i)) flags.add(itemFlags[i]);
		return flags.toArray(new ItemFlag[0]);
	}
	
	@Nullable
	@Contract("null,_ -> null")
	public static ItemableStack of(Material material, @Nullable Map<String,?> arguments) {
		if (Utils.isNull(material) || !material.isItem()) return null;
		ItemStack item = new ItemStack(material);
		if (arguments == null) return new ItemableStack(item);
		Integer damage = Utils.getInteger(arguments.get("Damage"));
		ItemMeta meta = item.getItemMeta();
		if (damage != null) {
			if (damage >= material.getMaxDurability()) return new ItemableStack(item);
			else if (damage >= 0) ((Damageable) meta).setDamage(damage);
		}
		meta.displayName(Utils.mapToComponent(arguments.get("Name")));
		meta.lore(Utils.mapToListComponent(arguments.get("Lore")));
		meta.setUnbreakable(Utils.thisOrThatOrNull(Utils.getBoolean(arguments.get("Unbreakable")),false));
		meta.setCustomModelData(Utils.getInteger(arguments.get("Model")));
		String skin = Utils.getString(arguments.get("Skin"));
		if (skin != null && material == Material.PLAYER_HEAD) Utils.setSkin((SkullMeta) meta,skin,null);
		meta.addItemFlags(getFlags(Utils.thisOrThatOrNull(Utils.getInteger(arguments.get("HideFlags")),0)));
		if (Tags.LEATHER.contains(material)) ((LeatherArmorMeta) meta).setColor(Utils.getColor(Utils.getString(arguments.get("Color"))));
		item.setItemMeta(meta);
		item.setAmount(Utils.thisOrThatOrNull(Utils.getInteger(arguments.get("Amount")),1));
		item.addUnsafeEnchantments(getEnchantments(arguments.get("Enchantments")));
		try {
			Restrictions.addRestrictions(item,
					((List<?>) arguments.get("Restrictions")).stream().map(Utils::getString).filter(Objects::nonNull).map(Restrictions::byName).filter(Objects::nonNull).toList());
		} catch (Exception e) {}
		return new ItemableStack(item);
	}
	
	@Nullable
	@Contract("null -> null")
	public static ItemableStack of(@Nullable Map<String,?> arguments) {
		if (arguments == null) return null;
		String material = Utils.getString(arguments.get("Material"));
		return material == null ? null : of(Material.getMaterial(material),arguments);
	}
	
	@Override
	@NotNull
	public Map<@NotNull String,?> toMap() {
		HashMap<String,Object> map = new HashMap<>();
		map.put("Material",item.getType().name());
		ItemMeta meta = item.getItemMeta();
		List<HashMap<String,?>> name = Utils.mapComponent(meta.displayName());
		if (name != null) map.put("Name",name);
		List<List<HashMap<@NotNull String,?>>> lore = Utils.thisOrThatOrNull(meta.lore(), new ArrayList<Component>()).stream().map(Utils::mapComponent).toList();
		if (!lore.isEmpty()) map.put("Name",lore);
		if (item.getType() == Material.PLAYER_HEAD) try {
			map.put("Skin",Objects.requireNonNull(Utils.getSkin(Objects.requireNonNull(Utils.getProfile((SkullMeta) meta)))).first());
		} catch (Exception e) {}
		if (meta.isUnbreakable()) map.put("Unbreakable",true);
		int model;
		if (meta.hasCustomModelData() && (model = meta.getCustomModelData()) > 0) map.put("Model",model);
		Color color;
		if (Tags.LEATHER.contains(item.getType())) if ((color = ((LeatherArmorMeta) meta).getColor()) != DEFAULT_LEATHER_COLOR) map.put("Color",color.asRGB());
		if (item.getAmount() > 1) map.put("Amount",item.getAmount());
		int flags = 0;
		for (ItemFlag flag : meta.getItemFlags()) flags += Math.pow(2,flag.ordinal());
		if (flags > 0) map.put("HideFlags",(short) flags);
		if (((Damageable) meta).getDamage() > 0) map.put("Damage",((Damageable) meta).getDamage());
		Map<String,Integer> enchantments = new HashMap<>();
		for (Map.Entry<Enchantment,Integer> entry : meta.getEnchants().entrySet()) if (entry.getValue() > 0) enchantments.put(entry.getKey().getKey().getKey(),entry.getValue());
		if (!enchantments.isEmpty()) map.put("Enchantments",enchantments);
		List<String> restrictions = Restrictions.getRestrictions(meta).stream().map(Restrictions.Restriction::name).toList();
		if (!restrictions.isEmpty()) map.put("Restrictions",restrictions);
		return map;
	}
	
	@NotNull
	public String ItemableKey() {
		return "item";
	}
	
	@NotNull
	public ItemableStack copy() {
		return new ItemableStack(item.clone());
	}
}