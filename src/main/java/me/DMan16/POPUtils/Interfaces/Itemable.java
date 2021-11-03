package me.DMan16.POPUtils.Interfaces;

import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface Itemable<V extends Itemable<V>> extends Copyable<V> {
	@NotNull ItemStack asItem();
	
	default boolean give(@NotNull Player player) {
		return Utils.addItems(player,asItem()).isEmpty();
	}
	
	@NotNull Map<@NotNull String,?> toMap();
}