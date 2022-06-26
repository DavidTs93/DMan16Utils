package me.DMan16.DMan16Utils.Minigames;

import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class MiniGame {
	private final @NotNull String key;
	
	public MiniGame(@NotNull String key) {
		this.key = Objects.requireNonNull(Utils.fixKey(key));
	}
	
	@NotNull
	public String key() {
		return key;
	}
	
	public boolean canJoin(@NotNull Player player) {
		return true;
	}
	
	void playerQuit(@NotNull Player player,@NotNull MiniGameInstance miniGameInstance) {}
	
	void playerTeleport(@NotNull Player player,@NotNull PlayerTeleportEvent event,@NotNull MiniGameInstance miniGameInstance) {
		event.setCancelled(true);
	}
}