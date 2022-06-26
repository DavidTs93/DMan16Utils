package me.DMan16.DMan16Utils.Holograms;

import me.DMan16.DMan16Utils.Events.SuccessfulJoinEvent;
import me.DMan16.DMan16Utils.Interfaces.Listener;
import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class HologramsManager implements Listener {
	private static final HashMap<@NotNull World,@NotNull Set<@NotNull Hologram<?>>> HOLOGRAMS = new HashMap<>();
	
	public HologramsManager() {
		register(DMan16UtilsMain.getInstance());
	}
	
	static void register(@NotNull Hologram<?> hologram) {
		HOLOGRAMS.putIfAbsent(hologram.getWorld(), new HashSet<>());
		HOLOGRAMS.get(hologram.getWorld()).add(hologram);
	}
	
	static void unregister(@NotNull Hologram<?> hologram) {
		if (HOLOGRAMS.containsKey(hologram.getWorld())) HOLOGRAMS.get(hologram.getWorld()).remove(hologram);
	}
	
	private static void spawn(@NotNull Player player, @NotNull World world) {
		new BukkitRunnable() {
			public void run() {
				Utils.runNotNull(HOLOGRAMS.get(world),holograms -> holograms.forEach(hologram -> hologram.spawn(player)));
			}
		}.runTaskLater(DMan16UtilsMain.getInstance(),1);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onJoin(SuccessfulJoinEvent event) {
		event.delayedTasks().add(() -> spawn(event.event.getPlayer(),event.event.getPlayer().getWorld()));
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onJoin(PlayerResourcePackStatusEvent event) {
		if (event.getStatus() != PlayerResourcePackStatusEvent.Status.ACCEPTED) spawn(event.getPlayer(),event.getPlayer().getWorld());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
		spawn(event.getPlayer(),event.getTo().getWorld());
	}
}