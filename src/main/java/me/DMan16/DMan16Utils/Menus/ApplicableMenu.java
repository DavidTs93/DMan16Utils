package me.DMan16.DMan16Utils.Menus;

import me.DMan16.DMan16Utils.Classes.Applicable;
import me.DMan16.DMan16Utils.Interfaces.Sortable;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public abstract class ApplicableMenu<A extends Applicable<V,T>,V,T> extends ListenerInventoryPages implements Sortable {
	private static final Component RESET_NAME = Utils.noItalic(Component.translatable("controls.reset",NamedTextColor.GREEN));
	private static final ItemStack DEFAULT_ITEM_RESET = Utils.makeItem(Material.PAPER,RESET_NAME,ItemFlag.values());
	
	protected int slotSort;
	protected int slotReset;
	protected A current;
	protected int currentSort;
	protected boolean ascending;
	protected List<A> applicables;
	
	protected <M extends ApplicableMenu<A,V,T>> ApplicableMenu(@NotNull Player player,@Nullable Component name,@NotNull JavaPlugin plugin,@NotNull List<A> applicables,@Nullable A current,@Nullable Function<M,@NotNull Boolean> doFirstMore) {
		super(player,player,5,name,true,plugin,(ApplicableMenu<A,V,T> menu) -> first(menu,applicables,current,doFirstMore));
	}
	
	@SuppressWarnings("unchecked")
	private static <P extends Applicable<R,S>,M extends ApplicableMenu<P,R,S>,R,S> boolean first(@NotNull ApplicableMenu<P,R,S> menu,@NotNull List<P> applicables,@Nullable P current,@Nullable Function<M,@NotNull Boolean> doFirstMore) {
		menu.slotSort = 4;
		menu.slotReset = 8;
		menu.currentSort = 0;
		menu.ascending = true;
		menu.applicables = applicables;
		menu.current = current;
		if (doFirstMore != null) return doFirstMore.apply((M) menu);
		return true;
	}
	
	@Override
	protected void otherSlot(@NotNull InventoryClickEvent event,int slot,ItemStack slotItem,@NotNull ClickType click) {
		if (slot == slotSort) {
			if (event.isRightClick()) ascending = !ascending;
			else currentSort = (currentSort + 1) % 3;
			sort();
		} else if (!otherSlots(event,slot,slotItem)) {
			A selected;
			Integer idx;
			if (slot == slotReset) selected = null;
			else if ((idx = getInnerIndexOverall(slot)) != null && idx < applicables.size()) selected = applicables.get(idx);
			else return;
			if (selected != current && setApplicable(selected)) {
				current = selected;
				setPage(currentPage);
			}
		}
	}
	
	public void sort() {
		if (currentSort == 0) applicables = Applicable.sortID(applicables,ascending);
		else if (currentSort == 1) applicables = Applicable.sortRarity(applicables,ascending);
		else applicables = Applicable.sortName(applicables,ascending);
		reloadPage();
	}
	
	@Override
	protected boolean secondSlotCheck(int slot,int inventorySlot,@NotNull ClickType click,@NotNull InventoryAction action,int hotbarSlot) {
		return super.secondSlotCheck(slot,inventorySlot,click,action,hotbarSlot) || slot >= size;
	}
	
	@Override
	@Positive
	public int maxPage() {
		return Math.max(1,(int) Math.ceil(applicables.size() / 28.0));
	}
	
	@Override
	protected void setPageContents() {
		Integer idx;
		A selected;
		for (int i = 0; i < size; i++) if ((idx = getInnerIndexOverall(i)) != null && idx < applicables.size()) setItem(i,(selected = applicables.get(idx)).item(true,selected == current));
		setItem(slotSort,SORTS.get((currentSort * 2) + (ascending ? 0 : 1)));
		ItemStack resetSkinItem = resetItem();
		if (resetSkinItem != null) setItem(slotReset,resetSkinItem);
		setPageContentsMore();
	}
	
	@Nullable
	protected ItemStack resetItem() {
		return null;
	}
	
	protected boolean otherSlots(@NotNull InventoryClickEvent event,int slot,ItemStack slotItem) {
		return false;
	}
	
	protected void setPageContentsMore() {}
	
	protected abstract boolean setApplicable(@Nullable A applicable);
	
	@NotNull
	protected static ItemStack defaultItemReset(@NotNull Material material) {
		ItemStack item = DEFAULT_ITEM_RESET.clone();
		item.setType(material);
		return item;
	}
	
	@NotNull
	protected static ItemStack defaultItemReset(@NotNull ItemStack item) {
		return Utils.cloneChange(item,true,RESET_NAME,false,null,-1,false,ItemFlag.values());
	}
}