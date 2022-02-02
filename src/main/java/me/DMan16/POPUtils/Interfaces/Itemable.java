package me.DMan16.POPUtils.Interfaces;

import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface Itemable<V extends Itemable<V>> extends Copyable<V> {
	@NotNull ItemStack asItem();
	
	default boolean give(@NotNull Player player, @Nullable Map<@NotNull Integer,@NotNull Integer> toRemove, int ... toEmpty) {
		return !Utils.addFully(player,asItem(),toRemove,toEmpty).isEmpty();
	}
	
	@NotNull Material material();
	
	@NotNull Component giveComponent();
	
	@NotNull Map<@NotNull String,Object> toMap();
	
	@NotNull String ItemableKey();
	
	@NotNull
	default String ItemableString() {
		return ItemableKey() + ":" + Utils.getJSONString(toMap());
	}
}