package me.DMan16.POPUtils.Items;

import me.DMan16.POPUtils.Interfaces.Itemable;
import me.DMan16.POPUtils.POPUtilsMain;
import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class RepairCommandListener implements CommandExecutor {
	public RepairCommandListener() {
		PluginCommand cmd = POPUtilsMain.getInstance().getCommand("repair");
		assert cmd != null;
		cmd.setExecutor(this);
	}
	
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player player)) return true;
		Itemable<?> item = ItemUtils.of(player.getInventory().getItemInMainHand());
		if (!(item instanceof Enchantable enchantable)) {
			player.sendMessage(Utils.chatColors("&cNo damageable item in main hand!"));
			return true;
		}
		int amount = enchantable.maxDurability();
		try {
			amount = Integer.parseInt(args[0]);
			if (amount <= 0) return true;
		} catch (Exception e) {}
		enchantable.reduceDamage(amount);
		player.getInventory().setItemInMainHand(enchantable.asItem());
		player.sendMessage(Utils.chatColors("&aRepaired " + (enchantable.damage() == 0 ? "fully" : "&b" + amount + "&a damage")));
		return true;
	}
}