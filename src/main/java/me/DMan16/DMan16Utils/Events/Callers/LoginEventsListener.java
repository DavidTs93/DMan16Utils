package me.DMan16.DMan16Utils.Events.Callers;

import me.DMan16.DMan16Utils.Classes.Pairs.Pair;
import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Events.SuccessfulJoinEvent;
import me.DMan16.DMan16Utils.Events.SuccessfulLoginEvent;
import me.DMan16.DMan16Utils.Interfaces.Listener;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Objects;

public class LoginEventsListener implements Listener {
	public LoginEventsListener() {
		register(DMan16UtilsMain.getInstance());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onSuccessfulLogin(me.DMan16.DMan16Updater.AllowLoginEvent event) {
		SuccessfulLoginEvent loginEvent = new SuccessfulLoginEvent(event.event);
		if (!loginEvent.callEventAndDoTasksIfNotCancelled()) event.event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,Utils.thisOrThatOrNull(loginEvent.kickMessage(),Component.empty()));
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (Utils.isPlayerNPC(player)) return;
		SuccessfulJoinEvent joinEvent = new SuccessfulJoinEvent(event);
		try (Connection connection = Utils.getConnection(); Statement statement = connection.createStatement()) {
			Pair<String,String> skin = Utils.getSkin(Utils.getProfile(player));
			Objects.requireNonNull(skin.first());
			statement.executeUpdate("UPDATE Players_Data SET SkinData='" + skin.first() + "', SkinSignature='" + skin.second() + "' WHERE UUID='" + player.getUniqueId() + "';");
		} catch (Exception e) {
			joinEvent.disallow(e);
		}
		if (!joinEvent.callEventAndDoTasksIfNotCancelled()) event.getPlayer().kick(Utils.KICK_MESSAGE);
		else {
			try (Connection connection = Utils.getConnection(); Statement statement = connection.createStatement()) {
				statement.executeUpdate("UPDATE Players_Data SET LastJoin = CURRENT_TIMESTAMP;");
			} catch (Exception e) {e.printStackTrace();}
			DMan16UtilsMain.getInstance().getPlayerPlayTimeLogger().justJoined(event.getPlayer());
		}
	}
}