package me.DMan16.POPUtils.Interfaces;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;

public interface Purchasable<V,T> {
	@NotNull
	V getDefaultCurrencyType();
	
	@NotNull
	V getCurrencyType();
	
	@Nullable
	BigInteger getPrice(@NotNull Player player, T val);
	
	@Nullable
	default V getDefaultCurrencyType2() {
		return null;
	}
	
	@Nullable
	default V getCurrencyType2() {
		return null;
	}
	
	@Nullable
	default BigInteger getPrice2(@NotNull Player player, T val) {
		return null;
	}
	
	@Nullable
	default V getDefaultCurrencyType3() {
		return null;
	}
	
	@Nullable
	default V getCurrencyType3() {
		return null;
	}
	
	@Nullable
	default BigInteger getPrice3(@NotNull Player player, T val) {
		return null;
	}
	
	@Nullable
	default V getDefaultCurrencyType4() {
		return null;
	}
	
	@Nullable
	default V getCurrencyType4() {
		return null;
	}
	
	@Nullable
	default BigInteger getPrice4(@NotNull Player player, T val) {
		return null;
	}
	
	boolean isPurchasable();
	
	boolean canAfford(@NotNull Player player, @NotNull BigInteger price, T val);
	
	default boolean canAfford2(@NotNull Player player, @NotNull BigInteger price, T val) {
		return true;
	}
	
	default boolean canAfford3(@NotNull Player player, @NotNull BigInteger price, T val) {
		return true;
	}
	
	default boolean canAfford4(@NotNull Player player, @NotNull BigInteger price, T val) {
		return true;
	}
	
	boolean isOwned(@NotNull Player player, T val);
	
	@Nullable
	ItemStack itemOwned(@NotNull Player player, T val);
	
	@Nullable
	ItemStack itemCantAfford(@NotNull Player player, T val);
	
	@Nullable
	default ItemStack itemCantPurchase(@NotNull Player player, T val) {
		return null;
	}
	
	@NotNull
	ItemStack itemCanPurchaseAndAfford(@NotNull Player player, T val);
	
	@Nullable
	default ItemStack itemPurchase(@NotNull Player player, T val) {
		if (isOwned(player,val)) return itemOwned(player,val);
		BigInteger price = getPrice(player,val);
		if (!isPurchasable() || price == null || price.compareTo(BigInteger.ZERO) < 0) return itemCantPurchase(player,val);
		else if (!canAfford(player,price,val)) return itemCantAfford(player,val);
		else return itemCanPurchaseAndAfford(player,val);
	}
	
	boolean purchase(@NotNull Player player, T val);
}