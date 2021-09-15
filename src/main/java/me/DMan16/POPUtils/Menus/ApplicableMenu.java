package me.DMan16.POPUtils.Menus;

import me.DMan16.POPUtils.Classes.Applicable;
import me.DMan16.POPUtils.Interfaces.Backable;
import me.DMan16.POPUtils.Interfaces.Sortable;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public abstract class ApplicableMenu<V extends Applicable<?,?>> extends ListenerInventoryPages implements Sortable {
	private static final ItemStack DEFAULT_ITEM_RESET = Utils.makeItem(Material.PAPER,Component.translatable("controls.reset",NamedTextColor.GREEN).
			decoration(TextDecoration.ITALIC,false),ItemFlag.values());
	
	protected int slotSort;
	protected int slotReset;
	protected V current;
	protected int currentSort;
	protected boolean ascending;
	protected List<V> applicables;
	
	public <T extends ApplicableMenu<V>> ApplicableMenu(@NotNull Player player, @NotNull Component menuName, @NotNull JavaPlugin plugin, @NotNull List<V> applicables, @Nullable V current,
														@Nullable Function<T,@NotNull Boolean> doFirstMore) {
		super(player,player,5,menuName,plugin,(ApplicableMenu<V> menu) -> first(menu,applicables,current,doFirstMore));
	}
	
	@SuppressWarnings("unchecked")
	private static <V extends Applicable<?,?>,T extends ApplicableMenu<V>> boolean first(@NotNull ApplicableMenu<V> menu, @NotNull List<V> applicables, @Nullable V current,
																						 @Nullable Function<T,@NotNull Boolean> doFirstMore) {
		menu.resetWithBorder = true;
		menu.slotSort = 4;
		menu.slotReset = 8;
		menu.currentSort = 0;
		menu.ascending = true;
		menu.applicables = applicables;
		menu.current = current;
		if (doFirstMore != null) return doFirstMore.apply((T) menu);
		return true;
	}
	
	@Override
	protected void otherSlot(@NotNull InventoryClickEvent event, int slot, ItemStack slotItem, @NotNull ClickType click) {
		if ((this instanceof Backable) && slot == slotBack()) ((Backable) this).goBack();
		else if (slot == slotSort) {
			if (event.isRightClick()) ascending = !ascending;
			else currentSort = (currentSort + 1) % 3;
			sort();
		} else if (!otherSlots(event,slot,slotItem)) {
			V selected;
			int idx;
			if (slot == slotReset) selected = null;
			else if ((idx = getIndex(slot)) >= 0 && idx < applicables.size()) selected = applicables.get(idx);
			else return;
			if (selected != current && setApplicable(selected)) {
				current = selected;
				setPage(currentPage);
			}
		}
	}
	
	public void sort() {
		if (currentSort == 0) applicables = Applicable.sortModel(applicables,ascending);
		else if (currentSort == 1) applicables = Applicable.sortRarity(applicables,ascending);
		else applicables = Applicable.sortName(applicables,ascending);
		setPage(currentPage);
	}
	
	@Override
	protected boolean secondSlotCheck(int slot, @NotNull ClickType click) {
		return super.secondSlotCheck(slot,click) || slot >= size;
	}
	
	@Override
	public int maxPage() {
		return Math.max(1,(int) Math.ceil(applicables.size() / 28.0));
	}
	
	@Override
	protected void setPageContents() {
		int idx;
		V selected;
		for (int i = 0; i < size; i++) if (!isBorder(i) && (idx = getIndex(i)) >= 0 && idx < applicables.size())
			inventory.setItem(i,(selected = applicables.get(idx)).item(true,selected == current));
		inventory.setItem(slotSort,SORTS.get((currentSort * 2) + (ascending ? 0 : 1)));
		ItemStack resetSkinItem = resetItem();
		if (resetSkinItem != null) inventory.setItem(slotReset,resetSkinItem);
		setPageContentsMore();
	}
	
	@Nullable
	protected ItemStack resetItem() {
		return null;
	}
	
	protected int getIndex(int slot) {
		return (currentPage - 1) * 7 * 4 + ((slot / 9) - 1) * 7 + (slot % 9) - 1;
	}
	
	protected boolean otherSlots(@NotNull InventoryClickEvent event, int slot, ItemStack slotItem) {
		return false;
	}
	
	protected void setPageContentsMore() {}
	
	protected abstract boolean setApplicable(@Nullable V applicable);
	
	@NotNull
	protected static ItemStack defaultItemReset(@NotNull Material material) {
		ItemStack item = DEFAULT_ITEM_RESET.clone();
		item.setType(material);
		return item;
	}
}