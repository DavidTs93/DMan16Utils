package me.DMan16.POPUtils.Classes;

import me.DMan16.POPUtils.POPUtilsMain;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class PluginItems {
	private final @Unmodifiable List<@NotNull ItemInitializerInfo> infos;
	private final HashMap<@NotNull String,@Nullable ItemStack> map;
	private BukkitTask task;
	
	public PluginItems(@NotNull ItemInitializerInfo ... infos) {
		this(Arrays.asList(infos));
	}
	
	public PluginItems(@NotNull List<@NotNull ItemInitializerInfo> infos) {
		this.infos = Collections.unmodifiableList(infos);
		map = new HashMap<>();
		infos.forEach(info -> map.put(info.ID(),null));
		task = null;
		initializeItemDatabase(infos);
	}
	
	public PluginItems reload() {
		if (task != null) task.cancel();
		getItemsDatabase();
		task = new BukkitRunnable() {
			public void run() {
				reload();
			}
		}.runTaskLater(POPUtilsMain.getInstance(),10 * 20);
		return this;
	}
	
	@NotNull
	public ItemStack getItem(@NotNull String name) {
		ItemStack item = map.get(name.toLowerCase());
		if (item == null) throw new IllegalArgumentException("Item \"" + name + "\" not found!");
		return item.clone();
	}
	
	private static void initializeItemDatabase(@NotNull List<@NotNull ItemInitializerInfo> infos) {
		try (Statement statement = Utils.getConnection().createStatement()) {
			List<String> values = new ArrayList<>();
			for (ItemInitializerInfo info : infos) values.add("('" + info.ID() + "'," + (Utils.isNull(info.material()) ? null : "'" + info.material().name() + "'") + "," +
					Math.max(info.model(),0) + "," + (info.skin() == null ? null : "'" + info.skin() + "'") + ")");
			statement.executeUpdate("INSERT IGNORE INTO PrisonPOP_Items (ID,Material,Model,Skin) VALUES " + String.join(",",values) + ";");
		} catch (Exception e) {e.printStackTrace();}
	}
	
	private void getItemsDatabase() {
		ItemStack item;
		try (Statement statement = Utils.getConnection().createStatement()) {
			for (ItemInitializerInfo info : infos) {
				item = getItemDatabase(statement,info.ID(),info.name(),info.material());
				if (info.alterItem() != null && !Utils.isNull(item)) item = info.alterItem().apply(item);
				map.put(info.ID(),item);
			}
		} catch (Exception e) {}
	}
	
	@Nullable
	@Contract("_,_,_,!null -> !null")
	private static ItemStack getItemDatabase(@NotNull Statement statement, @NotNull String nameID, @Nullable Component name, @Nullable Material defaultMaterial) {
		try (ResultSet result = statement.executeQuery("SELECT * FROM PrisonPOP_Items WHERE ID='" + nameID + "'")) {
			result.next();
			ItemStack item;
			Material material = Material.getMaterial(result.getString("Material"));
			int model = result.getInt("Model");
			String skin = result.getString("Skin");
			if (skin != null) {
				item = Utils.makeItem(Material.PLAYER_HEAD,name, ItemFlag.values());
				SkullMeta meta = (SkullMeta) item.getItemMeta();
				if (Utils.setSkin(meta,skin,null)) {
					item.setItemMeta(meta);
					return item;
				}
			}
			item = Utils.makeItem(Objects.requireNonNull(Utils.thisOrThatOrNull(material,defaultMaterial)),name,ItemFlag.values());
			if (model > 0) {
				ItemMeta meta = item.getItemMeta();
				meta.setCustomModelData(model);
				item.setItemMeta(meta);
			}
			return item;
		} catch (Exception e) {}
		return Utils.isNull(defaultMaterial) ? null : Utils.makeItem(defaultMaterial,name,ItemFlag.values());
	}
}