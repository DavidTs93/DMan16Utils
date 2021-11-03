package me.DMan16.POPUtils.Interfaces;

import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

public interface Backable {
	void goBack(@NotNull ClickType click);
	
	int slotBack();
}