package me.DMan16.DMan16Utils.Events;

import me.DMan16.DMan16Utils.Classes.CustomRecipe;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;

public final class SuccessfulPrepareCraftingEvent extends Event implements Cancellable {
	public final PrepareItemCraftEvent event;
	public final @NotNull @Unmodifiable List<@Nullable Itemable<?>> items;
	public final @Nullable CustomRecipe recipe;
	private @Nullable Itemable<?> result;
	private @Nullable List<@Nullable Itemable<?>> itemsAfter;
	private boolean cancelled;
	
	public SuccessfulPrepareCraftingEvent(@NotNull PrepareItemCraftEvent event,@NotNull List<@Nullable Itemable<?>> items,@Nullable CustomRecipe recipe,
										  @Nullable Itemable<?> result,@Nullable List<@Nullable Itemable<?>> itemsAfter) {
		this.event = event;
		this.items = Collections.unmodifiableList(items);
		this.recipe = recipe;
		this.result = result;
		this.itemsAfter = itemsAfter;
	}
	
	@NotNull
	public PrepareItemCraftEvent event() {
		return event;
	}
	
	@Nullable
	public Itemable<?> result() {
		return result;
	}
	
	@Nullable
	public List<@Nullable Itemable<?>> itemsAfter() {
		return itemsAfter;
	}
	
	@NotNull
	public SuccessfulPrepareCraftingEvent result(@Nullable Itemable<?> result) {
		this.result = result;
		return this;
	}
	
	@NotNull
	public SuccessfulPrepareCraftingEvent itemsAfter(@Nullable List<Itemable<?>> items) {
		this.itemsAfter = items;
		return this;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}