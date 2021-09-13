package me.DMan16.POPUtils.Listeners;

import me.DMan16.POPUtils.Classes.Listener;
import me.DMan16.POPUtils.Classes.Trio;
import me.DMan16.POPUtils.POPUtilsMain;
import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class CancelPlayers implements Listener {
	private final HashMap<Player,Trio<Boolean,Boolean,Integer>> players = new HashMap<>();
	private MoveListener move = null;
	
	public CancelPlayers() {
		register(POPUtilsMain.getInstance());
	}
	
	public void addPlayer(@NotNull Player player) {
		addPlayer(player,false,false);
	}
	
	public void addPlayer(@NotNull Player player, boolean allowRotation, boolean disableDamage) {
		if (Utils.isPlayerNPC(player)) return;
		int count = 1;
		if (players.containsKey(player)) {
			Trio<Boolean,Boolean,Integer> info = players.get(player);
			allowRotation = allowRotation && info.first();
			disableDamage = disableDamage || info.second();
			count += info.third();
		}
		players.put(player,Trio.of(allowRotation,disableDamage,count));
		check();
	}
	
	/**
	 * @return First - allow rotation, Second - disable damage, Third - counter
	 */
	public Trio<Boolean,Boolean,Integer> getPlayer(@NotNull Player player) {
		if (Utils.isPlayerNPC(player)) return null;
		return players.get(player);
	}
	
	public void removePlayer(@NotNull Player player) {
		removePlayer(player,false);
	}
	
	public void removePlayer(@NotNull Player player, boolean force) {
		if (!players.containsKey(player)) return;
		if (force) players.remove(player);
		else {
			Trio<Boolean,Boolean,Integer> info = players.get(player);
			if (info.third() - 1 <= 0) players.remove(player);
			else players.put(player,Trio.of(info.first(),info.second(),info.third() - 1));
		}
		check();
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
	public void unregisterOnLeaveEvent(PlayerQuitEvent event) {
		removePlayer(event.getPlayer(),true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent event) {
		if (players.containsKey(event.getPlayer())) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onSwap(PlayerSwapHandItemsEvent event) {
		if (players.containsKey(event.getPlayer())) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDrop(PlayerDropItemEvent event) {
		if (players.containsKey(event.getPlayer())) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(InventoryClickEvent event) {
		try {
			if (players.containsKey((Player) event.getWhoClicked())) event.setCancelled(true);
		} catch (Exception e) {}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if (players.containsKey(event.getPlayer())) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onHotbar(PlayerItemHeldEvent event) {
		if (players.containsKey(event.getPlayer())) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageEvent event) {
		if ((event.getEntity() instanceof Player) && players.containsKey((Player) event.getEntity()) && players.get((Player) event.getEntity()).second()) {
			event.setCancelled(true);
			event.setDamage(0);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onAirChange(EntityAirChangeEvent event) {
		if ((event.getEntity() instanceof Player) && players.containsKey((Player) event.getEntity()) && players.get((Player) event.getEntity()).second()) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onTarget(EntityTargetEvent event) {
		if (event.getTarget() != null && (event.getTarget() instanceof Player) && players.containsKey((Player) event.getTarget()) && players.get((Player) event.getTarget()).second())
			event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPotion(EntityPotionEffectEvent event) {
		if ((event.getEntity() instanceof Player) && players.containsKey((Player) event.getEntity()) && players.get((Player) event.getEntity()).second()) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onItemDamage(PlayerItemDamageEvent event) {
		if (players.containsKey(event.getPlayer()) && players.get(event.getPlayer()).second()) event.setCancelled(true);
	}
	
	private class MoveListener implements Listener {
		private MoveListener() {
			register(POPUtilsMain.getInstance());
		}
		
		@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
		public void onMove(PlayerMoveEvent event) {
			if (!players.containsKey(event.getPlayer())) return;
			if (!event.hasChangedBlock() && event.hasChangedOrientation() && players.containsKey(event.getPlayer()) && players.get(event.getPlayer()).first()) return;
			event.setCancelled(true);
		}
	}
}