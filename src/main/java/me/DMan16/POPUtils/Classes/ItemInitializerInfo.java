package me.DMan16.POPUtils.Classes;

import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ItemInitializerInfo {
	private final String key;
	private final Material material;
	private final Component name;
	private final int model;
	private final String skin;
	private final Function<ItemStack,ItemStack> alterItem;
	
	
	public ItemInitializerInfo(@NotNull String key, @NotNull Material material, @Nullable Component name, int model, @Nullable String skin,
							   @Nullable Function<ItemStack,ItemStack> alterItem) {
		this.key = key.toLowerCase();
		this.material = material;
		this.name = Utils.noItalic(name);
		this.model = model;
		this.skin = skin;
		this.alterItem = alterItem;
	}
	
	public ItemInitializerInfo(@NotNull String key, @NotNull Material material, @Nullable Component name, int model, @Nullable String skin) {
		this(key,material,name,model,skin,null);
	}
	
	public ItemInitializerInfo(@NotNull String key, @NotNull Material material, @Nullable Component name, int model, @Nullable Function<ItemStack,ItemStack> alterItem) {
		this(key,material,name,model,null,alterItem);
	}
	
	public ItemInitializerInfo(@NotNull String key, @NotNull Material material, @Nullable Component name, int model) {
		this(key,material,name,model,null,null);
	}
	
	public ItemInitializerInfo(@NotNull String key, @NotNull Material material, @Nullable Component name, @Nullable String skin,
							   @Nullable Function<ItemStack,ItemStack> alterItem) {
		this(key,material,name,0,skin,alterItem);
	}
	
	public ItemInitializerInfo(@NotNull String key, @NotNull Material material, @Nullable Component name, @Nullable Function<ItemStack,ItemStack> alterItem) {
		this(key,material,name,0,null,alterItem);
	}
	
	public ItemInitializerInfo(@NotNull String key, @NotNull Material material, @Nullable Component name, @Nullable String skin) {
		this(key,material,name,0,skin,null);
	}
	
	public ItemInitializerInfo(@NotNull String key, @NotNull Material material, @Nullable Component name) {
		this(key,material,name,0,null,null);
	}
	
	@NotNull
	@Contract(pure = true)
	public String key() {
		return key;
	}
	
	@NotNull
	@Contract(pure = true)
	public Material material() {
		return material;
	}
	
	@Nullable
	@Contract(pure = true)
	public Component name() {
		return name;
	}
	
	public int model() {
		return model;
	}
	
	@Nullable
	@Contract(pure = true)
	public String skin() {
		return skin;
	}
	
	@Nullable
	@Contract(pure = true)
	public Function<ItemStack,ItemStack> alterItem() {
		return alterItem;
	}
}