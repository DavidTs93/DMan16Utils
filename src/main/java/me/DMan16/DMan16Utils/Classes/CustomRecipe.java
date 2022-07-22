package me.DMan16.DMan16Utils.Classes;

import me.DMan16.DMan16Utils.Classes.Pairs.Pair;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class CustomRecipe {
	private final @NotNull Function<@NotNull List<@Nullable Itemable<?>>,@NotNull Pair<@Nullable List<@Nullable Itemable<?>>,@NotNull Itemable<?>>> function;
	
	public CustomRecipe(@NotNull Function<@NotNull List<@Nullable Itemable<?>>,@NotNull Pair<@Nullable List<@Nullable Itemable<?>>,@NotNull Itemable<?>>> function) {
		this.function = function;
	}
	
	@Nullable
	public Pair<@Nullable List<@Nullable Itemable<?>>,@NotNull Itemable<?>> getResult(@NotNull List<@Nullable Itemable<?>> items) {
		return function.apply(items);
	}
}