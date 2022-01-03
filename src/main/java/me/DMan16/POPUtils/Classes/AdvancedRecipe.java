package me.DMan16.POPUtils.Classes;

import me.DMan16.POPUtils.Interfaces.Itemable;
import me.DMan16.POPUtils.Interfaces.TriFunction;
import me.DMan16.POPUtils.Items.ItemableStack;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdvancedRecipe<V extends Inventory> {
	private final @NotNull TriFunction<@NotNull Itemable<?>,@Nullable ItemableStack,@Nullable Itemable<?>,@Nullable Trio<@Nullable Itemable<?>,@Nullable ItemableStack,@Nullable Itemable<?>>> function;
	
	public AdvancedRecipe(@NotNull TriFunction<@NotNull Itemable<?>,@Nullable ItemableStack,@Nullable Itemable<?>,@Nullable Trio<@Nullable Itemable<?>,@Nullable ItemableStack,@Nullable Itemable<?>>> function) {
		this.function = function;
	}
	
	@Nullable
	public Trio<@Nullable Itemable<?>,@Nullable ItemableStack,@NotNull Itemable<?>> getResult(@NotNull Itemable<?> first, @Nullable ItemableStack second, @Nullable Itemable<?> originalResult) {
		return function.apply(first,second,originalResult);
	}
}