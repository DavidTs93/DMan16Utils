package me.DMan16.POPUtils.Items;

import me.DMan16.POPUtils.Interfaces.Itemable;
import me.DMan16.POPUtils.POPUtilsMain;
import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class DamageCommandListener implements CommandExecutor {
	public DamageCommandListener() {
		PluginCommand cmd = POPUtilsMain.getInstance().getCommand("damage");
		assert cmd != null;
		cmd.setExecutor(this);
	}
	
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player player)) return true;
		Itemable<?> item = ItemUtils.of(player.getInventory().getItemInMainHand());
		if (!(item instanceof Enchantable enchantable)) {
			player.sendMessage(Utils.chatColors("&cNo damageable item item in main hand!"));
			return true;
		}
		if (args.length <= 0) player.sendMessage(Utils.chatColors("&6Damage: &b" + enchantable.damage()));
		else try {
			int damage = Integer.parseInt(args[0]);
			if (damage <= 0) return true;
			enchantable.addDamage(damage);
			player.getInventory().setItemInMainHand(enchantable.shouldBreak() ? null : enchantable.asItem());
			player.sendMessage(Utils.chatColors(enchantable.shouldBreak() ? "&6Item broke" : "&aAdded &b" + damage + "&a damage"));
		} catch (Exception e) {}
		return true;
	}
}