package me.DMan16.DMan16Utils.Minigames;

import me.DMan16.DMan16Utils.Classes.KeyedHashMap;
import me.DMan16.DMan16Utils.Classes.Pairs.Pair;
import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Interfaces.Listener;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class MiniGamesManager implements Listener {
	private final KeyedHashMap<@NotNull Pair<@NotNull MiniGame,@NotNull LinkedHashSet<@NotNull MiniGameInstance>>> miniGames = new KeyedHashMap<>();
	
	public MiniGamesManager(@NotNull JavaPlugin plugin) {
		if (plugin != DMan16UtilsMain.getInstance()) throw new IllegalArgumentException("Can't start a custom MiniGameManager!");
		register(plugin);
	}
	
	public boolean registerMiniGame(@NotNull MiniGame miniGame) {
		return miniGames.putIfAbsent(miniGame.key(),Pair.of(miniGame,new LinkedHashSet<>())) == null;
	}
	
	@Nullable
	public MiniGame getMiniGame(@NotNull String miniGame) {
		return Utils.applyNotNull(miniGames.get(miniGame),Pair::first);
	}
	
	@Unmodifiable
	@Nullable
	public Set<@NotNull MiniGameInstance> getMiniGameInstances(@NotNull String miniGame) {
		return Utils.applyNotNull(miniGames.get(miniGame),pair -> Collections.unmodifiableSet(pair.second()));
	}
	
	@Unmodifiable
	@Nullable
	public Set<@NotNull MiniGameInstance> getMiniGameInstances(@NotNull MiniGame miniGame) {
		return getMiniGameInstances(miniGame.key());
	}
	
	public boolean registerMiniGameInstance(@NotNull MiniGameInstance miniGameInstance) {
		Pair<MiniGame,LinkedHashSet<MiniGameInstance>> pair = miniGames.get(miniGameInstance.miniGame().key());
		if (pair == null) return false;
		return pair.second().add(miniGameInstance);
	}
	
	@Nullable
	public MiniGameInstance getPlayerActiveMiniGame(@NotNull Player player) {
		for (Map.Entry<String,Pair<MiniGame,LinkedHashSet<MiniGameInstance>>> entry : miniGames.entrySet()) for (MiniGameInstance miniGameInstance : entry.getValue().second()) if (miniGameInstance.hasPlayer(player)) return miniGameInstance;
		return null;
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Utils.runNotNull(getPlayerActiveMiniGame(event.getPlayer()),miniGameInstance -> miniGameInstance.playerQuit(event.getPlayer()));
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		Utils.runNotNull(getPlayerActiveMiniGame(event.getPlayer()),miniGameInstance -> miniGameInstance.playerTeleport(event.getPlayer(),event));
	}
}