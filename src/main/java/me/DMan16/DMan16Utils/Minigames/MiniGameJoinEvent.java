package me.DMan16.DMan16Utils.Minigames;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class MiniGameJoinEvent extends MiniGameEvent implements Cancellable {
	private boolean cancel;
	
	public MiniGameJoinEvent(@NotNull MiniGameInstance miniGameInstance,@NotNull Player player) {
		super(miniGameInstance,player);
	}
	
	public void setCancelled(final boolean cancel) {
		this.cancel = cancel;
	}
	
	public boolean isCancelled() {
		return cancel;
	}
}