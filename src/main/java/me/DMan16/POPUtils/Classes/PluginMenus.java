package me.DMan16.POPUtils.Classes;

import me.DMan16.POPUtils.Interfaces.InterfacesUtils;
import me.DMan16.POPUtils.POPUtilsMain;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

public class PluginMenus {
	private final @Unmodifiable HashMap<@NotNull String,@Nullable Component> infos;
	private final HashMap<@NotNull String,@NotNull Trio<@Nullable Component,@Nullable Component,@Nullable Boolean>> map;
	private BukkitTask task;
	
	@SafeVarargs
	public PluginMenus(@NotNull Trio<@NotNull String,@Nullable Component,@Nullable Boolean> ... infos) {
		this(Arrays.asList(infos));
	}
	
	public PluginMenus(@NotNull List<@NotNull Trio<@NotNull String,@Nullable Component,@Nullable Boolean>> infos) {
		this.infos = new HashMap<>();
		infos.forEach(info -> this.infos.put(info.first(),changeName(info.second())));
		map = new HashMap<>();
		infos.forEach(info -> map.put(info.first(),Trio.of(null,null,null)));
		task = null;
		initializeMenuDatabase(infos);
	}
	
	@Nullable
	private static Component changeName(@Nullable Component name) {
		if (name != null) {
			name = name.colorIfAbsent(NamedTextColor.DARK_GREEN);
			if (name.decoration(TextDecoration.ITALIC) == TextDecoration.State.NOT_SET) name = name.decoration(TextDecoration.ITALIC,false);
		}
		return name;
	}
	
	public PluginMenus reload() {
		if (task != null) task.cancel();
		getMenusDatabase();
		task = new BukkitRunnable() {
			public void run() {
				reload();
			}
		}.runTaskLater(POPUtilsMain.getInstance(),10 * 60 * 20);
		return this;
	}
	
	@Nullable
	public Component getName(@NotNull String name) {
		return getName(name,null,null);
	}
	
	@Nullable
	public Component getName(@NotNull String name, @Nullable Component postPrefix, @Nullable Component preSuffix) {
		Trio<Component,Component,Boolean> info = map.get(name);
		if (info == null) throw new IllegalArgumentException("Menu \"" + name + "\" not found!");
		Component menuName;
		Component base = infos.get(name);
		Component prefix = info.first();
		if (postPrefix != null) prefix = prefix == null ? postPrefix : prefix.append(postPrefix);
		Component suffix = info.second();
		if (preSuffix != null) suffix = suffix == null ? preSuffix : preSuffix.append(suffix);
		if (base == null) menuName = prefix == null ? suffix : (suffix == null ? prefix : prefix.append(suffix));
		else {
			menuName = prefix == null ? base : prefix.append(base);
			if (suffix != null) menuName = menuName.append(suffix);
		}
		return changeName(menuName);
	}
	
	@Nullable
	public Boolean getBorder(@NotNull String name) {
		Trio<Component,Component,Boolean> info = map.get(name);
		if (info == null) throw new IllegalArgumentException("Menu \"" + name + "\" not found!");
		return info.third();
	}
	
	private static void initializeMenuDatabase(@NotNull List<@NotNull Trio<@NotNull String,@Nullable Component,@Nullable Boolean>> infos) {
		try (Statement statement = Utils.getConnection().createStatement()) {
			List<String> values = new ArrayList<>();
			statement.executeUpdate("INSERT IGNORE INTO PrisonPOP_Menus (ID,Border) VALUES " + infos.stream().map(info ->
					"('" + info.first() + "'," + (info.third() == null ? null : (Boolean.TRUE.equals(info.third()) ? 1 : 0)) + ")").collect(Collectors.joining(",")) + ";");
		} catch (Exception e) {e.printStackTrace();}
	}
	
	private void getMenusDatabase() {
		try (Statement statement = Utils.getConnection().createStatement()) {
			infos.forEach((name,base) -> map.put(name,getMenuDatabase(statement,name)));
		} catch (Exception e) {}
	}
	
	@NotNull
	private static Trio<@Nullable Component,@Nullable Component,@Nullable Boolean> getMenuDatabase(@NotNull Statement statement, @NotNull String name) {
		Component prefix = null;
		Component suffix = null;
		Boolean border = null;
		if (!name.isEmpty()) try (ResultSet result = statement.executeQuery("SELECT * FROM PrisonPOP_Menus WHERE ID='" + name + "'")) {
			result.next();
			String text = result.getString("Prefix");
			if (text != null && !text.isEmpty()) prefix = (text.toLowerCase().startsWith(InterfacesUtils.TRANSLATABLE) ?
					Component.translatable(text.substring(InterfacesUtils.TRANSLATABLE.length())) : Component.text(text)).decoration(TextDecoration.ITALIC,false);
			text = result.getString("Suffix");
			if (text != null && !text.isEmpty()) suffix = (text.toLowerCase().startsWith(InterfacesUtils.TRANSLATABLE) ?
					Component.translatable(text.substring(InterfacesUtils.TRANSLATABLE.length())) : Component.text(Utils.chatColors(text))).decoration(TextDecoration.ITALIC,false);
			border = Utils.getBoolean(result,"Border");
		} catch (Exception e) {}
		return Trio.of(prefix,suffix,border);
	}
}