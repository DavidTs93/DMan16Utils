package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.Classes.Pairs.Pair;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class ItemUtils {
	private static final LinkedHashMap<@NotNull String,@NotNull ItemableInfo<?>> MAP = new LinkedHashMap<>();
	private static final LinkedHashMap<@NotNull Class<?>,@NotNull String> CLASS_MAP = new LinkedHashMap<>();
	
	public static <V extends Itemable<?>> boolean registerItemable(@NotNull String key,@NotNull ItemableInfo<V> info) {
		key = Utils.fixKey(key);
		if (key == null || MAP.containsKey(key) || CLASS_MAP.containsKey(info.getMappableClass())) return false;
		MAP.put(key,info);
		CLASS_MAP.put(info.getMappableClass(),key);
		key = CLASS_MAP.remove(ItemableStack.class);
		if (key != null) {
			MAP.put(key,Objects.requireNonNull(MAP.remove(key)));
			CLASS_MAP.put(ItemableStack.class,key);
		}
		return true;
	}
	
	@NotNull
	@Unmodifiable
	public static Set<@NotNull String> getRegisteredItemablesKeys() {
		return Collections.unmodifiableSet(MAP.keySet());
	}
	
	@Nullable
	private static <V extends Itemable<?>> V of(@Nullable ItemableInfo<V> info,@Nullable Map<String,?> arguments) {
		return info == null ? null : info.fromArguments(arguments);
	}
	
	@Nullable
	private static Itemable<?> ofOrSubstitute(@Nullable ItemableInfo<?> info,@Nullable Map<String,?> arguments) {
		return info == null ? null : (info.getMappableClass() == ItemableStack.class ? ItemableStack.ofOrSubstitute(arguments) : info.fromArguments(arguments));
	}
	
	@Nullable
	public static Itemable<?> of(@NotNull String key,@Nullable Map<String,?> arguments) {
		return of(MAP.get(Utils.fixKey(key)),arguments);
	}
	
	@Nullable
	public static Itemable<?> ofOrSubstitute(@NotNull String key,@Nullable Map<String,?> arguments) {
		return ofOrSubstitute(MAP.get(Utils.fixKey(key)),arguments);
	}
	
	@Nullable
	private static Itemable<?> of(@Nullable Pair<@NotNull String,@Nullable Map<String,?>> keyAndMap) {
		return keyAndMap == null ? null : of(keyAndMap.first(),keyAndMap.second());
	}
	
	@Nullable
	private static Itemable<?> ofOrSubstitute(@Nullable Pair<@NotNull String,@Nullable Map<String,?>> keyAndMap) {
		return keyAndMap == null ? null : ofOrSubstitute(keyAndMap.first(),keyAndMap.second());
	}
	
	@Nullable
	private static Pair<@NotNull String,@Nullable Map<String,?>> keyAndMap(@NotNull String str) {
		if (str.isEmpty()) return null;
		String key = null,arguments = null;
		if (str.contains(":")) {
			String[] arr = str.split(":",2);
			if (arr.length == 2) {
				key = arr[0];
				arguments = arr[1].isEmpty() ? null : arr[1];
			}
		} else key = str;
		return key == null ? null : Pair.of(key,Utils.getMapFromJSON(arguments));
	}
	
	@Nullable
	@SuppressWarnings("unchecked")
	@Contract("null,_ -> null")
	public static <V extends Itemable<?>> V of(@Nullable String str,@NotNull Class<V> clazz) {
		if (str == null) return null;
		Material material = Utils.getMaterial(str);
		if (material != null) return clazz == ItemableStack.class ? (V) ItemableStack.of(material) : null;
		Pair<String,Map<String,?>> keyAndMap = keyAndMap(str);
		try {
			if (keyAndMap == null) return (V) MAP.get(CLASS_MAP.get(clazz)).fromItem((ItemStack) Objects.requireNonNull(Utils.ObjectFromBase64(str)));
			ItemableInfo<?> info = MAP.get(Utils.fixKey(keyAndMap.first()));
			return info != null && info.getMappableClass().equals(clazz) ? (V) of(info,keyAndMap.second()) : null;
		} catch (Exception e) {}
		return null;
	}
	
	@Nullable
	@Contract("null -> null")
	public static Itemable<?> of(@Nullable String str) {
		if (str == null) return null;
		Material material = Utils.getMaterial(str);
		return material != null ? ItemableStack.of(material,null) : of(keyAndMap(str));
	}
	
	@Nullable
	@Contract("null -> null")
	public static Itemable<?> ofOrSubstitute(@Nullable String str) {
		if (str == null) return null;
		Material material = Utils.getMaterial(str);
		return material != null ? ItemableStack.ofOrSubstitute(material) : ofOrSubstitute(keyAndMap(str));
	}
	
	@Nullable
	@Contract("null -> null")
	public static Itemable<?> ofOrHolder(@Nullable String str) {
		if (str == null) return null;
		Material material = Utils.getMaterial(str);
		if (material != null) return ItemableStack.of(material,null);
		Itemable<?> itemable = of(keyAndMap(str));
		if (itemable != null) return itemable;
		ItemStack item = (ItemStack) Utils.ObjectFromBase64(str);
		return Utils.isNull(item) ? null : new ItemHolder(item);
	}
	
	@Nullable
	@Contract("null -> null")
	public static Itemable<?> ofOrSubstituteOrHolder(@Nullable String str) {
		if (str == null) return null;
		Material material = Utils.getMaterial(str);
		if (material != null) return ItemableStack.ofOrSubstitute(material,null);
		Itemable<?> itemable = ofOrSubstitute(keyAndMap(str));
		if (itemable != null) return itemable;
		ItemStack item = (ItemStack) Utils.ObjectFromBase64(str);
		return Utils.isNull(item) ? null : new ItemHolder(item);
	}
	
	@Nullable
	@SuppressWarnings("unchecked")
	public static <V extends Itemable<?>> V of(@NotNull Class<V> clazz,@Nullable Map<String,?> arguments) {
		String key = CLASS_MAP.get(clazz);
		if (key != null) try {
			return (V) of(key,arguments);
		} catch (Exception e) {}
		return null;
	}
	
	@Nullable
	@SuppressWarnings("unchecked")
	public static <V> V off(@NotNull Class<V> clazz,@Nullable Map<String,?> arguments) {
		String key = CLASS_MAP.get(clazz);
		if (key != null) try {
			return (V) of(key,arguments);
		} catch (Exception e) {e.printStackTrace();}
		else Utils.chatColorsLogPlugin("No class found?!");
		return null;
	}
	
	@Nullable
	@Contract("null,_ -> null; _,null -> null")
	private static <V extends Itemable<?>> V of(@Nullable ItemableInfo<V> info,@Nullable ItemStack item) {
		return info == null || item == null ? null : info.fromItem(item);
	}
	
	@Nullable
	@Contract("_,null -> null")
	public static Itemable<?> of(@NotNull String key,@Nullable ItemStack item) {
		return of(MAP.get(Utils.fixKey(key)),item);
	}
	
	@Nullable
	@Contract("null -> null")
	public static Itemable<?> of(@Nullable ItemStack item) {
		Itemable<?> itemable;
		if (item != null) for (ItemableInfo<?> info : MAP.values()) if ((itemable = info.fromItem(item)) != null) return itemable;
		return null;
	}
	
	@Nullable
	@Contract("null -> null; !null -> !null")
	public static Itemable<?> ofOrHolder(@Nullable ItemStack item) {
		if (item == null) return null;
		Itemable<?> itemable;
		for (ItemableInfo<?> info : MAP.values()) if ((itemable = info.fromItem(item)) != null) return itemable;
		return new ItemHolder(item);
	}
	
	@Nullable
	@Contract("null -> null")
	public static Itemable<?> ofOrSubstitute(@Nullable ItemStack item) {
		Itemable<?> itemable;
		if (item != null) for (ItemableInfo<?> info : MAP.values()) if ((itemable = info.fromItem(item)) != null) return itemable;
		return ItemableStack.ofOrSubstitute(item);
	}
	
	@Nullable
	@Contract("null -> null; !null -> !null")
	public static Itemable<?> ofOrSubstituteOrHolder(@Nullable ItemStack item) {
		if (item == null) return null;
		Itemable<?> itemable;
		for (ItemableInfo<?> info : MAP.values()) if ((itemable = info.fromItem(item)) != null) return itemable;
		itemable = ItemableStack.ofOrSubstitute(item);
		return itemable == null ? new ItemHolder(item) : itemable;
	}
	
	@Nullable
	@Contract("_,null -> null")
	@SuppressWarnings("unchecked")
	public static <V extends Itemable<?>> V of(@NotNull Class<V> clazz,@Nullable ItemStack item) {
		String key = CLASS_MAP.get(clazz);
		if (key != null) try {
			return (V) of(key,item);
		} catch (Exception e) {e.printStackTrace();}
		return null;
	}
}