package me.DMan16.POPUtils.Holograms;

import me.DMan16.POPUtils.Classes.Equipment;
import me.DMan16.POPUtils.Utils.PacketUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemHologram implements Hologram<ItemHologram> {
	protected int ID;
	protected ItemStack item;
	protected Location location;
	protected World world;
	protected Packet<?> create;
	protected PacketPlayOutEntityMetadata edit;
	protected PacketPlayOutEntityEquipment armor;
	protected PacketPlayOutEntityDestroy destroy;
	
	public ItemHologram(@NotNull ItemStack item) {
		this.ID = 0;
		this.item = item.clone();
		this.location = null;
		this.world = null;
		this.create = null;
		this.destroy = null;
	}
	
	public boolean spawn(@NotNull Location loc) {
		if (isSpawned() || loc.getWorld() == null || (this.location != null && this.location.equals(loc = loc.clone().subtract(0,1,0)))) return false;
		this.location = loc;
		this.world = loc.getWorld();
		EntityArmorStand stand = PacketUtils.createArmorStand(loc.clone().add(0,-1,0.25),null,true,true,true,false,
				true, new Equipment(item,item,item,null,null,null));
		ID = stand.getId();
		create = new PacketPlayOutSpawnEntityLiving(stand);
		edit = new PacketPlayOutEntityMetadata(ID,stand.getDataWatcher(),true);
		armor = PacketUtils.packetArmorNotNulls(ID, new Equipment(item, item, item, null, null, null));
		destroy = PacketUtils.packetDestroyEntity(ID);
		HologramsManager.register(this);
		Bukkit.getOnlinePlayers().forEach(this::spawn);
		return true;
	}
	
	public void spawn(@NotNull Player player) {
		if (isSpawned()) PacketUtils.sendPackets(player,destroy,create,edit,armor);
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
		return 0.5;
	}
	
	@NotNull
	public ItemHologram copy() {
		return new ItemHologram(item);
	}
	
	@Override
	public ItemHologram clone() {
		try {
			ItemHologram clone = (ItemHologram) super.clone();
			clone.ID = 0;
			clone.item = item.clone();
			clone.location = null;
			clone.world = null;
			clone.create = null;
			clone.destroy = null;
			return clone;
		} catch (Exception e) {}
		return copy();
	}
}