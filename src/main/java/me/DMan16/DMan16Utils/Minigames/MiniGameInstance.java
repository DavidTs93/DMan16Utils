package me.DMan16.DMan16Utils.Minigames;

import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MiniGameInstance {
	private static final @NotNull AtomicInteger MINI_GAME_COUNTER = new AtomicInteger();
	
	private final int ID = MINI_GAME_COUNTER.incrementAndGet();
	private final @NotNull MiniGame miniGame;
	private final @NotNull LinkedHashMap<@NotNull Player,@NotNull MiniGamePlayerData> players = new LinkedHashMap<>();
	private final @NotNull MiniGameRegion region;
	private @NotNull MiniGameState state = MiniGameState.INITIATING;
	
	protected MiniGameInstance(@NotNull MiniGame miniGame,@NotNull MiniGameRegion region) {
		this.miniGame = miniGame;
		this.region = region;
	}
	
	@NotNull
	public MiniGame miniGame() {
		return miniGame;
	}
	
	private boolean stateAllowsJoining() {
		return state == MiniGameState.LOBBY;
	}
	
	private boolean passesExtraJoinConditions(@NotNull Player player) {
		return true;
	}
	
	private void joinExtraStuff(@NotNull Player player) {}
	
	protected abstract @NotNull MiniGamePlayerData getPlayerData(@NotNull Player player);
	protected abstract void updatePlayerData(@NotNull Player player,@NotNull MiniGamePlayerData playerData);
	
	public final boolean join(@NotNull Player player) {
		if (!stateAllowsJoining()) return false;
		if (!miniGame.canJoin(player)) return false;
		if (!passesExtraJoinConditions(player)) return false;
		if (Utils.getMiniGamesManager().getPlayerActiveMiniGame(player) != null) return false;
		if (!new MiniGamePreJoinEvent(this,player).callEventAndDoTasksIfNotCancelled()) return false;
		if (!region.teleportPlayerToLobby(player)) return false;
		players.put(player,getPlayerData(player));
		joinExtraStuff(player);
		new MiniGameJoinEvent(this,player).callEventAndDoTasks();
		return true;
	}
	
	protected final boolean removePlayer(@NotNull Player player) {
		return players.remove(player) != null;
	}
	
	void playerQuit(@NotNull Player player) {
		if (removePlayer(player)) miniGame.playerQuit(player,this);
	}
	
	void playerTeleport(@NotNull Player player,@NotNull PlayerTeleportEvent event) {
		if (hasPlayer(player)) miniGame.playerTeleport(player,event,this);
	}
	
	@NotNull
	public MiniGameState state() {
		return state;
	}
	
	public boolean started() {
		return state == MiniGameState.RUNNING;
	}
	
	public boolean ended() {
		return state == MiniGameState.ENDED;
	}
	
	public boolean hasPlayer(@NotNull Player player) {
		return players.get(player) != null;
	}
	
	@Override
	public int hashCode() {
		return ID;
	}
}