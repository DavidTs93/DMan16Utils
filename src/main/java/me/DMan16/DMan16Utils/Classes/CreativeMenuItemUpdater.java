package me.DMan16.DMan16Utils.Classes;

import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.function.BiFunction;

public final class CreativeMenuItemUpdater {
	private static final LinkedHashMap<@NotNull String,@NotNull BiFunction<@Nullable Integer,@NotNull ItemStack,@Nullable Itemable<?>>> CHECKERS = new LinkedHashMap<>();
	
	public static boolean register(@NotNull String name, @NotNull BiFunction<@Nullable Integer,@NotNull ItemStack,@Nullable Itemable<?>> checker) {
		return (name = Utils.fixKey(name)) != null && CHECKERS.putIfAbsent(name,checker) == null;
	}
	
	public static boolean remove(@NotNull String name) {
		return !name.isEmpty() && CHECKERS.remove(name) != null;
	}
	
	@Nullable
	@Contract("_,null -> null")
	public static Pair<@NotNull BiFunction<@Nullable Integer,@NotNull ItemStack,@Nullable Itemable<?>>,@NotNull Itemable<?>> getResult(Integer slot,ItemStack item) {
		Itemable<?> result;
		if (item != null) for (BiFunction<@Nullable Integer,@NotNull ItemStack,Itemable<?>> checker : CHECKERS.values()) {
			result = checker.apply(slot,item);
			if (result != null) return Pair.of(checker,result);
		}
		return null;
	}
}