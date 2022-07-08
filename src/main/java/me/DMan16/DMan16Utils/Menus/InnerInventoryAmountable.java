package me.DMan16.DMan16Utils.Menus;

import me.DMan16.DMan16Utils.Interfaces.Amountable;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public abstract class InnerInventoryAmountable<V extends Itemable<V> & Amountable<V> > extends InnerInventory<V> {
	private Function<ItemStack,V> fromItem;
	
	protected <T extends InnerInventoryAmountable<V>> InnerInventoryAmountable(@NotNull Player player, @Nullable Component name, @NotNull JavaPlugin plugin, boolean owner,@NotNull UUID ID, Boolean allowEdit,@NotNull HashMap<@NotNull Integer,@NotNull List<@Nullable V>> originalMenu,
																			   @NotNull Function<ItemStack,V> fromItem, @Nullable Function<T,@NotNull Boolean> doFirstMore) {
		super(player,name,null,plugin,owner,ID,allowEdit,originalMenu,(InnerInventoryAmountable<V> inner) -> first(inner,fromItem,doFirstMore));
	}
	
	@SuppressWarnings("unchecked")
	private static <V extends Itemable<V> & Amountable<V>,T extends InnerInventoryAmountable<V>> boolean first(@NotNull InnerInventoryAmountable<V> inner,@NotNull Function<ItemStack,V> fromItem,@Nullable Function<T,@NotNull Boolean> doFirstMore) {
		inner.fromItem = fromItem;
		inner.rightClickJump = 10;
		if (doFirstMore != null) return doFirstMore.apply((T) inner);
		return true;
	}
	
	@Override
	protected boolean secondSlotCheck(int slot, int inventorySlot, @NotNull ClickType click, @NotNull InventoryAction action, int hotbarSlot) {
		return click.isCreativeAction() || slot < 0;
	}
	
	@Override
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onInventoryDrag(InventoryDragEvent event) {
		if (isThisInventory(event.getView().getTopInventory())) event.setCancelled(true);
	}
	
	@Override
	protected void setPageContents() {
		List<V> items = originalMenu.get(currentPage);
		for (int i = 0; i < items.size(); i++) setItem(i,items.get(i) == null ? null : items.get(i).asItem());
		setMorePageContents();
	}
	
	protected void setMorePageContents() {}
	
	@Override
	protected boolean clickCheck(@NotNull ClickType click) {
		return click == ClickType.DOUBLE_CLICK || click == ClickType.UNKNOWN || click == ClickType.WINDOW_BORDER_LEFT || click == ClickType.WINDOW_BORDER_RIGHT ||
				click.isCreativeAction();
	}
	
	@Override
	public int maxPage() {
		return this.originalMenu.size();
	}
	
	protected boolean firstCheckSlots(@NotNull InventoryClickEvent event, int slot, ItemStack slotItem, @NotNull ClickType click) {
		return false;
	}
	
	protected abstract int add(@NotNull V item);
	
	protected abstract int remove(@NotNull V item);
	
	protected abstract boolean swap(V item1, V item2);
	
	protected boolean cancelHotbar(int slot) {
		return false;
	}
	
	@Override
	protected void otherSlot(@NotNull InventoryClickEvent event, int slot, ItemStack slotItem, @NotNull ClickType click) {
		if (firstCheckSlots(event,slot,slotItem,click)) return;
		InventoryAction action = event.getAction();
		int amount;
		V item = fromItem.apply(slotItem),temp;
		if (slot < size - 9 || slot >= size) {
			if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
				event.setCancelled(true);
				if (item == null) return;
				if (slot < size) {
					amount = remove(item);
					if (amount <= 0) return;
					List<ItemStack> leftovers = Utils.addItems(player,item.copy(amount).asItem());
					amount = leftovers.isEmpty() ? 0 : leftovers.get(0).getAmount();
					item = amount <= 0 ? null : item.copy(item.amount() - amount);
					originalMenu.get(currentPage).set(slot,item);
					setItem(slot,item == null ? null : item.asItem());
				} else {
					amount = item.amount();
					Integer addSlot;
					while (amount > 0) {
						addSlot = null;
						for (int i = 0; i < originalMenu.get(currentPage).size(); i++)
							if ((temp = originalMenu.get(currentPage).get(i)) == null || (item.equals(temp) && temp.amount() < temp.maxSize())) {
								addSlot = i;
								break;
							}
						if (addSlot == null) break;
						temp = originalMenu.get(currentPage).get(addSlot);
						int itemAmount = temp == null ? 0 : temp.amount();
						item = item.copy(Math.min(amount + itemAmount,item.maxSize()) - itemAmount);
						int added = add(item);
						if (added <= 0) break;
						amount -= added;
						originalMenu.get(currentPage).set(addSlot,item);
						setItem(addSlot,item.asItem());
					}
					event.setCurrentItem(amount > 0 ? item.copy(amount).asItem() : null);
				}
			} else if (slot < size) {
				if (action == InventoryAction.SWAP_WITH_CURSOR) {
					if (item == null || item.amount() > item.maxSize() || !swap(this.originalMenu.get(currentPage).get(slot),fromItem.apply(event.getCursor())))
						event.setCancelled(true);
				} else if (action == InventoryAction.HOTBAR_SWAP) {
					item = fromItem.apply(click == ClickType.SWAP_OFFHAND ? player.getInventory().getItemInOffHand() : player.getInventory().getItem(event.getHotbarButton()));
					if (item == null || item.amount() > item.maxSize() || !swap(originalMenu.get(currentPage).get(slot),item)) event.setCancelled(true);
					else originalMenu.get(currentPage).set(slot,item);
				} else if (action == InventoryAction.PICKUP_SOME) event.setCancelled(true);
				else if (action == InventoryAction.PICKUP_ALL || action == InventoryAction.DROP_ALL_SLOT) {
					if (remove(item) > 0) originalMenu.get(currentPage).set(slot,null);
					else event.setCancelled(true);
				} else if (action == InventoryAction.PICKUP_ONE || action == InventoryAction.DROP_ONE_SLOT) {
					if (item != null && remove(item.copy(1)) > 0) originalMenu.get(currentPage).set(slot,item.amount() == 1 ? null : item.copy(item.amount() - 1));
					else event.setCancelled(true);
				} else if (action == InventoryAction.PICKUP_HALF) {
					if (item != null && (amount = remove(item.copy(item.amount() - (item.amount() / 2)))) > 0)
						originalMenu.get(currentPage).set(slot,item.amount() == 1 ? null : item.copy(amount));
					else event.setCancelled(true);
				} else if (Utils.notNull(event.getCursor()) && action.name().startsWith("PLACE_")) {
					ItemStack cursor = event.getCursor();
					if (item == null || !item.equals(fromItem.apply(cursor))) {
						event.setCancelled(true);
						return;
					}
					amount = add(action == InventoryAction.PLACE_ALL ? item : (action == InventoryAction.PLACE_ONE ? item.copy(1) :
							item.copy(item.maxSize() - item.amount())));
					if (amount > 0) originalMenu.get(currentPage).set(slot,item.copy(item.amount() + amount));
					else event.setCancelled(true);
				}
			}
		}
	}
	
	@Override
	protected void empty(@NotNull InventoryClickEvent event, int slot, @NotNull ClickType click, boolean isNull) {
		ItemStack cursor = event.getCursor();
		if (!isNull || slot >= size) return;
		V item;
		if (click == ClickType.SWAP_OFFHAND || click == ClickType.NUMBER_KEY) {
			if (click == ClickType.NUMBER_KEY && cancelHotbar(event.getHotbarButton())) event.setCancelled(true);
			else {
				item = fromItem.apply(click == ClickType.SWAP_OFFHAND ? player.getInventory().getItemInOffHand() : player.getInventory().getItem(event.getHotbarButton()));
				if (!swap(originalMenu.get(currentPage).get(slot),item)) event.setCancelled(true);
				else originalMenu.get(currentPage).set(slot,item);
			}
		} else if (Utils.notNull(cursor)) {
			if (event.getAction() == InventoryAction.PLACE_SOME || (event.getAction() != InventoryAction.PLACE_ALL && event.getAction() != InventoryAction.PLACE_ONE)) {
				event.setCancelled(true);
				return;
			}
			item = fromItem.apply(cursor);
			if (item == null || !item.equals(fromItem.apply(cursor))) {
				event.setCancelled(true);
				return;
			}
			int amount = add(event.getAction() == InventoryAction.PLACE_ALL ? item : item.copy(1));
			if (amount > 0) originalMenu.get(currentPage).set(slot,item.copy(item.amount() + amount));
			else event.setCancelled(true);
		}
	}
	
	@Override
	protected void updatingMenu() {}
	
	protected void updateUpdatingMenu(@NotNull List<@Nullable V> items) {
		originalMenu.put(currentPage,items);
	}
}