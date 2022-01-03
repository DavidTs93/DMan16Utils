package me.DMan16.POPUtils.Events;

import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SuccessfulLoginEvent extends Event implements Cancellable {
	public final AsyncPlayerPreLoginEvent event;
	private boolean cancelled;
	private Component kickMessage;
	
	public SuccessfulLoginEvent(@NotNull AsyncPlayerPreLoginEvent event) {
		super(true);
		this.event = event;
		this.cancelled = false;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	/**
	 * Can't be changed once it's disabled!
	 * Use {@link #disallow(Exception)} or {@link #notLoaded()} instead
	 */
	public void setCancelled(boolean cancel) {}
	
	public void disallow(@NotNull Exception exception) {
		cancelled = true;
		kickMessage = Utils.KICK_MESSAGE;
		exception.printStackTrace();
	}
	
	public void notLoaded() {
		cancelled = true;
		kickMessage = Utils.NOT_FINISHED_LOADING_MESSAGE;
	}
	
	@Nullable
	public Component kickMessage() {
		return kickMessage;
	}
}