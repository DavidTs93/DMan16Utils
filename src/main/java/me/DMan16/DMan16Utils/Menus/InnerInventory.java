package me.DMan16.DMan16Utils.Menus;

import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public abstract class InnerInventory<V extends Itemable<?>> extends ListenerInventoryPages {
	protected HashMap<@NotNull Integer,@NotNull HashMap<@NotNull Integer,@Nullable V>> originalMenu;
	protected HashMap<@NotNull Integer,@NotNull HashMap<@NotNull Integer,@Nullable V>> updatingMenu;
	protected UUID ID;
	protected boolean owner;
	protected boolean allowEdit;
	protected boolean clicks;
	
	protected <T extends InnerInventory<V>> InnerInventory(@NotNull Player viewer,@Nullable Component name,@Nullable Boolean border,@NotNull JavaPlugin plugin,boolean owner,@NotNull UUID ID,Boolean allowEdit,
														   @NotNull HashMap<@NotNull Integer,@NotNull HashMap<@NotNull Integer,@Nullable V>> originalMenu,@Nullable Function<T,@NotNull Boolean> doFirstMore) {
		super(viewer,viewer,5,name,border,plugin,(InnerInventory<V> inner) -> first(inner,ID,owner,allowEdit,originalMenu,doFirstMore));
	}
	
	protected InnerInventory(@NotNull Player viewer,@Nullable Component name,@Nullable Boolean border,@NotNull JavaPlugin plugin,boolean owner,@NotNull UUID ID,
							 @NotNull HashMap<@NotNull Integer,@NotNull HashMap<@NotNull Integer,@Nullable V>> originalMenu) {
		this(viewer,name,border,plugin,owner,ID,null,originalMenu,null);
	}
	
	@SuppressWarnings("unchecked")
	private static <V extends Itemable<?>,T extends InnerInventory<V>> boolean first(@NotNull InnerInventory<V> inner,@NotNull UUID ID,boolean owner,Boolean allowEdit,
																					 @NotNull HashMap<@NotNull Integer,@NotNull HashMap<@NotNull Integer,@Nullable V>> originalMenu,@Nullable Function<T,@NotNull Boolean> doFirstMore) {
		inner.ID = ID;
		inner.owner = owner;
		inner.allowEdit = allowEdit == null ? inner.owner : allowEdit;
		inner.originalMenu = originalMenu;
		inner.clicks = false;
		inner.rightClickJump = 10;
		inner.fancyButtons = true;
		inner.updatingMenu = inner.cloneMenu(originalMenu);
		if (doFirstMore != null) return doFirstMore.apply((T) inner);
		return true;
	}
	
	@NotNull
	protected final HashMap<@NotNull Integer,@NotNull HashMap<@NotNull Integer,@Nullable V>> cloneMenu(@NotNull HashMap<@NotNull Integer,@NotNull HashMap<@NotNull Integer,@Nullable V>> menu) {
		HashMap<Integer,HashMap<Integer,V>> map = new HashMap<>();
		HashMap<Integer,V> items;
		for (Map.Entry<Integer,HashMap<Integer,V>> entry : menu.entrySet()) {
			items = new HashMap<>() {{
				for (Entry<Integer,V> e : entry.getValue().entrySet()) if (!Utils.isNull(e.getValue())) put(e.getKey(),cloneOriginal(e.getValue()));
			}};
			if (!items.isEmpty()) map.put(entry.getKey(),items);
		}
		return map;
	}
	
	@Contract("null -> true")
	protected final boolean isPageEmpty(@Nullable HashMap<@NotNull Integer,@Nullable V> items) {
		if (!Utils.isNullOrEmpty(items)) for (V item : items.values()) if (!Utils.isNull(item)) return false;
		return true;
	}
	
	protected boolean samePages(@Nullable HashMap<@NotNull Integer,@Nullable V> page1,@Nullable HashMap<@NotNull Integer,@Nullable V> page2) {
		boolean empty1 = isPageEmpty(page1),empty2 = isPageEmpty(page2);
		if (empty1 && empty2) return true;
		else if (empty1 || empty2) return false;
		Set<Integer> slots = Utils.joinSets(page1.keySet(),page2.keySet());
		V item1,item2;
		for (Integer slot : slots) {
			empty1 = Utils.isNull(item1 = page1.get(slot));
			empty2 = Utils.isNull(item2 = page2.get(slot));
			if (empty1 && empty2) continue;
			if (empty1 || empty2) return false;
			if (!item1.equals(item2)) return false;
		}
		return true;
	}
	
	protected boolean hasChanged() {
		for (Integer slot : Utils.joinSets(originalMenu.keySet(),updatingMenu.keySet())) if (!samePages(originalMenu.get(slot),updatingMenu.get(slot))) return true;
		return false;
	}
	
	@Override
	protected boolean cancelCheck(int slot,int inventorySlot,@NotNull ClickType click,@NotNull InventoryAction action,int hotbarSlot) {
		return !allowEdit || (slot < size && slot >= size - 9);
	}
	
	@Override
	protected boolean secondSlotCheck(int slot,int inventorySlot,@NotNull ClickType click,@NotNull InventoryAction action,int hotbarSlot) {
		return super.secondSlotCheck(slot,inventorySlot,click,action,hotbarSlot) || slot >= size || slot < size - 9;
	}
	
	@Override
	protected void beforeSetPageAndReset(int newPage) {
		if (allowEdit) savePage();
	}
	
	@Override
	@EventHandler(ignoreCancelled = true,priority = EventPriority.LOWEST)
	public void onInventoryDrag(InventoryDragEvent event) {
		if (!isThisInventory(event.getView().getTopInventory())) return;
		if (!allowEdit) event.setCancelled(true);
		else for (int slot : event.getRawSlots()) if (slot < size && slot >= size - 9) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler(ignoreCancelled = true,priority = EventPriority.LOWEST)
	public void onCloseSaveEvent(InventoryCloseEvent event) {
		if (allowEdit && isThisInventory(event.getView().getTopInventory()) && event.getPlayer().equals(player) && !cancelCloseUnregister) saveExit();
	}
	
	@EventHandler(ignoreCancelled = true,priority = EventPriority.LOWEST)
	public void onQuitSaveEvent(PlayerQuitEvent event) {
		if (allowEdit && event.getPlayer().equals(player)) saveExit();
	}
	
	protected void savePage() {
		if (clicks && allowEdit && !samePages(originalMenu.get(currentPage),updatingMenu.get(currentPage))) {
			save();
			originalMenu = cloneMenu(updatingMenu);
		}
		clicks = false;
	}
	
	protected boolean owner() {
		return owner;
	}
	
	protected boolean allowEdit() {
		return allowEdit;
	}
	
	protected void saveExit() {
		savePage();
	}
	
	@SuppressWarnings("unchecked")
	protected V cloneOriginal(V item) {
		return Utils.isNull(item) ? null : (V) item.copy();
	}
	
	protected abstract void save();
}