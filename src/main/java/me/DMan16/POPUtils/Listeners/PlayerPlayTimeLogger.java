package me.DMan16.POPUtils.Listeners;

import me.DMan16.POPUtils.Interfaces.Listener;
import me.DMan16.POPUtils.POPUtilsMain;
import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

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
		register(POPUtilsMain.getInstance());
		this.lastUpdate = System.currentTimeMillis();
		this.joinedSinceLastUpdate = new HashMap<>();
		/*this.task = */new BukkitRunnable() {
			public void run() {
				new BukkitRunnable() {
					public void run() {
						update();
					}
				}.runTask(POPUtilsMain.getInstance());
			}
		}.runTaskTimerAsynchronously(POPUtilsMain.getInstance(),60 * 20,60 * 20);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onQuit(PlayerQuitEvent event) {
		if (joinedSinceLastUpdate.remove(event.getPlayer().getUniqueId()) != null) try (Statement statement = Utils.getConnection().createStatement()) {
			statement.executeUpdate("UPDATE PrisonPOP_Players SET LastQuit = CURRENT_TIMESTAMP, TotalPlayTimeMillis = TotalPlayTimeMillis + " + timeSinceLastUpdate(System.currentTimeMillis()) + " WHERE UUID = '" + event.getPlayer().getUniqueId() + "';");
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
			try (Statement statement = Utils.getConnection().createStatement()) {
				statement.executeUpdate("UPDATE PrisonPOP_Players SET TotalPlayTimeMillis = TotalPlayTimeMillis + " + timeSinceLastUpdate(now,joined) + " WHERE UUID = '" + ID + "';");
			} catch (Exception e) {e.printStackTrace();}
		});
		joinedSinceLastUpdate.clear();
		try (Statement statement = Utils.getConnection().createStatement()) {
			statement.executeUpdate("UPDATE PrisonPOP_Players SET TotalPlayTimeMillis = TotalPlayTimeMillis + " + time + " WHERE UUID IN ('" + players.stream().map(UUID::toString).collect(Collectors.joining("','")) + "');");
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public Long currentPlayTime(@NotNull UUID ID) {
		return joinedSinceLastUpdate.get(ID);
	}
}