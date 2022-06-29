package me.DMan16.DMan16Utils.Interfaces;

import me.DMan16.DMan16Utils.Classes.Empty;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface QuickPurchasable<V> extends Purchasable<V,Empty> {
	default boolean isPurchasable() {
		return true;
	}
	
	@Nullable
	default ItemStack itemCantAfford(@NotNull Player player,Empty val) {
		return itemCanPurchaseAndAfford(player,val);
	}
	
	default boolean isOwned(@NotNull Player player,Empty val) {
		return false;
	}
	
	@Nullable
	default ItemStack itemOwned(@NotNull Player player,Empty val) {
		return null;
	}
}