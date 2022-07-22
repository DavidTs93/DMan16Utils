package me.DMan16.DMan16Utils.Interfaces;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.function.Consumer;

public interface Purchasable<V,T> {
	@NotNull
	V getDefaultCurrencyType();
	
	@Nullable
	default V getDefaultCurrencyType2() {
		return null;
	}
	
	@Nullable
	default V getDefaultCurrencyType3() {
		return null;
	}
	
	@NotNull
	default V getCurrencyType() {
		return getDefaultCurrencyType();
	}
	
	@Nullable
	default V getCurrencyType2() {
		return getDefaultCurrencyType2();
	}
	
	@Nullable
	default V getCurrencyType3() {
		return getDefaultCurrencyType3();
	}
	
	@Nullable
	BigInteger getPrice(@NotNull Player player,T val);
	
	@Nullable
	default BigInteger getPrice2(@NotNull Player player,T val) {
		return null;
	}
	
	@Nullable
	default BigInteger getPrice3(@NotNull Player player,T val) {
		return null;
	}
	
	boolean isPurchasable();
	
	boolean isOwned(@NotNull Player player,T val);
	
	@Nullable
	ItemStack itemOwned(@NotNull Player player,T val);
	
	@Nullable
	ItemStack itemCantAfford(@NotNull Player player,T val);
	
	@Nullable
	default ItemStack itemCantPurchase(@NotNull Player player,T val) {
		return null;
	}
	
	@NotNull ItemStack itemCanPurchaseAndAfford(@NotNull Player player,T val);
	
	default boolean canAfford(@NotNull Player player,@NotNull BigInteger price,T val) {
		return true;
	}
	
	default void generatePurchaseItem(@NotNull Player player,T val,@NotNull Consumer<ItemStack> onGenerate,@Nullable Runnable onFail) {
		ItemStack item;
		if (isOwned(player,val)) item = itemOwned(player,val);
		else {
			BigInteger price = getPrice(player,val);
			if (!isPurchasable() || price == null || price.compareTo(BigInteger.ZERO) < 0) item = itemCantPurchase(player,val);
			else if (!canAfford(player,price,val)) item = itemCantAfford(player,val);
			else item = itemCanPurchaseAndAfford(player,val);
		}
		onGenerate.accept(item);
	}
	
	void purchase(@NotNull Player player,T val,@NotNull Runnable onSuccess,@Nullable Runnable onFail);
}