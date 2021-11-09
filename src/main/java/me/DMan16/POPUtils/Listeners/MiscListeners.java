package me.DMan16.POPUtils.Listeners;

import me.DMan16.POPUtils.Classes.Listener;
import me.DMan16.POPUtils.Classes.Pair;
import me.DMan16.POPUtils.Events.SuccessfulJoinEvent;
import me.DMan16.POPUtils.Events.SuccessfulLoginEvent;
import me.DMan16.POPUtils.POPUtilsMain;
import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Statement;
import java.util.Objects;

public class MiscListeners implements Listener {
	private static MiscListeners INSTANCE = null;
	
	public static void start() {
		if (INSTANCE != null) return;
		INSTANCE = new MiscListeners();
		INSTANCE.register(POPUtilsMain.getInstance());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onSuccessfulLogin(me.DMan16.POPUpdater.AllowLoginEvent event) {
		new SuccessfulLoginEvent(event.event).callEventAndDoTasksIfNotCancelled();
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		SuccessfulJoinEvent joinEvent = new SuccessfulJoinEvent(event);
		try (Statement statement = Utils.getConnection().createStatement()) {
			Pair<String,String> skin = Utils.getSkin(Utils.getProfile(player));
			statement.executeUpdate("UPDATE PrisonPOP_Players SET SkinData='" + Objects.requireNonNull(skin.first()) + "', SkinSignature='" + skin.second() + "' WHERE UUID='" +
					player.getUniqueId() + "';");
		} catch (Exception e) {
			joinEvent.disallow(e);
		}
		joinEvent.callEventAndDoTasksIfNotCancelled();
	}
}