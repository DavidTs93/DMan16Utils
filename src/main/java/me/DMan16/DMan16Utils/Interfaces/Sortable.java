package me.DMan16.DMan16Utils.Interfaces;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface Sortable {
	/**
	 * 0 = default, ascending
	 * 1 = default, descending
	 * 2 = rarity, ascending
	 * 3 = rarity, descending
	 * 4 = name, ascending
	 * 5 = name, descending
	 */
	@Unmodifiable List<ItemStack> SORTS = InterfacesUtils.createSorts();
	/**
	 * 0 = ascending
	 * 1 = descending
	 */
	@Unmodifiable List<ItemStack> ASCENDING_DESCENDING = InterfacesUtils.createAscendingDescending();
	
	void sort();
}