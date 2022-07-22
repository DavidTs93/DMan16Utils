package me.DMan16.DMan16Utils.Events;

import me.DMan16.DMan16Utils.Classes.AdvancedRecipe;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.SmithingInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SuccessfulPrepareSmithingEvent extends SuccessfulPrepareEvent<SmithingInventory> {
	public final PrepareSmithingEvent event;
	
	public SuccessfulPrepareSmithingEvent(@NotNull PrepareSmithingEvent event,@Nullable Itemable<?> first,@Nullable Itemable<?> second,@Nullable AdvancedRecipe<SmithingInventory> recipe,
										  @Nullable Itemable<?> result,@Nullable Itemable<?> firstAfter,@Nullable Itemable<?> secondAfter,@Nullable Itemable<?> originalResult) {
		super(first,second,recipe,result,firstAfter,secondAfter,originalResult);
		this.event = event;
	}
	
	@NotNull
	public PrepareSmithingEvent event() {
		return event;
	}
	
	@NotNull
	public SuccessfulPrepareSmithingEvent result(@Nullable Itemable<?> result) {
		this.result = result;
		return this;
	}
	
	@NotNull
	public SuccessfulPrepareSmithingEvent firstAfter(@Nullable Itemable<?> firstAfter) {
		this.firstAfter = firstAfter;
		return this;
	}
	
	@NotNull
	public SuccessfulPrepareSmithingEvent secondAfter(@Nullable Itemable<?> secondAfter) {
		this.secondAfter = secondAfter;
		return this;
	}
}