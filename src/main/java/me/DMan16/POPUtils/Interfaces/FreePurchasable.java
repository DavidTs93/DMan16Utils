package me.DMan16.POPUtils.Interfaces;

import me.DMan16.POPUtils.Classes.Empty;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;

public interface FreePurchasable<V> extends Purchasable<V,Empty> {
	default boolean isPurchasable() {
		return true;
	}
	
	@Nullable
	default ItemStack itemCantAfford(@NotNull Player player,Empty val) {
		return null;
	}
	
	@NotNull
	default BigInteger getPrice(@NotNull Player player,Empty val) {
		return BigInteger.ZERO;
	}
	
	default boolean isOwned(@NotNull Player player,Empty val) {
		return false;
	}
	
	@Nullable
	default ItemStack itemOwned(@NotNull Player player,Empty val) {
		return null;
	}
	
	@NotNull
	default ItemStack itemCanPurchaseAndAfford(@NotNull Player player,Empty val) {
		return new ItemStack(Material.STICK);
	}
	
	@NotNull ItemStack itemPurchase(@NotNull Player player, Empty empty);
}