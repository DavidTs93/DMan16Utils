package me.DMan16.POPUtils.Enums;

import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum CurrencyType {
	Money(Material.PAPER,Utils.chatColors("&a$")),
	Tokens(Material.EMERALD,Utils.chatColors("&2\u23E3")),
	XP(Material.EXPERIENCE_BOTTLE,Utils.chatColors("&aXP"),null);
	
	public final Material material;
	public final String suffix;
	public final Component name;
	
	CurrencyType(@NotNull Material material, String suffix) {
		this.material = material;
		this.suffix = suffix;
		this.name = Component.text("suffix").decoration(TextDecoration.ITALIC,false);
	}
	
	CurrencyType(@NotNull Material material, String suffix, Component name) {
		this.material = material;
		this.suffix = suffix;
		this.name = name;
	}
	
	@Nullable
	public static CurrencyType get(@NotNull String name) {
		if (!name.trim().isEmpty()) for (CurrencyType type : values()) if (type.name().equalsIgnoreCase(name.trim())) return type;
		return null;
	}
}