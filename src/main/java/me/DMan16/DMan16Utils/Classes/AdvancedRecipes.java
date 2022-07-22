package me.DMan16.DMan16Utils.Classes;

import me.DMan16.DMan16Utils.Classes.Pairs.Pair;
import me.DMan16.DMan16Utils.Classes.Trios.Trio;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.Items.ItemUtils;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

public class AdvancedRecipes<V extends Inventory> {
	private final LinkedHashMap<@NotNull String,@NotNull AdvancedRecipe<V>> RECIPES = new LinkedHashMap<>();
	
	public boolean register(@NotNull String name,@NotNull AdvancedRecipe<V> recipe) {
		return (name = Utils.fixKey(name)) != null && RECIPES.putIfAbsent(name,recipe) == null;
	}
	
	public boolean remove(@NotNull String name) {
		return !name.isEmpty() && RECIPES.remove(name) != null;
	}
	
	@Nullable
	@Contract("null,_,_ -> null")
	public Pair<@NotNull AdvancedRecipe<V>,@NotNull Trio<@Nullable Itemable<?>,@Nullable Itemable<?>,@NotNull Itemable<?>>> getResult(Itemable<?> first,Itemable<?> second,Itemable<?> originalResult) {
		Trio<@Nullable Itemable<?>,@Nullable Itemable<?>,@NotNull Itemable<?>> result;
		if (first != null) for (AdvancedRecipe<V> recipe : RECIPES.values()) {
			result = recipe.getResult(first,second,originalResult);
			if (result != null) return Pair.of(recipe,result);
		}
		return null;
	}
	
	@Nullable
	@Contract("null,_,_ -> null")
	public Pair<@NotNull AdvancedRecipe<V>,@NotNull Trio<@Nullable Itemable<?>,@Nullable Itemable<?>,@NotNull Itemable<?>>> getResult(ItemStack first,ItemStack second,ItemStack originalResult) {
		return getResult(ItemUtils.of(first),ItemUtils.of(second),ItemUtils.of(originalResult));
	}
}