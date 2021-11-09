package me.DMan16.POPUtils.Items;

import me.DMan16.POPUtils.Interfaces.Itemable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public class ItemableInfo<V extends Itemable<?>> {
	private final Class<V> clazz;
	private final Function<Map<String,?>,@Nullable V> fromArguments;
	private final Function<@Nullable ItemStack,@Nullable V> fromItem;
	
	public ItemableInfo(@NotNull Class<V> clazz, @NotNull Function<@Nullable Map<String,?>,@Nullable V> fromArguments, @Nullable Function<@NotNull ItemStack,@Nullable V> fromItem) {
		this.clazz = clazz;
		this.fromArguments = fromArguments;
		this.fromItem = fromItem;
	}
	
	@NotNull
	public Class<V> getItemableClass() {
		return clazz;
	}
	
	@Nullable
	public V fromArguments(@Nullable Map<String,?> arguments) {
		return arguments == null ? null : fromArguments.apply(arguments);
	}
	
	@Nullable
	public V fromItem(@NotNull ItemStack item) {
		return fromItem == null ? null : fromItem.apply(item);
	}
}