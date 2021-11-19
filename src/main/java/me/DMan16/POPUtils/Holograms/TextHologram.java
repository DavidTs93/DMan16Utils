package me.DMan16.POPUtils.Holograms;

import me.DMan16.POPUtils.Utils.NMSUtils;
import me.DMan16.POPUtils.Utils.PacketUtils;
import net.kyori.adventure.text.Component;
import net.minecraft.network.chat.ChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TextHologram implements Hologram<TextHologram> {
	public static final double LINE_HEIGHT = 0.5;
	
	private int ID;
	private final ChatBaseComponent line;
	private Location location;
	private World world;
	private List<Packet<?>> packets;
	private PacketPlayOutEntityDestroy destroy;
	
	public TextHologram(@NotNull ChatBaseComponent line) {
		this.ID = 0;
		this.line = line;
		this.location = null;
		this.world = null;
		this.packets = null;
		this.destroy = null;
	}
	
	@Nullable
	public static TextHologram of(@NotNull Component component) {
		ChatBaseComponent line = NMSUtils.componentToIChatBaseComponent(component);
		return line == null ? null : new TextHologram(line);
	}
	
	public boolean spawn(@NotNull Location location) {
		if (isSpawned() || location.getWorld() == null || (this.location != null && this.location.equals(location = location.clone().subtract(0,1,0)))) return false;
		this.location = location;
		this.world = location.getWorld();
		PacketPlayOutSpawnEntityLiving create = PacketUtils.packetCreateArmorStand(location);
		ID = create.b();
		packets = new ArrayList<>();
		packets.add(create);
		packets.add(PacketUtils.packetUpdateArmorStand(ID,PacketUtils.createDataWatcherArmorStandOptions(true,true,false,true,true)));
		packets.add(PacketUtils.packetUpdateArmorStand(ID,PacketUtils.createDataWatcherArmorStandName(line)));
		destroy = PacketUtils.packetDestroyEntity(ID);
		HologramsManager.register(this);
		Bukkit.getOnlinePlayers().forEach(this::spawn);
		return true;
	}
	
	public void spawn(@NotNull Player player) {
		if (!isSpawned()) return;
		PacketUtils.sendPackets(player,packets);
	}
	
	public void despawn() {
		if (!isSpawned()) return;
		HologramsManager.unregister(this);
		location = null;
		packets = null;
		Bukkit.getOnlinePlayers().forEach(player -> PacketUtils.sendPackets(player,destroy));
	}
	
	public boolean spawnOrMove(@NotNull Location location) {
		if (location.getWorld() == null || (this.location != null && this.location.equals(location.clone().subtract(0,1,0)))) return false;
		despawn();
		spawn(location);
		return true;
	}
	
	public boolean isSpawned() {
		return ID > 0 && location != null;
	}
	
	@Nullable
	public Location getLocation() {
		return location == null ? null : location.clone();
	}
	
	
	public World getWorld() {
		return world;
	}
	
	public double maxSize() {
		return LINE_HEIGHT;
	}
	
	@NotNull
	public TextHologram copy() {
		return new TextHologram(line);
	}
	
	@Override
	public TextHologram clone() {
		try {
			TextHologram clone = (TextHologram) super.clone();
			clone.ID = 0;
			clone.location = null;
			clone.world = null;
			clone.packets = null;
			clone.destroy = null;
			return clone;
		} catch (Exception e) {}
		return copy();
	}
}