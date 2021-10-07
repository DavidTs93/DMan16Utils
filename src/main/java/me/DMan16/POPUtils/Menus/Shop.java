package me.DMan16.POPUtils.Menus;

import me.DMan16.POPUtils.Classes.Pair;
import me.DMan16.POPUtils.Interfaces.Purchasable;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public abstract class Shop<V extends Purchasable<?,T>,T> extends ListenerInventoryPages {
	protected List<@NotNull HashMap<@NotNull Integer,@NotNull Pair<@NotNull V,@Nullable T>>> purchases;
	
	public <P extends Shop<V,T>> Shop(@NotNull Player player, int lines, @NotNull Component name, @NotNull String menuID, @NotNull JavaPlugin plugin,
									  @Nullable Function<P,@NotNull Boolean> doFirstMore) {
		super(player,player,lines,name,menuID,plugin,(Shop<V,T> shop) -> first(shop,doFirstMore));
	}
	
	protected void setPagePurchases() {
		purchases.get(currentPage - 1).forEach((slot,info) -> inventory.setItem(slot,info.first().itemPurchase(player,info.second())));
	}
	
	@SuppressWarnings("unchecked")
	private static <V extends Purchasable<?,T>,T,P extends Shop<V,T>> boolean first(@NotNull Shop<V,T> shop, @Nullable Function<P,@NotNull Boolean> doFirstMore) {
		shop.purchases = new ArrayList<>();
		shop.fancyButtons = true;
		if (doFirstMore != null) if (!doFirstMore.apply((P) shop)) return false;
		shop.setPurchases();
		return true;
	}
	
	public int maxPage() {
		return Math.max(1,purchases.size());
	}
	
	protected void otherSlot(@NotNull InventoryClickEvent event, int slot, ItemStack slotItem, @NotNull ClickType click) {
		HashMap<Integer,Pair<V,T>> page = purchases.get(currentPage - 1);
		Pair<V,T> purchase = page.get(slot);
		if (purchase != null) handlePurchase(event,slot,page,purchase,slotItem,click);
		else otherOtherSlot(event,slot,slotItem,click);
	}
	
	protected void setPageContents() {
		setPagePurchases();
		setMoreContents();
	}
	
	protected void handlePurchase(@NotNull InventoryClickEvent event, int slot, @NotNull HashMap<@NotNull Integer,@NotNull Pair<@NotNull V,@Nullable T>> page,
										   @NotNull Pair<@NotNull V,@Nullable T> purchase, ItemStack slotItem, @NotNull ClickType click) {
		handleAfterPurchase(purchase.first().purchase(player,purchase.second()),event,slot,page,purchase,slotItem,click);
	}
	
	protected void handleAfterPurchase(boolean purchaseSuccessful, @NotNull InventoryClickEvent event, int slot, @NotNull HashMap<@NotNull Integer,
			@NotNull Pair<@NotNull V,@Nullable T>> page, @NotNull Pair<@NotNull V,@Nullable T> purchase, ItemStack slotItem, @NotNull ClickType click) {
		setPage(currentPage);
	}
	
	@Override
	protected void beforeSetPage(int newPage) {
		alterPurchases(newPage);
	}
	
	protected void otherOtherSlot(@NotNull InventoryClickEvent event, int slot, ItemStack slotItem, @NotNull ClickType click) {}
	
	protected void alterPurchases(int page) {}
	
	protected void setMoreContents() {}
	
	protected abstract void setPurchases();
}