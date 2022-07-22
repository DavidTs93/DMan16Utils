package me.DMan16.DMan16Utils.Interfaces;

import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface PurchasableUpgrade<V,T> extends Purchasable<V,T> {
	@NotNull Component MAX_LEVEL_REACHED = Utils.noItalic(Component.translatable("menu.max_level_reached",NamedTextColor.GOLD));
	
	default boolean isOwned(@NotNull Player player,T val) {
		return false;
	}
	
	@Override
	@NotNull
	default ItemStack itemCantPurchase(@NotNull Player player,T val) {
		return Utils.addAfterLore(itemCanPurchaseAndAfford(player,val),List.of(Component.empty(),MAX_LEVEL_REACHED));
	}
}