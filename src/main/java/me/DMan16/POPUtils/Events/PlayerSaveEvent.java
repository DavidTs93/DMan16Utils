package me.DMan16.POPUtils.Events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerSaveEvent extends PlayerEvent {
	public PlayerSaveEvent(@NotNull Player player) {
		super(player);
	}
}