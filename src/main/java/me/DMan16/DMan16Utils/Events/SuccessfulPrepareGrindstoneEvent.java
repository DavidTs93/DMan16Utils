package me.DMan16.DMan16Utils.Events;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import me.DMan16.DMan16Utils.Classes.AdvancedRecipe;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import org.bukkit.inventory.GrindstoneInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SuccessfulPrepareGrindstoneEvent extends SuccessfulPrepareEvent<GrindstoneInventory> {
	public final PrepareResultEvent event;
	
	public SuccessfulPrepareGrindstoneEvent(@NotNull PrepareResultEvent event,@Nullable Itemable<?> first,@Nullable Itemable<?> second,@Nullable AdvancedRecipe<GrindstoneInventory> recipe,
											@Nullable Itemable<?> result,@Nullable Itemable<?> firstAfter,@Nullable Itemable<?> secondAfter,@Nullable Itemable<?> originalResult) {
		super(first,second,recipe,result,firstAfter,secondAfter,originalResult);
		this.event = event;
	}
	
	@NotNull
	public PrepareResultEvent event() {
		return event;
	}
	
	@NotNull
	public SuccessfulPrepareGrindstoneEvent result(@Nullable Itemable<?> result) {
		this.result = result;
		return this;
	}
	
	@NotNull
	public SuccessfulPrepareGrindstoneEvent firstAfter(@Nullable Itemable<?> firstAfter) {
		this.firstAfter = firstAfter;
		return this;
	}
	
	@NotNull
	public SuccessfulPrepareGrindstoneEvent secondAfter(@Nullable Itemable<?> secondAfter) {
		this.secondAfter = secondAfter;
		return this;
	}
}