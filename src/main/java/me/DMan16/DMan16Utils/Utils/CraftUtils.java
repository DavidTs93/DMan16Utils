package me.DMan16.DMan16Utils.Utils;

import me.DMan16.DMan16Utils.NMSWrappers.ItemStackWrapper;
import net.minecraft.world.level.block.Block;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.advancement.CraftAdvancement;
import org.bukkit.craftbukkit.v1_18_R2.entity.*;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_18_R2.scoreboard.CraftScoreboard;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftMagicNumbers;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class CraftUtils {
	@NotNull
	static Function<@NotNull ItemStack,net.minecraft.world.item.@NotNull ItemStack> asNMSCopy() {
		return CraftItemStack::asNMSCopy;
	}
	
	@Nullable
	@Contract("null -> null")
	public static ItemStack asCraftMirror(ItemStackWrapper item) {
		return item == null ? null : ((item.item() instanceof net.minecraft.world.item.ItemStack itemStack) ? CraftItemStack.asCraftMirror(itemStack) : null);
	}
	
	@NotNull
	public static ItemStack asCraftMirror(ItemStackWrapper.@NotNull Safe item) {
		return CraftItemStack.asCraftMirror((net.minecraft.world.item.ItemStack) item.item());
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
	
	@NotNull
	public static CraftAdvancement toCraft(@NotNull Advancement advancement) {
		return (CraftAdvancement) advancement;
	}
	
	public static Block getBlockCraftMagicNumbers(@NotNull Material material) {
		return CraftMagicNumbers.getBlock(material);
	}
}