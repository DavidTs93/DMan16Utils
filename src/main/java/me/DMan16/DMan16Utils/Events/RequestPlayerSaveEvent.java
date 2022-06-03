package me.DMan16.DMan16Utils.Events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RequestPlayerSaveEvent extends PlayerEvent {
	public RequestPlayerSaveEvent(@NotNull Player player) {
		super(player);
	}
}