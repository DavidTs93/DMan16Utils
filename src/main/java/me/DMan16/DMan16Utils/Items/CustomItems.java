package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Interfaces.Listener;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public final class CustomItems implements Listener {
	private static CustomItems INSTANCE = null;
	
	private final NamespacedKey keyCustomItem;
	private final HashMap<@NotNull String,@NotNull InteractableItem> items;
	
	private CustomItems() {
		keyCustomItem = new NamespacedKey(DMan16UtilsMain.getInstance(),"custom_item");
		items = new HashMap<>();
	}
	
	public static void init() {
		if (INSTANCE != null) return;
		INSTANCE = new CustomItems();
		INSTANCE.register(DMan16UtilsMain.getInstance());
	}
	
	public static boolean create(@NotNull InteractableItem item) {
		return INSTANCE.items.putIfAbsent(item.key(),item) == null;
	}
	
	@Nullable
	public static InteractableItem get(@NotNull String key) {
		return INSTANCE.items.get(Utils.fixKey(key));
	}
	
	public static boolean exists(@NotNull String key) {
		return INSTANCE.items.containsKey(Utils.fixKey(key));
	}
	
	@Nullable
	public static InteractableItem get(ItemStack item) {
		return INSTANCE.items.get(Utils.getKeyPersistentDataContainer(item,INSTANCE.keyCustomItem,PersistentDataType.STRING));
	}
	
	@Nullable
	public static InteractableItem get(ItemMeta meta) {
		return INSTANCE.items.get(Utils.getKeyPersistentDataContainer(meta,INSTANCE.keyCustomItem,PersistentDataType.STRING));
	}
	
	public static ItemStack set(ItemStack original,@NotNull String key) {
		return set(original,key,false);
	}
	
	public static ItemStack set(ItemStack original,@NotNull String key,boolean force) {
		InteractableItem item = get(key);
		return item == null ? original : Utils.setKeyPersistentDataContainer(original,INSTANCE.keyCustomItem,PersistentDataType.STRING,item.key());
	}
	
	public static ItemMeta set(ItemMeta meta,@NotNull String key) {
		return set(meta,key,false);
	}
	
	public static ItemMeta set(ItemMeta meta,@NotNull String key,boolean force) {
		InteractableItem item = get(key);
		return item == null ? meta : Utils.setKeyPersistentDataContainer(meta,INSTANCE.keyCustomItem,PersistentDataType.STRING,item.key(),force);
	}
	
	@EventHandler
	public void onInteractInteractableItem(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_AIR && (event.useItemInHand() == Event.Result.DENY || event.useInteractedBlock() == Event.Result.DENY)) return;
		InteractableItem item = get(event.getItem());
		if (item == null) return;
		item.rightClick(event);
		item.leftClick(event);
	}
}