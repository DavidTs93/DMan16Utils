package me.DMan16.POPUtils.Menus;

import me.DMan16.POPUtils.Interfaces.Backable;
import me.DMan16.POPUtils.POPUtils;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
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
	protected boolean resetWithBorder = false;
	protected int rightJump = 1;
	protected boolean fancyButtons = false;
	protected boolean openOnInitialize = true;
	protected @NotNull JavaPlugin plugin;
	protected InventoryView view;
	
	/**
	 * @param lines Number of lines NOT including the bottom (Close,Next,Previous)
	 */
	@SuppressWarnings("unchecked")
	public <V extends ListenerInventoryPages> ListenerInventoryPages(@Nullable InventoryHolder owner, @NotNull Player player, int lines, @Nullable Component name,
																	 @NotNull JavaPlugin plugin, @Nullable Function<V,@NotNull Boolean> doFirst) {
		super(Utils.makeInventory(owner,Objects.requireNonNull(lines > 5 || lines < 1 ? null : lines + 1,"Number of lines must be 1-5!"),name));
		this.plugin = plugin;
		this.player = player;
		if (doFirst != null) if (!doFirst.apply((V) this)) throw new IllegalArgumentException();
		setPage(1);
		if (openOnInitialize) open();
	}
	
	protected void open() {
		register(plugin);
		openInventory();
	}
	
	protected void openInventory() {
		view = player.openInventory(inventory);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!event.getView().getTopInventory().equals(inventory) || checkCancelled(event)) return;
		int slot = event.getRawSlot();
		ClickType click = event.getClick();
		if (!firstSlotCheck(slot,click)) {
			if (cancelCheck(slot,click)) event.setCancelled(true);
			if (!clickCheck(click)) {
				if (!secondSlotCheck(slot,click)) {
					ItemStack slotItem = event.getView().getItem(slot);
					if (isEmpty(slotItem)) empty(event,slot,click,Utils.isNull(slotItem));
					else if (slot == slotClose) event.getView().close();
					else if (slot == slotNext) next(click);
					else if (slot == slotPrevious) previous(click);
					else if (slot == slotBack && (this instanceof Backable)) ((Backable) this).goBack();
					else otherSlot(event,slot,slotItem,click);
				}
			}
		}
		if (event.isCancelled() && click == ClickType.SWAP_OFFHAND && Utils.isNull(event.getWhoClicked().getInventory().getItemInOffHand())) new BukkitRunnable() {
			public void run() {
				event.getWhoClicked().getInventory().setItemInOffHand(new ItemStack(Material.AIR));
				event.getWhoClicked().getInventory().setItemInOffHand(null);
			}
		}.runTaskLater(POPUtils.getInstance(),1);
	}
	
	public int slotBack() {
		return slotBack;
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
		return Math.max(1,click.isRightClick() ? rightJump : 1);
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
		inventory.setItem(slotClose,close());
		if (alwaysSetNext || currentPage < maxPage()) inventory.setItem(slotNext,next());
		if (alwaysSetPrevious || currentPage > 1) inventory.setItem(slotPrevious,previous());
		if (this instanceof Backable) inventory.setItem(slotBack(),BACK);
		cancelCloseUnregister = true;
		openInventory();
		new BukkitRunnable() {
			public void run() {
				cancelCloseUnregister = false;
			}
		}.runTask(POPUtils.getInstance());
	}
	
	protected void beforeSetPage(int newPage) {}
	
	protected boolean isBorder(int slot) {
		return (slot >= 0 && slot < 9) || (slot >= size - 9 && slot < size) || (slot % 9) == 0 || ((slot + 1) % 9) == 0;
	}
	
	@NotNull
	protected ItemStack close() {
		return CLOSE;
	}
	
	@NotNull
	protected ItemStack next() {
		if (!fancyButtons) return NEXT;
		ItemStack newNext = NEXT.clone();
		ItemMeta meta = newNext.getItemMeta();
		meta.displayName(meta.displayName().append(Component.text(" (" + (currentPage + 1) + ")").decoration(TextDecoration.ITALIC,false)));
		newNext.setItemMeta(meta);
		return newNext;
	}
	
	@NotNull
	protected ItemStack previous() {
		if (!fancyButtons) return PREVIOUS;
		ItemStack newPrevious = PREVIOUS.clone();
		ItemMeta meta = newPrevious.getItemMeta();
		meta.displayName(meta.displayName().append(Component.text(" (" + (currentPage - 1) + ")").decoration(TextDecoration.ITALIC,false)));
		newPrevious.setItemMeta(meta);
		return newPrevious;
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
		return click.isCreativeAction() || slot < 0;
	}
	
	protected abstract void setPageContents();
	public abstract int maxPage();
	protected abstract void otherSlot(@NotNull InventoryClickEvent event, int slot, ItemStack slotItem, @NotNull ClickType click);
}