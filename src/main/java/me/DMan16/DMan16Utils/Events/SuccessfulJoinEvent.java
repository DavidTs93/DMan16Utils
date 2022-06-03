package me.DMan16.DMan16Utils.Events;

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
	
	/**
	 * Can't be changed once it's disabled!
	 * Use {@link #disallow(Exception)} instead
	 */
	@Deprecated
	public void setCancelled(boolean cancel) {}
	
	public void disallow(@NotNull Exception exception) {
		cancelled = true;
		exception.printStackTrace();
	}
}