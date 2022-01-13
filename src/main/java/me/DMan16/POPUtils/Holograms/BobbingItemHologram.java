package me.DMan16.POPUtils.Holograms;

import me.DMan16.POPUtils.NMSWrappers.PacketWrapper;
import me.DMan16.POPUtils.Utils.PacketUtils;
import me.DMan16.POPUtils.Utils.ReflectionUtils;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
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
		ItemEntity entityItem = new ItemEntity(((ServerLevel) ReflectionUtils.getHandle(loc.getWorld()).world()),loc.getX(),loc.getY() - 1,loc.getZ(),
				((net.minecraft.world.item.ItemStack) ReflectionUtils.asNMSCopy(item).item()),0,0,0);
		entityItem.setNoGravity(true);
		ID = entityItem.getId();
		create = new PacketWrapper.Safe(new ClientboundAddEntityPacket(entityItem));
		edit = new PacketWrapper.Safe(new ClientboundSetEntityDataPacket(ID,entityItem.getEntityData(),true));
		destroy = PacketUtils.packetDestroyEntity(ID);
		HologramsManager.register(this);
		Bukkit.getOnlinePlayers().forEach(this::spawn);
		return true;
	}
}