package me.DMan16.POPUtils.Classes;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class PlaceHolder extends PlaceholderExpansion {
	private final JavaPlugin plugin;
	
	protected PlaceHolder(@NotNull JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
	public boolean persist() {
		return true;
	}
	
	public boolean canRegister() {
		return true;
	}
	
	public String getAuthor() {
		return plugin.getDescription().getAuthors().toString();
	}
	
	public String getIdentifier() {
		return plugin.getName();
	}
	
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}
	
	public String onPlaceholderRequest(Player player, String identifier) {
		return onRequest(player,identifier);
	}
	
	public abstract String onRequest(OfflinePlayer player, String identifier);
}