package me.DMan16.DMan16Utils.Utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlaceholderManager {
	@NotNull
	public String translate(@NotNull Player player,@NotNull String str) {
		return PlaceholderAPI.setPlaceholders(player,str);
	}
	
	@NotNull
	public List<String> translate(@NotNull Player player,@NotNull List<String> str) {
		return PlaceholderAPI.setPlaceholders(player,str);
	}
}