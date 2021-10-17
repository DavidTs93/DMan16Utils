package me.DMan16.POPUtils;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.DMan16.POPUtils.Classes.ItemInitializerInfo;
import me.DMan16.POPUtils.Classes.PluginsItems;
import me.DMan16.POPUtils.Events.Callers.EventCallers;
import me.DMan16.POPUtils.Listeners.CancelPlayers;
import me.DMan16.POPUtils.Listeners.PlayerVersionLogger;
import me.DMan16.POPUtils.Restrictions.RestrictionsCommandListener;
import me.DMan16.POPUtils.Utils.CitizensManager;
import me.DMan16.POPUtils.Utils.PlaceholderManager;
import me.DMan16.POPUtils.Utils.Utils;
import me.DMan16.POPUtils.Utils.WorldGuardManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public final class POPUtilsMain extends JavaPlugin {
	private static POPUtilsMain INSTANCE = null;
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
		initiateMenuItems();
		new EventCallers();
		new RestrictionsCommandListener();
		CancelPlayers = new CancelPlayers();
		if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) PAPIManager = new PlaceholderManager();
		if (getServer().getPluginManager().getPlugin("Citizens") != null) CitizensManager = new CitizensManager();
		if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) ProtocolManager = ProtocolLibrary.getProtocolManager();
		if (getServer().getPluginManager().getPlugin("ViaVersion") != null) PlayerVersionLogger = new PlayerVersionLogger();
	}
	
	private void initiateMenuItems() {
		PluginsItems.add(new ItemInitializerInfo("sort",Material.PAPER,Component.translatable("menu.prisonpop.sort_by",NamedTextColor.GOLD)));
		PluginsItems.add(new ItemInitializerInfo("menu_close",Material.BARRIER,Component.translatable("spectatorMenu.close",NamedTextColor.RED)));
		PluginsItems.add(new ItemInitializerInfo("menu_next",Material.ARROW,Component.translatable("spectatorMenu.next_page",NamedTextColor.AQUA)));
		PluginsItems.add(new ItemInitializerInfo("menu_previous",Material.ARROW,Component.translatable("spectatorMenu.previous_page",NamedTextColor.YELLOW)));
		PluginsItems.add(new ItemInitializerInfo("menu_done",Material.GREEN_STAINED_GLASS_PANE,Component.translatable("gui.done",NamedTextColor.GREEN)));
		PluginsItems.add(new ItemInitializerInfo("menu_ok",Material.GREEN_STAINED_GLASS_PANE,Component.translatable("gui.ok",NamedTextColor.GREEN)));
		PluginsItems.add(new ItemInitializerInfo("menu_ok_no",Material.GRAY_STAINED_GLASS_PANE,Component.translatable("gui.ok",NamedTextColor.GREEN).decoration(TextDecoration.STRIKETHROUGH,true)));
		PluginsItems.add(new ItemInitializerInfo("menu_cancel",Material.RED_STAINED_GLASS_PANE,Component.translatable("gui.cancel",NamedTextColor.RED)));
		PluginsItems.add(new ItemInitializerInfo("menu_back",Material.ARROW,Component.translatable("gui.back",NamedTextColor.GOLD)));
		PluginsItems.add(new ItemInitializerInfo("menu_up",Material.BARRIER,Component.translatable("gui.up",NamedTextColor.DARK_GREEN)));
		PluginsItems.add(new ItemInitializerInfo("menu_down",Material.BARRIER,Component.translatable("gui.down",NamedTextColor.DARK_RED)));
		PluginsItems.add(new ItemInitializerInfo("menu_yes",Material.GREEN_STAINED_GLASS_PANE,Component.translatable("gui.yes",NamedTextColor.GREEN)));
		PluginsItems.add(new ItemInitializerInfo("menu_no",Material.RED_STAINED_GLASS_PANE,Component.translatable("gui.no",NamedTextColor.RED)));
		PluginsItems.add(new ItemInitializerInfo("menu_border",Material.GRAY_STAINED_GLASS_PANE,Component.empty()));
		PluginsItems.add(new ItemInitializerInfo("menu_inside",Material.LIGHT_GRAY_STAINED_GLASS_PANE,Component.empty()));
	}
	
	public static POPUtilsMain getInstance() {
		return INSTANCE;
	}
	
	public WorldGuardManager getWorldGuardManager() {
		return WorldGuardManager;
	}
	
	public PlaceholderManager getPAPIManager() {
		return PAPIManager;
	}
	
	public CitizensManager getCitizensManager() {
		return CitizensManager;
	}
	
	public ProtocolManager getProtocolManager() {
		return ProtocolManager;
	}
	
	public CancelPlayers getCancelPlayers() {
		return CancelPlayers;
	}
	
	public PlayerVersionLogger getPlayerVersionLogger() {
		return PlayerVersionLogger;
	}
}