package me.DMan16.POPUtils.Listeners;

import com.viaversion.viaversion.api.Via;
import me.DMan16.POPUtils.Classes.Listener;
import me.DMan16.POPUtils.POPUtils;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerVersionLogger implements Listener,CommandExecutor {
	private static final String VERSIONS_PLAYERS_TABLE_NAME = "PrisonPOP_Versions_Players";
	private static final LinkedHashMap<@NotNull Integer,@NotNull String> VERSIONS = new LinkedHashMap<>();
	
	static {
		VERSIONS.put(4,"1_7_5");
		VERSIONS.put(5,"1_7_10");
		VERSIONS.put(47,"1_8");
		VERSIONS.put(107,"1_9");
		VERSIONS.put(108,"1_9_1");
		VERSIONS.put(109,"1_9_2");
		VERSIONS.put(110,"1_9_4");
		VERSIONS.put(210,"1_10");
		VERSIONS.put(315,"1_11");
		VERSIONS.put(316,"1_11_2");
		VERSIONS.put(335,"1_12");
		VERSIONS.put(338,"1_12_1");
		VERSIONS.put(340,"1_12_2");
		VERSIONS.put(393,"1_13");
		VERSIONS.put(401,"1_13_1");
		VERSIONS.put(404,"1_13_2");
		VERSIONS.put(477,"1_14");
		VERSIONS.put(480,"1_14_1");
		VERSIONS.put(485,"1_14_2");
		VERSIONS.put(490,"1_14_3");
		VERSIONS.put(498,"1_14_4");
		VERSIONS.put(573,"1_15");
		VERSIONS.put(575,"1_15_1");
		VERSIONS.put(578,"1_15_2");
		VERSIONS.put(735,"1_16");
		VERSIONS.put(736,"1_16_1");
		VERSIONS.put(751,"1_16_2");
		VERSIONS.put(753,"1_16_3");
		VERSIONS.put(754,"1_16_5");
		VERSIONS.put(755,"1_17");
		VERSIONS.put(756,"1_17_1");
	}
	
	public PlayerVersionLogger() {
		try {
			createTable();
			register(POPUtils.getInstance());
			Objects.requireNonNull(POPUtils.getInstance().getCommand("versions")).setExecutor(this);
		} catch (Exception e) {e.printStackTrace();}
	}
	
	private void createTable() throws SQLException {
		try (Statement statement = POPUtils.getConnection().createStatement()) {
			DatabaseMetaData data = POPUtils.getConnection().getMetaData();
			statement.execute("CREATE TABLE IF NOT EXISTS " + VERSIONS_PLAYERS_TABLE_NAME + " (UUID VARCHAR(36) NOT NULL UNIQUE," +
					VERSIONS.values().stream().map(version -> "v" + version.replace("_","v") + " BIT(1) NOT NULL DEFAULT 0").collect(Collectors.joining(",")) + ");");
			List<String> add = new ArrayList<>();
			for (String version : VERSIONS.values()) if (!data.getColumns(null,null,VERSIONS_PLAYERS_TABLE_NAME,"v" + version.replace("_","v")).next()) add.add(version);
			if (!add.isEmpty()) statement.execute("ALTER TABLE " + VERSIONS_PLAYERS_TABLE_NAME + " " +
					add.stream().map(version -> "v" + version.replace("_","v") + " BIT(1) NOT NULL DEFAULT 0").collect(Collectors.joining(",")) + ";");
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		if (event.getPlayer().isOnline()) try (Statement statement = POPUtils.getConnection().createStatement()) {
			UUID ID = event.getPlayer().getUniqueId();
			String version = Objects.requireNonNull(VERSIONS.get(Via.getAPI().getPlayerVersion(ID)));
			statement.executeUpdate("INSERT IGNORE INTO " + VERSIONS_PLAYERS_TABLE_NAME + " (UUID) VALUES ('" + ID + "');");
			statement.executeUpdate("UPDATE " + VERSIONS_PLAYERS_TABLE_NAME + " SET " + "v" + version.replace("_","v") + "=1 WHERE UUID='" + ID + "';");
		} catch (Exception e) {e.printStackTrace();}
	}
	
	@NotNull
	public HashMap<@NotNull String,@NotNull Integer> getUses() {
		LinkedHashMap<@NotNull String,@NotNull Integer> map = new LinkedHashMap<>();
		try (Statement statement = POPUtils.getConnection().createStatement()) {
			for (String version : VERSIONS.values()) {
				try (ResultSet results = statement.executeQuery("SELECT SUM(" + "v" + version.replace("_","v") + ") FROM " + VERSIONS_PLAYERS_TABLE_NAME)) {
					results.next();
					int amount = results.getInt("SUM(" + "v" + version.replace("_","v") + ")");
					if (amount > 0) map.put(version,amount);
				} catch (Exception e) {e.printStackTrace();}
			}
		} catch (Exception e) {e.printStackTrace();}
		return map;
	}
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		HashMap<String,Integer> uses = getUses();
		if (uses.isEmpty()) Utils.chatColors(sender,"&aNo versions recorded yet");
		else {
			String str = "title=Player versions;" + uses.entrySet().stream().map(entry -> entry.getKey().replace("_",".") + "=" + entry.getValue()).collect(Collectors.joining(";"));
			String url = "https://DavidTs93.github.io/pie_chart.html?v=" + Base64Coder.encodeString(str);
			ClickEvent click = ClickEvent.openUrl(url);
			Component msg;
			if (sender instanceof Player) msg = Component.text("Versions graph",NamedTextColor.GREEN).clickEvent(click);
			else msg = Component.text("Versions graph: ",NamedTextColor.GREEN).append(Component.text(url,NamedTextColor.BLUE).decoration(TextDecoration.UNDERLINED,true).clickEvent(click));
			sender.sendMessage(msg.decoration(TextDecoration.ITALIC,false));
		}
		return true;
	}
}