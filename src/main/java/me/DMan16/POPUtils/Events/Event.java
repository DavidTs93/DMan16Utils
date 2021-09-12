package me.DMan16.POPUtils.Events;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Event extends org.bukkit.event.Event  {
	private static final HandlerList handlers = new HandlerList();
	protected final List<Runnable> immediateTasks;
	protected final List<Runnable> delayedTasks;
	
	protected Event() {
		this.immediateTasks = new ArrayList<>();
		this.delayedTasks = new ArrayList<>();
	}
	
	@NotNull
	public List<Runnable> immediateTasks() {
		return immediateTasks;
	}
	
	@NotNull
	public List<Runnable> delayedTasks() {
		return delayedTasks;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	@NotNull
	public final HandlerList getHandlers() {
		return handlers;
	}
}