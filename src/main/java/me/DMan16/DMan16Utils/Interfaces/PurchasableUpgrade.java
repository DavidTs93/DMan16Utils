package me.DMan16.DMan16Utils.Interfaces;

import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface PurchasableUpgrade<V,T> extends Purchasable<V,T> {
	default boolean isOwned(@NotNull Player player, T val) {
		return false;
	}
	
	@Override
	@NotNull
	default ItemStack itemCantPurchase(@NotNull Player player, T val) {
		return Utils.setDisplayName(itemCanPurchaseAndAfford(player,val),Utils.noItalic(Component.translatable("menu.max_level_reached",NamedTextColor.GOLD)));
	}
}