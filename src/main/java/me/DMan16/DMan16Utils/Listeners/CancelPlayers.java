package me.DMan16.DMan16Utils.Listeners;

import me.DMan16.DMan16Utils.Classes.Pair;
import me.DMan16.DMan16Utils.Classes.Trio;
import me.DMan16.DMan16Utils.Interfaces.Listener;
import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Utils.Utils;
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
import java.util.HashMap;
import java.util.List;

public class CancelPlayers implements Listener {
	private final HashMap<@NotNull Player,@NotNull Trio<@NotNull List<@NotNull Boolean>,@NotNull List<@NotNull Boolean>,@NotNull List<@NotNull Boolean>>> players = new HashMap<>();
	private MoveListener move = null;
	
	public CancelPlayers() {
		register(DMan16UtilsMain.getInstance());
	}
	
	public void addPlayer(@NotNull Player player, boolean allowRotation, boolean disableDamage, boolean disableInventoryClicks) {
		if (!player.isOnline() || Utils.isPlayerNPC(player)) return;
		int count = 1;
		Trio<List<Boolean>,List<Boolean>,List<Boolean>> info = players.get(player);
		if (info == null) players.put(player,Trio.of(new ArrayList<>(List.of(allowRotation)), new ArrayList<>(List.of(disableDamage)), new ArrayList<>(List.of(disableInventoryClicks))));
		else {
			info.first.add(allowRotation);
			info.second.add(disableDamage);
			info.third.add(disableInventoryClicks);
		}
		check();
	}
	
	public void addPlayer(@NotNull Player player, boolean allowRotation, boolean disableDamage) {
		addPlayer(player,false,false,true);
	}
	
	public void addPlayer(@NotNull Player player) {
		addPlayer(player,false,false);
	}
	
	public void removePlayer(@NotNull Player player, boolean allowRotation, boolean disableDamage, boolean disableInventoryClicks) {
		Trio<List<Boolean>,List<Boolean>,List<Boolean>> info = players.get(player);
		if (info == null || !info.first.contains(allowRotation) || !info.second.contains(disableDamage) || !info.third.contains(disableInventoryClicks)) return;
		info.first.remove(allowRotation);
		info.second.remove(disableDamage);
		info.third.remove(disableInventoryClicks);
		if (info.first.isEmpty()) players.remove(player);
		check();
	}
	
	public void removePlayer(@NotNull Player player, boolean allowRotation, boolean disableDamage) {
		removePlayer(player,false,false,true);
	}
	
	public void removePlayer(@NotNull Player player) {
		removePlayer(player,false,false);
	}
	
	/**
	 * @return First: counter, Second: First - allow rotation, Second - disable damage, Third - disable inventory clicks
	 */
	@Nullable
	public Pair<@NotNull Integer,@NotNull Trio<@NotNull Boolean,@NotNull Boolean,@NotNull Boolean>> getPlayer(@NotNull Player player) {
		if (Utils.isPlayerNPC(player)) return null;
		Trio<List<Boolean>,List<Boolean>,List<Boolean>> info = players.get(player);
		if (info == null) return null;
		return Pair.of(info.first.size(),Trio.of(!info.first.contains(false),info.second.contains(true),info.third.contains(true)));
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
		if (players.containsKey(event.getPlayer())) event.setCancelled(true);
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
			if (disableInventoryClicks((Player) event.getWhoClicked())) event.setCancelled(true);
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
		Pair<Integer,Trio<Boolean,Boolean,Boolean>> info = getPlayer(player);
		return info != null && info.second.first;
	}
	
	private boolean allowRotation(Entity entity) {
		return (entity instanceof Player player) && allowRotation(player);
	}
	
	private boolean disableDamage(@NotNull Player player) {
		Pair<Integer,Trio<Boolean,Boolean,Boolean>> info = getPlayer(player);
		return info != null && info.second.second;
	}
	
	private boolean disableDamage(Entity entity) {
		return (entity instanceof Player player) && disableDamage(player);
	}
	
	private boolean disableInventoryClicks(@NotNull Player player) {
		Pair<Integer,Trio<Boolean,Boolean,Boolean>> info = getPlayer(player);
		return info != null && info.second.third;
	}
	
	private boolean disableInventoryClicks(Entity entity) {
		return (entity instanceof Player player) && disableInventoryClicks(player);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
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
			register(DMan16UtilsMain.getInstance());
		}
		
		@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
		public void onMove(PlayerMoveEvent event) {
			if (!players.containsKey(event.getPlayer())) return;
			if (event.hasChangedPosition() || (event.hasChangedOrientation() && !allowRotation(event.getPlayer()))) event.setCancelled(true);
		}
	}
}