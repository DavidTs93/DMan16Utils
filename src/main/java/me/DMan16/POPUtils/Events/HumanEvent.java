package me.DMan16.POPUtils.Events;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class HumanEvent extends Event  {
	private final HumanEntity human;
	
	protected HumanEvent(@NotNull HumanEntity human) {
		this.human = human;
	}
	
	@NotNull
	public final HumanEntity human() {
		return human;
	}
	
	@NotNull
	public final HumanEntity getHuman() {
		return human;
	}
}