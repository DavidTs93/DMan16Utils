package me.DMan16.DMan16Utils.Classes;

import me.DMan16.DMan16Utils.Interfaces.Copyable;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Equipment implements Copyable<Equipment>,Cloneable {
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
	
	public Equipment(@NotNull EntityEquipment equipment) {
		this(equipment.getItemInMainHand(),equipment.getItemInOffHand(),equipment.getHelmet(),equipment.getChestplate(),equipment.getLeggings(),equipment.getBoots());
	}
	
	private Equipment(@NotNull Equipment equipment) {
		this(equipment.mainHand,equipment.offHand,equipment.helmet,equipment.chestplate,equipment.leggings,equipment.boots);
	}
	
	@Nullable
	private static ItemStack cloneOrNull(ItemStack item) {
		return Utils.isNull(item) ? null : item.clone();
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
	
	@NotNull
	public Equipment copy() {
		return new Equipment(this);
	}
	
	@Override
	public Equipment clone() {
		return copy();
	}
}
