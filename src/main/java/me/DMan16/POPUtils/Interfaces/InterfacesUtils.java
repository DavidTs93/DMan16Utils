package me.DMan16.POPUtils.Interfaces;

import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

public class InterfacesUtils {
	public static final String TRANSLATABLE = "translatable: ";
	public static final Component CHOSEN = Component.translatable("menu.prisonpop.chosen",NamedTextColor.GREEN).decoration(TextDecoration.ITALIC,false);
	
	@NotNull
	@Unmodifiable
	static List<ItemStack> createSorts() {
		List<List<Component>> lores = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			List<Component> lore = new ArrayList<>();
			lore.add(Component.empty());
			lore.add(Component.translatable("menu.prisonpop.click_to_change_left").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));
			lore.add(line("generator.default",(i / 2) == 0));
			lore.add(line("menu.prisonpop.rarity",(i / 2) == 1));
			lore.add(line("menu.prisonpop.name",(i / 2) == 2));
			lore.add(Component.empty());
			lore.add(Component.translatable("menu.prisonpop.click_to_change_right").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));
			lore.add(line("menu.prisonpop.ascending",(i % 2) == 0));
			lore.add(line("menu.prisonpop.descending",(i % 2) == 1));
			lores.add(lore);
		}
		ItemStack sort = Utils.ITEMS.getItem("sort");
		return lores.stream().map(lore -> Utils.setLore(sort.clone(),lore)).toList();
	}
	
	@NotNull
	public static Component line(@NotNull String name, boolean selected) {
		if (selected) return Component.text("► ").append(Component.translatable(name)).color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC,false);
		return Component.translatable(name).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC,false);
	}
}