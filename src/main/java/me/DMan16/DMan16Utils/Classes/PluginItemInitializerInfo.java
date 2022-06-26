package me.DMan16.DMan16Utils.Classes;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class PluginItemInitializerInfo extends BasicItemableGeneral<PluginItemInitializerInfo> {
	public PluginItemInitializerInfo(@NotNull Material material,@Nullable Component name,@Nullable Integer model,@Nullable String skin,@Nullable Function<ItemStack,ItemStack> alterItem) {
		super(material,name,model,skin,alterItem);
	}
	
	public PluginItemInitializerInfo(@NotNull Material material,@Nullable Component name,@Nullable Integer model,@Nullable String skin) {
		this(material,name,model,skin,null);
	}
	
	public PluginItemInitializerInfo(@NotNull Material material,@Nullable Component name,@Nullable Integer model,@Nullable Function<ItemStack,ItemStack> alterItem) {
		this(material,name,model,null,alterItem);
	}
	
	public PluginItemInitializerInfo(@NotNull Material material,@Nullable Component name,@Nullable Integer model) {
		this(material,name,model,null,null);
	}
	
	public PluginItemInitializerInfo(@NotNull Material material,@Nullable Component name,@Nullable String skin,@Nullable Function<ItemStack,ItemStack> alterItem) {
		this(material,name,null,skin,alterItem);
	}
	
	public PluginItemInitializerInfo(@NotNull Material material,@Nullable Component name,@Nullable Function<ItemStack,ItemStack> alterItem) {
		this(material,name,null,null,alterItem);
	}
	
	public PluginItemInitializerInfo(@NotNull Material material,@Nullable Component name,@Nullable String skin) {
		this(material,name,null,skin,null);
	}
	
	public PluginItemInitializerInfo(@NotNull Material material,@Nullable Component name) {
		this(material,name,null,null,null);
	}
	
	@NotNull
	public PluginItemInitializerInfo copy(@NotNull Material material,@Nullable Component name,@Nullable Integer model,@Nullable String skin,@Nullable Function<ItemStack,ItemStack> alterItem) {
		return new PluginItemInitializerInfo(material,name,model,skin,alterItem);
	}
	
	@NotNull
	public String mappableKey() {
		return "plugin_item_initializer_info";
	}
}