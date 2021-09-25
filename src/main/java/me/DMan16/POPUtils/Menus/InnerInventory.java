package me.DMan16.POPUtils.Menus;

import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class InnerInventory<V> extends ListenerInventoryPages {
	protected HashMap<@NotNull Integer,@NotNull List<@Nullable V>> originalMenu;
	protected HashMap<@NotNull Integer,@NotNull List<@Nullable V>> updatingMenu;
	protected UUID ID;
	protected boolean owner;
	protected boolean first;
	protected boolean allowEdit;
	
	protected InnerInventory(@NotNull Player viewer, @NotNull Component menuName, @NotNull JavaPlugin plugin, boolean owner,@NotNull UUID ID,
							 @NotNull HashMap<@NotNull Integer,@NotNull List<@Nullable V>> originalMenu) {
		this(viewer,menuName,plugin,owner,ID,null,originalMenu,null);
	}
	
	protected <T extends InnerInventory<V>> InnerInventory(@NotNull Player viewer, @NotNull Component menuName, @NotNull JavaPlugin plugin, boolean owner, @NotNull UUID ID, Boolean allowEdit,
							 @NotNull HashMap<@NotNull Integer,@NotNull List<@Nullable V>> originalMenu, @Nullable Function<T,@NotNull Boolean> doFirstMore) {
		super(viewer,viewer,5,menuName,plugin,(InnerInventory<V> inner) -> first(inner,ID,owner,allowEdit,originalMenu,doFirstMore));
//				ID,owner,allowEdit,originalMenu,objs);
	}
	
	@SuppressWarnings("unchecked")
	private static <V,T extends InnerInventory<V>> boolean first(@NotNull InnerInventory<V> inner, @NotNull UUID ID, boolean owner, Boolean allowEdit, @NotNull HashMap<@NotNull Integer,
			@NotNull List<@Nullable V>> originalMenu, @Nullable Function<T,@NotNull Boolean> doFirstMore) {
		inner.ID = ID;
		inner.owner = owner;
		inner.allowEdit = allowEdit == null ? inner.owner : allowEdit;
		inner.originalMenu = originalMenu;
		inner.updatingMenu();
		inner.first = true;
		inner.rightJump = 10;
		inner.fancyButtons = true;
		if (doFirstMore != null) if (!doFirstMore.apply((T) inner)) throw new IllegalArgumentException();
		return true;
	}
	
	protected void updatingMenu() {
		this.updatingMenu = new HashMap<>();
		this.originalMenu.forEach((page,items) -> this.updatingMenu.put(page,items.stream().map(this::cloneOriginal).collect(Collectors.toList())));
	}
	
	@Override
	protected boolean cancelCheck(int slot, @NotNull ClickType click, int hotbarSlot) {
		return !allowEdit || (slot < size && slot >= size - 9);
	}
	
	@Override
	protected boolean secondSlotCheck(int slot, @NotNull ClickType click) {
		return super.secondSlotCheck(slot,click) || slot >= size || slot < size - 9;
	}
	
	@Override
	protected void reset() {
		for (int i = 0; i < size - 9; i++) inventory.setItem(i,null);
		for (int i = size - 9; i < size; i++) inventory.setItem(i, ITEM_EMPTY_BORDER);
	}
	
	@Override
	protected void beforeSetPage(int newPage) {
		if (this.allowEdit) savePage();
	}
	
	@Override
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onInventoryDrag(InventoryDragEvent event) {
		if (!event.getView().getTopInventory().equals(inventory)) return;
		if (!this.allowEdit) event.setCancelled(true);
		else for (int slot : event.getRawSlots()) if (slot < size && slot >= size - 9) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onCloseSaveEvent(InventoryCloseEvent event) {
		if (this.allowEdit && event.getView().getTopInventory().equals(inventory) && event.getPlayer().equals(player) && !cancelCloseUnregister) saveExit();
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onQuitSaveEvent(PlayerQuitEvent event) {
		if (this.allowEdit && event.getPlayer().equals(player)) saveExit();
	}
	
	protected void savePage() {
		if (first) {
			first = false;
			return;
		}
		List<ItemStack> pageItems = new ArrayList<>();
		for (int i = 0; i < size - 9; i++) {
			ItemStack item = inventory.getItem(i);
			pageItems.add(Utils.isNull(item) ? null : item);
		}
		updateUpdatingMenu(pageItems);
	}
	
	protected boolean owner() {
		return owner;
	}
	
	protected boolean allowEdit() {
		return allowEdit;
	}
	
	protected void saveExit() {
		savePage();
		save();
	}
	
	protected V cloneOriginal(V item) {
		return item;
	}
	
	protected abstract void save();
	protected abstract void updateUpdatingMenu(@NotNull List<ItemStack> items);
}