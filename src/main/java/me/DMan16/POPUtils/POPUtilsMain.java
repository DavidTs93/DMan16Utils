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

public class POPUtilsMain extends JavaPlugin {
	private static POPUtilsMain instance = null;
	public static final String pluginName = "PrisonPOP";
	public static final String pluginNameColors = "&bPrison&d&lPOP";
	private static WorldGuardManager WorldGuardManager = null;
	private static PlaceholderManager PAPIManager = null;
	private static CitizensManager CitizensManager = null;
	private static ProtocolManager ProtocolManager = null;
	private static CancelPlayers CancelPlayers = null;
	private static PlayerVersionLogger PlayerVersionLogger;
	
	public void onLoad() {
		if (getServer().getPluginManager().getPlugin("WorldGuard") != null) WorldGuardManager = new WorldGuardManager();
	}
	
	public void onEnable() {
		instance = this;
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
		Utils.chatColorsLogPlugin(pluginNameColors + "&a disabled");
	}
	
	private void firstOfAll() {
		new EventCallers();
		CancelPlayers = new CancelPlayers();
		if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) PAPIManager = new PlaceholderManager();
		if (getServer().getPluginManager().getPlugin("Citizens") != null) CitizensManager = new CitizensManager();
		if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) ProtocolManager = ProtocolLibrary.getProtocolManager();
		if (getServer().getPluginManager().getPlugin("ViaVersion") != null) PlayerVersionLogger = new PlayerVersionLogger();
	}
	
	public static POPUtilsMain getInstance() {
		return instance;
	}
	
	public static Connection getConnection() {
		return POPUpdaterMain.getConnection();
	}
	
	public static WorldGuardManager getWorldGuardManager() {
		return WorldGuardManager;
	}
	
	public static PlaceholderManager getPAPIManager() {
		return PAPIManager;
	}
	
	public static CitizensManager getCitizensManager() {
		return CitizensManager;
	}
	
	public static ProtocolManager getProtocolManager() {
		return ProtocolManager;
	}
	
	public static CancelPlayers getCancelPlayers() {
		return CancelPlayers;
	}
	
	public static PlayerVersionLogger getPlayerVersionLogger() {
		return PlayerVersionLogger;
	}
}