package me.DMan16.DMan16Utils.Classes;

import me.DMan16.DMan16Utils.Classes.Pairs.Pair;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class CustomRecipes {
	private final LinkedHashMap<@NotNull String,@NotNull CustomRecipe> RECIPES = new LinkedHashMap<>();
	
	public boolean register(@NotNull String name,@NotNull CustomRecipe recipe) {
		return (name = Utils.fixKey(name)) != null && RECIPES.putIfAbsent(name,recipe) == null;
	}
	
	public boolean remove(@NotNull String name) {
		return !name.isEmpty() && RECIPES.remove(name) != null;
	}
	
	@Nullable
	@Contract("null -> null")
	public Pair<@NotNull CustomRecipe,@NotNull Pair<@Nullable List<@Nullable Itemable<?>>,@NotNull Itemable<?>>> getResult(List<Itemable<?>> items) {
		Pair<@Nullable List<@Nullable Itemable<?>>,@NotNull Itemable<?>> result;
		if (items != null && !items.isEmpty() && items.stream().anyMatch(Objects::nonNull)) for (CustomRecipe recipe : RECIPES.values()) {
			result = recipe.getResult(items);
			if (result != null) return Pair.of(recipe,result);
		}
		return null;
	}
}