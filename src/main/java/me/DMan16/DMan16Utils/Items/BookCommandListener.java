package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class BookCommandListener implements CommandExecutor,TabCompleter {
	public BookCommandListener() {
		PluginCommand command = DMan16UtilsMain.getInstance().getCommand("book");
		assert command != null;
		command.setExecutor(this);
	}
	
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player player) || args.length <= 0) return true;
		Enchantment ench = Utils.getEnchantment(args[0]);
		if (ench == null) {
			player.sendMessage("enchantment doesn't exist?!");
			return true;
		}
		int lvl = 1;
		try {
			lvl = Utils.clamp(Integer.parseInt(args[1]),1,Enchantable.getMaxLevel(ench));
		} catch (Exception e) {}
		Utils.runNotNull(ItemableStack.of(Utils.addEnchantment(new ItemStack(Material.ENCHANTED_BOOK),ench,lvl)),item -> player.getInventory().addItem(item.asItem()));
		return true;
	}
	
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
		if (args.length == 0 || args.length == 1) {
			Stream<String> enchants = Arrays.stream(Enchantment.values()).map(ench -> ench.getKey().getKey());
			return args.length == 0 ? enchants.toList() : enchants.filter(cmd -> Utils.containsTabComplete(args[0],cmd)).map(String::toLowerCase).toList();
		}
		Enchantment ench = Utils.getEnchantment(args[0]);
		if (ench == null) return new ArrayList<>();
		if (args.length == 2) return IntStream.range(ench.getStartLevel(),Enchantable.getMaxLevel(ench) + 1).mapToObj(Integer::toString).toList();
		return new ArrayList<>();
	}
}