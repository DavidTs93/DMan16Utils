package me.DMan16.DMan16Utils.Interfaces;

import me.DMan16.DMan16Utils.Restrictions.Restrictions;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Itemable<V extends Itemable<V>> extends Copyable<V>,Mappable {
	@NotNull ItemStack asItem();
	@NotNull Material material();
	@NotNull Component giveComponent();
	
	static boolean addFully(@NotNull Itemable<?> item,@NotNull Player player,@Nullable Map<@NotNull Integer,@NotNull Integer> toRemove,int ... toEmpty) {
		return !Utils.addFully(player,item.asItem(),toRemove,toEmpty).isEmpty();
	}
	
	default boolean canPassAsThis(@NotNull Itemable<?> item) {
		return equals(item);
	}
	
	/**
	 * @param onSuccess - will run Sync
	 * @param onFail - will run ASync/Sync
	 */
	default void give(@NotNull Player player,@Nullable Runnable onSuccess,@Nullable Runnable onFail,@Nullable Map<@NotNull Integer,@NotNull Integer> toRemove,int ... toEmpty) {
		giveItem(player,onSuccess,onFail,toRemove,toEmpty);
	}
	
	/**
	 * @param onSuccess - will run Sync
	 * @param onFail - will run ASync/Sync
	 */
	default void giveItem(@NotNull Player player,@Nullable Runnable onSuccess,@Nullable Runnable onFail,@Nullable Map<@NotNull Integer,@NotNull Integer> toRemove,int ... toEmpty) {
		if (addFully(this,player,toRemove,toEmpty)) {
			if (onSuccess != null) onSuccess.run();
		} else if (onFail != null) onFail.run();
	}
	
	@NotNull
	default Component commandGiveItemAmountGetMessage(@NotNull Player player,int amount) {
		int given;
		if (amount <= 0) given = 0;
		else {
			ItemStack item = asItem();
			List<ItemStack> items;
			if (Restrictions.Unstackable.is(item)) {
				items = new ArrayList<>();
				items.add(item);
				while (items.size() < amount) items.add(asItem());
			} else items = Utils.asAmount(item,amount);
			given = amount - Utils.addItems(player,items).stream().map(ItemStack::getAmount).reduce(0,Integer::sum);
		}
		Component msg;
		if (given > 0) {
			msg = giveComponent();
			if (given > 1) msg = msg.append(Component.text(" x" + given,NamedTextColor.WHITE));
			msg = Component.text("Gave ",NamedTextColor.GREEN).append(msg);
		} else msg = Component.text("No items given",NamedTextColor.GOLD);
		return msg;
	}
}