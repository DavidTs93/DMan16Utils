package me.DMan16.POPUtils.Menus;

import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class Confirmation extends ListenerInventory {
	private static final ItemStack ITEM_CONFIRM_YES = Utils.makeItem(Material.GREEN_STAINED_GLASS_PANE,
			Component.translatable("gui.ok",NamedTextColor.GREEN).decoration(TextDecoration.ITALIC,false),ItemFlag.values());
	private static final ItemStack ITEM_CONFIRM_NO = Utils.makeItem(Material.GRAY_STAINED_GLASS_PANE,Component.translatable("gui.ok",NamedTextColor.GREEN).
			decoration(TextDecoration.ITALIC,false).decoration(TextDecoration.STRIKETHROUGH,true),ItemFlag.values());
	private static final ItemStack ITEM_CANCEL = Utils.makeItem(Material.RED_STAINED_GLASS_PANE,Component.translatable("gui.cancel",NamedTextColor.RED).
			decoration(TextDecoration.ITALIC,false),ItemFlag.values());
	
	protected int slotCancel;
	protected int slotConfirm;
	protected final Player player;
	protected final boolean canConfirm;
	
	protected Confirmation(@NotNull Player player, @NotNull Component menuName, @Nullable List<Component> noConfirmLore, @NotNull JavaPlugin plugin, Object ... objs) {
		super(Bukkit.getServer().createInventory(player,InventoryType.HOPPER,menuName));
		this.player = player;
		this.slotConfirm = 0;
		this.slotCancel = 4;
		first(objs);
		this.canConfirm = canConfirm();
		this.register(plugin);
		this.inventory.setItem(slotCancel,ITEM_CANCEL);
		this.inventory.setItem(slotConfirm,this.canConfirm ? ITEM_CONFIRM_YES : (noConfirmLore == null ? ITEM_CONFIRM_NO :
				Utils.cloneChange(ITEM_CONFIRM_NO,false,null,true,noConfirmLore,-1,false)));
		player.openInventory(this.inventory);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onClickEvent(InventoryClickEvent event) {
		if (!event.getView().getTopInventory().equals(this.inventory)) return;
		event.setCancelled(true);
		int slot = event.getRawSlot();
		if (slot > 4 || (!event.isRightClick() && !event.isLeftClick())) return;
		if (slot == slotConfirm && this.canConfirm) confirm();
		if (slot == slotCancel || (slot == slotConfirm && this.canConfirm)) done();
	}
	
	
	protected void first(Object ... objs) {}
	
	protected abstract boolean canConfirm();
	protected abstract void confirm();
	protected abstract void done();
}