package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.Interfaces.Repairable;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class RepairCommandListener implements CommandExecutor {
	public RepairCommandListener() {
		PluginCommand cmd = DMan16UtilsMain.getInstance().getCommand("repair");
		assert cmd != null;
		cmd.setExecutor(this);
	}
	
	public boolean onCommand(@NotNull CommandSender sender,@NotNull Command cmd,@NotNull String label,@NotNull String[] args) {
		if (!(sender instanceof Player player)) return true;
		Itemable<?> item = ItemUtils.of(player.getInventory().getItemInMainHand());
		if (!(item instanceof Repairable repairable)) {
			player.sendMessage(Utils.chatColors("&cNo damageable item in main hand!"));
			return true;
		}
		int amount = repairable.maxDurability();
		try {
			amount = Integer.parseInt(args[0]);
			if (amount <= 0) return true;
		} catch (Exception e) {}
		repairable.reduceDamage(amount);
		player.getInventory().setItemInMainHand(item.asItem());
		player.sendMessage(Utils.chatColors("&aRepaired " + (repairable.damage() == 0 ? "fully" : "&b" + amount + "&a damage")));
		return true;
	}
}