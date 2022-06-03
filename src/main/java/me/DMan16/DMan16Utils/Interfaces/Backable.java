package me.DMan16.DMan16Utils.Interfaces;

import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

public interface Backable {
	void goBack(@NotNull ClickType click);
	
	int slotBack();
}