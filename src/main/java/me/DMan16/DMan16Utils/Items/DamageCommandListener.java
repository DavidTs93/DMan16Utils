package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Interfaces.Repairable;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class DamageCommandListener implements CommandExecutor {
	public DamageCommandListener() {
		PluginCommand cmd = DMan16UtilsMain.getInstance().getCommand("damage");
		assert cmd != null;
		cmd.setExecutor(this);
	}
	
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player player)) return true;
		Itemable<?> item = ItemUtils.of(player.getInventory().getItemInMainHand());
		if (!(item instanceof Repairable repairable)) {
			player.sendMessage(Utils.chatColors("&cNo damageable item item in main hand!"));
			return true;
		}
		if (args.length <= 0) player.sendMessage(Utils.chatColors("&6Damage: &b" + repairable.damage()));
		else try {
			int damage = Integer.parseInt(args[0]);
			if (damage <= 0) return true;
			repairable.addDamage(damage);
			player.getInventory().setItemInMainHand(item.asItem());
			player.sendMessage(Utils.chatColors(repairable.shouldBreak() ? "&6Item broke" : "&aAdded &b" + damage + "&a damage"));
		} catch (Exception e) {}
		return true;
	}
}