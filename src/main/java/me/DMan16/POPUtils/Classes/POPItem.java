package me.DMan16.POPUtils.Classes;

import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public record POPItem(@NotNull String key, @Nullable Consumer<@NotNull PlayerInteractEvent> rightClick, @Nullable Consumer<@NotNull PlayerInteractEvent> leftClick) {
	public POPItem(@NotNull String key, @Nullable Consumer<@NotNull PlayerInteractEvent> rightClick, @Nullable Consumer<@NotNull PlayerInteractEvent> leftClick) {
		this.key = Utils.fixKey(key);
		this.rightClick = rightClick;
		this.leftClick = leftClick;
	}
	
	@NotNull
	public POPItem rightClick(@NotNull PlayerInteractEvent event) {
		if (rightClick != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) rightClick.accept(event);
		return this;
	}
	
	@NotNull
	public POPItem leftClick(@NotNull PlayerInteractEvent event) {
		if (leftClick != null && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) leftClick.accept(event);
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof POPItem other)) return false;
		return other.key().equalsIgnoreCase(key());
	}
}