package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class EnchantCommandListener implements CommandExecutor,TabCompleter {
	private static final @NotNull ItemStack BOOK = new ItemStack(Material.BOOK);
	
	public EnchantCommandListener() {
		PluginCommand command = DMan16UtilsMain.getInstance().getCommand("enchant");
		assert command != null;
		command.setExecutor(this);
	}
	
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player player) || args.length < 2) return true;
		boolean set = args[0].equalsIgnoreCase("set");
		if (!set && !args[0].equalsIgnoreCase("remove")) return true;
		Enchantment ench = Utils.getEnchantment(args[1]);
		if (ench == null) return true;
		ItemStack item = player.getInventory().getItemInMainHand();
		if (Utils.isNull(item) || item.getType() == Material.ENCHANTED_BOOK) return true;
		int lvl = args.length > 2 ? Utils.thisOrThatOrNull(Utils.getInteger(args[2]),0) : 1;
		if (set && Utils.sameItem(item,BOOK)) item = Utils.setEnchantments(BOOK.clone(),Map.of(ench,lvl));
		else item = Utils.setEnchantments(item.clone(),Utils.applyGetOriginal(new HashMap<>(Utils.thisOrThatOrNull(Utils.getStoredEnchants(item),item.getEnchantments())),
					enchants -> set ? enchants.put(ench,lvl) : enchants.remove(ench)));
		Itemable<?> itemable = ItemUtils.ofOrHolder(item);
		player.getInventory().setItemInMainHand(itemable.asItem());
		return true;
	}
	
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
		if (args.length == 0) return List.of("set","remove");
		if (args.length == 1) return Stream.of("set","remove").filter(cmd -> Utils.containsTabComplete(args[0],cmd)).map(String::toLowerCase).toList();
		if (args.length == 2) return Arrays.stream(Enchantment.values()).map(ench -> ench.getKey().getKey()).filter(cmd -> Utils.containsTabComplete(args[1],cmd)).
				map(String::toLowerCase).toList();
		Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(args[1]));
		if (ench == null || !args[0].equalsIgnoreCase("set")) return new ArrayList<>();
		if (args.length == 3) return IntStream.range(ench.getStartLevel(),ench.getMaxLevel() + 1).mapToObj(Integer::toString).toList();
		return new ArrayList<>();
	}
}