package me.DMan16.POPUtils.Interfaces;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;

public interface Purchasable<V> {
	@NotNull
	V getCurrencyType();
	
	@Nullable
	BigInteger getPrice();
	
	boolean isPurchasable();
	
	boolean canAfford(@NotNull Player player);
	
	@NotNull
	ItemStack asPurchaseItem(@NotNull Player player);
	
	void purchase(@NotNull Player player);
}