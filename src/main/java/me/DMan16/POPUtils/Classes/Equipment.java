package me.DMan16.POPUtils.Classes;

import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Equipment {
	private final ItemStack mainHand;
	private final ItemStack offHand;
	private final ItemStack helmet;
	private final ItemStack chestplate;
	private final ItemStack leggings;
	private final ItemStack boots;
	
	public Equipment(ItemStack mainHand, ItemStack offHand, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
		this.mainHand = mainHand;
		this.offHand = offHand;
		this.helmet = helmet;
		this.chestplate = chestplate;
		this.leggings = leggings;
		this.boots = boots;
	}
	
	public Equipment(@NotNull Equipment equipment) {
		this(equipment.mainHand,equipment.offHand,equipment.helmet,equipment.chestplate,equipment.leggings,equipment.boots);
	}
	
	private static ItemStack cloneOrNull(ItemStack item) {
		if (item == null) return null;
		return Utils.clone(item);
	}
	
	public ItemStack mainHand() {
		return cloneOrNull(mainHand);
	}
	
	public ItemStack offHand() {
		return cloneOrNull(offHand);
	}
	
	public ItemStack helmet() {
		return cloneOrNull(helmet);
	}
	
	public ItemStack chestplate() {
		return cloneOrNull(chestplate);
	}
	
	public ItemStack leggings() {
		return cloneOrNull(leggings);
	}
	
	public ItemStack boots() {
		return cloneOrNull(boots);
	}
}
