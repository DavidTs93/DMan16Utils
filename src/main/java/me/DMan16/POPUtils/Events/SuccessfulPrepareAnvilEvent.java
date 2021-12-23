package me.DMan16.POPUtils.Events;

import me.DMan16.POPUtils.Classes.AdvancedRecipe;
import me.DMan16.POPUtils.Interfaces.Itemable;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SuccessfulPrepareAnvilEvent extends SuccessfulPrepareEvent<AnvilInventory> {
	public final PrepareAnvilEvent event;
	
	public SuccessfulPrepareAnvilEvent(@NotNull PrepareAnvilEvent event, @Nullable Itemable<?> first, @Nullable Itemable<?> second, @Nullable AdvancedRecipe<AnvilInventory> recipe,
									   @Nullable Itemable<?> result, @Nullable Itemable<?> firstAfter, @Nullable Itemable<?> secondAfter) {
		super(first,second,recipe,result,firstAfter,secondAfter);
		this.event = event;
	}
	
	@NotNull
	public PrepareAnvilEvent event() {
		return event;
	}
	
	@NotNull
	public SuccessfulPrepareAnvilEvent result(@Nullable Itemable<?> result) {
		this.result = result;
		return this;
	}
	
	@NotNull
	public SuccessfulPrepareAnvilEvent firstAfter(@Nullable Itemable<?> firstAfter) {
		this.firstAfter = firstAfter;
		return this;
	}
	
	@NotNull
	public SuccessfulPrepareAnvilEvent secondAfter(@Nullable Itemable<?> secondAfter) {
		this.secondAfter = secondAfter;
		return this;
	}
}