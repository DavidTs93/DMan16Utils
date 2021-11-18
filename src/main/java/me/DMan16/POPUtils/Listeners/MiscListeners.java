package me.DMan16.POPUtils.Listeners;

import me.DMan16.POPUtils.Interfaces.Listener;
import me.DMan16.POPUtils.POPUtilsMain;
import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

public class MiscListeners implements Listener {
	public MiscListeners() {
		register(POPUtilsMain.getInstance());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onDeathNPC(PlayerDeathEvent event) {
		if (Utils.isPlayerNPC(event.getPlayer())) event.deathMessage(null);
	}
}