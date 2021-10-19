package me.DMan16.POPUtils.Menus;

import me.DMan16.POPUtils.Interfaces.Backable;
import me.DMan16.POPUtils.POPUtilsMain;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

public abstract class ListenerInventoryPages extends ListenerInventory {
	protected int currentPage = 1;
	protected int slotClose = size - 5;
	protected int slotNext = size - 1;
	protected int slotPrevious = size - 9;
	protected int slotBack = 0;
	protected final Player player;
	protected boolean alwaysSetNext = false;
	protected boolean alwaysSetPrevious = false;
	private final Boolean border;
//	protected boolean resetFillInside = false;
	protected int rightClickJump = 1;
	protected boolean fancyButtons = false;
	protected boolean openOnInitialize = true;
	protected @NotNull JavaPlugin plugin;
	
	/**
	 * @param lines Number of lines - NOT including the bottom (Close,Next,Previous) - 1-5
	 */
	@SuppressWarnings("unchecked")
	protected <V extends ListenerInventoryPages> ListenerInventoryPages(@Nullable InventoryHolder owner, @NotNull Player player, int lines, @Nullable Component name,
																		 @Nullable Boolean border, @NotNull JavaPlugin plugin, @Nullable Function<V,@NotNull Boolean> doFirst) {
		super(Utils.makeInventory(owner,Objects.requireNonNull(lines > 5 || lines < 1 ? null : lines + 1,"Number of lines must be 1-5!"),Utils.noItalic(name)));
		this.plugin = plugin;
		this.player = player;
		this.border = border;
		if (doFirst != null) if (!doFirst.apply((V) this)) throw new IllegalArgumentException();
		setPage(1);
		if (openOnInitialize) open(plugin,player);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!isThisInventory(event.getView().getTopInventory()) || checkCancelled(event)) return;
		int slot = event.getRawSlot();
		int inventorySlot = event.getSlot();
		ClickType click = event.getClick();
		InventoryAction action = event.getAction();
		int hotbar = event.getHotbarButton();
		if (!firstSlotCheck(slot,click)) {
			if (cancelCheck(slot,inventorySlot,click,action,hotbar)) event.setCancelled(true);
			if (!clickCheck(click)) {
				if (!secondSlotCheck(slot,inventorySlot,click,action,hotbar)) {
					ItemStack slotItem = event.getView().getItem(slot);
					if (isEmpty(slotItem)) empty(event,slot,click,Utils.isNull(slotItem));
					else if (slot == slotNext && shouldSetNext()) next(click);
					else if (slot == slotPrevious && shouldSetPrevious()) previous(click);
					else if (slot == slotBack && (this instanceof Backable)) ((Backable) this).goBack();
					else if (slot == slotClose) event.getView().close();
					else otherSlot(event,slot,slotItem,click);
				}
			}
		}
		if (event.isCancelled() && click == ClickType.SWAP_OFFHAND && Utils.isNull(event.getWhoClicked().getInventory().getItemInOffHand())) new BukkitRunnable() {
			public void run() {
				event.getWhoClicked().getInventory().setItemInOffHand(new ItemStack(Material.AIR));
				event.getWhoClicked().getInventory().setItemInOffHand(null);
			}
		}.runTaskLater(POPUtilsMain.getInstance(),1);
	}
	
	public int slotBack() {
		return slotBack;
	}
	
	protected Boolean border() {
		return border;
	}
	
	protected boolean clickCheck(@NotNull ClickType click) {
		return click == ClickType.DOUBLE_CLICK || (!click.isRightClick() && !click.isLeftClick() && !click.isCreativeAction());
	}
	
	protected void empty(@NotNull InventoryClickEvent event, int slot, @NotNull ClickType click, boolean isNull) {}
	
	protected boolean checkCancelled(@NotNull InventoryClickEvent event) {
		return event.isCancelled();
	}
	
	protected void next(@NotNull ClickType click) {
		changePage(change(click));
	}
	
	protected void previous(@NotNull ClickType click) {
		changePage(-change(click));
	}
	
	private int change(@NotNull ClickType click) {
		return Math.max(1,click.isRightClick() ? rightClickJump : 1);
	}
	
	protected void changePage(int num) {
		setPage(Math.min(Math.max(currentPage + num,1),maxPage()));
	}
	
	protected void reset() {
		if (border == null) {
			for (int i = 0; i < size - 9; i++) setItem(i,null);
			for (int i = size - 9; i < size; i++) setItem(i,itemBorder());
		} else if (border) for (int i = 0; i < size; i++) setItem(i,isBorder(i) ? itemBorder() : null);
		else for (int i = 0; i < size; i++) setItem(i,null);
	}
	
	public void setPage(int newPage) {
		if (newPage < 1 || (newPage == 1 && maxPage() == 0) || newPage > maxPage()) return;
		beforeSetPageAndReset(newPage);
		currentPage = newPage;
		reset();
		setPageContents();
		setItem(slotClose,itemClose());
		if (shouldSetNext()) setItem(slotNext,next());
		if (shouldSetPrevious()) setItem(slotPrevious,previous());
		if (this instanceof Backable) setItem(slotBack,itemBack());
		cancelCloseUnregister = true;
		openInventory(player);
		new BukkitRunnable() {
			public void run() {
				cancelCloseUnregister = false;
			}
		}.runTask(POPUtilsMain.getInstance());
	}
	
	protected boolean shouldSetNext() {
		return alwaysSetNext || currentPage < maxPage();
	}
	
	protected boolean shouldSetPrevious() {
		return alwaysSetPrevious || currentPage > 1;
	}
	
	protected void beforeSetPageAndReset(int newPage) {}
	
	protected boolean isBorder(int slot) {
		return (slot >= 0 && slot < 9) || (slot >= size - 9 && slot < size) || (slot % 9) == 0 || ((slot + 1) % 9) == 0;
	}
	
	@NotNull
	protected ItemStack next() {
		if (!fancyButtons) return super.itemNext();
		ItemStack newNext = super.itemNext();
		ItemMeta meta = newNext.getItemMeta();
		meta.displayName(meta.displayName().append(Component.text(" (" + (currentPage + 1) + ")").decoration(TextDecoration.ITALIC,false)));
		newNext.setItemMeta(meta);
		return newNext;
	}
	
	@NotNull
	protected ItemStack previous() {
		if (!fancyButtons) return super.itemPevious();
		ItemStack newPrevious = super.itemPevious();
		ItemMeta meta = newPrevious.getItemMeta();
		meta.displayName(meta.displayName().append(Component.text(" (" + (currentPage - 1) + ")").decoration(TextDecoration.ITALIC,false)));
		newPrevious.setItemMeta(meta);
		return newPrevious;
	}
	
	protected boolean isEmpty(@Nullable ItemStack item) {
		return Utils.isNull(item) || Utils.sameItem(itemBorder(),item) || Utils.sameItem(itemInside(),item);
	}
	
	protected boolean cancelCheck(int slot, int inventorySlot, @NotNull ClickType click, @NotNull InventoryAction action, int hotbarSlot) {
		return true;
	}
	
	protected boolean firstSlotCheck(int slot, @NotNull ClickType click) {
		return false;
	}
	
	protected boolean secondSlotCheck(int slot, int inventorySlot, @NotNull ClickType click, @NotNull InventoryAction action, int hotbarSlot) {
		return click.isCreativeAction() || slot < 0;
	}
	
	protected abstract void setPageContents();
	public abstract int maxPage();
	protected abstract void otherSlot(@NotNull InventoryClickEvent event, int slot, ItemStack slotItem, @NotNull ClickType click);
}