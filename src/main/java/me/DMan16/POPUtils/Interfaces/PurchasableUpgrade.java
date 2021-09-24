package me.DMan16.POPUtils.Interfaces;

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
	
	@NotNull
	default ItemStack itemCantPurchase(@NotNull Player player, T val) {
		ItemStack item = itemCanPurchaseAndAfford(player,val);
		ItemMeta meta = item.getItemMeta();
		meta.displayName(Component.translatable("menu.prisonpop.max_level_reached",NamedTextColor.GOLD).decoration(TextDecoration.ITALIC,false));
		item.setItemMeta(meta);
		return item;
	}
}