package me.DMan16.DMan16Utils.Menus;

import me.DMan16.DMan16Utils.Classes.BasicItemableGeneral;
import me.DMan16.DMan16Utils.Classes.Pairs.Pair;
import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class ListenerTradeInputInventory extends ListenerInventoryPages {
	protected static final @NotNull NamespacedKey KEY = new NamespacedKey(DMan16UtilsMain.getInstance(),"trade_input_inventory");
	protected static final @NotNull @Unmodifiable List<@NotNull Integer> DEFAULT_REQUIRED_SLOTS = List.of(28,29,30,37,38,39);
	protected static final @NotNull @Unmodifiable List<@NotNull Integer> DEFAULT_PAYMENT_SLOTS = List.of(32,33,34,41,42,43);
	protected static final @NotNull String DEFAULT_OK_SKIN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2UyYTUzMGY0MjcyNmZhN2EzMWVmYWI4ZTQzZGFkZWUxODg5MzdjZjgyNGFmODhlYThlNGM5M2E0OWM1NzI5NCJ9fX0=";
	
	protected int slotItem;
	protected Itemable<?> item;
	protected List<@NotNull ItemStack> requiredPayment;
	protected List<@NotNull Integer> requiredSlots;
	protected List<@NotNull Integer> paymentSlots;
	protected int slotResult;
	protected List<ItemStack> leftoversForResult;
	
	public <V extends ListenerTradeInputInventory> ListenerTradeInputInventory(@Nullable InventoryHolder owner,@NotNull Player player,@Nullable Component name,@NotNull JavaPlugin plugin,@NotNull Itemable<?> item,
																			   @NotNull List<ItemStack> requiredPayment,@Nullable Function<V,@NotNull Boolean> doFirst) {
		super(owner,player,5,name,true,plugin,(ListenerTradeInputInventory menu) -> first(menu,item,requiredPayment,doFirst));
	}
	
	@SuppressWarnings("unchecked")
	private static <V extends ListenerTradeInputInventory> boolean first(@NotNull ListenerTradeInputInventory menu,@NotNull Itemable<?> item,@NotNull List<ItemStack> requiredPayment,@Nullable Function<V,@NotNull Boolean> doFirst) {
		menu.item = item;
		menu.slotItem = 4;
		menu.requiredPayment = requiredPayment.stream().filter(Utils::notNull).toList();
		if (requiredPayment.isEmpty()) return false;
		menu.requiredSlots = DEFAULT_REQUIRED_SLOTS;
		menu.paymentSlots = DEFAULT_PAYMENT_SLOTS;
		menu.slotResult = menu.size - 1;
		menu.leftoversForResult = null;
		return doFirst == null || doFirst.apply((V) menu);
	}
	
	protected Pair<@NotNull ItemStack,@NotNull String> lock(ItemStack item) {
		return Utils.isNull(item) ? null : lock(item,UUID.randomUUID().toString() + (ThreadLocalRandom.current().nextInt(1000000) + 1) + UUID.randomUUID());
	}
	
	@NotNull
	protected Pair<@NotNull ItemStack,@NotNull String> lock(@NotNull ItemStack item,@NotNull String str) {
		return Pair.of(Utils.setKeyPersistentDataContainer(item,KEY,PersistentDataType.STRING,str,true),str);
	}
	
	@Override
	protected void afterCloseUnregister(InventoryCloseEvent event) {
		clearPayments();
	}
	
	@Override
	protected boolean cancelCheck(int slot,int inventorySlot,@NotNull ClickType click,@NotNull InventoryAction action,int hotbarSlot) {
		return slot < size && !paymentSlots.contains(slot);
	}
	
	protected final void setResult() {
		setItem(slotResult,lock((leftoversForResult != null ? legalResultItem() : illegalResultItem()).asItem()).first());
	}
	
	protected void setPageContents() {
		setItem(slotItem,lock(item.asItem()).first());
		setResult();
		for (int i = 0; i < requiredSlots.size() && i < requiredPayment.size(); i++) setItem(requiredSlots.get(i),lock(requiredPayment.get(i)).first());
	}
	
	@Positive
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
		for (int i = 0; i < size; i++) setItem(i,isBorder(i) ? itemBorder().asItem() : (paymentSlots.contains(i) ? null : itemInside().asItem()));
	}
	
	protected boolean testBeforeResult() {
		return true;
	}
	
	@Override
	protected void otherSlot(@NotNull InventoryClickEvent event,int slot,ItemStack slotItem,@NotNull ClickType click) {
		if (slot == slotResult) {
			if (leftoversForResult != null) new BukkitRunnable() {
				public void run() {
					if (testBeforeResult()) result();
				}
			}.runTask(DMan16UtilsMain.getInstance());
		} else if ((paymentSlots.contains(slot) && !click.isCreativeAction()) || (click == ClickType.DOUBLE_CLICK || click.isShiftClick())) updateLegal();
	}
	
	protected void updateLegal() {
		leftoversForResult = checkLegalPayment();
		setResult();
	}
	
	@NotNull
	protected BasicItemableGeneral<?> illegalResultItem() {
		return itemBorder();
	}
	
	@NotNull
	protected BasicItemableGeneral<?> legalResultItem() {
		return itemOk();
	}
	
	protected void clearPayments() {
		Utils.givePlayer(player,player.getLocation(),false,paymentSlots.stream().map(this::getItem).filter(Utils::notNull).toList());
	}
	
	@NotNull
	protected List<ItemStack> removePaymentCheckLegal(@NotNull Inventory inv,@NotNull List<ItemStack> paymentItems) {
		if (paymentItems.isEmpty()) return paymentItems;
		List<ItemStack> requiredCopy = new ArrayList<>(requiredPayment);
		for (ItemStack item1 : requiredCopy) for (ItemStack item2 : paymentItems) if (Utils.sameItem(item1,item2)) {
			int amount = Utils.clamp(item1.getAmount(),0,item2.getAmount());
			item1.setAmount(item1.getAmount() - amount);
			item2.setAmount(item2.getAmount() - amount);
		}
		for (int i = 0; i < requiredSlots.size() && i < requiredCopy.size(); i++) inv.setItem(requiredSlots.get(i),Utils.subtract(requiredCopy.get(i),0));
		paymentItems = paymentItems.stream().map(i -> Utils.isNull(i) ? null : i).collect(Collectors.toList());
		while (paymentItems.size() > paymentSlots.size()) paymentItems.remove(paymentItems.size() - 1);
		for (int i = 0; i < paymentItems.size(); i++) inv.setItem(paymentSlots.get(i),Utils.subtract(paymentItems.get(i),0));
		return paymentItems;
	}
	
	/**
	 * @return null = illegal, !null = legal
	 */
	@Nullable
	protected List<ItemStack> checkLegalPayment(@NotNull List<ItemStack> paymentItems) {
		Inventory inv = Bukkit.createInventory(null,6 * 9);
		for (int i = 0; i < requiredSlots.size() && i < requiredPayment.size(); i++) inv.setItem(requiredSlots.get(i),requiredPayment.get(i));
		if (paymentItems.size() > paymentSlots.size()) {
			paymentItems = new ArrayList<>(paymentItems);
			while (paymentItems.size() > paymentSlots.size()) paymentItems.remove(paymentItems.size() - 1);
		}
		for (int i = 0; i < paymentItems.size(); i++) inv.setItem(paymentSlots.get(i),paymentItems.get(i));
		paymentItems = removePaymentCheckLegal(inv,paymentItems);
		int count = 0;
		for (int i : requiredSlots) if (Utils.notNull(inv.getItem(i))) count++;
		return count <= 0 ? paymentItems : null;
	}
	
	/**
	 * @return null = illegal, !null = legal - result is the leftovers
	 */
	@Nullable
	protected List<ItemStack> checkLegalPayment() {
		List<ItemStack> paymentItems = new ArrayList<>();
		ItemStack item;
		for (int slot : paymentSlots) if (!isEmpty(item = getItem(slot))) paymentItems.add(item);
		return checkLegalPayment(paymentItems);
	}
	
	protected abstract void result();
}