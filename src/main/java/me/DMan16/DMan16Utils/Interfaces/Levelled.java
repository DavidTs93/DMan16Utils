package me.DMan16.DMan16Utils.Interfaces;

import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;

public interface Levelled<V extends Itemable<V>> extends Itemable<V> {
	
	@NotNull Component name();
	
	@Positive int level();
	
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
	
	@NotNull V increaseLevel(@NotNull Player player,int slot,int levels);
	
	@NotNull
	default Component levelLine() {
		return Utils.noItalic(Component.translatable("menu.level_x",NamedTextColor.GREEN).args(Component.text(level(),NamedTextColor.AQUA)));
	}
	
	@NotNull
	default Component giveComponent() {
		return name().append(Component.text(" (",NamedTextColor.WHITE).append(levelLine().color(NamedTextColor.WHITE)).append(Component.text(")"))).hoverEvent(asItem().asHoverEvent());
	}
}