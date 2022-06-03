package me.DMan16.DMan16Utils.Classes;

import me.DMan16.DMan16Utils.Interfaces.Copyable;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EquipmentHolder implements Copyable<EquipmentHolder>,Cloneable {
	private final ItemStack mainHand;
	private final ItemStack offHand;
	private final ItemStack helmet;
	private final ItemStack chestplate;
	private final ItemStack leggings;
	private final ItemStack boots;
	
	public EquipmentHolder(ItemStack mainHand,ItemStack offHand,ItemStack helmet,ItemStack chestplate,ItemStack leggings,ItemStack boots) {
		this.mainHand = mainHand;
		this.offHand = offHand;
		this.helmet = helmet;
		this.chestplate = chestplate;
		this.leggings = leggings;
		this.boots = boots;
	}
	
	public EquipmentHolder(@NotNull EntityEquipment equipment) {
		this(equipment.getItemInMainHand(),equipment.getItemInOffHand(),equipment.getHelmet(),equipment.getChestplate(),equipment.getLeggings(),equipment.getBoots());
	}
	
	private EquipmentHolder(@NotNull EquipmentHolder equipment) {
		this(equipment.mainHand,equipment.offHand,equipment.helmet,equipment.chestplate,equipment.leggings,equipment.boots);
	}
	
	@Nullable
	private static ItemStack itemOrNull(ItemStack item) {
		return Utils.isNull(item) ? null : item;
	}
	
	public ItemStack mainHand() {
		return itemOrNull(mainHand);
	}
	
	public ItemStack offHand() {
		return itemOrNull(offHand);
	}
	
	public ItemStack helmet() {
		return itemOrNull(helmet);
	}
	
	public ItemStack chestplate() {
		return itemOrNull(chestplate);
	}
	
	public ItemStack leggings() {
		return itemOrNull(leggings);
	}
	
	public ItemStack boots() {
		return itemOrNull(boots);
	}
	
	@NotNull
	public EquipmentHolder copy() {
		return new EquipmentHolder(this);
	}
	
	@Override
	public EquipmentHolder clone() {
		return copy();
	}
}
