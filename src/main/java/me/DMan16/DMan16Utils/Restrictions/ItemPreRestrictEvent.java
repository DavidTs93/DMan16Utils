package me.DMan16.DMan16Utils.Restrictions;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

public final class ItemPreRestrictEvent extends ItemRestrictItEvent implements Cancellable {
	private boolean cancel;
	
	public ItemPreRestrictEvent(HumanEntity human, Restrictions.Restriction restriction, ItemStack item) {
		super(human,restriction,item);
	}
	
	public void setCancelled(final boolean cancel) {
		this.cancel = cancel;
	}
	
	public boolean isCancelled() {
		return cancel;
	}
}