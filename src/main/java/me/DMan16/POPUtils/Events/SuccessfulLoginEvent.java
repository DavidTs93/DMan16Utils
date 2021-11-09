package me.DMan16.POPUtils.Events;

import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.NotNull;

public class SuccessfulLoginEvent extends Event implements Cancellable {
	public final AsyncPlayerPreLoginEvent event;
	private boolean cancelled;
	
	public SuccessfulLoginEvent(@NotNull AsyncPlayerPreLoginEvent event) {
		super(true);
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
		event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,Utils.KICK_MESSAGE);
		setCancelled(true);
	}
	
	public void notLoaded() {
		event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,Utils.NOT_FINISHED_LOADING_MESSAGE);
		setCancelled(true);
	}
}