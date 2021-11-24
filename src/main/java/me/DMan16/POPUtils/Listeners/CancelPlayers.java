package me.DMan16.POPUtils.Listeners;

import me.DMan16.POPUtils.Classes.Pair;
import me.DMan16.POPUtils.Interfaces.Listener;
import me.DMan16.POPUtils.Classes.Trio;
import me.DMan16.POPUtils.POPUtilsMain;
import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CancelPlayers implements Listener {
	private final HashMap<@NotNull Player,@NotNull Pair<@NotNull List<@NotNull Boolean>,@NotNull List<@NotNull Boolean>>> players = new HashMap<>();
	private MoveListener move = null;
	
	public CancelPlayers() {
		register(POPUtilsMain.getInstance());
	}
	
	public void addPlayer(@NotNull Player player, boolean allowRotation, boolean disableDamage) {
		if (Utils.isPlayerNPC(player)) return;
		int count = 1;
		Pair<List<Boolean>,List<Boolean>> info = players.get(player);
		if (info == null) players.put(player,Pair.of(new ArrayList<>(Arrays.asList(allowRotation)), new ArrayList<>(Arrays.asList(disableDamage))));
		else {
			info.first().add(allowRotation);
			info.second().add(disableDamage);
		}
		check();
	}
	
	public void addPlayer(@NotNull Player player) {
		addPlayer(player,false,false);
	}
	
	public void removePlayer(@NotNull Player player, boolean allowRotation, boolean disableDamage) {
		Pair<List<Boolean>,List<Boolean>> info = players.get(player);
		if (info == null || !info.first.contains(allowRotation) || !info.second.contains(disableDamage)) return;
		info.first.remove(allowRotation);
		info.second.remove(disableDamage);
		if (info.first.isEmpty()) players.remove(player);
		check();
	}
	
	public void removePlayer(@NotNull Player player) {
		removePlayer(player,false,false);
	}
	
	/**
	 * @return First - allow rotation, Second - disable damage, Third - counter
	 */
	@Nullable
	public Trio<@NotNull Boolean,@NotNull Boolean,@NotNull Integer> getPlayer(@NotNull Player player) {
		if (Utils.isPlayerNPC(player)) return null;
		Pair<List<Boolean>,List<Boolean>> info = players.get(player);
		if (info == null) return null;
		return Trio.of(!info.first.contains(false),info.second.contains(true),info.first.size());
	}
	
	public boolean isPlayerCancelled(@NotNull Player player) {
		return players.containsKey(player);
	}
	
	private void check() {
		if (players.isEmpty()) {
			if (move != null) {
				move.unregister();
				move = null;
			}
		} else if (move == null) move = new MoveListener();
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void cancelDeath(PlayerDeathEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void unregisterOnLeaveEvent(PlayerQuitEvent event) {
		players.remove(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent event) {
		if (players.containsKey(event.getPlayer())) event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onSwap(PlayerSwapHandItemsEvent event) {
		if (players.containsKey(event.getPlayer())) event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onDrop(PlayerDropItemEvent event) {
		if (players.containsKey(event.getPlayer())) event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPickup(EntityPickupItemEvent event) {
		if ((event.getEntity() instanceof Player player) && players.containsKey(player)) event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onClick(InventoryClickEvent event) {
		try {
			if (players.containsKey((Player) event.getWhoClicked())) event.setCancelled(true);
		} catch (Exception e) {}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if (players.containsKey(event.getPlayer())) event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onHotbar(PlayerItemHeldEvent event) {
		if (players.containsKey(event.getPlayer())) event.setCancelled(true);
	}
	
	private boolean allowRotation(@NotNull Player player) {
		Trio<Boolean,Boolean,Integer> info = getPlayer(player);
		return info != null && info.first;
	}
	
	private boolean allowRotation(Entity entity) {
		return (entity instanceof Player player) && allowRotation(player);
	}
	
	private boolean disableDamage(@NotNull Player player) {
		Trio<Boolean,Boolean,Integer> info = getPlayer(player);
		return info != null && info.second;
	}
	
	private boolean disableDamage(Entity entity) {
		return (entity instanceof Player player) && disableDamage(player);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageEvent event) {
		if (!disableDamage(event.getEntity())) return;
		event.setCancelled(true);
		event.setDamage(0);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onAirChange(EntityAirChangeEvent event) {
		if (disableDamage(event.getEntity())) event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onTarget(EntityTargetEvent event) {
		if (disableDamage(event.getTarget())) event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPotion(EntityPotionEffectEvent event) {
		if (disableDamage(event.getEntity())) event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onItemDamage(PlayerItemDamageEvent event) {
		if (disableDamage(event.getPlayer())) event.setCancelled(true);
	}
	
	private class MoveListener implements Listener {
		private MoveListener() {
			register(POPUtilsMain.getInstance());
		}
		
		@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
		public void onMove(PlayerMoveEvent event) {
			if (!players.containsKey(event.getPlayer())) return;
			if (event.hasChangedPosition() || (event.hasChangedOrientation() && !allowRotation(event.getPlayer()))) event.setCancelled(true);
		}
	}
}