package me.DMan16.POPUtils;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.DMan16.POPUtils.Classes.ItemInitializerInfo;
import me.DMan16.POPUtils.Effects.CommandTestEffects;
import me.DMan16.POPUtils.Holograms.HologramsManager;
import me.DMan16.POPUtils.Items.*;
import me.DMan16.POPUtils.Events.Callers.EventCallers;
import me.DMan16.POPUtils.Listeners.CancelPlayers;
import me.DMan16.POPUtils.Listeners.MiscListeners;
import me.DMan16.POPUtils.Listeners.PlayerVersionLogger;
import me.DMan16.POPUtils.Classes.AdvancedRecipes;
import me.DMan16.POPUtils.Restrictions.RestrictionsCommandListener;
import me.DMan16.POPUtils.Utils.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.*;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
	private final AdvancedRecipes<AnvilInventory> advancedAnvilRecipes = new AdvancedRecipes<>();
	private final AdvancedRecipes<SmithingInventory> advancedSmithingRecipes = new AdvancedRecipes<>();
	
	public void onLoad() {
		INSTANCE = this;
		if (getServer().getPluginManager().getPlugin("WorldGuard") != null) WorldGuardManager = new WorldGuardManager();
	}
	
	public void onEnable() {
		Utils.chatColorsLogPlugin("&aConnected to MySQL database");
		try {
			if (!ItemUtils.registerItemable("item", new ItemableInfo<>(ItemableStack.class,ItemableStack::of,ItemableStack::of)))
				throw new Exception("Failed to register \"item\" Itemable!");
			if (!ItemUtils.registerItemable("command", new ItemableInfo<>(ItemableCommand.class,ItemableCommand::of,null)))
				throw new Exception("Failed to register \"command\" Itemable!");
			POPItems.start();
		} catch (Exception e) {
			Utils.chatColorsLogPlugin("&fPOPUtils&c problem! Error:");
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
			Bukkit.shutdown();
			return;
		}
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
		removeRecipes();
		new EventCallers();
		new RestrictionsCommandListener();
		new ItemCommandListener();
		new BookCommandListener();
		new EnchantCommandListener();
		new MiscListeners();
		CancelPlayers = new CancelPlayers();
		new HologramsManager();
		new CommandTestEffects();
		if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) PAPIManager = new PlaceholderManager();
		if (getServer().getPluginManager().getPlugin("Citizens") != null) CitizensManager = new CitizensManager();
		if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) ProtocolManager = ProtocolLibrary.getProtocolManager();
		if (getServer().getPluginManager().getPlugin("ViaVersion") != null) try {
			PlayerVersionLogger = new PlayerVersionLogger();
		} catch (Exception e) {e.printStackTrace();}
	}
	
	private void initiateMenuItems() {
		PluginsItems.add(new ItemInitializerInfo("sort",Material.PAPER,Component.translatable("menu.prisonpop.sort_by",NamedTextColor.GOLD)));
		PluginsItems.add(new ItemInitializerInfo("menu_close",Material.BARRIER,Component.translatable("spectatorMenu.close",NamedTextColor.RED)));
		PluginsItems.add(new ItemInitializerInfo("menu_next",Material.ARROW,Component.translatable("spectatorMenu.next_page",NamedTextColor.AQUA)));
		PluginsItems.add(new ItemInitializerInfo("menu_previous",Material.ARROW,Component.translatable("spectatorMenu.previous_page",NamedTextColor.YELLOW)));
		PluginsItems.add(new ItemInitializerInfo("menu_done",Material.GREEN_STAINED_GLASS_PANE,Component.translatable("gui.done",NamedTextColor.GREEN)));
		PluginsItems.add(new ItemInitializerInfo("menu_ok",Material.GREEN_STAINED_GLASS_PANE,Component.translatable("gui.ok",NamedTextColor.GREEN)));
		PluginsItems.add(new ItemInitializerInfo("menu_ok_no",Material.GRAY_STAINED_GLASS_PANE,Component.translatable("gui.ok",NamedTextColor.GREEN,TextDecoration.STRIKETHROUGH)));
		PluginsItems.add(new ItemInitializerInfo("menu_cancel",Material.RED_STAINED_GLASS_PANE,Component.translatable("gui.cancel",NamedTextColor.RED)));
		PluginsItems.add(new ItemInitializerInfo("menu_back",Material.ARROW,Component.translatable("gui.back",NamedTextColor.GOLD)));
		PluginsItems.add(new ItemInitializerInfo("menu_up",Material.BARRIER,Component.translatable("gui.up",NamedTextColor.DARK_GREEN)));
		PluginsItems.add(new ItemInitializerInfo("menu_down",Material.BARRIER,Component.translatable("gui.down",NamedTextColor.DARK_RED)));
		PluginsItems.add(new ItemInitializerInfo("menu_yes",Material.GREEN_STAINED_GLASS_PANE,Component.translatable("gui.yes",NamedTextColor.GREEN)));
		PluginsItems.add(new ItemInitializerInfo("menu_no",Material.RED_STAINED_GLASS_PANE,Component.translatable("gui.no",NamedTextColor.RED)));
		PluginsItems.add(new ItemInitializerInfo("menu_border",Material.BLACK_STAINED_GLASS_PANE,Component.empty()));
		PluginsItems.add(new ItemInitializerInfo("menu_inside",Material.LIGHT_GRAY_STAINED_GLASS_PANE,Component.empty()));
		PluginsItems.add(new ItemInitializerInfo("menu_inside_dark",Material.GRAY_STAINED_GLASS_PANE,Component.empty()));
	}
	
	private void removeRecipes() {
		Set<Recipe> removed = new HashSet<>();
		Iterator<Recipe> recipes = Bukkit.recipeIterator();
		Recipe recipe;
		while (recipes.hasNext()) if (((recipe = recipes.next()) instanceof ComplexRecipe) || (recipe instanceof ShapedRecipe) || (recipe instanceof ShapelessRecipe) ||
				(recipe instanceof SmithingRecipe)/* || (recipe instanceof StonecuttingRecipe)*/) {
			recipes.remove();
			removed.add(recipe);
		}/* else if (recipe instanceof FurnaceRecipe furnace) {
			Utils.chatColorsLogPlugin("&aFurnace recipe: &b" + furnace.getKey().getKey() + "&a, input: &b" + furnace.getInput().getType().name() + "&a, result: &b" +
					furnace.getResult().getType().name() + "&a, time: &b" + furnace.getCookingTime() + "&a, XP: &b" + furnace.getExperience() + "&a, group: &b" + furnace.getGroup());
		} else if (recipe instanceof BlastingRecipe blast) {
			Utils.chatColorsLogPlugin("&aBlasting recipe: &b" + blast.getKey().getKey() + "&a, input: &b" + blast.getInput().getType().name() + "&a, result: &b" +
					blast.getResult().getType().name() + "&a, time: &b" + blast.getCookingTime() + "&a, XP: &b" + blast.getExperience() + "&a, group: &b" + blast.getGroup());
		} else if (recipe instanceof SmokingRecipe smoke) {
			Utils.chatColorsLogPlugin("&aSmoking recipe: &b" + smoke.getKey().getKey() + "&a, input: &b" + smoke.getInput().getType().name() + "&a, result: &b" +
					smoke.getResult().getType().name() + "&a, time: &b" + smoke.getCookingTime() + "&a, XP: &b" + smoke.getExperience() + "&a, group: &b" + smoke.getGroup());
		} else if (recipe instanceof CampfireRecipe camp) {
			Utils.chatColorsLogPlugin("&aCampfire recipe: &b" + camp.getKey().getKey() + "&a, input: &b" + camp.getInput().getType().name() + "&a, result: &b" +
					camp.getResult().getType().name() + "&a, time: &b" + camp.getCookingTime() + "&a, XP: &b" + camp.getExperience() + "&a, group: &b" + camp.getGroup());
		}*/
		Utils.setRemovedRecipes(removed);
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
	
	@NotNull
	public AdvancedRecipes<AnvilInventory> getAdvancedAnvilRecipes() {
		return advancedAnvilRecipes;
	}
	
	@NotNull
	public AdvancedRecipes<SmithingInventory> getAdvancedSmithingRecipes() {
		return advancedSmithingRecipes;
	}
}