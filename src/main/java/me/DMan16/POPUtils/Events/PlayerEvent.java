package me.DMan16.POPUtils.Events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerEvent extends HumanEvent  {
	protected PlayerEvent(@NotNull Player player) {
		super(player);
	}
	
	@NotNull
	public final Player player() {
		return (Player) human();
	}
	
	@NotNull
	public final Player getPlayer() {
		return (Player) getHuman();
	}
}