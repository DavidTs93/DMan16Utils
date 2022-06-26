package me.DMan16.DMan16Utils.Minigames;

import me.DMan16.DMan16Utils.Events.PlayerEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class MiniGameEvent extends PlayerEvent {
	private final @NotNull MiniGameInstance miniGameInstance;
	
	protected MiniGameEvent(@NotNull MiniGameInstance miniGameInstance,@NotNull Player player) {
		super(player);
		this.miniGameInstance = miniGameInstance;
	}
	
	@NotNull
	public final MiniGameInstance miniGameInstance() {
		return miniGameInstance;
	}
}