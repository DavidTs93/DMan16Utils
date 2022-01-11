package me.DMan16.POPUtils.Events;

import me.DMan16.POPUtils.Enums.ArmorSlot;
import me.DMan16.POPUtils.Enums.EquipMethod;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArmorEquipEvent extends PlayerEvent implements Cancellable {
	private boolean cancel = false;
	private final EquipMethod method;
	private final ArmorSlot slot;
	private final ItemStack oldArmor;
	private final ItemStack newArmor;
	private final int hotbar;
	
	public ArmorEquipEvent(@NotNull Player player, @NotNull EquipMethod method, @NotNull ArmorSlot slot, @Nullable ItemStack oldArmor, @Nullable ItemStack newArmor) {
		this(player,method,slot,oldArmor,newArmor,-1);
	}
	
	/**
	 * @param player the player
	 * @param method EquipMethod
	 * @param oldArmor old armor before the change
	 * @param newArmor new armor after the change
	 */
	public ArmorEquipEvent(@NotNull Player player, @NotNull EquipMethod method, @NotNull ArmorSlot slot, @Nullable ItemStack oldArmor, @Nullable ItemStack newArmor, int hotbar) {
		super(player);
		this.method = method;
		this.slot = slot;
		this.oldArmor = oldArmor;
		this.newArmor = newArmor;
		this.hotbar = hotbar;
	}
	
	public void setCancelled(boolean cancel) {
		if (isCancellable()) this.cancel = cancel;
	}
	
	public boolean isCancelled() {
		return cancel;
	}
	
	public boolean isCancellable() {
		return method != EquipMethod.DEATH && method != EquipMethod.BROKE;
	}
	
	@NotNull
	public EquipMethod getEquipMethod() {
		return method;
	}
	
	@NotNull
	public ArmorSlot getSlot() {
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