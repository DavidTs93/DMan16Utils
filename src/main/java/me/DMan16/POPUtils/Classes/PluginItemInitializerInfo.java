package me.DMan16.POPUtils.Classes;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class PluginItemInitializerInfo extends BasicItemable {
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
	
	@Override
	@NotNull
	@Contract(pure = true)
	public PluginItemInitializerInfo copy() {
		return new PluginItemInitializerInfo(material,name,model,skin,alterItem);
	}
	
	@Override
	@NotNull
	public String ItemableKey() {
		return "plugin_item_initializer_info";
	}
}