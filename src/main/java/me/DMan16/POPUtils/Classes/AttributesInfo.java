package me.DMan16.POPUtils.Classes;

import com.google.common.collect.LinkedHashMultimap;
import me.DMan16.POPUtils.Enums.ArmorSlot;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.world.item.EnumArmorMaterial;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class AttributesInfo {
	private static final Component EMPTY = Component.text("  ");
	private static final @Unmodifiable Map<@NotNull EquipmentSlot,@NotNull UUID> SLOT_UUID_MAP = Map.of(EquipmentSlot.HEAD,UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB151"),
			EquipmentSlot.CHEST,UUID.fromString("9F3D476D-C118-4544-8365-64846904B482"),EquipmentSlot.LEGS,UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E03"),
			EquipmentSlot.FEET,UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B64"),EquipmentSlot.HAND,UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5C5"),
			EquipmentSlot.OFF_HAND,UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA6"));
	
	public final float health;
	public final float armor;
	public final float armorToughness;
	public final float knockbackResistance;
	public final float movementSpeed;
	public final float attackDamage;
	public final float luck;
	
	private AttributesInfo(Number health, Number armor, Number armorToughness, Number attackDamage, Number knockbackResistance, Number luck, Number movementSpeed) {
		this.health = f(health);
		this.armor = f(armor);
		this.armorToughness = f(armorToughness);
		this.attackDamage = f(attackDamage);
		this.knockbackResistance = f(knockbackResistance);
		this.luck = f(luck);
		this.movementSpeed = f(movementSpeed);
		if (this.health == 0 && this.armor == 0 && this.armorToughness == 0 && this.attackDamage == 0 && this.knockbackResistance == 0 && this.luck == 0 && this.movementSpeed == 0)
			throw new IllegalArgumentException();
	}
	
	private static float f(Number f) {
		return f == null ? 0 : Utils.roundAfterDot(f.floatValue(),2);
	}
	
	@Nullable
	public static AttributesInfo of(Number health, Number armor, Number armorToughness, Number attackDamage, Number knockbackResistance, Number luck, Number movementSpeed) {
		try {
			return new AttributesInfo(health,armor,armorToughness,attackDamage,knockbackResistance,luck,movementSpeed);
		} catch (Exception e) {}
		return null;
	}
	
	@NotNull
	public static AttributesInfo of(@NotNull EnumArmorMaterial armorEnum, @NotNull ArmorSlot slot) {
		return new AttributesInfo(0,armorEnum.b(slot.enumSlot),armorEnum.e(),0,armorEnum.f() * 100,0,0);
	}
	
	@Nullable
	@Contract(pure = true)
	public AttributesInfo join(AttributesInfo info) {
		return info == null ? this : of(this.health + info.health,this.armor + info.armor,this.armorToughness + info.armorToughness,
				this.attackDamage + info.attackDamage,this.knockbackResistance + info.knockbackResistance,this.luck + info.luck,
				this.movementSpeed + info.movementSpeed);
	}
	
	@NotNull
	public List<Component> lore() {
		List<Component> lore = new ArrayList<>();
		stringAttribute(health,false,lore,Attribute.GENERIC_MAX_HEALTH.translationKey(),NamedTextColor.RED);
		stringAttribute(armor,false,lore,Attribute.GENERIC_ARMOR.translationKey(),NamedTextColor.GRAY);
		stringAttribute(armorToughness,false,lore,Attribute.GENERIC_ARMOR_TOUGHNESS.translationKey(),NamedTextColor.GRAY);
		stringAttribute(attackDamage,false,lore,Attribute.GENERIC_ATTACK_DAMAGE.translationKey(),NamedTextColor.BLUE);
		stringAttribute(knockbackResistance,true,lore,Attribute.GENERIC_KNOCKBACK_RESISTANCE.translationKey(),NamedTextColor.DARK_PURPLE);
		stringAttribute(luck,false,lore,Attribute.GENERIC_LUCK.translationKey(),NamedTextColor.GREEN);
		stringAttribute(movementSpeed,true,lore,Attribute.GENERIC_MOVEMENT_SPEED.translationKey(),NamedTextColor.YELLOW);
		return lore;
	}
	
	@NotNull
	public ItemStack addAttributes(@NotNull ItemStack item, @NotNull String key, @NotNull EquipmentSlot slot) {
		LinkedHashMultimap<Attribute,AttributeModifier> map = LinkedHashMultimap.create();
		if (health != 0) map.put(Attribute.GENERIC_MAX_HEALTH,getAttribute(health,false,key,slot));
		if (armor != 0) map.put(Attribute.GENERIC_ARMOR,getAttribute(armor,false,key,slot));
		if (armorToughness != 0) map.put(Attribute.GENERIC_ARMOR_TOUGHNESS,getAttribute(armorToughness,false,key,slot));
		if (attackDamage != 0) map.put(Attribute.GENERIC_ATTACK_DAMAGE,getAttribute(attackDamage,false,key,slot));
		if (knockbackResistance != 0) map.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE,getAttribute(knockbackResistance,true,key,slot));
		if (luck != 0) map.put(Attribute.GENERIC_LUCK,getAttribute(luck,false,key,slot));
		if (movementSpeed != 0) map.put(Attribute.GENERIC_MOVEMENT_SPEED,getAttribute(movementSpeed,true,key,slot));
		item.setItemMeta(Utils.runGetOriginal(item.getItemMeta(),meta -> meta.setAttributeModifiers(map)));
		return item;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof AttributesInfo info) && health == info.health && armor == info.armor && armorToughness == info.armorToughness && knockbackResistance == info.knockbackResistance &&
				movementSpeed == info.movementSpeed && attackDamage == info.attackDamage && luck == info.luck;
	}
	
	@NotNull
	private AttributeModifier getAttribute(float amount, boolean percent, @NotNull String key, @NotNull EquipmentSlot slot) {
		return new AttributeModifier(SLOT_UUID_MAP.get(slot),key,percent ? amount / 100 : amount,AttributeModifier.Operation.ADD_NUMBER,slot);
	}
	
	private void stringAttribute(float amount, boolean percent, @NotNull List<Component> lore, @NotNull String translate, @NotNull TextColor color) {
		if (amount == 0) return;
		StringBuilder str = new StringBuilder();
		if (amount > 0) str.append("+");
		str.append(Utils.toString(amount,2));
		if (percent) str.append("%");
		lore.add(Utils.noItalic(EMPTY.append(Component.text(str.toString(),amount > 0 ? NamedTextColor.AQUA : NamedTextColor.DARK_RED)).append(Component.space()).
				append(Component.translatable(translate,color))));
	}
}