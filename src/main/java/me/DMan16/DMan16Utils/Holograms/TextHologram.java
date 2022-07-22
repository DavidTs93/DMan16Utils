package me.DMan16.DMan16Utils.Holograms;

import me.DMan16.DMan16Utils.NMSWrappers.ComponentWrapper;
import me.DMan16.DMan16Utils.NMSWrappers.PacketWrapper;
import me.DMan16.DMan16Utils.Utils.NMSUtils;
import me.DMan16.DMan16Utils.Utils.PacketUtils;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundAddMobPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TextHologram implements Hologram<TextHologram> {
	public static final double LINE_HEIGHT = 0.25;
	
	protected int ID;
	protected final ComponentWrapper line;
	protected Location location;
	protected World world;
	protected PacketWrapper.Safe create;
	protected PacketWrapper.Safe edit;
	protected PacketWrapper.Safe destroy;
	
	public TextHologram(@NotNull ComponentWrapper line) {
		this.ID = 0;
		this.line = line;
		this.location = null;
		this.world = null;
		this.create = null;
		this.destroy = null;
	}
	
	@Nullable
	public static TextHologram of(@NotNull Component component) {
		ComponentWrapper line = NMSUtils.componentToNMSComponent(component);
		return line == null ? null : new TextHologram(line);
	}
	
	public boolean spawn(@NotNull Location location) {
		if (isSpawned() || location.getWorld() == null || (this.location != null && this.location.equals(location = location.clone().subtract(0,1,0)))) return false;
		this.location = location;
		this.world = location.getWorld();
		ArmorStand stand = (ArmorStand) PacketUtils.createArmorStand(location,line,true,true,false,false,true).armorStand();
		ID = stand.getId();
		create = new PacketWrapper.Safe(new ClientboundAddMobPacket(stand));
		edit = new PacketWrapper.Safe(new ClientboundSetEntityDataPacket(ID,stand.getEntityData(),true));
		destroy = PacketUtils.packetDestroyEntity(ID);
		HologramsManager.register(this);
		Bukkit.getOnlinePlayers().forEach(this::spawn);
		return true;
	}
	
	public void spawn(@NotNull Player player) {
		if (isSpawned()) PacketUtils.sendPackets(player,destroy,create,edit);
	}
	
	public void despawn() {
		if (!isSpawned()) return;
		HologramsManager.unregister(this);
		location = null;
		create = null;
		Bukkit.getOnlinePlayers().forEach(player -> PacketUtils.sendPackets(player,destroy));
		destroy = null;
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
}