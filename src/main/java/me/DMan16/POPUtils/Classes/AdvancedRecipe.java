package me.DMan16.POPUtils.Classes;

import me.DMan16.POPUtils.Classes.Trio;
import me.DMan16.POPUtils.Interfaces.Itemable;
import me.DMan16.POPUtils.Items.ItemableStack;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class AdvancedRecipe<V extends Inventory> {
	private final @NotNull BiFunction<@NotNull Itemable<?>,@Nullable ItemableStack,@Nullable Trio<@Nullable Itemable<?>,@Nullable ItemableStack,@NotNull Itemable<?>>> function;
	
	public AdvancedRecipe(@NotNull BiFunction<@NotNull Itemable<?>,@Nullable ItemableStack,@Nullable Trio<@Nullable Itemable<?>,@Nullable ItemableStack,@NotNull Itemable<?>>> function) {
		this.function = function;
	}
	
	@Nullable
	public Trio<@Nullable Itemable<?>,@Nullable ItemableStack,@NotNull Itemable<?>> getResult(@NotNull Itemable<?> first, @Nullable ItemableStack second) {
		return function.apply(first,second);
	}
}