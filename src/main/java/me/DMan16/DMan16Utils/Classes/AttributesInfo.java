package me.DMan16.DMan16Utils.Classes;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import me.DMan16.DMan16Utils.Enums.EquipSlot;
import me.DMan16.DMan16Utils.NMSWrappers.ItemWrapper;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TridentItem;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.*;

import java.util.*;

public final class AttributesInfo {
	private static final Component EMPTY = Utils.noItalic(Component.text("  "));
	private static final @Unmodifiable Map<@NotNull EquipmentSlot,@NotNull UUID> SLOT_UUID_MAP = Map.of(EquipmentSlot.HEAD,UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB151"),
			EquipmentSlot.CHEST,UUID.fromString("9F3D476D-C118-4544-8365-64846904B482"),EquipmentSlot.LEGS,UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E03"),
			EquipmentSlot.FEET,UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B64"),EquipmentSlot.HAND,UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5C5"),
			EquipmentSlot.OFF_HAND,UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA6"));
	public static final int HSL_JUMP = 20;
	
	public final float health;
	public final float armor;
	public final float armorToughness;
	public final float knockbackResistance;
	public final float attackDamage;
	public final float attackDamagePercent;
	public final @Nullable Float rangedMult;
	private final @Nullable @Positive @Range(from = 1,to = 100) Integer attackSpeed;
	public final float luck;
	public final float movementSpeed;
	
	public int score() {
		int score = 0;
		score += health / 2;
		score += armor / 2;
		score += (armorToughness + 2) / 4;
		score += knockbackResistance / 10;
		score += attackDamage * 2;
		score += attackDamagePercent / 5;
		if (rangedMult != null) score += rangedMult / 5;
		if (attackSpeed != null) score += attackSpeed / HSL_JUMP;
		score += luck;
		score += movementSpeed / 10;
		return score;
	}
	
	private AttributesInfo(@Nullable Number health,@Nullable Number armor,@Nullable Number armorToughness,@Nullable Number knockbackResistance,@Nullable Number attackDamage,@Nullable Number attackDamagePercent,@Nullable Number rangedMult,@Nullable Number attackSpeed,
						   @Nullable Number luck,@Nullable Number movementSpeed) {
		this.health = f(health);
		this.armor = f(armor);
		this.armorToughness = f(armorToughness);
		this.attackDamage = f(attackDamage);
		this.attackDamagePercent = f(attackDamagePercent);
		this.rangedMult = rangedMult == null ? null : f(rangedMult);
		this.attackSpeed = attackSpeed == null ? null : Utils.clamp(attackSpeed.intValue(),0,99) + 1;
		this.knockbackResistance = f(knockbackResistance);
		this.luck = f(luck);
		this.movementSpeed = f(movementSpeed);
		if (this.health == 0 && this.armor == 0 && this.armorToughness == 0 && this.attackDamage == 0 && this.rangedMult == null && this.knockbackResistance == 0 && this.luck == 0 && this.movementSpeed == 0) throw new IllegalArgumentException();
	}
	
	private static float f(@Nullable Number f) {
		return f == null ? 0 : Utils.roundAfterDot(f.floatValue(),2);
	}
	
	@Nullable
	public static AttributesInfo of(@Nullable Number health,@Nullable Number armor,@Nullable Number armorToughness,@Nullable Number knockbackResistance,@Nullable Number attackDamage,@Nullable Number attackDamagePercent,@Nullable Number rangedMult,@Nullable Number attackSpeed,
									@Nullable Number luck,@Nullable Number movementSpeed) {
		try {
			return new AttributesInfo(health,armor,armorToughness,knockbackResistance,attackDamage,attackDamagePercent,rangedMult,attackSpeed,luck,movementSpeed);
		} catch (Exception e) {}
		return null;
	}
	
	@Nullable
	public static AttributesInfo of(@Nullable Number health,@Nullable Number armor,@Nullable Number armorToughness,@Nullable Number knockbackResistance,@Nullable Number attackDamage,@Nullable Number luck,@Nullable Number movementSpeed) {
		try {
			return new AttributesInfo(health,armor,armorToughness,knockbackResistance,attackDamage,null,null,null,luck,movementSpeed);
		} catch (Exception e) {}
		return null;
	}
	
	@Nullable
	private static Double d(@NotNull Multimap<net.minecraft.world.entity.ai.attributes.Attribute,net.minecraft.world.entity.ai.attributes.AttributeModifier> map,@NotNull net.minecraft.world.entity.ai.attributes.Attribute attribute) {
		Collection<net.minecraft.world.entity.ai.attributes.AttributeModifier> attributes = map.get(attribute);
		if (attributes.isEmpty()) return null;
		Double amount = Utils.roundAfterDot(attributes.iterator().next().getAmount(),2);
		if (amount == null) return null;
		if (attribute == Attributes.KNOCKBACK_RESISTANCE) amount *= 100;
		else if (attribute == Attributes.ATTACK_SPEED) amount = (amount + 4) * 25;
		return amount;
	}
	
	@NotNull
	public static AttributesInfo of(ItemWrapper.@NotNull Safe itemWrapper,@NotNull EquipSlot slot) {
		Multimap<net.minecraft.world.entity.ai.attributes.Attribute,net.minecraft.world.entity.ai.attributes.AttributeModifier> map = ((Item) itemWrapper.item()).getDefaultAttributeModifiers((net.minecraft.world.entity.EquipmentSlot) slot.enumSlot.slot());
		boolean projectile = (itemWrapper.item() instanceof ProjectileWeaponItem) || (itemWrapper.item() instanceof TridentItem);
		if (map.isEmpty() && !projectile) throw new IllegalArgumentException("Attributes map is empty!");
		return new AttributesInfo(0,d(map,Attributes.ARMOR),d(map,Attributes.ARMOR_TOUGHNESS),d(map,Attributes.KNOCKBACK_RESISTANCE),d(map,Attributes.ATTACK_DAMAGE),0,projectile ? 1 : null,d(map,Attributes.ATTACK_SPEED),0,0);
	}
	
	@Nullable
	private static Number join(@Nullable Number f1,@Nullable Number f2) {
		if (f1 == null) return f2;
		if (f2 == null) return f1;
		return f1.floatValue() + f2.floatValue();
	}
	
	@Nullable
	@Positive
	public Float attackSpeed() {
		return attackSpeed == null ? null : attackSpeed(attackSpeed);
	}
	
	private static float attackSpeed(@Positive @Range(from = 1,to = 100) int attackSpeed) {
		return 4 * attackSpeed / 100f;
	}
	
	@Nullable
	@Contract(pure = true)
	public AttributesInfo join(AttributesInfo info) {
		return info == null ? this : of(this.health + info.health,this.armor + info.armor,this.armorToughness + info.armorToughness,this.knockbackResistance + info.knockbackResistance,this.attackDamage + info.attackDamage,
				this.attackDamagePercent + info.attackDamagePercent,join(this.rangedMult,info.rangedMult),join(this.attackSpeed,info.attackSpeed),this.luck + info.luck,this.movementSpeed + info.movementSpeed);
	}
	
	@NotNull
	public List<Component> lore() {
		List<Component> lore = new ArrayList<>();
		stringAttribute(health,false,lore,Attribute.GENERIC_MAX_HEALTH.translationKey(),NamedTextColor.RED);
		stringAttribute(armor,false,lore,Attribute.GENERIC_ARMOR.translationKey(),NamedTextColor.GRAY);
		stringAttribute(armorToughness,false,lore,Attribute.GENERIC_ARMOR_TOUGHNESS.translationKey(),NamedTextColor.GRAY);
		stringAttribute(knockbackResistance,true,lore,Attribute.GENERIC_KNOCKBACK_RESISTANCE.translationKey(),NamedTextColor.DARK_PURPLE);
		stringAttribute(attackDamage,false,lore,Attribute.GENERIC_ATTACK_DAMAGE.translationKey(),NamedTextColor.BLUE);
		if (rangedMult != null) stringAttribute(rangedMult,true,lore,"attribute.name.projectile_damage",NamedTextColor.GOLD);
		stringAttribute(luck,false,lore,Attribute.GENERIC_LUCK.translationKey(),NamedTextColor.GREEN);
		stringAttribute(movementSpeed,true,lore,Attribute.GENERIC_MOVEMENT_SPEED.translationKey(),NamedTextColor.YELLOW);
		if (attackSpeed != null) {
			lore.add(Component.empty());
			lore.add(EMPTY.append(Component.translatable(Attribute.GENERIC_ATTACK_SPEED.translationKey(),NamedTextColor.LIGHT_PURPLE)).append(Component.text(": ",NamedTextColor.WHITE)).append(Component.text(attackSpeed,Utils.getTextColorHSL((attackSpeed / HSL_JUMP) * HSL_JUMP,100,50))));
		}
		return lore;
	}
	
	@NotNull
	public static ItemStack addAttributesNull(@NotNull ItemStack item,@NotNull String key,@NotNull EquipmentSlot slot) {
		LinkedHashMultimap<Attribute,AttributeModifier> map = LinkedHashMultimap.create();
		map.put(Attribute.GENERIC_MOVEMENT_SPEED,getAttribute(-0.00000000000000001f,false,key,false,slot == EquipmentSlot.CHEST ? EquipmentSlot.FEET : EquipmentSlot.CHEST));
		item.setItemMeta(Utils.runGetOriginal(item.getItemMeta(),meta -> meta.setAttributeModifiers(map)));
		return item;
	}
	
	@NotNull
	public ItemStack addAttributes(@NotNull ItemStack item,@NotNull String key,@NotNull EquipmentSlot slot) {
		LinkedHashMultimap<Attribute,AttributeModifier> map = LinkedHashMultimap.create();
		if (health != 0) map.put(Attribute.GENERIC_MAX_HEALTH,getAttribute(health,false,key,false,slot));
		if (armor != 0) map.put(Attribute.GENERIC_ARMOR,getAttribute(armor,false,key,false,slot));
		if (armorToughness != 0) map.put(Attribute.GENERIC_ARMOR_TOUGHNESS,getAttribute(armorToughness,false,key,false,slot));
		if (knockbackResistance != 0) map.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE,getAttribute(knockbackResistance,true,key,false,slot));
		if (attackDamage != 0) map.put(Attribute.GENERIC_ATTACK_DAMAGE,getAttribute(attackDamage,false,key,false,slot));
		if (attackDamagePercent != 0) map.put(Attribute.GENERIC_ATTACK_DAMAGE,getAttribute(attackDamagePercent,true,key,true,slot));
		Utils.runNotNull(attackSpeed(),speed -> map.put(Attribute.GENERIC_ATTACK_SPEED,getAttribute(4 - speed,false,key,false,slot)));
		if (luck != 0) map.put(Attribute.GENERIC_LUCK,getAttribute(luck,false,key,false,slot));
		if (movementSpeed != 0) map.put(Attribute.GENERIC_MOVEMENT_SPEED,getAttribute(movementSpeed,true,key,true,slot));
		item.setItemMeta(Utils.runGetOriginal(item.getItemMeta(),meta -> meta.setAttributeModifiers(map)));
		return item;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof AttributesInfo info) && health == info.health && armor == info.armor && armorToughness == info.armorToughness && knockbackResistance == info.knockbackResistance && attackDamage == info.attackDamage && Objects.equals(rangedMult,info.rangedMult) &&
				Objects.equals(attackSpeed,info.attackSpeed) && luck == info.luck && movementSpeed == info.movementSpeed;
	}
	
	@NotNull
	private static AttributeModifier getAttribute(float amount,boolean percent,@NotNull String key,boolean multiply,@NotNull EquipmentSlot slot) {
		return new AttributeModifier(SLOT_UUID_MAP.get(slot),key,percent ? amount / 100f : amount,multiply ? AttributeModifier.Operation.MULTIPLY_SCALAR_1 : AttributeModifier.Operation.ADD_NUMBER,slot);
	}
	
	private void stringAttribute(float amount,boolean percent,@NotNull List<Component> lore,@NotNull String translate,@NotNull TextColor color) {
		if (amount == 0) return;
		StringBuilder str = new StringBuilder();
		if (amount > 0) str.append("+");
		str.append(Utils.toString(amount,2));
		if (percent) str.append("%");
		lore.add(EMPTY.append(Component.text(str.toString(),amount > 0 ? NamedTextColor.AQUA : NamedTextColor.DARK_RED)).append(Component.space()).append(Component.translatable(translate,color)));
	}
	
	@NotNull
	@Contract(pure = true)
	public AttributesInfo withHealth(@Nullable Number health) {
		return new AttributesInfo(health,armor,armorToughness,knockbackResistance,attackDamage,attackDamagePercent,rangedMult,attackSpeed,luck,movementSpeed);
	}
	
	@NotNull
	@Contract(pure = true)
	public AttributesInfo withArmor(@Nullable Number armor) {
		return new AttributesInfo(health,armor,armorToughness,knockbackResistance,attackDamage,attackDamagePercent,rangedMult,attackSpeed,luck,movementSpeed);
	}
	
	@NotNull
	@Contract(pure = true)
	public AttributesInfo withKnockbackResistance(@Nullable Number knockbackResistance) {
		return new AttributesInfo(health,armor,armorToughness,knockbackResistance,attackDamage,attackDamagePercent,rangedMult,attackSpeed,luck,movementSpeed);
	}
	
	@NotNull
	@Contract(pure = true)
	public AttributesInfo withAttackDamage(@Nullable Number attackDamage) {
		return new AttributesInfo(health,armor,armorToughness,knockbackResistance,attackDamage,attackDamagePercent,rangedMult,attackSpeed,luck,movementSpeed);
	}
	
	@NotNull
	@Contract(pure = true)
	public AttributesInfo withAttackDamagePercent(@Nullable Number attackDamagePercent) {
		return new AttributesInfo(health,armor,armorToughness,knockbackResistance,attackDamage,attackDamagePercent,rangedMult,attackSpeed,luck,movementSpeed);
	}
	
	@NotNull
	@Contract(pure = true)
	public AttributesInfo withRangedMult(@Nullable Number rangedMult) {
		return new AttributesInfo(health,armor,armorToughness,knockbackResistance,attackDamage,attackDamagePercent,rangedMult,attackSpeed,luck,movementSpeed);
	}
	
	@NotNull
	@Contract(pure = true)
	public AttributesInfo withAttackSpeed(@Nullable Number attackSpeed) {
		return new AttributesInfo(health,armor,armorToughness,knockbackResistance,attackDamage,attackDamagePercent,rangedMult,attackSpeed,luck,movementSpeed);
	}
	
	@NotNull
	@Contract(pure = true)
	public AttributesInfo withLuck(@Nullable Number luck) {
		return new AttributesInfo(health,armor,armorToughness,knockbackResistance,attackDamage,attackDamagePercent,rangedMult,attackSpeed,luck,movementSpeed);
	}
	
	@NotNull
	@Contract(pure = true)
	public AttributesInfo withMovementSpeed(@Nullable Number movementSpeed) {
		return new AttributesInfo(health,armor,armorToughness,knockbackResistance,attackDamage,attackDamagePercent,rangedMult,attackSpeed,luck,movementSpeed);
	}
}