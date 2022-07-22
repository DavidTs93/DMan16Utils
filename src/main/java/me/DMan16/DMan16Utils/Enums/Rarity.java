package me.DMan16.DMan16Utils.Enums;

import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Rarity {
	COMMON(0,NamedTextColor.WHITE),
	UNCOMMON(20,NamedTextColor.GRAY),
	RARE(50,NamedTextColor.YELLOW),
	EPIC(100,NamedTextColor.BLUE),
	MYTHICAL(200,NamedTextColor.RED),
	LEGENDARY(500,NamedTextColor.LIGHT_PURPLE),
	GOD(1000,TextColor.color(112,51,173));
	
	private static final String prefix = "menu.rarity.";
	
	public final int level;
	public final TextColor color;
	
	Rarity(int level,TextColor color) {
		this.level = level;
		this.color = color;
	}
	
	@NotNull
	public Component displayName() {
		return Utils.noItalic(Component.translatable(prefix + name().toLowerCase(),color));
	}
	
	@NotNull
	public static Rarity get(int level) {
		if (level <= 0) return COMMON;
		Rarity rarity = COMMON;
		for (Rarity val : values()) {
			if (level >= val.level) rarity = val;
			else break;
		}
		return rarity;
	}
	
	@Nullable
	public static Rarity get(@NotNull String rarity) {
		for (Rarity val : values()) if (val.name().equalsIgnoreCase(rarity)) return val;
		return null;
	}
}