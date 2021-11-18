package me.DMan16.POPUtils.Interfaces;

import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface Itemable<V extends Itemable<V>> extends Copyable<V> {
	@NotNull ItemStack asItem();
	
	default boolean give(@NotNull Player player) {
		return Utils.addFully(player,asItem());
	}
	
	@NotNull Map<@NotNull String,?> toMap();
	
	@NotNull String ItemableKey();
	
	@NotNull
	default String ItemableString() {
		return ItemableKey() + ":" + Utils.getJSONString(toMap());
	}
}