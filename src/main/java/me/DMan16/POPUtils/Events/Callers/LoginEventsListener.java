package me.DMan16.POPUtils.Events.Callers;

import me.DMan16.POPUtils.Classes.Pair;
import me.DMan16.POPUtils.Events.SuccessfulJoinEvent;
import me.DMan16.POPUtils.Events.SuccessfulLoginEvent;
import me.DMan16.POPUtils.Interfaces.Listener;
import me.DMan16.POPUtils.POPUtilsMain;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Statement;
import java.util.Objects;

public class LoginEventsListener implements Listener {
	public LoginEventsListener() {
		register(POPUtilsMain.getInstance());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onSuccessfulLogin(me.DMan16.POPUpdater.AllowLoginEvent event) {
		SuccessfulLoginEvent loginEvent = new SuccessfulLoginEvent(event.event);
		if (!loginEvent.callEventAndDoTasksIfNotCancelled()) event.event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,Utils.thisOrThatOrNull(loginEvent.kickMessage(),Component.empty()));
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (Utils.isPlayerNPC(player)) return;
		SuccessfulJoinEvent joinEvent = new SuccessfulJoinEvent(event);
		try (Statement statement = Utils.getConnection().createStatement()) {
			Pair<String,String> skin = Utils.getSkin(Utils.getProfile(player));
			Objects.requireNonNull(skin.first());
			statement.executeUpdate("UPDATE PrisonPOP_Players SET SkinData='" + skin.first() + "', SkinSignature='" + skin.second() + "' WHERE UUID='" + player.getUniqueId() + "';");
		} catch (Exception e) {
			joinEvent.disallow(e);
		}
		if (!joinEvent.callEventAndDoTasksIfNotCancelled()) event.getPlayer().kick(Utils.KICK_MESSAGE);
		else {
			try (Statement statement = Utils.getConnection().createStatement()) {
				statement.executeUpdate("UPDATE PrisonPOP_Players SET LastJoin = CURRENT_TIMESTAMP;");
			} catch (Exception e) {e.printStackTrace();}
			POPUtilsMain.getInstance().getPlayerPlayTimeLogger().justJoined(event.getPlayer());
		}
	}
}