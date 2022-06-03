package me.DMan16.DMan16Utils.Listeners;

import me.DMan16.DMan16Utils.Interfaces.Listener;
import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerPlayTimeLogger implements Listener {
	private long lastUpdate;
	private final HashMap<@NotNull UUID,@NotNull Long> joinedSinceLastUpdate;
//	private final BukkitTask task;
	
	public PlayerPlayTimeLogger() {
		register(DMan16UtilsMain.getInstance());
		this.lastUpdate = System.currentTimeMillis();
		this.joinedSinceLastUpdate = new HashMap<>();
		/*this.task = */new BukkitRunnable() {
			public void run() {
				new BukkitRunnable() {
					public void run() {
						update();
					}
				}.runTask(DMan16UtilsMain.getInstance());
			}
		}.runTaskTimerAsynchronously(DMan16UtilsMain.getInstance(),60 * 20,60 * 20);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onQuit(PlayerQuitEvent event) {
		try (Connection connection = Utils.getConnection(); Statement statement = connection.createStatement()) {
			statement.executeUpdate("UPDATE Players_Data SET LastQuit=CURRENT_TIMESTAMP" + (joinedSinceLastUpdate.remove(event.getPlayer().getUniqueId()) != null ? ", TotalPlayTimeMillis=TotalPlayTimeMillis + " + timeSinceLastUpdate(System.currentTimeMillis()) : "") +
					" WHERE UUID='" + event.getPlayer().getUniqueId() + "';");
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public void justJoined(@NotNull Player player) {
		justJoined(player.getUniqueId());
	}
	
	public void justJoined(@NotNull UUID ID) {
		joinedSinceLastUpdate.putIfAbsent(ID,System.currentTimeMillis());
	}
	
	private long timeSinceLastUpdate(long now,long lastUpdate) {
		return now - lastUpdate;
	}
	
	private long timeSinceLastUpdate(long now) {
		return timeSinceLastUpdate(now,lastUpdate);
	}
	
	private void update() {
		final long now = System.currentTimeMillis(),time = timeSinceLastUpdate(now);
		lastUpdate = now;
		Set<UUID> players = Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
		players.removeAll(joinedSinceLastUpdate.keySet());
		joinedSinceLastUpdate.forEach((ID,joined) -> {
			try (Connection connection = Utils.getConnection(); Statement statement = connection.createStatement()) {
				statement.executeUpdate("UPDATE Players_Data SET TotalPlayTimeMillis=TotalPlayTimeMillis + " + timeSinceLastUpdate(now,joined) + " WHERE UUID='" + ID + "';");
			} catch (Exception e) {e.printStackTrace();}
		});
		joinedSinceLastUpdate.clear();
		try (Connection connection = Utils.getConnection(); Statement statement = connection.createStatement()) {
			statement.executeUpdate("UPDATE Players_Data SET TotalPlayTimeMillis=TotalPlayTimeMillis + " + time + " WHERE UUID IN ('" + players.stream().map(UUID::toString).collect(Collectors.joining("','")) + "');");
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public Long currentPlayTime(@NotNull UUID ID) {
		return joinedSinceLastUpdate.get(ID);
	}
}