package me.DMan16.POPUtils.Restrictions;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

public final class ItemPreRestrictEvent extends ItemRestrictItEvent implements Cancellable {
	private boolean cancel;
	
	public ItemPreRestrictEvent(Restrictions.Restriction restriction, HumanEntity player, ItemStack item) {
		super(restriction,player,item);
	}
	
	public final void setCancelled(final boolean cancel) {
		this.cancel = cancel;
	}
	
	public final boolean isCancelled() {
		return cancel;
	}
}