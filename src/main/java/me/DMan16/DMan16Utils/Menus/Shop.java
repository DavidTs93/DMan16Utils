package me.DMan16.DMan16Utils.Menus;

import me.DMan16.DMan16Utils.Classes.Pairs.Pair;
import me.DMan16.DMan16Utils.Interfaces.Purchasable;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public abstract class Shop<V extends Purchasable<?,T>,T> extends ListenerInventoryPages {
	protected List<@NotNull HashMap<@NotNull Integer,@NotNull Pair<@NotNull V,@Nullable T>>> purchases;
	
	protected  <P extends Shop<V,T>> Shop(@NotNull Player player,int lines,@Nullable Component name,@Nullable Boolean border,@NotNull JavaPlugin plugin,@Nullable Function<P,@NotNull Boolean> doFirstMore) {
		super(player,player,lines,name,border,plugin,(Shop<V,T> shop) -> first(shop,doFirstMore));
	}
	
	protected void setPurchases(@NotNull Iterator<@NotNull V> iter) {
		purchases = new ArrayList<>(generatePages(iter,purchase -> Pair.of(purchase,(T) null),null,0,lines - 1,0,8,border() == null ? slot -> slot >= size - 9 : (border() ? this::isBorder : null),null).values());
	}
	
	protected void setPurchases(@NotNull List<@NotNull V> list) {
		setPurchases(list.iterator());
	}
	
	@Nullable
	protected T alterValueSetPagePurchases(@Nullable T val) {
		return val;
	}
	
	protected void setPagePurchases() {
		if (purchases != null && !purchases.isEmpty()) purchases.get(currentPage - 1).forEach((slot,info) -> info.first().generatePurchaseItem(player,alterValueSetPagePurchases(info.second()),item -> setItem(slot,item),() -> setFailedItem(slot)));
	}
	
	protected void setFailedItem(int slot) {}
	
	@SuppressWarnings("unchecked")
	private static <V extends Purchasable<?,T>,T,P extends Shop<V,T>> boolean first(@NotNull Shop<V,T> shop,@Nullable Function<P,@NotNull Boolean> doFirstMore) {
		shop.purchases = new ArrayList<>();
		shop.fancyButtons = true;
		if (doFirstMore != null) if (!doFirstMore.apply((P) shop)) return false;
		shop.setPurchases();
		return true;
	}
	
	@Positive
	public int maxPage() {
		return Math.max(1,purchases.size());
	}
	
	protected void otherSlot(@NotNull InventoryClickEvent event,int slot,ItemStack slotItem,@NotNull ClickType click) {
		HashMap<Integer,Pair<V,T>> page = purchases.isEmpty() ? null : purchases.get(currentPage - 1);
		Pair<V,T> purchase = page == null || page.isEmpty() ? null : page.get(slot);
		if (purchase != null) handlePurchase(event,slot,page,purchase,slotItem,click);
		else otherOtherSlot(event,slot,slotItem,click);
	}
	
	protected void setPageContents() {
		setPagePurchases();
		setMoreContents();
	}
	
	@Nullable
	protected T alterValueHandlePurchase(@Nullable T val) {
		return val;
	}
	
	protected void handlePurchase(@NotNull InventoryClickEvent event,int slot,@NotNull HashMap<@NotNull Integer,@NotNull Pair<@NotNull V,@Nullable T>> page,@NotNull Pair<@NotNull V,@Nullable T> purchase,ItemStack slotItem,@NotNull ClickType click) {
		purchase.first().purchase(player,alterValueHandlePurchase(purchase.second()),() -> handleAfterPurchase(true,event,slot,page,purchase,slotItem,click),() -> handleAfterPurchase(false,event,slot,page,purchase,slotItem,click));
	}
	
	protected void handleAfterPurchase(boolean purchaseSuccessful,@NotNull InventoryClickEvent event,int slot,@NotNull HashMap<@NotNull Integer,@NotNull Pair<@NotNull V,@Nullable T>> page,@NotNull Pair<@NotNull V,@Nullable T> purchase,ItemStack slotItem,@NotNull ClickType click) {
		if (!purchaseSuccessful) return;
		Utils.savePlayer(player);
		reloadPage();
	}
	
	@Override
	protected void beforeSetPageAndReset(int newPage) {
		alterPurchases(newPage);
	}
	
	protected void otherOtherSlot(@NotNull InventoryClickEvent event,int slot,ItemStack slotItem,@NotNull ClickType click) {}
	
	protected void alterPurchases(int page) {}
	
	protected void setMoreContents() {}
	
	protected abstract void setPurchases();
}