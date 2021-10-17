package me.DMan16.POPUtils.Menus;

import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public abstract class Confirmation extends ListenerInventory {
	protected int slotCancel;
	protected int slotConfirm;
	protected final Player player;
	
	@SuppressWarnings("unchecked")
	protected <V extends Confirmation> Confirmation(@NotNull Player player, @NotNull Component name, @Nullable List<Component> noConfirmLore,
													@NotNull JavaPlugin plugin, @Nullable Function<V,@NotNull Boolean> doFirst) {
		super(Bukkit.getServer().createInventory(player,InventoryType.HOPPER,noItalic(name)));
		this.player = player;
		this.slotConfirm = 0;
		this.slotCancel = 4;
		if (doFirst != null) if (!doFirst.apply((V) this)) throw new IllegalArgumentException();
		this.register(plugin);
		setItem(slotCancel,itemCancel());
		setItem(slotConfirm,canConfirm() ? itemOk() : (noConfirmLore == null ? itemOkNo() :
				Utils.cloneChange(itemOkNo(),false,null,true,noConfirmLore,-1,false)));
		open(plugin,player);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onClickEvent(InventoryClickEvent event) {
		if (!isThisInventory(event.getView().getTopInventory())) return;
		event.setCancelled(true);
		int slot = event.getRawSlot();
		if (slot > 4 || (!event.isRightClick() && !event.isLeftClick())) return;
		if (slot == slotConfirm && canConfirm()) {
			confirm();
			done();
		} else if (slot == slotCancel) done();
	}
	
	protected abstract boolean canConfirm();
	protected abstract void confirm();
	protected abstract void done();
}