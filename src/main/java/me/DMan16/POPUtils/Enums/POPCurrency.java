package me.DMan16.POPUtils.Enums;

import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum POPCurrency {
	Money(Material.PAPER,Utils.chatColors("&a$")),
	Tokens(Material.EMERALD,Utils.chatColors("&2\u23E3")),
	XP(Material.EXPERIENCE_BOTTLE,Utils.chatColors("&aXP"),null);
	
	public final Material material;
	public final String suffix;
	public final Component name;
	
	POPCurrency(@NotNull Material material, String suffix) {
		this.material = material;
		this.suffix = suffix;
		this.name = Component.text(Utils.splitCapitalize(name()) + " " + suffix).decoration(TextDecoration.ITALIC,false);
	}
	
	POPCurrency(@NotNull Material material, String suffix, Component name) {
		this.material = material;
		this.suffix = suffix;
		this.name = name;
	}
	
	@Nullable
	public static POPCurrency get(String name) {
		if (name != null && !name.trim().isEmpty()) for (POPCurrency type : values()) if (type.name().equalsIgnoreCase(name.trim())) return type;
		return null;
	}
}