package me.DMan16.POPUtils.Utils;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CraftUtils {
	@NotNull
	public static net.minecraft.world.item.ItemStack asNMSCopy(@NotNull ItemStack item) {
		return CraftItemStack.asNMSCopy(item);
	}
	
	@NotNull
	public static ItemStack asCraftMirror(@NotNull net.minecraft.world.item.ItemStack item) {
		return CraftItemStack.asCraftMirror(item);
	}
	
	@NotNull
	public static CraftPlayer toCraft(@NotNull Player player) {
		return (CraftPlayer) player;
	}
	
	@NotNull
	public static CraftHumanEntity toCraft(@NotNull HumanEntity human) {
		return (CraftHumanEntity) human;
	}
	
	@NotNull
	public static CraftLivingEntity toCraft(@NotNull LivingEntity entity) {
		return (CraftLivingEntity) entity;
	}
	
	@NotNull
	public static CraftEntity toCraft(@NotNull Entity entity) {
		return (CraftEntity) entity;
	}
	
	@NotNull
	public static CraftWorld toCraft(@NotNull World world) {
		return (CraftWorld) world;
	}
}