package me.DMan16.POPUtils.Items;

import me.DMan16.POPUtils.POPUtilsMain;
import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemCommandListener implements CommandExecutor,TabCompleter {
	private static final List<String> BASE = Arrays.asList("help");
	
	public ItemCommandListener() {
		PluginCommand command = POPUtilsMain.getInstance().getCommand("item");
		assert command != null;
		command.setExecutor(this);
		command.setTabCompleter(this);
	}
	
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		int idx;
		if (args.length == 0) {
			return true;
		}
		if ((idx = BASE.indexOf(args[0].toLowerCase())) < 0 || idx == 0) return true;
		Player player = (Player) sender;
		return true;
	}
	
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
		if (args.length == 1) return BASE.stream().filter(cmd -> Utils.containsTabComplete(args[0],cmd)).map(String::toLowerCase).collect(Collectors.toList());
		int idx = BASE.indexOf(args[0].toLowerCase());
		if (args.length == 2 && idx >= 0)
			return Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).filter(name -> Utils.containsTabComplete(args[1],name)).collect(Collectors.toList());
		return null;
	}
}