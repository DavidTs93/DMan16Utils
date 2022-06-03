package me.DMan16.DMan16Utils.Events.Callers;

import me.DMan16.DMan16Utils.Events.BlockChangeEvent;
import me.DMan16.DMan16Utils.Interfaces.Listener;
import me.DMan16.DMan16Utils.DMan16UtilsMain;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;

public class BlockChangeListener implements Listener {
	public BlockChangeListener() {
		register(DMan16UtilsMain.getInstance());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onFade(BlockFadeEvent event) {
		new BlockChangeEvent(event).callEventAndDoTasksIfNotCancelled();
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onGrow(BlockGrowEvent event) {
		new BlockChangeEvent(event).callEventAndDoTasksIfNotCancelled();
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onForm(BlockFormEvent event) {
		new BlockChangeEvent(event).callEventAndDoTasksIfNotCancelled();
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onSpread(BlockSpreadEvent event) {
		new BlockChangeEvent(event).callEventAndDoTasksIfNotCancelled();
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onSpread(EntityBlockFormEvent event) {
		new BlockChangeEvent(event).callEventAndDoTasksIfNotCancelled();
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onDecay(LeavesDecayEvent event) {
		new BlockChangeEvent(event).callEventAndDoTasksIfNotCancelled();
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onFluidLevelChange(FluidLevelChangeEvent event) {
		new BlockChangeEvent(event).callEventAndDoTasksIfNotCancelled();
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onMoistureChange(MoistureChangeEvent event) {
		new BlockChangeEvent(event).callEventAndDoTasksIfNotCancelled();
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onAbsorb(SpongeAbsorbEvent event) {
		new BlockChangeEvent(event).callEventAndDoTasksIfNotCancelled();
	}
}