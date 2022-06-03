package me.DMan16.DMan16Utils.Interfaces;

import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface Levelled<V extends Itemable<V>> extends Itemable<V> {
	
	@NotNull Component name();
	
	int level();
	
	boolean isMaxLevel();
	
	int leftToLevel(int level);
	
	int leftToMaxLevel();
	
	boolean canIncreaseLevel(int levels);
	
	@NotNull V increaseLevel(@NotNull Player player, int slot, int levels);
	
	@NotNull
	default Component levelLine() {
		return Utils.noItalic(Component.translatable("menu.level_x",NamedTextColor.GREEN).args(Component.text(level(),NamedTextColor.AQUA)));
	}
	
	@NotNull
	default Component giveComponent() {
		return name().append(Component.text(" (",NamedTextColor.WHITE).append(levelLine().color(NamedTextColor.WHITE)).append(Component.text(")"))).
				hoverEvent(asItem().asHoverEvent());
	}
}