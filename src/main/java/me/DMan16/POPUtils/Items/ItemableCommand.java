package me.DMan16.POPUtils.Items;

import me.DMan16.POPUtils.Interfaces.Itemable;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ItemableCommand implements Itemable<ItemableCommand> {
	private final ItemableStack item;
	private final String command;
	private final ExecuteAs executeAs;
	private final Component displayName;
	
	private ItemableCommand(@NotNull ItemableStack item, @NotNull String command, @NotNull ExecuteAs executeAs, @NotNull Component displayName) {
		this.item = item;
		this.command = command;
		this.executeAs = executeAs;
		this.displayName = displayName;
	}
	
	@NotNull
	public Material material() {
		return item.material();
	}
	
	@NotNull
	public ItemStack asItem() {
		return item.asItem();
	}
	
	@Override
	public boolean give(@NotNull Player player, @Nullable Map<@NotNull Integer, @NotNull Integer> toRemove, int ... toEmpty) {
		String command = this.command.replaceAll("<(?i)name>",player.getName()).replaceAll("<(?i)uuid>",player.getUniqueId().toString());
		if (executeAs == ExecuteAs.OP) {
			if (player.isOp()) player.performCommand(command);
			else {
				if (player.isOnline()) {
					Utils.addCancelledPlayer(player);
					player.setOp(true);
				} else Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"op " + player.getName());
				player.performCommand(command);
				if (player.isOnline()) {
					player.setOp(false);
					Utils.removeCancelledPlayer(player);
				} else Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"deop " + player.getName());
			}
		} else if (executeAs == ExecuteAs.CONSOLE) Bukkit.dispatchCommand(Bukkit.getConsoleSender(),command);
		else player.performCommand(command);
		return true;
	}
	
	@Nullable
	@Contract("null,_,_ -> null; _,null,_ -> null")
	public static ItemableCommand of(ItemableStack item, String command, ExecuteAs executeAs) {
		Component displayName;
		if (item == null || command == null || command.trim().isEmpty() || (displayName = item.asItem().getItemMeta().displayName()) == null) return null;
		command = command.trim();
		if (command.startsWith("/")) command = command.replaceFirst("/","");
		return new ItemableCommand(item,command,executeAs == null ? ExecuteAs.PLAYER : executeAs,displayName);
	}
	
	@Nullable
	@Contract("null -> null")
	public static ItemableCommand of(@Nullable Map<String,?> arguments) {
		return arguments == null ? null : of(ItemableStack.of(arguments),Utils.getString(arguments.get("Command")),ExecuteAs.get(Utils.getString(arguments.get("ExecuteAs"))));
	}
	
	@Override
	public @NotNull Map<@NotNull String,Object> toMap() {
		Map<String,Object> map = new HashMap<>(item.toMap());
		map.put("Command",command);
		map.put("ExecuteAs",executeAs.name());
		return map;
	}
	
	@NotNull
	public String ItemableKey() {
		return "command";
	}
	
	@NotNull
	public ItemableCommand copy() {
		return this;
	}
	
	@NotNull
	public Component giveComponent() {
		return displayName;
	}
	
	public enum ExecuteAs {
		PLAYER,
		OP,
		CONSOLE;
		
		@Nullable
		public static ExecuteAs get(String name) {
			if (name != null) for (ExecuteAs value : values()) if (name.equalsIgnoreCase(value.name())) return value;
			return null;
		}
	}
}