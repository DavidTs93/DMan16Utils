package me.DMan16.POPUtils;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.DMan16.POPUpdater.POPUpdaterMain;
import me.DMan16.POPUtils.Events.Callers.EventCallers;
import me.DMan16.POPUtils.Listeners.CancelPlayers;
import me.DMan16.POPUtils.Listeners.PlayerVersionLogger;
import me.DMan16.POPUtils.Utils.CitizensManager;
import me.DMan16.POPUtils.Utils.PlaceholderManager;
import me.DMan16.POPUtils.Utils.Utils;
import me.DMan16.POPUtils.Utils.WorldGuardManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;

public final class POPUtils extends JavaPlugin {
	private static POPUtils INSTANCE = null;
	public static final String PLUGIN_NAME = "PrisonPOP";
	public static final String PLUGIN_NAME_COLORS = "&bPrison&d&lPOP";
	private WorldGuardManager WorldGuardManager = null;
	private PlaceholderManager PAPIManager = null;
	private CitizensManager CitizensManager = null;
	private ProtocolManager ProtocolManager = null;
	private CancelPlayers CancelPlayers = null;
	private PlayerVersionLogger PlayerVersionLogger = null;
	
	public void onLoad() {
		INSTANCE = this;
		if (getServer().getPluginManager().getPlugin("WorldGuard") != null) WorldGuardManager = new WorldGuardManager();
	}
	
	public void onEnable() {
		Utils.chatColorsLogPlugin("&aConnected to MySQL database");
		firstOfAll();
		Utils.chatColorsLogPlugin("&aLoaded, running on version: &f" + Utils.getVersion() + "&a, Java version: &f" + Utils.javaVersion());
		if (WorldGuardManager != null) Utils.chatColorsLogPlugin("&aHooked to &fWorldGuard");
		if (PAPIManager != null) Utils.chatColorsLogPlugin("&aHooked to &fPlaceholderAPI");
		if (CitizensManager != null) Utils.chatColorsLogPlugin("&aHooked to &fCitizens");
		if (ProtocolManager != null) Utils.chatColorsLogPlugin("&aHooked to &fProtocolLib");
	}

	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		Utils.chatColorsLogPlugin(PLUGIN_NAME_COLORS + "&a disabled");
	}
	
	private void firstOfAll() {
		new EventCallers();
		CancelPlayers = new CancelPlayers();
		if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) PAPIManager = new PlaceholderManager();
		if (getServer().getPluginManager().getPlugin("Citizens") != null) CitizensManager = new CitizensManager();
		if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) ProtocolManager = ProtocolLibrary.getProtocolManager();
		if (getServer().getPluginManager().getPlugin("ViaVersion") != null) PlayerVersionLogger = new PlayerVersionLogger();
	}
	
	public static POPUtils getInstance() {
		return INSTANCE;
	}
	
	public static Connection getConnection() {
		return POPUpdaterMain.getConnection();
	}
	
	public static WorldGuardManager getWorldGuardManager() {
		return INSTANCE.WorldGuardManager;
	}
	
	public static PlaceholderManager getPAPIManager() {
		return INSTANCE.PAPIManager;
	}
	
	public static CitizensManager getCitizensManager() {
		return INSTANCE.CitizensManager;
	}
	
	public static ProtocolManager getProtocolManager() {
		return INSTANCE.ProtocolManager;
	}
	
	public static CancelPlayers getCancelPlayers() {
		return INSTANCE.CancelPlayers;
	}
	
	public static PlayerVersionLogger getPlayerVersionLogger() {
		return INSTANCE.PlayerVersionLogger;
	}
}