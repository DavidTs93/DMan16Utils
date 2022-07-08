package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.Interfaces.ItemableAmountable;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ItemHolder implements ItemableAmountable<ItemHolder> {
	private final ItemStack item;
	
	/**
	 * The item is cloned
	 */
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
	
	@NotNull
	public ItemHolder copy(@Positive int amount) {
		return Utils.runGetOriginal(copy(),copy -> copy.item.setAmount(Math.min(amount,maxSize())));
	}
	
	@Positive
	public int amount() {
		return item.getAmount();
	}
}