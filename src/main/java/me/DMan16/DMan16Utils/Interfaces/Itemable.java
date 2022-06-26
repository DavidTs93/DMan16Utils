package me.DMan16.DMan16Utils.Interfaces;

import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface Itemable<V extends Itemable<V>> extends Copyable<V>,Mappable {
	@NotNull ItemStack asItem();
	
	/**
	 * @param onSuccess - will run Sync
	 * @param onFail - will run ASync/Sync
	 */
	default void give(@NotNull Player player,@Nullable Runnable onSuccess,@Nullable Runnable onFail,@Nullable Map<@NotNull Integer,@NotNull Integer> toRemove,int ... toEmpty) {
		if (Utils.addFully(player,asItem(),toRemove,toEmpty).isEmpty()) {
			if (onSuccess != null) onSuccess.run();
		} else if (onFail != null) onFail.run();
	}
	
	@NotNull Material material();
	
	@NotNull Component giveComponent();
}