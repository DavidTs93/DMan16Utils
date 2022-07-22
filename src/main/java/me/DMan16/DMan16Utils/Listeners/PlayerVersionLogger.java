package me.DMan16.DMan16Utils.Listeners;

import com.viaversion.viaversion.api.Via;
import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Events.SuccessfulJoinEvent;
import me.DMan16.DMan16Utils.Interfaces.Listener;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerVersionLogger implements Listener,CommandExecutor {
	private static final String VERSIONS_PLAYERS_TABLE_NAME = "Versions_Players";
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
		VERSIONS.put(757,"1_18");
		VERSIONS.put(758,"1_18_2");
		VERSIONS.put(759,"1_19");
	}
	
	public PlayerVersionLogger() throws SQLException {
		createTable();
		register(DMan16UtilsMain.getInstance());
		Objects.requireNonNull(DMan16UtilsMain.getInstance().getCommand("versions")).setExecutor(this);
	}
	
	private void createTable() throws SQLException {
		try (Connection connection = Utils.getConnection(); Statement statement = connection.createStatement()) {
			DatabaseMetaData data = connection.getMetaData();
			statement.execute("CREATE TABLE IF NOT EXISTS " + VERSIONS_PLAYERS_TABLE_NAME + " (UUID VARCHAR(36) NOT NULL PRIMARY KEY," +
					VERSIONS.values().stream().map(version -> "v" + version.replace("_","v") + " BIT(1) NOT NULL DEFAULT 0").collect(Collectors.joining(",")) + ");");
//			List<Integer> versions = new ArrayList<>();
//			Utils.chatColorsLogPlugin("&6First: " + Via.getAPI().getFullSupportedVersions().iterator().next().getClass().getName());
//			for (Object obj : Via.getAPI().getSupportedVersions()) {
//				Integer version = Utils.getInteger(obj);
//				if (version != null) versions.add(version);
//			}
//			Utils.chatColorsLogPlugin("&6Versions: " + versions.stream().map(Object::toString).collect(Collectors.joining(",")));
//			Collections.reverse(versions);
			List<String> versions = VERSIONS.entrySet().stream().filter(entry -> entry.getKey() > 600).map(Map.Entry::getValue).sorted(Comparator.reverseOrder()).toList();
			for (String version : versions) {
				version = "v" + version.replace("_","v");
				if (!data.getColumns(null,null,VERSIONS_PLAYERS_TABLE_NAME,version).next())
					statement.execute("ALTER TABLE " + VERSIONS_PLAYERS_TABLE_NAME + " ADD " + version + " BIT(1) NOT NULL DEFAULT 0;");
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onJoin(SuccessfulJoinEvent event) {
		try (Connection connection = Utils.getConnection(); Statement statement = connection.createStatement()) {
			UUID ID = event.event.getPlayer().getUniqueId();
			String version = Objects.requireNonNull(VERSIONS.get(Via.getAPI().getPlayerVersion(ID)));
			statement.executeUpdate("INSERT IGNORE INTO " + VERSIONS_PLAYERS_TABLE_NAME + " (UUID) VALUES ('" + ID + "');");
			statement.executeUpdate("UPDATE " + VERSIONS_PLAYERS_TABLE_NAME + " SET " + "v" + version.replace("_","v") + "=1 WHERE UUID='" + ID + "';");
		} catch (Exception e) {
			event.disallow(e);
		}
	}
	
	@NotNull
	public HashMap<@NotNull String,@NotNull Integer> getUses() {
		LinkedHashMap<@NotNull String,@NotNull Integer> map = new LinkedHashMap<>();
		try (Connection connection = Utils.getConnection(); Statement statement = connection.createStatement()) {
			List<String> versions = VERSIONS.entrySet().stream().filter(entry -> entry.getKey() > 600).map(Map.Entry::getValue).sorted(Comparator.reverseOrder()).toList();
			for (String version : versions) {
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
	public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label,String[] args) {
		HashMap<String,Integer> uses = getUses();
		if (uses.isEmpty()) Utils.chatColors(sender,"&aNo versions recorded yet");
		else {
			String str = "title=Player versions;" + uses.entrySet().stream().map(entry -> entry.getKey().replace("_",".") + "=" + entry.getValue()).collect(Collectors.joining(";"));
			String url = "https://DavidTs93.github.io/pie_chart.html?v=" + Base64Coder.encodeString(str);
			ClickEvent click = ClickEvent.openUrl(url);
			sender.sendMessage(Utils.noItalic(Component.text("Versions graph: ",NamedTextColor.GREEN,TextDecoration.UNDERLINED).append(Component.text(url,NamedTextColor.BLUE).clickEvent(click))));
		}
		return true;
	}
}