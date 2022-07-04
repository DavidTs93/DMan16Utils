package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

public final class SocketsCommandListener implements CommandExecutor,TabCompleter {
	private static final @Unmodifiable List<@NotNull String> BASE = List.of("set","increase","random","max");
	public SocketsCommandListener() {
		PluginCommand cmd = DMan16UtilsMain.getInstance().getCommand("sockets");
		assert cmd != null;
		cmd.setExecutor(this);
		cmd.setTabCompleter(this);
		cmd.setUsage(Utils.chatColors("&aUsage: /sockets <set/random/max> <set:amount>"));
	}
	
	public boolean onCommand(@NotNull CommandSender sender,@NotNull Command cmd,@NotNull String label,@NotNull String[] args) {
		if (!(sender instanceof Player player)) return true;
		if (args.length < 2) return false;
		Itemable<?> item = ItemUtils.of(player.getInventory().getItemInMainHand());
		if (!(item instanceof Socketable<?> socketable)) {
			player.sendMessage(Utils.chatColors("&cNo Socketable item in main hand!"));
			return true;
		}
		int idx = BASE.indexOf(args[0].toLowerCase());
		if (idx < 0) return false;
		if (idx == 0) try {
			int amount = Integer.parseInt(args[1]);
			if (amount <= 0) return false;
			Utils.setSlot(player,socketable.setMaxEnchantmentSlots(amount).asItem(),EquipmentSlot.HAND);
		} catch (Exception e) {
			return false;
		} else if (idx == 1) {
			if (socketable.addEnchantmentSlot()) Utils.setSlot(player,socketable.asItem(),EquipmentSlot.HAND);
			else player.sendMessage(Utils.chatColors("&eAlready at max sockets"));
		} else if (idx == 2) Utils.setSlot(player,socketable.setRandomMaxEnchantments().asItem(),EquipmentSlot.HAND);
		else if (idx == 3) Utils.setSlot(player,socketable.setMaxEnchantmentSlots(socketable.maxPossibleEnchantments()).asItem(),EquipmentSlot.HAND);
		return true;
	}
	
	public List<String> onTabComplete(@NotNull CommandSender sender,@NotNull Command command,@NotNull String alias,String[] args) {
		if (args.length == 0) return BASE;
		if (args.length == 1) return BASE.stream().filter(cmd -> Utils.containsTabComplete(args[0],cmd)).map(String::toLowerCase).toList();
		return new ArrayList<>();
	}
}