package me.DMan16.POPUtils.Menus;

import me.DMan16.POPUtils.Classes.Listener;
import me.DMan16.POPUtils.Interfaces.Menu;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public abstract class ListenerInventory implements Listener,Menu {
	protected static final int LINE_SIZE = 9;
	private static final HashMap<@NotNull Player,@NotNull ListenerInventory> playerMenus = new HashMap<>();
	
	protected final Inventory inventory;
	protected final int size;
	protected boolean cancelCloseUnregister = false;
	private InventoryView view;
	
	protected ListenerInventory(@NotNull Inventory inv) {
		this.inventory = inv;
		size = inv.getSize();
	}
	
	@Nullable
	public static ListenerInventory getOpenInventory(@NotNull Player player) {
		return playerMenus.get(player);
	}
	
	@Override
	public final void unregister() {
		HandlerList.unregisterAll(this);
		playerMenus.values().remove(this);
	}
	
	protected final void open(@NotNull JavaPlugin plugin, @NotNull Player player) {
		register(plugin);
		view = player.openInventory(inventory);
		playerMenus.put(player,this);
	}
	
	protected final void openInventory(@NotNull Player player) {
		view = player.openInventory(inventory);
	}
	
	protected InventoryView view() {
		return view;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void unregisterOnClose(InventoryCloseEvent event) {
		if (event.getView().getTopInventory().equals(inventory) && !cancelCloseUnregister) unregister();
	}
	
	@EventHandler(ignoreCancelled = true)
	public void unregisterOnLeaveEvent(PlayerQuitEvent event) {
		if ((inventory.getHolder() instanceof OfflinePlayer) && event.getPlayer().getUniqueId().equals(((OfflinePlayer) inventory.getHolder()).getUniqueId()))
			unregister();
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onInventoryDrag(InventoryDragEvent event) {
		if (event.getView().getTopInventory().equals(inventory)) for (int slot : event.getRawSlots()) if (slot < inventory.getSize()) {
			event.setCancelled(true);
			return;
		}
	}
	
	protected ItemStack itemClose() {
		return Utils.ITEMS.getItem("menu_close");
	}
	
	protected ItemStack itemNext() {
		return Utils.ITEMS.getItem("menu_next");
	}
	
	protected ItemStack itemPevious() {
		return Utils.ITEMS.getItem("menu_previous");
	}
	
	protected ItemStack itemOk() {
		return Utils.ITEMS.getItem("menu_ok");
	}
	
	protected ItemStack itemOkNo() {
		return Utils.ITEMS.getItem("menu_ok_no");
	}
	
	protected ItemStack itemCancel() {
		return Utils.ITEMS.getItem("menu_cancel");
	}
	
	protected ItemStack itemBack() {
		return Utils.ITEMS.getItem("menu_back");
	}
	
	protected ItemStack itemBorder() {
		return Utils.ITEMS.getItem("menu_border");
	}
	
	protected ItemStack itemInside() {
		return Utils.ITEMS.getItem("menu_inside");
	}
}