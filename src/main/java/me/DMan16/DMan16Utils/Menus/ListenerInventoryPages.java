package me.DMan16.DMan16Utils.Menus;

import me.DMan16.DMan16Utils.Classes.BasicItemableGeneral;
import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Interfaces.Backable;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.function.Function;

public abstract class ListenerInventoryPages extends ListenerInventory {
	protected final @NotNull JavaPlugin plugin;
	/**
	 * {@link ListenerInventory#size} / 9
	 */
	protected final @Positive @Range(from = 2,to = 6) int lines;
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
	
	/**
	 * @param lines Number of lines - NOT including the bottom (Close,Next,Previous) - 1-5
	 */
	@SuppressWarnings("unchecked")
	protected <V extends ListenerInventoryPages> ListenerInventoryPages(@Nullable InventoryHolder owner,@NotNull Player player,@Positive @Range(from = 1,to = 5) int lines,@Nullable Component name,
																		@Nullable Boolean border,@NotNull JavaPlugin plugin,@Nullable Function<V,@NotNull Boolean> doFirst) {
		super(Utils.makeInventory(owner,lines + 1,Utils.noItalic(name)));
		this.plugin = plugin;
		this.lines = lines + 1;
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
					else if ((this instanceof Backable backable) && slot == slotBack()) backable.goBack(click);
					else if (slot == slotClose && shouldSetClose()) clickClose();
					else otherSlot(event,slot,slotItem,click);
				}
			}
		}
		if (event.isCancelled() && click == ClickType.SWAP_OFFHAND && Utils.isNull(event.getWhoClicked().getInventory().getItemInOffHand())) new BukkitRunnable() {
			public void run() {
				event.getWhoClicked().getInventory().setItemInOffHand(new ItemStack(Material.AIR));
				event.getWhoClicked().getInventory().setItemInOffHand(null);
			}
		}.runTaskLater(DMan16UtilsMain.getInstance(),1);
	}
	
	protected void clickClose() {
		new BukkitRunnable() {
			public void run() {
				close();
			}
		}.runTaskLater(DMan16UtilsMain.getInstance(),1);
	}
	
	/**
	 * If an inner slot: 0-(28 * {@link #maxPage()} - 1)
	 * Else: null
	 */
	@Nullable
	protected Integer getInnerIndexOverall(int slot) {
		return slot < 0 || slot >= size || isBorder(slot) ? null : (currentPage - 1) * 7 * 4 + ((slot / 9) - 1) * 7 + (slot % 9) - 1;
	}
	
	/**
	 * If an inner slot: 0-27
	 * Else: null
	 */
	@Nullable
	protected Integer getInnerIndex(int slot) {
		return slot < 0 || slot >= size || isBorder(slot) ? null : ((slot / 9) - 1) * 7 + (slot % 9) - 1;
	}
	
	@Nullable
	public static Integer getInnerIndex(int slot,int size,@Nullable Function<@NotNull Integer,@NotNull Boolean> isBorder) {
		return slot < 0 || slot >= size || (isBorder != null && isBorder.apply(slot)) ? null : ((slot / 9) - 1) * 7 + (slot % 9) - 1;
	}
	
	public int slotBack() {
		return slotBack;
	}
	
	@MonotonicNonNull
	protected Boolean border() {
		return border;
	}
	
	protected boolean clickCheck(@NotNull ClickType click) {
		return click == ClickType.DOUBLE_CLICK || (!click.isRightClick() && !click.isLeftClick() && !click.isCreativeAction());
	}
	
	protected void empty(@NotNull InventoryClickEvent event,int slot,@NotNull ClickType click,boolean isNull) {}
	
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
		setPage(Utils.clamp(currentPage + num,1,maxPage()));
	}
	
	protected void reset() {
		clear();
		if (border == null) for (int i = size - 9; i < size; i++) setItem(i,itemBorder().asItem());
		else if (border) for (int i = 0; i < size; i++) if (isBorder(i)) setItem(i,itemBorder().asItem());
	}
	
	public void setPage(int newPage) {
		if (newPage < 1 || (newPage == 1 && maxPage() == 0) || newPage > maxPage()) return;
		beforeSetPageAndReset(newPage);
		currentPage = newPage;
		reset();
		setPageContents();
		if (shouldSetClose()) setItem(slotClose,itemClose().asItem());
		if (shouldSetNext()) setItem(slotNext,next().asItem());
		if (shouldSetPrevious()) setItem(slotPrevious,previous().asItem());
		if (this instanceof Backable) setItem(slotBack,itemBack().asItem());
		if (reopenInventory()) {
			boolean old = cancelCloseUnregister;
			cancelCloseUnregister = true;
			openInventory(player);
			if (!old) new BukkitRunnable() {
				public void run() {
					cancelCloseUnregister = false;
				}
			}.runTask(DMan16UtilsMain.getInstance());
		}
	}
	
	protected boolean reopenInventory() {
		return true;
	}
	
	public void reloadPage() {
		setPage(currentPage);
	}
	
	protected boolean shouldSetClose() {
		return true;
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
	protected BasicItemableGeneral<?> next() {
		return !fancyButtons ? super.itemNext() : super.itemNext().copyChangeNameIf(name -> name.append(Component.text(" (" + (currentPage - 1) + ")")),Objects::nonNull);
	}
	
	@NotNull
	protected BasicItemableGeneral<?> previous() {
		return !fancyButtons ? super.itemPevious() : super.itemPevious().copyChangeNameIf(name -> name.append(Component.text(" (" + (currentPage - 1) + ")")),Objects::nonNull);
	}
	
	@Contract("null -> true")
	protected boolean isEmpty(@Nullable ItemStack item) {
		return Utils.isNull(item) || Utils.sameItem(itemBorder().asItem(),item) || Utils.sameItem(itemInside().asItem(),item) || Utils.sameItem(itemInsideDark().asItem(),item);
	}
	
	protected boolean cancelCheck(int slot,int inventorySlot,@NotNull ClickType click,@NotNull InventoryAction action,int hotbarSlot) {
		return true;
	}
	
	protected boolean firstSlotCheck(int slot,@NotNull ClickType click) {
		return false;
	}
	
	protected boolean secondSlotCheck(int slot,int inventorySlot,@NotNull ClickType click,@NotNull InventoryAction action,int hotbarSlot) {
		return click.isCreativeAction() || slot < 0;
	}
	
	protected abstract void setPageContents();
	public abstract int maxPage();
	protected abstract void otherSlot(@NotNull InventoryClickEvent event,int slot,ItemStack slotItem,@NotNull ClickType click);
	
	@NotNull
	public static <V,T> LinkedHashMap<@NotNull @Positive Integer,@NotNull LinkedHashMap<@NotNull @NonNegative Integer,@NotNull T>> generatePages(@NotNull Iterator<@Nullable V> iter,@NotNull Function<@Nullable V,@Nullable T> convert,@Nullable Function<@NotNull T,@NotNull Boolean> isNull,
																																				 @NonNegative @Range(from = 0,to = 5) int startLine,@NonNegative @Range(from = 0,to = 5) int endLine,
																																				 @NonNegative @Range(from = 0,to = 8) int startColumn,@NonNegative @Range(from = 0,to = 8) int endColumn,
																																				 @Nullable Function<@NotNull Integer,@NotNull Boolean> ignoreSlot,@Nullable Function<@NotNull Integer,@NotNull Boolean> allowSlot) {
		if (endLine < startLine || endColumn < startColumn) throw new IllegalArgumentException();
		LinkedHashMap<Integer,LinkedHashMap<Integer,T>> pages = new LinkedHashMap<>();
		List<Integer> slots = new ArrayList<>();
		int slot;
		for (int i = startLine; i <= endLine; i++) for (int j = startColumn; j <= endColumn; j++) {
			slot = (i * 9) + j;
			if ((allowSlot == null || allowSlot.apply(slot)) && (ignoreSlot == null || !ignoreSlot.apply(slot))) slots.add(slot);
		}
		if (slots.isEmpty()) return pages;
		T item;
		LinkedHashMap<Integer,T> page;
		while (iter.hasNext()) {
			page = new LinkedHashMap<>();
			for (int i = 0; i < slots.size() && iter.hasNext(); i++) if ((item = convert.apply(iter.next())) != null && (isNull == null || !isNull.apply(item))) page.put(slots.get(i),item);
			pages.put(pages.size(),page);
		}
		return pages;
	}
	
	@NotNull
	public static <V,T> LinkedHashMap<@NotNull @Positive Integer,@NotNull LinkedHashMap<@NotNull @NonNegative Integer,@NotNull T>> generatePages(@NotNull Collection<@Nullable V> collection,@NotNull Function<@Nullable V,@Nullable T> convert,@Nullable Function<@NotNull T,@NotNull Boolean> isNull,
																																				 @NonNegative @Range(from = 0,to = 5) int startLine,@NonNegative @Range(from = 0,to = 5) int endLine,
																																				 @NonNegative @Range(from = 0,to = 8) int startColumn,@NonNegative @Range(from = 0,to = 8) int endColumn,
																																				 @Nullable Function<@NotNull Integer,@NotNull Boolean> ignoreSlot,@Nullable Function<@NotNull Integer,@NotNull Boolean> allowSlot) {
		return generatePages(collection.iterator(),convert,isNull,startLine,startColumn,endLine,endColumn,ignoreSlot,allowSlot);
	}
	
	@NotNull
	public static <V,T> LinkedHashMap<@NotNull @Positive Integer,@NotNull LinkedHashMap<@NotNull @NonNegative Integer,@NotNull T>> generatePages(@NotNull Iterator<@Nullable V> iter,@NotNull Function<@Nullable V,@Nullable T> convert,@Nullable Function<@NotNull T,@NotNull Boolean> isNull,
																																				 @NonNegative @Range(from = 0,to = 5) int startLine,@NonNegative @Range(from = 0,to = 5) int endLine,
																																				 @NonNegative @Range(from = 0,to = 8) int startColumn,@NonNegative @Range(from = 0,to = 8) int endColumn,
																																				 @Nullable Collection<@NotNull Integer> ignoreSlots,@Nullable Collection<@NotNull Integer> allowedSlots) {
		return generatePages(iter,convert,isNull,startLine,startColumn,endLine,endColumn,ignoreSlots == null ? null : ignoreSlots::contains,allowedSlots == null ? null : allowedSlots::contains);
	}
	
	@NotNull
	public static <V,T> LinkedHashMap<@NotNull @Positive Integer,@NotNull LinkedHashMap<@NotNull @NonNegative Integer,@NotNull T>> generatePages(@NotNull Collection<@Nullable V> collection,@NotNull Function<@Nullable V,@Nullable T> convert,@Nullable Function<@NotNull T,@NotNull Boolean> isNull,
																																				 @NonNegative @Range(from = 0,to = 5) int startLine,@NonNegative @Range(from = 0,to = 5) int endLine,
																																				 @NonNegative @Range(from = 0,to = 8) int startColumn,@NonNegative @Range(from = 0,to = 8) int endColumn,
																																			   @Nullable Collection<@NotNull Integer> ignoreSlots,@Nullable Collection<@NotNull Integer> allowedSlots) {
		return generatePages(collection.iterator(),convert,isNull,startLine,startColumn,endLine,endColumn,ignoreSlots,allowedSlots);
	}
	
	@NotNull
	public static <V> LinkedHashMap<@NotNull @Positive Integer,@NotNull LinkedHashMap<@NotNull @NonNegative Integer,@NotNull V>> generatePages(@NotNull Iterator<@Nullable V> iter,@Nullable Function<@NotNull V,@NotNull Boolean> isNull,
																																			   @NonNegative @Range(from = 0,to = 5) int startLine,@NonNegative @Range(from = 0,to = 5) int endLine,
																																			   @NonNegative @Range(from = 0,to = 8) int startColumn,@NonNegative @Range(from = 0,to = 8) int endColumn,
																																			   @Nullable Function<@NotNull Integer,@NotNull Boolean> ignoreSlot,@Nullable Function<@NotNull Integer,@NotNull Boolean> allowSlot) {
		return generatePages(iter,Utils::self,isNull,startLine,startColumn,endLine,endColumn,ignoreSlot,allowSlot);
	}
	
	@NotNull
	public static <V> LinkedHashMap<@NotNull @Positive Integer,@NotNull LinkedHashMap<@NotNull @NonNegative Integer,@NotNull V>> generatePages(@NotNull Collection<@Nullable V> collection,@Nullable Function<@NotNull V,@NotNull Boolean> isNull,
																																			   @NonNegative @Range(from = 0,to = 5) int startLine,@NonNegative @Range(from = 0,to = 5) int endLine,
																																			   @NonNegative @Range(from = 0,to = 8) int startColumn,@NonNegative @Range(from = 0,to = 8) int endColumn,
																																			   @Nullable Function<@NotNull Integer,@NotNull Boolean> ignoreSlot,@Nullable Function<@NotNull Integer,@NotNull Boolean> allowSlot) {
		return generatePages(collection.iterator(),isNull,startLine,startColumn,endLine,endColumn,ignoreSlot,allowSlot);
	}
	
	@NotNull
	public static <V> LinkedHashMap<@NotNull @Positive Integer,@NotNull LinkedHashMap<@NotNull @NonNegative Integer,@NotNull V>> generatePages(@NotNull Iterator<@Nullable V> iter,@Nullable Function<@NotNull V,@NotNull Boolean> isNull,
																																			   @NonNegative @Range(from = 0,to = 5) int startLine,@NonNegative @Range(from = 0,to = 5) int endLine,
																																			   @NonNegative @Range(from = 0,to = 8) int startColumn,@NonNegative @Range(from = 0,to = 8) int endColumn,
																																			   @Nullable Collection<@NotNull Integer> ignoreSlots,@Nullable Collection<@NotNull Integer> allowedSlots) {
		return generatePages(iter,isNull,startLine,startColumn,endLine,endColumn,ignoreSlots == null ? null : ignoreSlots::contains,allowedSlots == null ? null : allowedSlots::contains);
	}
	
	@NotNull
	public static <V> LinkedHashMap<@NotNull @Positive Integer,@NotNull LinkedHashMap<@NotNull @NonNegative Integer,@NotNull V>> generatePages(@NotNull Collection<@Nullable V> collection,@Nullable Function<@NotNull V,@NotNull Boolean> isNull,
																																			   @NonNegative @Range(from = 0,to = 5) int startLine,@NonNegative @Range(from = 0,to = 5) int endLine,
																																			   @NonNegative @Range(from = 0,to = 8) int startColumn,@NonNegative @Range(from = 0,to = 8) int endColumn,
																																			   @Nullable Collection<@NotNull Integer> ignoreSlots,@Nullable Collection<@NotNull Integer> allowedSlots) {
		return generatePages(collection.iterator(),isNull,startLine,startColumn,endLine,endColumn,ignoreSlots,allowedSlots);
	}
}