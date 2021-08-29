package me.DMan16.POPUtils.Restrictions;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public abstract class ItemRestrictItEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final Restrictions.Restriction restriction;
	private final HumanEntity player;
	private final ItemStack item;
	
	public ItemRestrictItEvent(Restrictions.Restriction restriction, HumanEntity player, ItemStack item) {
		this.restriction = restriction;
		this.player = player;
		this.item = item;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public final HandlerList getHandlers() {
		return handlers;
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