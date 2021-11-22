package me.DMan16.POPUtils.Holograms;

import me.DMan16.POPUtils.Utils.CraftUtils;
import me.DMan16.POPUtils.Utils.PacketUtils;
import me.DMan16.POPUtils.Utils.ReflectionUtils;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.world.entity.item.EntityItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BobbingItemHologram extends ItemHologram {
	public BobbingItemHologram(@NotNull ItemStack item) {
		super(item);
	}
	
	@Override
	public boolean spawn(@NotNull Location loc) {
		if (isSpawned() || loc.getWorld() == null || (this.location != null && this.location.equals(loc = loc.clone().subtract(0,1,0)))) return false;
		this.location = loc;
		this.world = loc.getWorld();
		EntityItem entityItem = new EntityItem(ReflectionUtils.getHandle(loc.getWorld()),loc.getX(),loc.getY() - 1,loc.getZ(),CraftUtils.asNMSCopy(item),0,0,0);
		entityItem.setNoGravity(true);
		ID = entityItem.getId();
		create = new PacketPlayOutSpawnEntity(entityItem);
		edit = new PacketPlayOutEntityMetadata(ID,entityItem.getDataWatcher(),true);
		destroy = PacketUtils.packetDestroyEntity(ID);
		HologramsManager.register(this);
		Bukkit.getOnlinePlayers().forEach(this::spawn);
		return true;
	}
}