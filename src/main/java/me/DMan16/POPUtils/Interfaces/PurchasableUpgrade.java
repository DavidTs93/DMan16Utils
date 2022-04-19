package me.DMan16.POPUtils.Interfaces;

import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;

public interface PurchasableUpgrade<V,T> extends Purchasable<V,T> {
	default boolean isOwned(@NotNull Player player, T val) {
		return false;
	}
	
	@Override
	@NotNull
	default ItemStack itemCantPurchase(@NotNull Player player, T val) {
		return Utils.setDisplayName(itemCanPurchaseAndAfford(player,val),Utils.noItalic(Component.translatable("menu.prisonpop.max_level_reached",NamedTextColor.GOLD)));
	}
}