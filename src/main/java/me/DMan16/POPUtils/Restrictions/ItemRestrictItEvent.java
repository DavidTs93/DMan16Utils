package me.DMan16.POPUtils.Restrictions;

import me.DMan16.POPUtils.Events.Event;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public abstract class ItemRestrictItEvent extends Event {
	private final Restrictions.Restriction restriction;
	private final HumanEntity player;
	private final ItemStack item;
	
	protected ItemRestrictItEvent(Restrictions.Restriction restriction, HumanEntity player, ItemStack item) {
		this.restriction = restriction;
		this.player = player;
		this.item = item;
	}
	
	public final Restrictions.Restriction getRestriction() {
		return restriction;
	}
	
	public final HumanEntity getPlayer() {
		return player;
	}
	
	public final ItemStack getItem() {
		return item;
	}
}