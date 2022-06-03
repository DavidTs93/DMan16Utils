package me.DMan16.DMan16Utils.Restrictions;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public final class ItemPostRestrictEvent extends ItemRestrictItEvent {
	public ItemPostRestrictEvent(HumanEntity human, Restrictions.Restriction restriction, ItemStack item) {
		super(human,restriction,item);
	}
}