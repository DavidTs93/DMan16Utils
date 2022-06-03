package me.DMan16.DMan16Utils.Restrictions;

import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RestrictionsCommandListener implements CommandExecutor,TabCompleter {
	private static final List<String> BASE = Arrays.asList("get","add","remove");
	
	public RestrictionsCommandListener() {
		PluginCommand command = DMan16UtilsMain.getInstance().getCommand("restrictions");
		assert command != null;
		command.setExecutor(this);
		command.setTabCompleter(this);
	}
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (args.length < 1) return true;
		if (!(sender instanceof Player player)) {
			Utils.chatColors(sender,"&cOnly players can use this command!");
			return true;
		}
		ItemStack item = player.getInventory().getItemInMainHand();
		if (Utils.isNull(item)) {
			Utils.chatColors(sender,"&cYou must be holding and item in your main hand!");
			return true;
		}
		int idx = BASE.indexOf(args[0].toLowerCase());
		if (idx < 0) return true;
		if (idx == 0) {
			List<Restrictions.Restriction> restrictions = Restrictions.getRestrictions(item);
			Utils.chatColors(sender,"&aRestrictions on item:\n" + restrictions.stream().map(restriction -> "&b" + restriction.name()).collect(Collectors.joining("&f,")));
		} else if (args.length > 1) {
			if (idx == 1 || idx == 2) {
				List<Restrictions.Restriction> oldRestrictions = Restrictions.getRestrictions(item);
				List<Restrictions.Restriction> restrictions = Arrays.stream(args).map(Restrictions::byName).filter(Objects::nonNull).toList();
				item = idx == 1 ? Restrictions.addRestrictions(item,restrictions) : Restrictions.removeRestrictions(item,restrictions);
				List<Restrictions.Restriction> newRestrictions = new ArrayList<>(Restrictions.getRestrictions(item));
				newRestrictions.removeAll(oldRestrictions);
				Utils.chatColors(sender,newRestrictions.isEmpty() ? "&6No Restrictions " + (idx == 1 ? "added" : "removed") : "&a" + newRestrictions.size() +
						" Restrictions " + (idx == 1 ? "added" : "removed") + ":\n" +
						newRestrictions.stream().map(restriction -> "&b" + restriction.name()).collect(Collectors.joining("&f,")));
			}
		}
		return true;
	}
	
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
		if (args.length == 1) return BASE.stream().filter(name -> Utils.containsTabComplete(args[0],name)).toList();
		int idx = BASE.indexOf(args[0]);
		if (idx <= 0) return new ArrayList<>();
		if (idx == 1 || idx == 2) {
			List<String> restrictions = Restrictions.getRestrictions().stream().map(Restrictions.Restriction::name).
					filter(name -> Utils.containsTabComplete(args[args.length - 1],name)).collect(Collectors.toList());
			restrictions.removeAll(Arrays.stream(Arrays.copyOfRange(args,1,args.length - 1)).map(String::toLowerCase).toList());
			return restrictions;
		}
		return new ArrayList<>();
	}
}