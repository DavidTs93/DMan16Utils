package me.DMan16.DMan16Utils.Holograms;

import me.DMan16.DMan16Utils.Interfaces.Copyable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface Hologram<V extends Hologram<V>> extends Copyable<V>,Cloneable {
	
	boolean spawn(@NotNull Location location);
	
	void spawn(@NotNull Player player);
	
	void despawn();
	
	boolean spawnOrMove(@NotNull Location location);
	
	boolean isSpawned();
	
	World getWorld();
	
	double maxSize();
	
	@NotNull V copy();
	
	V clone();
}