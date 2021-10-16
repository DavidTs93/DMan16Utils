package me.DMan16.POPUtils.Utils;

import me.DMan16.POPUtils.Classes.POPItem;
import me.DMan16.POPUtils.POPUtilsMain;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class POPItems {
	private final static NamespacedKey POPITEM_KEY = new NamespacedKey(POPUtilsMain.getInstance(),"popitem");
	private static final HashMap<@NotNull String,@NotNull POPItem> ITEMS = new HashMap<>();
	
	public static boolean create(@NotNull POPItem item) {
		return ITEMS.putIfAbsent(item.key(),item) == null;
	}
	
	@Nullable
	public static POPItem get(@NotNull String key) {
		return ITEMS.get(Utils.fixKey(key));
	}
	
	public static boolean exists(@NotNull String key) {
		return ITEMS.containsKey(Utils.fixKey(key));
	}
	
	@Nullable
	public static POPItem get(ItemStack item) {
		return ITEMS.get(Utils.getKeyPersistentDataContainer(item,POPITEM_KEY,PersistentDataType.STRING));
	}
	
	@Nullable
	public static POPItem get(ItemMeta meta) {
		return ITEMS.get(Utils.getKeyPersistentDataContainer(meta,POPITEM_KEY,PersistentDataType.STRING));
	}
	
	public static ItemStack set(ItemStack original, @NotNull String key) {
		return set(original,key,false);
	}
	
	public static ItemStack set(ItemStack original, @NotNull String key, boolean force) {
		POPItem item = get(key);
		return item == null ? original : Utils.setKeyPersistentDataContainer(original,POPITEM_KEY,PersistentDataType.STRING,item.key());
	}
	
	public static ItemMeta set(ItemMeta meta, @NotNull String key) {
		return set(meta,key,false);
	}
	
	public static ItemMeta set(ItemMeta meta, @NotNull String key, boolean force) {
		POPItem item = get(key);
		return item == null ? meta : Utils.setKeyPersistentDataContainer(meta,POPITEM_KEY,PersistentDataType.STRING,item.key(),force);
	}
}