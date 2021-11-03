package me.DMan16.POPUtils.Items;

import me.DMan16.POPUtils.Interfaces.Itemable;
import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.Bukkit;
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
	
	private ItemableCommand(@NotNull ItemableStack item, @NotNull String command, @NotNull ExecuteAs executeAs) {
		this.item = item;
		this.command = command;
		this.executeAs = executeAs;
	}
	
	@NotNull
	public ItemStack asItem() {
		return item.asItem();
	}
	
	@Override
	public boolean give(@NotNull Player player) {
		String command = this.command.replaceAll("<(?i)name>",player.getName()).replace("<(?i)uuid>",player.getUniqueId().toString());
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
		if (item == null || command == null || command.trim().isEmpty() || item.asItem().getItemMeta().displayName() == null) return null;
		command = command.trim();
		if (command.startsWith("/")) command = command.replaceFirst("/","");
		return new ItemableCommand(item,command,executeAs == null ? ExecuteAs.PLAYER : executeAs);
	}
	
	@Nullable
	@Contract("null -> null")
	public static ItemableCommand of(@Nullable Map<String,?> arguments) {
		return arguments == null ? null : of(ItemableStack.of(arguments),Utils.getString(arguments.get("Command")),ExecuteAs.get(Utils.getString(arguments.get("ExecuteAs"))));
	}
	
	@Override
	@NotNull
	public Map<@NotNull String,?> toMap() {
		Map<String,Object> map = new HashMap<>(item.toMap());
		map.put("Command",command);
		map.put("ExecuteAs",executeAs.name());
		return map;
	}
	
	@NotNull
	public ItemableCommand copy() {
		return this;
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