package me.DMan16.POPUtils.Restrictions;

import me.DMan16.POPUtils.Events.Event;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public abstract class ItemRestrictItEvent extends Event {
	private final HumanEntity human;
	private final Restrictions.Restriction restriction;
	private final ItemStack item;
	
	protected ItemRestrictItEvent(HumanEntity human, Restrictions.Restriction restriction, ItemStack item) {
		this.human = human;
		this.restriction = restriction;
		this.item = item;
	}
	
	public final Restrictions.Restriction getRestriction() {
		return restriction;
	}
	
	public final ItemStack getItem() {
		return item;
	}
	
	public final HumanEntity human() {
		return human;
	}
	
	public final HumanEntity getHuman() {
		return human;
	}
}