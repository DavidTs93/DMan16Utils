package me.DMan16.POPUtils.Restrictions;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public final class ItemPostRestrictEvent extends ItemRestrictItEvent {
	
	public ItemPostRestrictEvent(Restrictions.Restriction restriction, HumanEntity player, ItemStack item) {
		super(restriction,player,item);
	}
}