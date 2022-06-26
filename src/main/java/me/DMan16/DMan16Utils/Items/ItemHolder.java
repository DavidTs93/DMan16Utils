package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ItemHolder implements Itemable<ItemHolder> {
	private final ItemStack item;
	
	public ItemHolder(@NotNull ItemStack item) {
		this.item = item.clone();
	}
	
	@NotNull
	public Material material() {
		return item.getType();
	}
	
	@NotNull
	public ItemStack asItem() {
		return item.clone();
	}
	
	@Override
	public @NotNull Map<@NotNull String,Object> toMap() {
		return new HashMap<>();
	}
	
	@NotNull
	public String mappableKey() {
		return "item_holder";
	}
	
	@NotNull
	public ItemHolder copy() {
		return new ItemHolder(item);
	}
	
	@NotNull
	public Component giveComponent() {
		return Component.empty();
	}
	
	@NotNull
	public String stringMappable() {
		return Utils.thisOrThatOrNull(Utils.ObjectToBase64(item),"");
	}
}