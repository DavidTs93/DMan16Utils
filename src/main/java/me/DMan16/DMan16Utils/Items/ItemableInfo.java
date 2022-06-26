package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.Classes.MappableInfo;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public final class ItemableInfo<V extends Itemable<?>> extends MappableInfo<V,ItemStack> {
	public ItemableInfo(@NotNull Class<V> clazz, @NotNull Function<@Nullable Map<String,?>,@Nullable V> fromArguments, @Nullable Function<@NotNull ItemStack,@Nullable V> fromItem) {
		super(clazz,fromArguments,fromItem);
	}
	
	@Nullable
	public V fromItem(@NotNull ItemStack item) {
		return fromObject(item);
	}
}