package me.DMan16.POPUtils.Interfaces;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface Sortable {
	@Unmodifiable List<ItemStack> SORTS = InterfacesUtils.createSorts();
	
	void sort();
}