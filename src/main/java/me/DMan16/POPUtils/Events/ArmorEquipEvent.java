package me.DMan16.POPUtils.Events;

import me.DMan16.POPUtils.Enums.EquipMethod;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ArmorEquipEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancel = false;
	private final EquipMethod method;
	private final EquipmentSlot slot;
	private final ItemStack oldArmor;
	private final ItemStack newArmor;
	private final int hotbar;
	
	public ArmorEquipEvent(@NotNull Player player, @NotNull EquipMethod method, @NotNull EquipmentSlot slot, @Nullable ItemStack oldArmor, @Nullable ItemStack newArmor) {
		this(player,method,slot,oldArmor,newArmor,-1);
	}
	
	/**
	 * @param player the player
	 * @param method EquipMethod
	 * @param oldArmor old armor before the change
	 * @param newArmor new armor after the change
	 */
	public ArmorEquipEvent(@NotNull Player player, @NotNull EquipMethod method, @NotNull EquipmentSlot slot, @Nullable ItemStack oldArmor, @Nullable ItemStack newArmor, int hotbar) {
		super(player);
		this.method = method;
		this.slot = slot;
		this.oldArmor = oldArmor;
		this.newArmor = newArmor;
		this.hotbar = hotbar;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public final @NotNull HandlerList getHandlers() {
		return handlers;
	}
	
	public final void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	
	public final boolean isCancelled() {
		return cancel;
	}
	
	@NotNull
	public EquipMethod getEquipMethod() {
		return method;
	}
	
	@NotNull
	public EquipmentSlot getSlot() {
		return slot;
	}
	
	@Nullable
	public ItemStack getOldArmor() {
		return oldArmor;
	}
	
	@Nullable
	public ItemStack getNewArmor() {
		return newArmor;
	}
	
	public int getHotbarSlot() {
		return hotbar;
	}
	
	public boolean hasHotbarSlot() {
		return hotbar > 0;
	}
}