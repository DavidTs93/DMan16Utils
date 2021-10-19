package me.DMan16.POPUtils.Interfaces;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface Itemable {
	@NotNull ItemStack asItem();
}