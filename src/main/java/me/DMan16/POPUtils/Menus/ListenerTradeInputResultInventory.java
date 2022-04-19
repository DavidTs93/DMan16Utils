package me.DMan16.POPUtils.Menus;

import me.DMan16.POPUtils.Interfaces.Itemable;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.function.Function;

public abstract class ListenerTradeInputResultInventory extends ListenerInventoryPages {
	protected static final @NotNull @Unmodifiable List<@NotNull Integer> DEFAULT_EXTRA_SLOTS = List.of(30,31,32,39,40,41);
	
	protected int slotItem;
	protected Itemable<?> item;
	protected List<@NotNull ItemStack> extraItems;
	protected List<@NotNull Integer> extraItemsSlots;
	
	public <V extends ListenerTradeInputResultInventory> ListenerTradeInputResultInventory(@Nullable InventoryHolder owner,@NotNull Player player,@Nullable Component name,@NotNull JavaPlugin plugin,@NotNull Itemable<?> item,@NotNull List<ItemStack> extraItems,
																						   @Nullable Function<V,@NotNull Boolean> doFirst) {
		super(owner,player,5,name,true,plugin,(ListenerTradeInputResultInventory menu) -> first(menu,item,extraItems,doFirst));
	}
	
	@SuppressWarnings("unchecked")
	private static <V extends ListenerTradeInputResultInventory> boolean first(@NotNull ListenerTradeInputResultInventory menu,@NotNull Itemable<?> item,@NotNull List<ItemStack> extraItems,@Nullable Function<V,@NotNull Boolean> doFirst) {
		menu.slotItem = 13;
		menu.item = item;
		menu.extraItems = extraItems;
		menu.extraItemsSlots = DEFAULT_EXTRA_SLOTS;
		return doFirst == null || doFirst.apply((V) menu);
	}
	
	@Override
	protected void afterCloseUnregister(InventoryCloseEvent event) {
		clearItems();
	}
	
	@Override
	protected boolean cancelCheck(int slot,int inventorySlot,@NotNull ClickType click,@NotNull InventoryAction action,int hotbarSlot) {
		return slot < size && slotItem != slot && !extraItemsSlots.contains(slot);
	}
	
	protected void setPageContents() {
		setItem(slotItem,item.asItem());
		for (int i = 0; i < extraItemsSlots.size() && i < extraItems.size(); i++) setItem(extraItemsSlots.get(i),extraItems.get(i));
	}
	
	public int maxPage() {
		return 1;
	}
	
	@Override
	protected boolean clickCheck(@NotNull ClickType click) {
		return false;
	}
	
	@Override
	protected void reset() {
		clear();
		for (int i = 0; i < size; i++) setItem(i,isBorder(i) ? itemBorder().asItem() : (slotItem == i || extraItemsSlots.contains(i) ? null : itemInside().asItem()));
	}
	
	@Override
	protected void otherSlot(@NotNull InventoryClickEvent event, int slot, ItemStack slotItem, @NotNull ClickType click) {}
	
	protected void clearItems() {
		ItemStack item = getItem(slotItem);
		if (!Utils.isNull(item)) Utils.givePlayer(player,player.getLocation(),false,item);
		Utils.givePlayer(player,player.getLocation(),false,extraItemsSlots.stream().map(this::getItem).filter(i -> !Utils.isNull(i)).toList());
	}
}