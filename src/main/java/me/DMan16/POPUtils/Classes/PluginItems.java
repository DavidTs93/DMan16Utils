package me.DMan16.POPUtils.Classes;

import me.DMan16.POPUtils.POPUtils;
import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PluginItems {
	private final @Unmodifiable List<@NotNull ItemInitializerInfo> infos;
	private HashMap<@NotNull String,@Nullable ItemStack> map;
	private BukkitTask task;
	
	public PluginItems(@NotNull ItemInitializerInfo ... infos) {
		this(Arrays.asList(infos));
	}
	
	public PluginItems(@NotNull List<@NotNull ItemInitializerInfo> infos) {
		Utils.initializeItemDatabase(infos);
		this.infos = Collections.unmodifiableList(infos);
		task = null;
	}
	
	public PluginItems reload() {
		if (task != null) task.cancel();
		map = Utils.getItemsDatabase(infos);
		task = new BukkitRunnable() {
			public void run() {
				reload();
			}
		}.runTaskLater(POPUtils.getInstance(),10 * 20);
		return this;
	}
	
	@Nullable
	public ItemStack getItem(@NotNull String name) {
		ItemStack item = map.get(name.toLowerCase());
		return item == null ? null : item.clone();
	}
}