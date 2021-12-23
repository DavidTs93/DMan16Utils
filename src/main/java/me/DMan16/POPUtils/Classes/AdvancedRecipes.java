package me.DMan16.POPUtils.Classes;

import me.DMan16.POPUtils.Interfaces.Itemable;
import me.DMan16.POPUtils.Items.ItemUtils;
import me.DMan16.POPUtils.Items.ItemableStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

public class AdvancedRecipes<V extends Inventory> {
	private final LinkedHashMap<@NotNull String,@NotNull AdvancedRecipe<V>> RECIPES = new LinkedHashMap<>();
	
	public boolean register(@NotNull String name, @NotNull AdvancedRecipe<V> recipe) {
		return !name.isEmpty() && RECIPES.putIfAbsent(name,recipe) == null;
	}
	
	public boolean remove(@NotNull String name) {
		return !name.isEmpty() && RECIPES.remove(name) != null;
	}
	
	@Nullable
	@Contract("null,_ -> null")
	public Pair<@NotNull AdvancedRecipe<V>,@NotNull Trio<@Nullable Itemable<?>,@Nullable ItemableStack,@NotNull Itemable<?>>> getResult(Itemable<?> first,ItemableStack second) {
		Trio<@Nullable Itemable<?>,@Nullable ItemableStack,@NotNull Itemable<?>> result;
		if (first != null) for (AdvancedRecipe<V> recipe : RECIPES.values()) {
			result = recipe.getResult(first,second);
			if (result != null) return Pair.of(recipe,result);
		}
		return null;
	}
	
	@Nullable
	@Contract("null,_ -> null")
	public Pair<@NotNull AdvancedRecipe<V>,@NotNull Trio<@Nullable Itemable<?>,@Nullable ItemableStack,@NotNull Itemable<?>>> getResult(ItemStack first,ItemStack second) {
		return getResult(ItemUtils.of(first),ItemUtils.of(ItemableStack.class,second));
	}
}