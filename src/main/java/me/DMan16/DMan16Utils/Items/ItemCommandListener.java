package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemCommandListener implements CommandExecutor,TabCompleter {
	private static final Set<@NotNull String> MATERIALS = Arrays.stream(Material.values()).map(Enum::name).collect(Collectors.toSet());
	
	public ItemCommandListener() {
		PluginCommand command = DMan16UtilsMain.getInstance().getCommand("item");
		assert command != null;
		command.setExecutor(this);
		command.setTabCompleter(this);
	}
	
	public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label,String[] args) {
		if (args.length < 3) {
			if (args.length <= 0 && (sender instanceof Player player)) {
				ItemStack item = player.getInventory().getItemInMainHand();
				if (Utils.notNull(item)) {
					Itemable<?> itemable = ItemUtils.ofOrSubstituteOrHolder(item);
					player.sendMessage(Component.text("Item class: ",NamedTextColor.GREEN).append(Component.text(itemable.getClass().getName(),NamedTextColor.AQUA)).append(Component.newline()).
							append(Component.text("Item string: ",NamedTextColor.GREEN)).append(Component.text(itemable.stringMappable(),NamedTextColor.AQUA).clickEvent(ClickEvent.copyToClipboard(itemable.stringMappable()))));
				}
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("item")) {
			Itemable<?> itemable = ItemUtils.ofOrSubstituteOrHolder(String.join(" ",Arrays.copyOfRange(args,1,args.length)));
			sender.sendMessage(itemable == null ? Component.text("Item not found") : Component.text("Item class: ",NamedTextColor.GREEN).append(Component.text(itemable.getClass().getName(),NamedTextColor.AQUA)).append(Component.newline()).
					append(Component.text("Item string: ",NamedTextColor.GREEN)).append(Component.text(itemable.stringMappable(),NamedTextColor.AQUA).clickEvent(ClickEvent.copyToClipboard(itemable.stringMappable()))));
			return true;
		}
		if (!args[0].equalsIgnoreCase("give")) return true;
		Player player;
		player = Bukkit.getPlayer(args[1]);
		if (player == null || !player.isOnline()) sender.sendMessage(Utils.chatColors("Player not found!"));
		else {
			Itemable<?> item = ItemUtils.ofOrSubstitute(String.join(" ",Arrays.copyOfRange(args,2,args.length)));
			if (item == null) sender.sendMessage(Utils.chatColors("Item not found"));
			else item.give(player,() -> sender.sendMessage(Component.text("Gave ",NamedTextColor.GREEN).append(item.giveComponent().colorIfAbsent(NamedTextColor.WHITE))),
					() -> sender.sendMessage(Utils.chatColors("Couldn't give item of class " + item.getClass().getName()) + ", ItemStack null: " + Utils.isNull(item.asItem())),null);
		}
		return true;
	}
	
	public List<String> onTabComplete(@NotNull CommandSender sender,@NotNull Command command,@NotNull String alias,String[] args) {
		if (args.length == 1) return Stream.of("give","item").filter(cmd -> Utils.containsTabComplete(args[0],cmd)).map(String::toLowerCase).toList();
		boolean give = args[0].equalsIgnoreCase("give");
		if (!give && !args[0].equalsIgnoreCase("item")) return new ArrayList<>();
		if (args.length == 2 && give) return Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).filter(name -> Utils.containsTabComplete(args[1],name)).toList();
		if ((args.length == 3 && give) || args.length == 2) {
			Set<String> set = Utils.joinSets(ItemUtils.getRegisteredItemablesKeys(),MATERIALS).stream().filter(name -> Utils.containsTabComplete(args[2],name)).map(String::toLowerCase).collect(Collectors.toSet());
			boolean removed = set.remove(args[2].toLowerCase());
			List<String> list = new ArrayList<>(set);
			if (removed) list.add(0,args[2].toLowerCase());
			return list;
		}
		return new ArrayList<>();
	}
}