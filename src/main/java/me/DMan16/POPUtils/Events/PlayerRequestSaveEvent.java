package me.DMan16.POPUtils.Events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerRequestSaveEvent extends PlayerEvent {
	public PlayerRequestSaveEvent(@NotNull Player player) {
		super(player);
	}
}