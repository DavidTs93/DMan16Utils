package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.Classes.Pair;
import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Interfaces.EnchantmentXPHolder;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.Interfaces.Repairable;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.command.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class XPHolderCommandListener implements CommandExecutor,TabCompleter {
	private static final @Unmodifiable List<@NotNull String> BASE = List.of("add","get","next","progress");
	
	public XPHolderCommandListener() {
		PluginCommand cmd = DMan16UtilsMain.getInstance().getCommand("xpholder");
		assert cmd != null;
		cmd.setExecutor(this);
		cmd.setTabCompleter(this);
		cmd.setUsage(Utils.chatColors("&aUsage: /XPHolder <add/get/next/progress> <enchantment> <add:amount>"));
	}
	
	public boolean onCommand(@NotNull CommandSender sender,@NotNull Command cmd,@NotNull String label,@NotNull String[] args) {
		if (!(sender instanceof Player player)) return true;
		if (args.length < 2) return false;
		Itemable<?> item = ItemUtils.of(player.getInventory().getItemInMainHand());
		if (!(item instanceof EnchantmentXPHolder xpHolder)) {
			player.sendMessage(Utils.chatColors("&cNo EnchantmentXPHolder item in main hand!"));
			return true;
		}
		int idx = BASE.indexOf(args[0].toLowerCase());
		if (idx < 0) return false;
		Enchantment ench = Utils.getEnchantment(args[1]);
		if (ench == null) {
			player.sendMessage(Utils.chatColors("&cEnchantment not found!"));
			return true;
		}
		if (idx == 0) try {
			if ((item instanceof Repairable repairable) && repairable.shouldBreak()) {
				player.sendMessage(Utils.chatColors("&cCan't add XP to broken item!"));
				return true;
			}
			int amount = Integer.parseInt(args[2]);
			if (amount < 0) return false;
			Pair<Integer,Boolean> result = xpHolder.addEnchantmentXP(ench,amount);
			if (result == null) player.sendMessage(Utils.chatColors("&cEnchantment not present on the item"));
			else {
				if (amount > 0) player.sendMessage(Utils.chatColors(result.first == 0 ? "&aEnchantment &b" + ench.getKey().getKey() + "&a at max level - no XP added" : ("&b" + result.first() + "&a XP added to enchantment &b" + ench.getKey().getKey() + (result.second() ? "&a - level increased!" : ""))));
				Utils.setSlot(player,item.asItem(),EquipmentSlot.HAND);
			}
		} catch (Exception e) {
			return false;
		} else if (idx == 1) {
			Integer xp = xpHolder.getEnchantmentXP(ench);
			if (xp == null) player.sendMessage(Utils.chatColors("&cEnchantment not present on the item"));
			else player.sendMessage(Utils.chatColors("&aEnchantment &b" + ench.getKey().getKey() + "&a XP: &b" + xp));
		} else if (idx == 2) {
			Integer xp = xpHolder.getXPToNextLevel(ench);
			if (xp == null) player.sendMessage(Utils.chatColors("&cEnchantment not present on the item"));
			else player.sendMessage(Utils.chatColors("&aEnchantment &b" + ench.getKey().getKey() + "&a XP to next level: &b" + xp));
		} else if (idx == 3) {
			Float progress = xpHolder.enchantmentProgress(ench);
			if (progress == null) player.sendMessage(Utils.chatColors("&cEnchantment not present on the item"));
			else player.sendMessage(Utils.chatColors("&aEnchantment &b" + ench.getKey().getKey() + "&a progress: &b" + Utils.toString(progress) + "%"));
		}
		return true;
	}
	
	public List<String> onTabComplete(@NotNull CommandSender sender,@NotNull Command command,@NotNull String alias,String[] args) {
		if (args.length == 0) return BASE;
		if (args.length == 1) return BASE.stream().filter(cmd -> Utils.containsTabComplete(args[0],cmd)).map(String::toLowerCase).toList();
		if (args.length == 2 && BASE.contains(args[0].toLowerCase())) return Arrays.stream(Enchantment.values()).map(ench -> ench.getKey().getKey()).filter(cmd -> Utils.containsTabComplete(args[1],cmd)).map(String::toLowerCase).toList();
		return new ArrayList<>();
	}
}