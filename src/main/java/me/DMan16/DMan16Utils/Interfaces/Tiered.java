package me.DMan16.DMan16Utils.Interfaces;

import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Enums.Rarity;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;

public interface Tiered<V extends Tiered<V>> {
	NamespacedKey TIER = new NamespacedKey(DMan16UtilsMain.getInstance(),"tier");
	
	@NonNegative int tier();
	
	boolean includeZero();
	
	@NotNull
	default TextColor tierColor() {
		int tier = tier();
		if (!includeZero()) tier--;
		Iterator<Rarity> iter = Arrays.asList(Rarity.values()).iterator();
		Rarity rarity = iter.next();
		while (iter.hasNext()) {
			if (rarity.ordinal() >= tier) return rarity.color;
			else rarity = iter.next();
		}
		return rarity.color;
	}
	
	@NotNull
	default Component tierComponent() {
		return Utils.noItalic(Component.translatable("menu.tier_x",tierColor()).args(Component.text(tier())));
	}
	
	@NotNull
	default Component addTierComponent(@NotNull Component comp) {
		return comp.append(Component.text(" (",NamedTextColor.WHITE)).append(tierComponent()).append(Component.text(")",NamedTextColor.WHITE));
	}
	
	@Nullable
	@Contract("null -> null; !null -> !null")
	default ItemStack setTierKey(@Nullable ItemStack item) {
		return Utils.setKeyPersistentDataContainer(item,TIER,PersistentDataType.INTEGER,tier());
	}
	
	@Nullable
	static Integer getTier(@Nullable ItemStack item) {
		return Utils.getKeyPersistentDataContainer(item,TIER,PersistentDataType.INTEGER);
	}
}