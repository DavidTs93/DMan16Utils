package me.DMan16.POPUtils.Utils;

import net.minecraft.world.level.block.Block;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.*;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_18_R2.scoreboard.CraftScoreboard;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftMagicNumbers;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class CraftUtils {
	@NotNull
	static Function<@NotNull ItemStack,net.minecraft.world.item.@NotNull ItemStack> asNMSCopy() {
		return CraftItemStack::asNMSCopy;
	}
	
//	@NotNull
//	public static ItemStack asCraftMirror(@NotNull net.minecraft.world.item.ItemStack item) {
//		return CraftItemStack.asCraftMirror(item);
//	}
	
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
	public static CraftMob toCraft(@NotNull Mob entity) {
		return ((CraftMob) entity);
	}
	
	@NotNull
	public static CraftEntity toCraft(@NotNull Entity entity) {
		return (CraftEntity) entity;
	}
	
	@NotNull
	public static CraftWorld toCraft(@NotNull World world) {
		return (CraftWorld) world;
	}
	
	@NotNull
	public static CraftScoreboard toCraft(@NotNull Scoreboard scoreboard) {
		return (CraftScoreboard) scoreboard;
	}
	
	public static Block getBlockCraftMagicNumbers(@NotNull Material material) {
		return CraftMagicNumbers.getBlock(material);
	}
}