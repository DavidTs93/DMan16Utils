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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class Confirmation extends ListenerInventory {
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
		this.inventory.setItem(slotCancel,CANCEL);
		this.inventory.setItem(slotConfirm,this.canConfirm ? OK : (noConfirmLore == null ? OK_NO :
				Utils.cloneChange(OK_NO,false,null,true,noConfirmLore,-1,false)));
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