package me.DMan16.POPUtils.Items;

import me.DMan16.POPUtils.Interfaces.Listener;
import me.DMan16.POPUtils.POPUtilsMain;
import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class POPItems implements Listener {
	private final static NamespacedKey POPITEM_KEY = new NamespacedKey(POPUtilsMain.getInstance(),"popitem");
	private static final HashMap<@NotNull String,@NotNull InteractableItem> ITEMS = new HashMap<>();
	private static POPItems INSTANCE = null;
	
	public static void start() {
		if (INSTANCE == null) {
			INSTANCE = new POPItems();
			INSTANCE.register(POPUtilsMain.getInstance());
		}
	}
	
	public static boolean create(@NotNull InteractableItem item) {
		return ITEMS.putIfAbsent(item.key(),item) == null;
	}
	
	@Nullable
	public static InteractableItem get(@NotNull String key) {
		return ITEMS.get(Utils.fixKey(key));
	}
	
	public static boolean exists(@NotNull String key) {
		return ITEMS.containsKey(Utils.fixKey(key));
	}
	
	@Nullable
	public static InteractableItem get(ItemStack item) {
		return ITEMS.get(Utils.getKeyPersistentDataContainer(item,POPITEM_KEY,PersistentDataType.STRING));
	}
	
	@Nullable
	public static InteractableItem get(ItemMeta meta) {
		return ITEMS.get(Utils.getKeyPersistentDataContainer(meta,POPITEM_KEY,PersistentDataType.STRING));
	}
	
	public static ItemStack set(ItemStack original, @NotNull String key) {
		return set(original,key,false);
	}
	
	public static ItemStack set(ItemStack original, @NotNull String key, boolean force) {
		InteractableItem item = get(key);
		return item == null ? original : Utils.setKeyPersistentDataContainer(original,POPITEM_KEY,PersistentDataType.STRING,item.key());
	}
	
	public static ItemMeta set(ItemMeta meta, @NotNull String key) {
		return set(meta,key,false);
	}
	
	public static ItemMeta set(ItemMeta meta, @NotNull String key, boolean force) {
		InteractableItem item = get(key);
		return item == null ? meta : Utils.setKeyPersistentDataContainer(meta,POPITEM_KEY,PersistentDataType.STRING,item.key(),force);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteractInteractableItem(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_AIR &&
				(event.useItemInHand() == Event.Result.DENY || event.useInteractedBlock() == Event.Result.DENY)) return;
		InteractableItem item = get(event.getItem());
		if (item != null) {
			item.rightClick(event);
			item.leftClick(event);
		}
	}
}