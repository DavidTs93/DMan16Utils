package me.DMan16.DMan16Utils.Holograms;

import me.DMan16.DMan16Utils.NMSWrappers.ComponentWrapper;
import me.DMan16.DMan16Utils.NMSWrappers.PacketWrapper;
import me.DMan16.DMan16Utils.Utils.NMSUtils;
import me.DMan16.DMan16Utils.Utils.PacketUtils;
import me.DMan16.DMan16Utils.Utils.Utils;
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
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MultiLineHologram implements Hologram<MultiLineHologram> {
	public static final double LINE_DIFFERENCE = 0.3;
	protected @Unmodifiable List<@NotNull Integer> IDs;
	protected final @Unmodifiable List<@NotNull ComponentWrapper> lines;
	protected Location location;
	protected World world;
	protected @Unmodifiable List<PacketWrapper.@NotNull Safe> creates;
	protected @Unmodifiable List<PacketWrapper.@NotNull Safe> edits;
	protected @Unmodifiable List<PacketWrapper.@NotNull Safe> destroys;
	
	public MultiLineHologram(@NotNull List<@NotNull ComponentWrapper> lines) {
		if (lines.isEmpty()) throw new IllegalArgumentException("Must have at least one line!");
		this.IDs = new ArrayList<>();
		this.lines = Collections.unmodifiableList(lines);
		this.location = null;
		this.world = null;
		this.creates = null;
		this.destroys = null;
	}
	
	public MultiLineHologram(@NotNull ComponentWrapper ... lines) {
		this(Arrays.asList(lines));
	}
	
	@Nullable
	public static MultiLineHologram of(@NotNull List<@NotNull Component> components) {
		List<ComponentWrapper> lines = components.stream().map(NMSUtils::componentToNMSComponent).filter(Objects::nonNull).collect(Collectors.toList());
		return lines.isEmpty() ? null : new MultiLineHologram(lines);
	}
	
	@Nullable
	public static MultiLineHologram of(@NotNull Component ... components) {
		return of (Arrays.asList(components));
	}
	
	public boolean spawn(@NotNull Location location) {
		if (isSpawned() || location.getWorld() == null || (this.location != null && this.location.equals(location = location.clone().subtract(0,1,0)))) return false;
		this.location = location;
		this.world = location.getWorld();
		final Location loc = location.clone().add(0,LINE_DIFFERENCE,0);
		List<@NotNull ArmorStand> stands = lines.stream().map(line -> (ArmorStand) PacketUtils.createArmorStand(loc.subtract(0,LINE_DIFFERENCE,0),line,true,true,false,false,true).armorStand()).toList();
		IDs = stands.stream().map(ArmorStand::getId).toList();
		creates = stands.stream().map(ClientboundAddMobPacket::new).map(PacketWrapper.Safe::new).toList();
		edits = IntStream.range(0,IDs.size()).mapToObj(i -> new PacketWrapper.Safe(new ClientboundSetEntityDataPacket(IDs.get(i),stands.get(i).getEntityData(),true))).toList();
		destroys = IDs.stream().map(PacketUtils::packetDestroyEntity).toList();
		HologramsManager.register(this);
		Bukkit.getOnlinePlayers().forEach(this::spawn);
		return true;
	}
	
	public void spawn(@NotNull Player player) {
		if (!isSpawned()) return;
		PacketUtils.sendPackets(player,Utils.joinLists(destroys,creates,edits));
	}
	
	public void despawn() {
		if (!isSpawned()) return;
		HologramsManager.unregister(this);
		location = null;
		creates = null;
		Bukkit.getOnlinePlayers().forEach(player -> PacketUtils.sendPackets(player,Utils.joinLists(destroys)));
		destroys = null;
	}
	
	public boolean spawnOrMove(@NotNull Location location) {
		if (location.getWorld() == null || (this.location != null && this.location.equals(location.clone().subtract(0,1,0)))) return false;
		despawn();
		spawn(location);
		return true;
	}
	
	public boolean isSpawned() {
		return location != null;
	}
	
	@Nullable
	public Location getLocation() {
		return location == null ? null : location.clone();
	}
	
	
	public World getWorld() {
		return world;
	}
	
	public double maxSize() {
		return TextHologram.LINE_HEIGHT * lines.size() + LINE_DIFFERENCE * (lines.size() - 1);
	}
	
	@NotNull
	public MultiLineHologram copy() {
		return new MultiLineHologram(lines);
	}
}