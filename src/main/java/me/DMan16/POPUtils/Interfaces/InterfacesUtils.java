package me.DMan16.POPUtils.Interfaces;

import me.DMan16.POPUtils.Classes.BasicItemable;
import me.DMan16.POPUtils.Classes.PluginItemInitializerInfo;
import me.DMan16.POPUtils.Items.PluginsItems;
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
	public static final Component CHOSEN = Utils.noItalic(Component.translatable("menu.prisonpop.chosen",NamedTextColor.GREEN));
	
	@NotNull
	@Unmodifiable
	static List<@NotNull ItemStack> createSorts() {
		List<List<Component>> lores = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			List<Component> lore = new ArrayList<>();
			lore.add(Component.empty());
			lore.add(Utils.noItalic(Component.translatable("menu.prisonpop.click_to_change_left").color(NamedTextColor.GRAY)));
			lore.add(line("generator.default",(i / 2) == 0));
			lore.add(line("menu.prisonpop.rarity",(i / 2) == 1));
			lore.add(line("menu.prisonpop.name",(i / 2) == 2));
			lore.add(Component.empty());
			lore.add(Utils.noItalic(Component.translatable("menu.prisonpop.click_to_change_right").color(NamedTextColor.GRAY)));
			lore.add(line("menu.prisonpop.ascending",(i % 2) == 0));
			lore.add(line("menu.prisonpop.descending",(i % 2) == 1));
			lores.add(lore);
		}
		return createItems(lores);
	}
	
	@NotNull
	@Unmodifiable
	static List<@NotNull ItemStack> createAscendingDescending() {
		List<List<Component>> lores = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			List<Component> lore = new ArrayList<>();
			lore.add(Component.empty());
			lore.add(Utils.noItalic(Component.translatable("menu.prisonpop.click_to_change").color(NamedTextColor.GRAY)));
			lore.add(line("menu.prisonpop.ascending",i == 0));
			lore.add(line("menu.prisonpop.descending",i == 1));
			lores.add(lore);
		}
		return createItems(lores);
	}
	
	@NotNull
	@Unmodifiable
	private static List<@NotNull ItemStack> createItems(@NotNull List<List<Component>> lores) {
		PluginItemInitializerInfo sort = PluginsItems.getItem("sort");
		return lores.stream().map(BasicItemable::setLoreFunction).map(sort::copyAddAlterItem).map(Itemable::asItem).toList();
	}
	
	@NotNull
	public static Component line(@NotNull String name, boolean selected) {
		return selected ? Utils.noItalic(Component.text("► ").append(Component.translatable(name)).color(NamedTextColor.AQUA)) : Utils.noItalic(Component.translatable(name).color(NamedTextColor.WHITE));
	}
}