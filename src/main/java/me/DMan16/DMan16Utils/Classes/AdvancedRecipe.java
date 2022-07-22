package me.DMan16.DMan16Utils.Classes;

import me.DMan16.DMan16Utils.Classes.Trios.Trio;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.Interfaces.TriFunction;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdvancedRecipe<V extends Inventory> {
	private final @NotNull TriFunction<@NotNull Itemable<?>,@Nullable Itemable<?>,@Nullable Itemable<?>,@Nullable Trio<@Nullable Itemable<?>,@Nullable Itemable<?>,@Nullable Itemable<?>>> function;
	
	public AdvancedRecipe(@NotNull TriFunction<@NotNull Itemable<?>,@Nullable Itemable<?>,@Nullable Itemable<?>,@Nullable Trio<@Nullable Itemable<?>,@Nullable Itemable<?>,@Nullable Itemable<?>>> function) {
		this.function = function;
	}
	
	@Nullable
	public Trio<@Nullable Itemable<?>,@Nullable Itemable<?>,@NotNull Itemable<?>> getResult(@NotNull Itemable<?> first,@Nullable Itemable<?> second,@Nullable Itemable<?> originalResult) {
		return function.apply(first,second,originalResult);
	}
}