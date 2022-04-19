package me.DMan16.POPUtils.Menus;

import me.DMan16.POPUtils.Classes.BasicItemable;
import me.DMan16.POPUtils.Interfaces.Listener;
import me.DMan16.POPUtils.Items.PluginsItems;
import me.DMan16.POPUtils.Interfaces.Menu;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;

public abstract class ListenerInventory implements Listener,Menu {
	protected static final int LINE_SIZE = 9;
	private static final HashMap<@NotNull Player,@NotNull ListenerInventory> PLAYER_MENUS = new HashMap<>();
	
	private final Inventory inventory;
	protected final int size;
	protected boolean cancelCloseUnregister = false;
	private final HashMap<@NotNull Player,@NotNull InventoryView> playerViews;
	private boolean registered;
	
	protected ListenerInventory(@NotNull Inventory inv) {
		this.inventory = inv;
		this.size = inv.getSize();
		this.playerViews = new HashMap<>();
		this.registered = false;
	}
	
	protected void clear() {
		inventory.clear();
	}
	
	protected void clear(@NotNull Collection<@NotNull Integer> slots) {
		slots.forEach(inventory::clear);
	}
	
	protected void clear(int ... slots) {
		for (int slot : slots) inventory.clear(slot);
	}
	
	protected void subtract(int amount, int ... slots) {
		for (int slot : slots) setItem(slot,Utils.subtract(getItem(slot),amount));
	}
	
	protected void subtract(int amount, @NotNull Collection<@NotNull Integer> slots) {
		slots.forEach(slot -> setItem(slot,Utils.subtract(getItem(slot),amount)));
	}
	
	protected boolean isThisInventory(@NotNull Inventory inv) {
		return inv.equals(inventory);
	}
	
	protected void setItem(int slot, @Nullable ItemStack item) {
		if (legalSlot(slot)) inventory.setItem(slot,item);
	}
	
	public boolean legalSlot(int slot) {
		return slot >= 0 && slot < size;
	}
	
	@Nullable
	protected ItemStack getItem(int slot) {
		return inventory.getItem(slot);
	}
	
	@Nullable
	public static ListenerInventory getOpenInventory(@NotNull Player player) {
		return PLAYER_MENUS.get(player);
	}
	
	@Override
	public final void unregister() {
		if (registered) HandlerList.unregisterAll(this);
		PLAYER_MENUS.values().remove(this);
	}
	
	protected void close(boolean unregister, boolean cancelCloseUnregister) {
		if (unregister) {
			unregister();
			afterCloseUnregister(null);
			afterClose();
		} else if (cancelCloseUnregister) this.cancelCloseUnregister = true;
		inventory.close();
	}
	
	public void close(boolean cancelCloseUnregister) {
		close(false,cancelCloseUnregister);
	}
	
	public void close() {
		close(true,false);
	}
	
	protected void afterClose() {}
	
	protected final void open(@NotNull JavaPlugin plugin, @NotNull Player player) {
		if (!registered) {
			if (!register(plugin)) return;
			registered = true;
		}
		open(player);
	}
	
	@Nullable
	protected InventoryView open(@NotNull Player player) {
		if (!registered) return null;
		InventoryView view = openInventory(player);
		if (view == null) return null;
		PLAYER_MENUS.put(player,this);
		afterOpen(player,view);
		return view;
	}
	
	protected void afterOpen(@NotNull Player player, @NotNull InventoryView view) {}
	
	@Nullable
	protected final InventoryView openInventory(@NotNull Player player) {
		InventoryView view = player.openInventory(inventory);
		if (view == null) return null;
		playerViews.put(player,view);
		return view;
	}
	
	@Nullable
	protected InventoryView view(@NotNull Player player) {
		return playerViews.get(player);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void unregisterOnClose(InventoryCloseEvent event) {
		if (!isThisInventory(event.getView().getTopInventory())) return;
		if (!cancelCloseUnregister) {
			unregister();
			afterCloseUnregister(event);
		}
		afterClose();
	}
	
	protected void afterCloseUnregister(InventoryCloseEvent event) {}
	
	@EventHandler(ignoreCancelled = true)
	public void unregisterOnLeaveEvent(PlayerQuitEvent event) {
		if ((inventory.getHolder() instanceof OfflinePlayer) && event.getPlayer().getUniqueId().equals(((OfflinePlayer) inventory.getHolder()).getUniqueId())) {
			unregister();
			afterLeaveUnregister(event);
		}
	}
	
	protected void afterLeaveUnregister(PlayerQuitEvent event) {}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onInventoryDrag(InventoryDragEvent event) {
		if (isThisInventory(event.getView().getTopInventory())) for (int slot : event.getRawSlots()) if (slot < size) {
			event.setCancelled(true);
			return;
		}
	}
	
	protected BasicItemable itemClose() {
		return PluginsItems.getItem("menu_close");
	}
	
	protected BasicItemable itemNext() {
		return PluginsItems.getItem("menu_next");
	}
	
	protected BasicItemable itemPevious() {
		return PluginsItems.getItem("menu_previous");
	}
	
	protected BasicItemable itemOk() {
		return PluginsItems.getItem("menu_ok");
	}
	
	protected BasicItemable itemOkNo() {
		return PluginsItems.getItem("menu_ok_no");
	}
	
	protected BasicItemable itemCancel() {
		return PluginsItems.getItem("menu_cancel");
	}
	
	protected BasicItemable itemBack() {
		return PluginsItems.getItem("menu_back");
	}
	
	protected BasicItemable itemBorder() {
		return PluginsItems.getItem("menu_border");
	}
	
	protected BasicItemable itemInside() {
		return PluginsItems.getItem("menu_inside");
	}
	
	protected BasicItemable itemInsideDark() {
		return PluginsItems.getItem("menu_inside_dark");
	}
	
	@NotNull
	public static TranslatableComponent defaultMenuName(@NotNull String name,boolean bold) {
		return Utils.noItalic(bold ? Component.translatable(name,NamedTextColor.DARK_GREEN,TextDecoration.BOLD) : Component.translatable(name,NamedTextColor.DARK_GREEN));
	}
	
	@NotNull
	public static TranslatableComponent defaultMenuName(@NotNull String name) {
		return defaultMenuName(name,true);
	}
}