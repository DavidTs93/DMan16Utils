package me.DMan16.POPUtils.Menus;

import me.DMan16.POPUtils.Classes.Listener;
import me.DMan16.POPUtils.Interfaces.Menu;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class ListenerInventory implements Listener,Menu {
	protected static final int LINE_SIZE = 9;
	protected static final ItemStack CLOSE = Utils.makeItem(Material.BARRIER,Component.translatable("spectatorMenu.close",NamedTextColor.RED).decoration(TextDecoration.ITALIC,false),ItemFlag.values());
	protected static final ItemStack NEXT = Utils.makeItem(Material.ARROW,Component.translatable("spectatorMenu.next_page",NamedTextColor.AQUA).decoration(TextDecoration.ITALIC,false),ItemFlag.values());
	protected static final ItemStack PREVIOUS = Utils.makeItem(Material.ARROW,Component.translatable("spectatorMenu.previous_page",NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC,false),ItemFlag.values());
	protected static final ItemStack DONE = Utils.makeItem(Material.GREEN_STAINED_GLASS_PANE,Component.translatable("gui.done",NamedTextColor.GREEN).decoration(TextDecoration.ITALIC,false),ItemFlag.values());
	protected static final ItemStack OK = Utils.makeItem(Material.GREEN_STAINED_GLASS_PANE,Component.translatable("gui.ok",NamedTextColor.GREEN).decoration(TextDecoration.ITALIC,false),ItemFlag.values());
	protected static final ItemStack OK_NO = Utils.makeItem(Material.GRAY_STAINED_GLASS_PANE,Component.translatable("gui.ok",NamedTextColor.GREEN).decoration(TextDecoration.ITALIC,false).decoration(TextDecoration.STRIKETHROUGH,true),ItemFlag.values());
	protected static final ItemStack CANCEL = Utils.makeItem(Material.RED_STAINED_GLASS_PANE,Component.translatable("gui.cancel",NamedTextColor.RED).decoration(TextDecoration.ITALIC,false),ItemFlag.values());
	protected static final ItemStack BACK = Utils.makeItem(Material.ARROW,Component.translatable("gui.back",NamedTextColor.GOLD).decoration(TextDecoration.ITALIC,false),ItemFlag.values());
	protected static final ItemStack UP = Utils.makeItem(Material.BARRIER,Component.translatable("gui.up",NamedTextColor.DARK_GREEN).decoration(TextDecoration.ITALIC,false),ItemFlag.values());
	protected static final ItemStack DOWN = Utils.makeItem(Material.BARRIER,Component.translatable("gui.down",NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC,false),ItemFlag.values());
	protected static final ItemStack YES = Utils.makeItem(Material.GREEN_STAINED_GLASS_PANE,Component.translatable("gui.yes",NamedTextColor.GREEN).decoration(TextDecoration.ITALIC,false),ItemFlag.values());
	protected static final ItemStack NO = Utils.makeItem(Material.RED_STAINED_GLASS_PANE,Component.translatable("gui.no",NamedTextColor.RED).decoration(TextDecoration.ITALIC,false),ItemFlag.values());
	protected static final ItemStack ITEM_EMPTY = Utils.makeItem(Material.GRAY_STAINED_GLASS_PANE,Component.empty(),ItemFlag.values());
	
	protected final Inventory inventory;
	protected final int size;
	protected boolean cancelCloseUnregister = false;
	
	public ListenerInventory(@NotNull Inventory inv) {
		this.inventory = inv;
		size = inv.getSize();
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
}