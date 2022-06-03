package me.DMan16.DMan16Utils.Listeners;

import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class UUIDCommandListener implements CommandExecutor,TabCompleter {
	public UUIDCommandListener() {
		PluginCommand cmd = DMan16UtilsMain.getInstance().getCommand("uuid");
		assert cmd != null;
		cmd.setExecutor(this);
		cmd.setUsage("/uuid <name>");
	}
	
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
		if (args.length <= 0) return false;
		UUID id = Bukkit.getPlayerUniqueId(args[0]);
		String name = Utils.thisOrThatOrNull(id == null ? null : Utils.applyNotNull(Bukkit.getPlayer(id),Player::getName),args[0]);
		sender.sendMessage(Utils.chatColors(id == null ? "&cPlayer &f\"" + name + "\"&c not found" : "&aPlayer &f\"" + name + "\"&a UUID: &b" + id));
		return true;
	}
	
	public List<String> onTabComplete(@NotNull CommandSender sender,@NotNull Command command,@NotNull String alias,String[] args) {
		if (args.length == 0) return Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
		if (args.length == 1) return Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).filter(name -> Utils.containsTabComplete(args[0],name)).collect(Collectors.toList());
		return new ArrayList<>();
	}
}