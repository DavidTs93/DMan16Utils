package me.DMan16.POPUtils.Events;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Event extends org.bukkit.event.Event  {
	private static final HandlerList handlers = new HandlerList();
	private final List<Runnable> immediateTasks;
	private final List<Runnable> delayedTasks;
	
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
	
	public Event callEventAndDoTasks() {
		callEvent();
		doTasks();
		return this;
	}
	
	public boolean callEventAndDoTasksIfNotCancelled() {
		if (!callEvent()) return false;
		doTasks();
		return true;
	}
	
	public void doTasks() {
		doImmediateTasks();
		doDelayedTasks();
	}
	
	public void doImmediateTasks() {
		immediateTasks().forEach(Runnable::run);
	}
	
	public void doDelayedTasks() {
		delayedTasks().forEach(Runnable::run);
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