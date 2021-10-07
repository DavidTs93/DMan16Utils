package me.DMan16.POPUtils.Classes;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public record ItemInitializerInfo(@NotNull String ID, @Nullable Material material, @Nullable Component name, int model, @Nullable String skin,
								  @Nullable Function<ItemStack,ItemStack> alterItem) {
	public ItemInitializerInfo(@NotNull String ID, @Nullable Material material, @Nullable Component name, int model, @Nullable String skin) {
		this(ID,material,name,model,skin,null);
	}
	
	@Override
	public String ID() {
		return ID.toLowerCase();
	}
}