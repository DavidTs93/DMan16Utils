package me.DMan16.DMan16Utils;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.DMan16.DMan16Utils.Classes.AdvancedRecipe;
import me.DMan16.DMan16Utils.Classes.AdvancedRecipes;
import me.DMan16.DMan16Utils.Classes.CustomRecipes;
import me.DMan16.DMan16Utils.Classes.PluginItemInitializerInfo;
import me.DMan16.DMan16Utils.Classes.Trios.Trio;
import me.DMan16.DMan16Utils.Effects.CommandTestEffects;
import me.DMan16.DMan16Utils.Events.Callers.EventCallers;
import me.DMan16.DMan16Utils.Holograms.HologramsManager;
import me.DMan16.DMan16Utils.Interfaces.*;
import me.DMan16.DMan16Utils.Items.*;
import me.DMan16.DMan16Utils.Listeners.*;
import me.DMan16.DMan16Utils.Minigames.MiniGamesManager;
import me.DMan16.DMan16Utils.Restrictions.RestrictionsCommandListener;
import me.DMan16.DMan16Utils.Utils.CitizensManager;
import me.DMan16.DMan16Utils.Utils.PlaceholderManager;
import me.DMan16.DMan16Utils.Utils.Utils;
import me.DMan16.DMan16Utils.Utils.WorldGuardManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.util.Map;
import java.util.Objects;

public final class DMan16UtilsMain extends JavaPlugin {
	private static DMan16UtilsMain INSTANCE = null;
	public static final String PLUGIN_NAME = "DMan16Utils";
	public static final String PLUGIN_NAME_COLORS = "&#7033ADDMan16&bUtils";
	private WorldGuardManager WorldGuardManager = null;
	private PlaceholderManager PAPIManager = null;
	private CitizensManager CitizensManager = null;
	private ProtocolManager ProtocolManager = null;
	private CancelPlayers CancelPlayers = null;
	private PlayerVersionLogger PlayerVersionLogger = null;
	private PlayerPlayTimeLogger PlayerPlayTimeLogger = null;
	private MiniGamesManager MiniGamesManager = null;
	private final AdvancedRecipes<AnvilInventory> advancedAnvilRecipes = new AdvancedRecipes<>();
	private final AdvancedRecipes<SmithingInventory> advancedSmithingRecipes = new AdvancedRecipes<>();
	private final AdvancedRecipes<GrindstoneInventory> advancedGrindstoneRecipes = new AdvancedRecipes<>();
	private final CustomRecipes customRecipes = new CustomRecipes();
	
	public void onLoad() {
		if (INSTANCE != null) throw new IllegalArgumentException("DMan16Utils already exists!");
		INSTANCE = this;
		if (getServer().getPluginManager().getPlugin("WorldGuard") != null) WorldGuardManager = new WorldGuardManager();
	}
	
	public void onEnable() {
		try {
			try (Connection connection = Utils.getConnection(); Statement statement = connection.createStatement()) {
				DatabaseMetaData data = connection.getMetaData();
				statement.execute("CREATE TABLE IF NOT EXISTS " + Utils.PLAYERS_SKINS_TABLE + " (UUID VARCHAR(36) NOT NULL PRIMARY KEY)");
				if (!data.getColumns(null,null,Utils.PLAYERS_SKINS_TABLE,"UUID").next()) statement.execute("ALTER TABLE " + Utils.PLAYERS_SKINS_TABLE + " ADD UUID VARCHAR(36) NOT NULL PRIMARY KEY;");
				
			}
			Utils.chatColorsLogPlugin("&aConnected to MySQL database");
			if (!ItemUtils.registerItemable("item",new ItemableInfo<>(ItemableStack.class,ItemableStack::of,ItemableStack::of))) throw new Exception("Failed to register \"item\" Itemable!");
			if (!ItemUtils.registerItemable("command",new ItemableInfo<>(ItemableCommand.class,ItemableCommand::of,null))) throw new Exception("Failed to register \"command\" Itemable!");
			CustomItems.init();
		} catch (Exception e) {
			Utils.chatColorsLogPlugin("&fDMan16Utils&c problem! Error:");
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
			Bukkit.shutdown();
			return;
		}
		firstOfAll();
		Utils.chatColorsLogPlugin("&aLoaded,running on version: &f" + Utils.getVersion() + "&a,Java version: &f" + Utils.javaVersion());
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
//		removeRecipes();
		new EventCallers();
		new RestrictionsCommandListener();
		new ItemCommandListener();
		new BookCommandListener();
		new EnchantCommandListener();
		new DamageCommandListener();
		new RepairCommandListener();
		new FixCommandListener();
		new UUIDCommandListener();
		new XPHolderCommandListener();
		new SocketsCommandListener();
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
		MiniGamesManager = new MiniGamesManager(INSTANCE);
		PlayerPlayTimeLogger = new PlayerPlayTimeLogger();
		Utils.advancedAnvilRecipes().register("item_repair",new AdvancedRecipe<>((first,second,originalResult) -> {
			if (!(first instanceof Repairable repairable) || !(second instanceof ItemableAmountable<?> repair)) return null;
			ItemableAmountable<?> repairItem = repairable.repairItem();
			if (repairItem == null || (!repairItem.canPassAsThis(repair) && !second.canPassAsThis(repair))) return null;
			int amount = repair.amount() / repairItem.amount();
			int fix = repairable.fix(amount);
			if (fix >= amount) return null;
			amount = repair.amount() - ((amount - fix) * repairItem.amount());
			return Trio.of(null,amount <= 0 ? null : repair.copy(amount),first);
		}));
		Utils.advancedAnvilRecipes().register("enchantable_enchant_book",new AdvancedRecipe<>((first,second,originalResult) -> {
			Map<Enchantment,Integer> enchants;
			if (!(first instanceof Enchantable<?> enchantable) || enchantable.shouldBreak() || !(second instanceof EnchantmentsHolder holder) || second.material() != Material.ENCHANTED_BOOK || (enchants = holder.getEnchantments()).isEmpty()) return null;
			Enchantable<?> result = enchantable.copy();
			if (enchants.entrySet().stream().noneMatch(entry -> result.addEnchantment(entry.getKey(),entry.getValue()))) return null;
			if ((result instanceof EnchantmentXPHolder resultXP) && (second instanceof EnchantmentXPHolder secondXP)) enchants.keySet().forEach(enchantment -> Utils.runNotNull(secondXP.getEnchantmentXP(enchantment),xp -> resultXP.addEnchantmentXP(enchantment,xp)));
			return Trio.of(null,null,result);
		}));
		Utils.advancedGrindstoneRecipes().register("enchantable_grind",new AdvancedRecipe<>((first,second,originalResult) -> {
			if (first != null && second != null) return null;
			return (Utils.thisOrThatOrNull(first,second) instanceof Enchantable<?> enchantable) && enchantable.enchantmentsAmount() > 0 ? Trio.of(null,null,enchantable.clearEnchantments()) : null;
		}));
		Utils.advancedAnvilRecipes().register("item_enchant",new AdvancedRecipe<>((first,second,originalResult) ->
				!Objects.equals(Utils.applyNotNull(first,Itemable::material),Material.ENCHANTED_BOOK) && Objects.equals(Utils.applyNotNull(second,Itemable::material),Material.ENCHANTED_BOOK) ? Trio.of(null,null,originalResult) : null));
	}
	
	private void initiateMenuItems() {
		PluginsItems.add("sort",new PluginItemInitializerInfo(Material.PAPER,Component.translatable("menu.sort_by",NamedTextColor.GOLD)));
		PluginsItems.add("menu_close",new PluginItemInitializerInfo(Material.BARRIER,Component.translatable("spectatorMenu.close",NamedTextColor.RED)));
		PluginsItems.add("menu_next",new PluginItemInitializerInfo(Material.ARROW,Component.translatable("spectatorMenu.next_page",NamedTextColor.AQUA)));
		PluginsItems.add("menu_previous",new PluginItemInitializerInfo(Material.ARROW,Component.translatable("spectatorMenu.previous_page",NamedTextColor.YELLOW)));
		PluginsItems.add("menu_done",new PluginItemInitializerInfo(Material.GREEN_STAINED_GLASS_PANE,Component.translatable("gui.done",NamedTextColor.GREEN)));
		PluginsItems.add("menu_ok",new PluginItemInitializerInfo(Material.GREEN_STAINED_GLASS_PANE,Component.translatable("gui.ok",NamedTextColor.GREEN)));
		PluginsItems.add("menu_ok_no",new PluginItemInitializerInfo(Material.GRAY_STAINED_GLASS_PANE,Component.translatable("gui.ok",NamedTextColor.GREEN,TextDecoration.STRIKETHROUGH)));
		PluginsItems.add("menu_cancel",new PluginItemInitializerInfo(Material.RED_STAINED_GLASS_PANE,Component.translatable("gui.cancel",NamedTextColor.RED)));
		PluginsItems.add("menu_back",new PluginItemInitializerInfo(Material.ARROW,Component.translatable("gui.back",NamedTextColor.GOLD)));
		PluginsItems.add("menu_up",new PluginItemInitializerInfo(Material.BARRIER,Component.translatable("gui.up",NamedTextColor.DARK_GREEN)));
		PluginsItems.add("menu_down",new PluginItemInitializerInfo(Material.BARRIER,Component.translatable("gui.down",NamedTextColor.DARK_RED)));
		PluginsItems.add("menu_yes",new PluginItemInitializerInfo(Material.GREEN_STAINED_GLASS_PANE,Component.translatable("gui.yes",NamedTextColor.GREEN)));
		PluginsItems.add("menu_no",new PluginItemInitializerInfo(Material.RED_STAINED_GLASS_PANE,Component.translatable("gui.no",NamedTextColor.RED)));
		PluginsItems.add("menu_border",new PluginItemInitializerInfo(Material.BLACK_STAINED_GLASS_PANE,Component.empty()));
		PluginsItems.add("menu_inside",new PluginItemInitializerInfo(Material.LIGHT_GRAY_STAINED_GLASS_PANE,Component.empty()));
		PluginsItems.add("menu_inside_dark",new PluginItemInitializerInfo(Material.GRAY_STAINED_GLASS_PANE,Component.empty()));
	}
	
	public static DMan16UtilsMain getInstance() {
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
	
	public PlayerPlayTimeLogger getPlayerPlayTimeLogger() {
		return PlayerPlayTimeLogger;
	}
	
	public MiniGamesManager getMiniGamesManager() {
		return MiniGamesManager;
	}
	
	@NotNull
	public AdvancedRecipes<AnvilInventory> advancedAnvilRecipes() {
		return advancedAnvilRecipes;
	}
	
	@NotNull
	public AdvancedRecipes<SmithingInventory> advancedSmithingRecipes() {
		return advancedSmithingRecipes;
	}
	
	@NotNull
	public AdvancedRecipes<GrindstoneInventory> advancedGrindstoneRecipes() {
		return advancedGrindstoneRecipes;
	}
	
	@NotNull
	public CustomRecipes customRecipes() {
		return customRecipes;
	}
}