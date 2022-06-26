package me.DMan16.DMan16Utils.Minigames;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface MiniGameRegion {
	boolean teleportPlayerToLobby(@NotNull Player player);
	
	boolean intersects(@NotNull MiniGameRegion region);
}