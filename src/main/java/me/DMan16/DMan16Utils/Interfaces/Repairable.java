package me.DMan16.DMan16Utils.Interfaces;

import me.DMan16.DMan16Utils.Items.ItemableStack;
import org.bukkit.Material;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Repairable {
	@Positive int fixDivide();
	
	ItemableAmountable<?> repairItem();
	
	@NonNegative int damage();
	
	/**
	 * @return leftover amount
	 */
	@NonNegative int fix(@NonNegative int amount);
	
	@Positive int maxDurability();
	
	default boolean shouldBreak() {
		return damage() >= maxDurability();
	}
	
	@NotNull Repairable addDamage(@NonNegative int amount);
	
	@NotNull Repairable reduceDamage(@NonNegative int amount);
	
	@NonNegative int damageItemStack();
	
	@NonNegative
	default int damageItemStack(@NotNull Material material) {
		int damage = damage();
		return damage <= 0 ? 0 : Math.max(1,damage / (maxDurability() * material.getMaxDurability()));
	}
	
	@Nullable
	static ItemableAmountable<?> repairItemFromMaterial(Material material) {
		Itemable<?> item = ItemableStack.ofOrSubstitute(material);
		return (item instanceof ItemableAmountable<?> i) ? i : null;
	}
}