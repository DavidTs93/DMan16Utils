package me.DMan16.POPUtils.Utils;

import me.DMan16.POPUtils.POPUtilsMain;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class ListenerInventoryPages extends ListenerInventory {
	protected int currentPage = 1;
	protected int closeSlot = size - 5;
	protected int nextSlot = size - 1;
	protected int previousSlot = size - 9;
	protected final Player player;
	protected boolean alwaysSetNext = false;
	protected boolean alwaysSetPrevious = false;
	protected boolean resetWithBorder = false;
	
	/**
	 * @param lines Number of lines NOT including the bottom (Close,Next,Previous)
	 */
	public ListenerInventoryPages(@Nullable InventoryHolder owner, @NotNull Player player, int lines, @Nullable Component name, @NotNull JavaPlugin plugin, Object ... objs) {
		super(Utils.makeInventory(owner,Objects.requireNonNull(lines > 5 || lines < 1 ? null : lines + 1,"Number of lines must be 1-5!"),name));
		this.player = player;
		first(objs);
		setPage(1);
		register(plugin);
		player.openInventory(inventory);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!event.getView().getTopInventory().equals(inventory) || checkCancelled(event)) return;
		int slot = event.getRawSlot();
		ClickType click = event.getClick();
		if (firstSlotCheck(slot,click)) return;
		if (cancelCheck(slot,click)) event.setCancelled(true);
		if (!click.isRightClick() && !click.isLeftClick()) return;
		if (secondSlotCheck(slot,click)) return;
		ItemStack slotItem = event.getView().getItem(slot);
		if (isEmpty(slotItem)) return;
		if (slot == closeSlot) event.getView().close();
		else if (slot == nextSlot) next(click);
		else if (slot == previousSlot) previous(click);
		else otherSlot(event,slot,slotItem);
	}
	
	protected boolean checkCancelled(InventoryClickEvent event) {
		return event.isCancelled();
	}
	
	protected void next(@NotNull ClickType click) {
		changePage(1);
	}
	
	protected void previous(@NotNull ClickType click) {
		changePage(-1);
	}
	
	protected void changePage(int num) {
		setPage(Math.min(Math.max(currentPage + num,1),maxPage()));
	}
	
	protected void reset() {
		for (int i = 0; i < size; i++) inventory.setItem(i,resetWithBorder ? (isBorder(i) ? ITEM_EMPTY : null) : null);
	}
	
	public void setPage(int page) {
		if (page < 1 || (page == 1 && maxPage() == 0) || page > maxPage()) return;
		beforeSetPage(page);
		currentPage = page;
		reset();
		setPageContents();
		inventory.setItem(closeSlot,close());
		if (alwaysSetNext || currentPage < maxPage()) inventory.setItem(nextSlot,next());
		if (alwaysSetPrevious || currentPage > 1) inventory.setItem(previousSlot,previous());
		cancelCloseUnregister = true;
		player.openInventory(inventory);
		new BukkitRunnable() {
			public void run() {
				cancelCloseUnregister = false;
			}
		}.runTask(POPUtilsMain.getInstance());
	}
	
	protected void beforeSetPage(int page) {}
	
	protected void first(Object ... objs) {}
	
	protected boolean isBorder(int slot) {
		return (slot >= 0 && slot < 9) || (slot >= size - 9 && slot < size) || (slot % 9) == 0 || ((slot + 1) % 9) == 0;
	}
	
	@NotNull
	protected ItemStack close() {
		return CLOSE;
	}
	
	@NotNull
	protected ItemStack next() {
		return NEXT;
	}
	
	@NotNull
	protected ItemStack previous() {
		return PREVIOUS;
	}
	
	protected boolean isEmpty(@Nullable ItemStack item) {
		return Utils.isNull(item) || Utils.sameItem(ITEM_EMPTY,item);
	}
	
	protected boolean cancelCheck(int slot, @NotNull ClickType click) {
		return true;
	}
	
	protected boolean firstSlotCheck(int slot, @NotNull ClickType click) {
		return false;
	}
	
	protected boolean secondSlotCheck(int slot, @NotNull ClickType click) {
		return click.isCreativeAction();
	}
	
	protected abstract void setPageContents();
	public abstract int maxPage();
	protected abstract void otherSlot(@NotNull InventoryClickEvent event, int slot, ItemStack slotItem);
}