package me.DMan16.POPUtils.Events;

import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class SuccessfulJoinEvent extends Event implements Cancellable {
	public final PlayerJoinEvent event;
	private boolean cancelled;
	
	public SuccessfulJoinEvent(@NotNull PlayerJoinEvent event) {
		this.event = event;
		this.cancelled = false;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
	
	public void disallow(@NotNull Exception exception) {
		exception.printStackTrace();
		event.getPlayer().kick(Utils.KICK_MESSAGE);
		setCancelled(true);
	}
}