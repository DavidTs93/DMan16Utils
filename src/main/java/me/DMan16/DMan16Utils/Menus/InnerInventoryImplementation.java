package me.DMan16.DMan16Utils.Menus;

import me.DMan16.DMan16Utils.Classes.Pairs.Pair;
import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Interfaces.Amountable;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.Items.ItemUtils;
import me.DMan16.DMan16Utils.Items.NullItemable;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class InnerInventoryImplementation<V extends Itemable<?>> extends InnerInventory<V> {
	protected <T extends InnerInventoryImplementation<V>> InnerInventoryImplementation(@NotNull Player player,@Nullable Component name,@Nullable Boolean border,@NotNull JavaPlugin plugin,boolean owner,@NotNull UUID ID,Boolean allowEdit,
																					   @NotNull HashMap<@NotNull Integer,@NotNull HashMap<@NotNull Integer,@Nullable V>> originalMenu,@Nullable Function<T,@NotNull Boolean> doFirst) {
		super(player,name,border,plugin,owner,ID,allowEdit,originalMenu,doFirst);
	}
	
	@SuppressWarnings("unchecked")
	private static <V extends Itemable<V>,T extends InnerInventoryImplementation<V>> boolean first(@NotNull InnerInventoryImplementation<V> menu,@Nullable Function<T,@NotNull Boolean> doFirst) {
		if (menu.testItem(NullItemable.NullItemable) != null) return false;
		if (doFirst != null) return doFirst.apply((T) menu);
		return true;
	}
	
	@Override
	protected boolean secondSlotCheck(int slot,int inventorySlot,@NotNull ClickType click,@NotNull InventoryAction action,int hotbarSlot) {
		return click.isCreativeAction() || slot < 0;
	}
	
	@Override
	@EventHandler(ignoreCancelled = true,priority = EventPriority.MONITOR)
	public void onInventoryDrag(InventoryDragEvent event) {
		if (!isThisInventory(event.getView().getTopInventory())) return;
		if (!allowEdit) {
			event.setCancelled(true);
			return;
		}
		Map<Integer,Integer> add = event.getNewItems().entrySet().stream().filter(entry -> Utils.notNull(entry.getValue())).collect(Collectors.toMap(Map.Entry::getKey,e -> e.getValue().getAmount()));
		Pair<Itemable<?>,Integer> result = updateDrag(ItemUtils.ofOrSubstituteOrHolder(event.getOldCursor()),add,Math.max(Utils.thisOrThatOrNull(Utils.applyNotNull(event.getCursor(),ItemStack::getAmount),0),0));
		if (result == null) {
			event.setCancelled(true);
			return;
		} else if (Utils.isNull(result.first()) && result.second() == null) return;
		Integer amount;
		if (result.second() != null) amount = result.second();
		else amount = Utils.applyNotNull(event.getCursor(),ItemStack::getAmount);
		if (amount != null && amount <= 0) {
			event.setCursor(null);
			return;
		}
		ItemStack item;
		if (Utils.notNull(result.first())) item = result.first().asItem();
		else if (Utils.isNull(event.getCursor())) item = event.getOldCursor().asOne();
		else item = event.getCursor();
		Utils.runNotNull(amount,item::setAmount);
		event.setCursor(item);
	}
	
	protected boolean isAllowedInnerSlot(int slot) {
		return slot >= 0 && slot < size - 9;
	}
	
	protected boolean isAllowedItemInnerSlot(int slot,V item) {
		return isAllowedInnerSlot(slot);
	}
	
	@Nullable
	@SuppressWarnings("unchecked")
	protected final V testItem(Itemable<?> item) {
		return Utils.getNoException(() -> (V) item);
	}
	
	@Nullable
	protected Pair<@Nullable Itemable<?>,@Nullable @Positive Integer> updateDrag(@NotNull Itemable<?> original,@NotNull Map<@NotNull Integer,@NotNull Integer> amountMap,@NonNegative int result) {
		if (amountMap.isEmpty() || !amountMap.keySet().stream().allMatch(this::isAllowedInnerSlot)) return null;
		V item = testItem(original);
		if (item == null) return null;
		for (Map.Entry<Integer,Integer> entry : amountMap.entrySet()) if (!canAddFully(entry.getKey(),item,entry.getValue())) return null;
		return Pair.of(null,null);
	}
	
	@Override
	protected void setPageContents() {
		setInnerSlots(null);
		setMorePageContents();
	}
	
	protected void replaceChecked(@NonNegative int slot,@Nullable V item) {
		if (Utils.notNull(item)) updatingMenu.computeIfAbsent(currentPage,p -> new HashMap<>()).put(slot,item);
		else Utils.runNotNull(updatingMenu.get(currentPage),page -> page.remove(slot));
	}
	
	@Nullable
	protected V getOriginalInnerItem(@NonNegative int slot) {
		if (!isAllowedInnerSlot(slot)) return null;
		HashMap<Integer,V> page = originalMenu.get(currentPage);
		if (Utils.isNullOrEmpty(page)) return null;
		V item = page.get(slot);
		return Utils.isNull(item) ? null : item;
	}
	
	@Nullable
	protected V getUpdatingInnerItem(@NonNegative int slot) {
		if (!isAllowedInnerSlot(slot)) return null;
		HashMap<Integer,V> page = updatingMenu.get(currentPage);
		if (Utils.isNullOrEmpty(page)) return null;
		V item = page.get(slot);
		return Utils.isNull(item) ? null : item;
	}
	
	protected final void setInnerSlots(@Nullable Collection<@NotNull Integer> slots) {
		if (slots != null && slots.isEmpty()) return;
		V item;
		for (int i = 0; i < size - 9; i++) {
			if (slots != null && !slots.contains(i)) continue;
			if (!isAllowedInnerSlot(i)) setNotAllowedInnerSlot(i);
			else if ((item = getOriginalInnerItem(i)) == null) setEmpty(i);
			else setItem(i,item.asItem());
		}
	}
	
	protected void setNotAllowedInnerSlot(@NonNegative int slot) {
		if (border() == null || !border() || !isBorder(slot)) setItem(slot,null);
	}
	
	protected void setEmpty(@NonNegative int slot) {
		setItem(slot,null);
	}
	
	protected void setMorePageContents() {}
	
	@Override
	protected boolean clickCheck(@NotNull ClickType click) {
		return click == ClickType.DOUBLE_CLICK || click == ClickType.UNKNOWN || click.isCreativeAction();
	}
	
	protected boolean firstCheckSlots(@NotNull InventoryClickEvent event,int slot,V item,@NotNull ClickType click) {
		return false;
	}
	
	protected boolean canAddFully(int slot,@NotNull V item,@Positive int amount) {
		if (!isAllowedInnerSlot(slot)) return false;
		V current = getUpdatingInnerItem(slot);
		if (current == null) return isAllowedItemInnerSlot(slot,item);
		if (!item.equals(current)) return false;
		return (current instanceof Amountable<?> amountable) && amount <= amountable.toMaxSize();
	}
	
	@Nullable
	@NonNegative
	protected Integer add(int slot,@NotNull V item,@Positive int amount) {
		return addIfEmpty(slot,item) ? (Integer) 0 : addToExisting(slot,item,amount);
	}
	
	protected boolean addIfEmpty(int slot,@NotNull V item) {
		if (getUpdatingInnerItem(slot) != null || !isAllowedItemInnerSlot(slot,item)) return false;
		replaceChecked(slot,item);
		return true;
	}
	
	/**
	 * @return how much was added
	 */
	@Nullable
	@NonNegative
	@SuppressWarnings("unchecked")
	protected Integer addToExisting(int slot,@NotNull V item,@Positive int amount) {
		if (!isAllowedInnerSlot(slot)) return null;
		V current = getUpdatingInnerItem(slot);
		if (!(current instanceof Amountable<?> amountable) || !item.equals(current)) return null;
		int before = amountable.amount();
		amountable = amountable.add(amount);
		replaceChecked(slot,(V) amountable);
		return amount - (amountable.amount() - before);
	}
	
	@Nullable
	@SuppressWarnings("unchecked")
	protected Supplier<@Nullable V> removeExisting(int slot,@Positive int amount) {
		if (!isAllowedInnerSlot(slot)) return null;
		V current = getUpdatingInnerItem(slot),after;
		if (current == null) return null;
		if (!(current instanceof Amountable<?> amountable) || (after = (V) amountable.copySubtractOrNull(amount)) == null) {
			remove(slot);
			return () -> current;
		}
		replaceChecked(slot,after);
		return () -> (V) amountable.copy(amount);
	}
	
	@Nullable
	protected Supplier<@Nullable V> swap(int slot,V item) {
		if (!isAllowedItemInnerSlot(slot,item)) return null;
		@Nullable V current = getUpdatingInnerItem(slot);
		replaceChecked(slot,item);
		return () -> current;
	}
	
	@Nullable
	protected Supplier<@Nullable V> remove(int slot) {
		return swap(slot,null);
	}
	
	protected final int convertSlot(int slot) {
		slot -= size - 9;
		if (slot >= 4 * 9) slot -= 4 * 9;
		return slot;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected void otherSlot(@NotNull InventoryClickEvent event,int slot,ItemStack slotItem,@NotNull ClickType click) {
		V innerItem = getUpdatingInnerItem(slot),fromEvent;
		if (firstCheckSlots(event,slot,innerItem,click)) return;
		if (slot < size && !isAllowedInnerSlot(slot)) {
			event.setCancelled(true);
			return;
		}
		Set<Integer> updateSlots = new HashSet<>();
		Pair<Integer,Itemable<?>> updateInventory = null;
		Supplier<@Nullable V> getItem;
		InventoryAction action = event.getAction();
		if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
			event.setCancelled(true);
			if (slot < size) {
				if (!isAllowedInnerSlot(slot) || innerItem == null) return;
				List<ItemStack> leftovers = Utils.addItems(player,innerItem.asItem());
				int added = leftovers.stream().map(ItemStack::getAmount).reduce(0,Integer::sum);
				if (added <= 0) return;
				Objects.requireNonNull(removeExisting(slot,added));
				clicks = true;
				updateSlots.add(slot);
			} else {
				fromEvent = testItem(ItemUtils.ofOrSubstituteOrHolder(slotItem));
				if (fromEvent == null) return;
				int itemAmount = (fromEvent instanceof Amountable<?> amountable) ? amountable.amount() : 1;
				Integer added;
				for (int i = 0; i < size - 9 && itemAmount > 0; i++) {
					added = addToExisting(i,fromEvent,itemAmount);
					if (added != null) {
						clicks = true;
						itemAmount -= added;
						updateSlots.add(i);
					}
				}
				if (itemAmount > 0) for (int i = 0; i < size - 9 && itemAmount > 0; i++) {
					if (updateSlots.contains(i)) continue;
					added = add(i,fromEvent,itemAmount);
					if (added != null) {
						clicks = true;
						itemAmount -= added;
						updateSlots.add(i);
					}
				}
				if (!updateSlots.isEmpty()) updateInventory = Pair.of(convertSlot(slot),itemAmount <= 0 || !(fromEvent instanceof Amountable<?> amountable) ? null : (V) amountable.copy(itemAmount));
			}
		} else if (slot < size) {
			if (action == InventoryAction.SWAP_WITH_CURSOR) {
				event.setCancelled(true);
				if ((fromEvent = testItem(ItemUtils.ofOrSubstituteOrHolder(event.getCursor()))) == null || (getItem = swap(slot,fromEvent)) == null) return;
				clicks = true;
				updateSlots.add(slot);
				updateInventory = Pair.of(null,getItem.get());
			} else if (action == InventoryAction.HOTBAR_SWAP) {
				event.setCancelled(true);
				if ((fromEvent = testItem(ItemUtils.ofOrSubstituteOrHolder(click == ClickType.SWAP_OFFHAND ? Utils.getFromSlot(player,EquipmentSlot.OFF_HAND) : Utils.getFromSlot(player,event.getHotbarButton())))) == null || (getItem = swap(slot,fromEvent)) == null) return;
				clicks = true;
				updateSlots.add(slot);
				updateInventory = Pair.of(click == ClickType.SWAP_OFFHAND ? Utils.getSlot(EquipmentSlot.OFF_HAND) : event.getHotbarButton(),getItem.get());
			} else if (action == InventoryAction.PICKUP_SOME) event.setCancelled(true);
			else if (action == InventoryAction.PICKUP_ALL || action == InventoryAction.DROP_ALL_SLOT) {
				if (innerItem == null || (getItem = remove(slot)) == null) {
					event.setCancelled(true);
					return;
				}
				clicks = true;
				updateSlots.add(slot);
				if (action == InventoryAction.PICKUP_ALL) {
					event.setCancelled(true);
					updateInventory = Pair.of(null,getItem.get());
				}
			} else if (action == InventoryAction.PICKUP_ONE || action == InventoryAction.DROP_ONE_SLOT) {
				if (innerItem == null || (getItem = removeExisting(slot,1)) == null) {
					event.setCancelled(true);
					return;
				}
				clicks = true;
				updateSlots.add(slot);
				if (action == InventoryAction.PICKUP_ONE) {
					event.setCancelled(true);
					updateInventory = Pair.of(null,getItem.get());
				}
			} else if (action == InventoryAction.PICKUP_HALF) {
				event.setCancelled(true);
				if (innerItem == null) return;
				int amount;
				if (innerItem instanceof Amountable<?> amountable) amount = amountable.amount() - (amountable.amount() / 2);
				else amount = 1;
				if ((getItem = removeExisting(slot,amount)) == null) {
					event.setCancelled(true);
					return;
				}
				clicks = true;
				updateSlots.add(slot);
				updateInventory = Pair.of(null,getItem.get());
			} else {
				event.setCancelled(true);
				return;
			}
		}
		saveClick(updateSlots,updateInventory);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected void empty(@NotNull InventoryClickEvent event,int slot,@NotNull ClickType click,boolean isNull) {
		V innerItem = getUpdatingInnerItem(slot),fromEvent;
		if (firstCheckSlots(event,slot,innerItem,click) || innerItem != null || slot >= size) return;
		if (!isAllowedInnerSlot(slot)) {
			event.setCancelled(true);
			return;
		}
		Set<Integer> updateSlots = new HashSet<>();
		Pair<Integer,Itemable<?>> updateInventory = null;
		InventoryAction action = event.getAction();
		if (action == InventoryAction.HOTBAR_SWAP) {
			event.setCancelled(true);
			if ((fromEvent = testItem(ItemUtils.ofOrSubstituteOrHolder(click == ClickType.SWAP_OFFHAND ? Utils.getFromSlot(player,EquipmentSlot.OFF_HAND) : Utils.getFromSlot(player,event.getHotbarButton())))) == null || !addIfEmpty(slot,fromEvent)) return;
			clicks = true;
			updateSlots.add(slot);
			updateInventory = Pair.of(click == ClickType.SWAP_OFFHAND ? Utils.getSlot(EquipmentSlot.OFF_HAND) : event.getHotbarButton(),null);
		} else if (Utils.isNull(fromEvent = testItem(ItemUtils.ofOrSubstituteOrHolder(event.getCursor()))) || (action != InventoryAction.PLACE_ALL && action != InventoryAction.PLACE_ONE)) {
			event.setCancelled(true);
			return;
		} else {
			if (action == InventoryAction.PLACE_ONE) if (!(fromEvent instanceof Amountable<?> amountable)) {
				event.setCancelled(true);
				return;
			} else fromEvent = (V) amountable.copy(1);
			if (!addIfEmpty(slot,fromEvent)) {
				event.setCancelled(true);
				return;
			}
			clicks = true;
			updateSlots.add(slot);
		}
		saveClick(updateSlots,updateInventory);
	}
	
	protected void saveClick(@NotNull Set<@NotNull Integer> updateSlots,@Nullable Pair<@Nullable Integer,@Nullable Itemable<?>> updateInventory) {
		savePage();
		new BukkitRunnable() {
			public void run() {
				setInnerSlots(updateSlots);
				if (updateInventory == null) return;
				if (updateInventory.first() == null) player.setItemOnCursor(Utils.applyNotNull(updateInventory.second(),Itemable::asItem));
				else Utils.setSlot(player,Utils.applyNotNull(updateInventory.second(),Itemable::asItem),updateInventory.first());
			}
		}.runTaskLater(DMan16UtilsMain.getInstance(),1);
	}
}