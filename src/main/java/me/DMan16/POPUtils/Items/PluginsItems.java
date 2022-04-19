package me.DMan16.POPUtils.Items;

import me.DMan16.POPUtils.Classes.PluginItemInitializerInfo;
import me.DMan16.POPUtils.Utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PluginsItems {
	private static final HashMap<@NotNull String,@NotNull PluginItemInitializerInfo> map = new HashMap<>();
	
	public static boolean add(@NotNull String key,@NotNull PluginItemInitializerInfo info) {
		key = Utils.fixKey(key);
		return key != null && map.putIfAbsent(key,info) == null;
	}
	
	@NotNull
	public static PluginItemInitializerInfo getItem(@NotNull String key) {
		PluginItemInitializerInfo item = map.get(Utils.fixKey(key));
		if (item == null) throw new IllegalArgumentException("Item \"" + key + "\" not found!");
		return item.copy();
	}
}