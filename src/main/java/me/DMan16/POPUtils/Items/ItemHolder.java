package me.DMan16.POPUtils.Items;

import me.DMan16.POPUtils.Interfaces.Itemable;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
	public String ItemableKey() {
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
	public String ItemableString() {
		return Utils.thisOrThatOrNull(Utils.ObjectToBase64(item),"");
	}
}