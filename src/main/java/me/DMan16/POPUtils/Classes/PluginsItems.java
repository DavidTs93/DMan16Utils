package me.DMan16.POPUtils.Classes;

import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PluginsItems {
	private static final HashMap<@NotNull String,@Nullable ItemStack> map = new HashMap<>();
	
	public PluginsItems(@NotNull TreeMap<String,String> map) {
	
	}
	
	public static void add(@NotNull String key, ItemStack item) {
		if (!Utils.isNull(item)) if (map.putIfAbsent(key,item) != null) throw new IllegalArgumentException("Failed to create \"" + key + "\" PluginItem!");
	}
	
	public static void add(ItemInitializerInfo info) {
		if (info == null) return;
		ItemStack item = null;
		if (info.skin() != null) {
			item = Utils.makeItem(Material.PLAYER_HEAD,info.name(),ItemFlag.values());
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			if (Utils.setSkin(meta,info.skin(),null)) item.setItemMeta(meta);
		}
		if (item == null) item = Utils.makeItem(info.material(),info.name(),info.model(),ItemFlag.values());
		if (info.alterItem() != null && !Utils.isNull(item)) item = info.alterItem().apply(item);
		add(info.key(),item);
	}
	
	@NotNull
	public static ItemStack getItem(@NotNull String name) {
		ItemStack item = map.get(name.toLowerCase());
		if (item == null) throw new IllegalArgumentException("Item \"" + name + "\" not found!");
		return item.clone();
	}
}