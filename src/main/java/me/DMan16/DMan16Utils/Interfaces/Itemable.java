package me.DMan16.DMan16Utils.Interfaces;

import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface Itemable<V extends Itemable<V>> extends Copyable<V>,Mappable {
	@NotNull ItemStack asItem();
	
	@NotNull Material material();
	
	@NotNull Component giveComponent();
	
	default boolean canPassAsThis(@NotNull Itemable<?> item) {
		return equals(item);
	}
	
	/**
	 * @param onSuccess - will run Sync
	 * @param onFail - will run ASync/Sync
	 */
	default void give(@NotNull Player player,@Nullable Runnable onSuccess,@Nullable Runnable onFail,@Nullable Map<@NotNull Integer,@NotNull Integer> toRemove,int ... toEmpty) {
		if (!Utils.addFully(player,asItem(),toRemove,toEmpty).isEmpty()) {
			if (onSuccess != null) onSuccess.run();
		} else if (onFail != null) onFail.run();
	}
	
	@NotNull
	default Component commandGiveAmountGetMessage(@NotNull Player player,int amount) {
		int given = amount <= 0 ? 0 : amount - Utils.addItems(player,Utils.asAmount(asItem(),amount)).stream().map(ItemStack::getAmount).reduce(0,Integer::sum);
		Component msg;
		if (given > 0) {
			msg = giveComponent();
			if (given > 1) msg = msg.append(Component.text(" x" + given,NamedTextColor.WHITE));
			msg = Component.text("Gave ",NamedTextColor.GREEN).append(msg);
		} else msg = Component.text("No items given",NamedTextColor.GOLD);
		return msg;
	}
}