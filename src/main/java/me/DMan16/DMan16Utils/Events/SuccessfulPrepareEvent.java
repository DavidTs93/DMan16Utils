package me.DMan16.DMan16Utils.Events;

import me.DMan16.DMan16Utils.Classes.AdvancedRecipe;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SuccessfulPrepareEvent<V extends Inventory> extends Event implements Cancellable {
	public final @Nullable Itemable<?> first;
	public final @Nullable Itemable<?> second;
	public final @Nullable AdvancedRecipe<V> recipe;
	protected @Nullable Itemable<?> result;
	protected @Nullable Itemable<?> firstAfter;
	protected @Nullable Itemable<?> secondAfter;
	public final @Nullable Itemable<?> originalResult;
	private boolean cancelled;
	
	public SuccessfulPrepareEvent(@Nullable Itemable<?> first,@Nullable Itemable<?> second,@Nullable AdvancedRecipe<V> recipe,
								  @Nullable Itemable<?> result,@Nullable Itemable<?> firstAfter,@Nullable Itemable<?> secondAfter, @Nullable Itemable<?> originalResult) {
		this.first = first;
		this.second = second;
		this.recipe = recipe;
		this.result = result;
		this.firstAfter = firstAfter;
		this.secondAfter = secondAfter;
		this.originalResult = originalResult;
		this.cancelled = false;
	}
	
	public final boolean isCancelled() {
		return cancelled;
	}
	
	public final void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
	
	@Nullable
	public final Itemable<?> result() {
		return result;
	}
	
	@Nullable
	public final Itemable<?> firstAfter() {
		return firstAfter;
	}
	
	@Nullable
	public final Itemable<?> secondAfter() {
		return secondAfter;
	}
	
	public abstract @NotNull InventoryEvent event();
	public abstract @NotNull SuccessfulPrepareEvent<V> result(@Nullable Itemable<?> result);
	public abstract @NotNull SuccessfulPrepareEvent<V> firstAfter(@Nullable Itemable<?> firstAfter);
	public abstract @NotNull SuccessfulPrepareEvent<V> secondAfter(@Nullable Itemable<?> secondAfter);
}