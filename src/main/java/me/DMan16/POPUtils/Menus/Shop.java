package me.DMan16.POPUtils.Menus;

import me.DMan16.POPUtils.Interfaces.Purchasable;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Shop<V extends Purchasable<?>> extends ListenerInventoryPages {
	protected final List<@NotNull HashMap<@NotNull Integer,@NotNull V>> purchases = new ArrayList<>();
	
	public Shop(@NotNull Player player, int lines, @NotNull Component name, @NotNull JavaPlugin plugin, Object ... objs) {
		super(player,player,lines,name,plugin,objs);
	}
	
	@Override
	protected void first(Object ... objs) {
		resetWithBorder = true;
		fancyButtons = true;
		firstMore(objs);
		setPurchases();
	}
	
	public int maxPage() {
		return Math.max(1,purchases.size());
	}
	
	protected void otherSlot(@NotNull InventoryClickEvent event, int slot, ItemStack slotItem, @NotNull ClickType click) {
		HashMap<Integer,V> page = purchases.get(currentPage - 1);
		V purchase = page.get(slot);
		if (purchase != null) purchase.purchase(player);
		else otherOtherSlot(event,slot,slotItem,click);
	}
	
	@Override
	protected void beforeSetPage(int newPage) {
		alterPurchases(newPage);
	}
	
	protected void setPageContents() {
		setPagePurchases(purchases.get(currentPage - 1));
		setMoreContents();
	}
	
	protected void setPagePurchases(@NotNull HashMap<@NotNull Integer, @NotNull V> page) {
		page.forEach((slot,item) -> inventory.setItem(slot,item.asPurchaseItem(player)));
	}
	
	protected void firstMore(Object ... objs) {}
	
	protected void otherOtherSlot(@NotNull InventoryClickEvent event, int slot, ItemStack slotItem, @NotNull ClickType click) {}
	
	protected void alterPurchases(int page) {}
	
	protected void setMoreContents() {}
	
	protected abstract void setPurchases();
}