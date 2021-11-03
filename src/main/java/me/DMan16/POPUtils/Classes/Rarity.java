package me.DMan16.POPUtils.Classes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public enum Rarity {
	COMMON(0,NamedTextColor.WHITE),
	UNCOMMON(20,NamedTextColor.GRAY),
	RARE(50,NamedTextColor.YELLOW),
	EPIC(100,NamedTextColor.BLUE),
	MYTHICAL(200,NamedTextColor.RED),
	LEGENDARY(500,NamedTextColor.LIGHT_PURPLE),
	GOD(1000,TextColor.color(112,51,173));
	
	private static final String prefix = "menu.prisonpop.rarity.";
	
	public final int level;
	public final TextColor color;
	
	Rarity(int level, TextColor color) {
		this.level = level;
		this.color = color;
	}
	
	@NotNull
	public Component displayName() {
		return Component.translatable(prefix + name().toLowerCase(),color).decoration(TextDecoration.ITALIC,false);
	}
	
	public static Rarity get(int level) {
		if (level <= 0) return COMMON;
		Rarity rarity = COMMON;
		for (Rarity val : values()) {
			if (level >= val.level) rarity = val;
			else break;
		}
		return rarity;
	}
}