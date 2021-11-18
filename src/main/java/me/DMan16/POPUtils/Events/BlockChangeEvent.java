package me.DMan16.POPUtils.Events;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

public class BlockChangeEvent extends Event implements Cancellable {
	public final BlockEvent event;
	
	public BlockChangeEvent(@NotNull BlockEvent event) {
		this.event = event;
	}
	
	@NotNull
	public Block getBlock() {
		return event.getBlock();
	}
	
	public boolean isCancelled() {
		return (event instanceof Cancellable cancellable) && cancellable.isCancelled();
	}
	
	public void setCancelled(boolean cancel) {
		if (event instanceof Cancellable cancellable) cancellable.setCancelled(cancel);
	}
}