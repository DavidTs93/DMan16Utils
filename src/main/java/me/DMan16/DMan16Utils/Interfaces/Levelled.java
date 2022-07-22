package me.DMan16.DMan16Utils.Interfaces;

import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;

public interface Levelled<V extends Itemable<V>> extends Itemable<V> {
	@NotNull NamespacedKey LEVEL_KEY = new NamespacedKey(DMan16UtilsMain.getInstance(),"level");
	
	@NotNull
	default ItemStack setLevelPDC(@NotNull ItemStack item) {
		return Utils.applyOrOriginalIf(item,i -> Utils.setKeyPersistentDataContainer(item,LEVEL_KEY,PersistentDataType.INTEGER,level()),level() > minLevel());
	}
	
	@Positive
	static int getLevelPDC(@NotNull ItemStack item,@Positive int minLevel) {
		return Math.max(Utils.thisOrThatOrNull(Utils.getKeyPersistentDataContainer(item,LEVEL_KEY,PersistentDataType.INTEGER),minLevel),minLevel);
	}
	
	@NotNull Component displayName();
	
	@Positive int level();
	
	@Positive
	default int minLevel() {
		return 1;
	}
	
	@Positive int maxLevel();
	
	default int leftToLevel(@Positive int level) {
		return level() - level;
	}
	
	@NonNegative
	default int leftToMaxLevel() {
		return leftToLevel(maxLevel());
	}
	
	default boolean isMaxLevel() {
		return leftToMaxLevel() == 0;
	}
	
	default boolean canIncreaseLevel(@Positive int levels) {
		return level() + levels <= maxLevel();
	}
	
	@NotNull V increaseLevel(@Positive int levels);
	
	@NotNull
	default Component levelLine() {
		return Utils.noItalic(Component.translatable("menu.level_x",NamedTextColor.GREEN).args(Component.text(level(),NamedTextColor.AQUA)));
	}
	
	@NotNull
	default Component giveComponent() {
		return displayName().append(Component.text(" (",NamedTextColor.WHITE).append(levelLine().color(NamedTextColor.WHITE)).append(Component.text(")"))).hoverEvent(asItem().asHoverEvent());
	}
	
	@NotNull
	@SuppressWarnings("unchecked")
	default V save(@NotNull Player player,@NonNegative int slot) {
		Utils.setSlot(player,asItem(),slot);
		return (V) this;
	}
}