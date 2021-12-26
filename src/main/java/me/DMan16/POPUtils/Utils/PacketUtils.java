package me.DMan16.POPUtils.Utils;

import me.DMan16.POPUtils.Classes.Equipment;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Vector3f;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.level.border.WorldBorder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class PacketUtils {
	public static final Vector3f Vector3f0 = Vector3f(0,0,0);
	public static float ARMOR_STAND_HEIGHT = 1.975f;
	public static float ARMOR_STAND_HEIGHT_SMALL = 0.9875f;
	
	public static void sendPackets(@NotNull Player player, Packet<?> ... packets) {
		EntityPlayer handle = ReflectionUtils.getHandle(player);
		for (Packet<?> packet : packets) if (packet != null) handle.b.sendPacket(packet);
	}
	
	public static void sendPackets(@NotNull Player player, List<Packet<?>> packets) {
		if (packets == null) return;
		EntityPlayer handle = ReflectionUtils.getHandle(player);
		for (Packet<?> packet : packets) if (packet != null) handle.b.sendPacket(packet);
	}
	
//	public static int getNextEntityID() {
//		int ID = 0;
//		try {
//			Field entityCounter = Entity.class.getDeclaredField("b");
//			entityCounter.setAccessible(true);
//			ID = ((AtomicInteger) entityCounter.get(null)).incrementAndGet();
//		} catch (Exception e) {e.printStackTrace();}
//		return ID;
//	}
	
	@NotNull
	public static Vector3f Vector3f(float a, float b, float c) {
		return new Vector3f(a,b,c);
	}
	
	@NotNull
	public static PacketPlayOutGameStateChange packetChangeGameMode(@NotNull GameMode newGameMode) {
		return new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.d,newGameMode.getValue());
	}
	
	@NotNull
	public static EntityArmorStand createArmorStand(@NotNull Location loc, IChatBaseComponent name, boolean invisible, boolean small, boolean arms, boolean plate, boolean marker,
													Vector3f head, Vector3f body, Vector3f leftArm, Vector3f rightArm, Vector3f leftLeg, Vector3f rightLeg) {
		EntityArmorStand stand = new EntityArmorStand(ReflectionUtils.getHandle(loc.getWorld()),loc.getX(),loc.getY(),loc.getZ());
		stand.setCustomName(name);
		stand.setCustomNameVisible(true);
		stand.setInvisible(invisible);
		stand.setSmall(small);
		stand.setArms(arms);
		stand.setBasePlate(plate);
		stand.setMarker(true);
		if (head != null) stand.setHeadPose(head);
		if (body != null) stand.setBodyPose(body);
		if (leftArm != null) stand.setLeftArmPose(leftArm);
		if (rightArm != null) stand.setRightArmPose(rightArm);
		if (leftLeg != null) stand.setLeftLegPose(leftLeg);
		if (rightLeg != null) stand.setRightLegPose(rightLeg);
		return stand;
	}
	
	@NotNull
	public static EntityArmorStand createArmorStand(@NotNull Location loc, IChatBaseComponent name, boolean invisible, boolean small, boolean arms, boolean plate, boolean marker) {
		return createArmorStand(loc,name,invisible,small,arms,plate,marker,null,null,null,null,null,null);
	}
	
	@NotNull
	public static EntityArmorStand createArmorStand(@NotNull Location loc, IChatBaseComponent name, boolean invisible, boolean small, boolean arms, boolean plate,
													boolean marker, Vector3f head, Vector3f body, Vector3f leftArm, Vector3f rightArm, Vector3f leftLeg, Vector3f rightLeg,
													@NotNull Equipment equipment, boolean mainHand, boolean offHand, boolean helmet, boolean chestplate,
													boolean leggings, boolean boots) {
		EntityArmorStand stand = new EntityArmorStand(ReflectionUtils.getHandle(loc.getWorld()),loc.getX(),loc.getY(),loc.getZ());
		stand.setYRot(loc.getYaw());
		stand.setCustomName(name);
		stand.setCustomNameVisible(name != null);
		stand.setInvisible(invisible);
		stand.setSmall(small);
		stand.setArms(arms);
		stand.setBasePlate(plate);
		stand.setMarker(true);
		if (head != null) stand.setHeadPose(head);
		if (body != null) stand.setBodyPose(body);
		if (leftArm != null) stand.setLeftArmPose(leftArm);
		if (rightArm != null) stand.setRightArmPose(rightArm);
		if (leftLeg != null) stand.setLeftLegPose(leftLeg);
		if (rightLeg != null) stand.setRightLegPose(rightLeg);
		if (mainHand) stand.setSlot(EnumItemSlot.a,ReflectionUtils.ItemAsNMSCopy(equipment.mainHand()));
		if (offHand) stand.setSlot(EnumItemSlot.b,ReflectionUtils.ItemAsNMSCopy(equipment.offHand()));
		if (helmet) stand.setSlot(EnumItemSlot.f,ReflectionUtils.ItemAsNMSCopy(equipment.helmet()));
		if (chestplate) stand.setSlot(EnumItemSlot.e,ReflectionUtils.ItemAsNMSCopy(equipment.chestplate()));
		if (leggings) stand.setSlot(EnumItemSlot.d,ReflectionUtils.ItemAsNMSCopy(equipment.leggings()));
		if (boots) stand.setSlot(EnumItemSlot.c,ReflectionUtils.ItemAsNMSCopy(equipment.boots()));
		return stand;
	}
	
	@NotNull
	public static EntityArmorStand createArmorStand(@NotNull Location loc, IChatBaseComponent name, boolean invisible, boolean small, boolean arms, boolean plate,
													boolean marker, @NotNull Equipment equipment) {
		return createArmorStand(loc,name,invisible,small,arms,plate,marker,null,null,null,null,null,null,equipment,
				equipment.mainHand() != null,equipment.offHand() != null,equipment.helmet() != null,
				equipment.chestplate() != null,equipment.leggings() != null,equipment.boots() != null);
	}
	
	@NotNull
	public static EntityArmorStand createArmorStand(@NotNull Location loc, IChatBaseComponent name, boolean invisible, boolean small, boolean arms, boolean plate,
													boolean marker, Vector3f head, Vector3f body, Vector3f leftArm, Vector3f rightArm, Vector3f leftLeg, Vector3f rightLeg,
													@NotNull Equipment equipment) {
		return createArmorStand(loc,name,invisible,small,arms,plate,marker,head,body,leftArm,rightArm,leftLeg,rightLeg,equipment,
				equipment.mainHand() != null,equipment.offHand() != null,equipment.helmet() != null,
				equipment.chestplate() != null,equipment.leggings() != null,equipment.boots() != null);
	}
	
	@NotNull
	public static PacketPlayOutSpawnEntityLiving packetCreateArmorStand(@NotNull Location loc, IChatBaseComponent name, boolean invisible, boolean small, boolean arms, boolean plate,
																		boolean marker, Vector3f head, Vector3f body, Vector3f leftArm, Vector3f rightArm, Vector3f leftLeg, Vector3f rightLeg,
																		@NotNull Equipment equipment) {
		return new PacketPlayOutSpawnEntityLiving(createArmorStand(loc,name,invisible,small,arms,plate,marker,head,body,leftArm,rightArm,leftLeg,rightLeg,equipment));
	}
	
	@NotNull
	public static PacketPlayOutEntityEquipment packetArmor(int ID, @NotNull Equipment equipment, boolean mainHand, boolean offHand, boolean helmet, boolean chestplate,
														   boolean leggings, boolean boots) {
		List<com.mojang.datafixers.util.Pair<EnumItemSlot,net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
		if (mainHand) list.add(com.mojang.datafixers.util.Pair.of(EnumItemSlot.a,ReflectionUtils.ItemAsNMSCopy(equipment.mainHand())));
		if (offHand) list.add(com.mojang.datafixers.util.Pair.of(EnumItemSlot.b,ReflectionUtils.ItemAsNMSCopy(equipment.offHand())));
		if (helmet) list.add(com.mojang.datafixers.util.Pair.of(EnumItemSlot.f,ReflectionUtils.ItemAsNMSCopy(equipment.helmet())));
		if (chestplate) list.add(com.mojang.datafixers.util.Pair.of(EnumItemSlot.e,ReflectionUtils.ItemAsNMSCopy(equipment.chestplate())));
		if (leggings) list.add(com.mojang.datafixers.util.Pair.of(EnumItemSlot.d,ReflectionUtils.ItemAsNMSCopy(equipment.leggings())));
		if (boots) list.add(com.mojang.datafixers.util.Pair.of(EnumItemSlot.c,ReflectionUtils.ItemAsNMSCopy(equipment.boots())));
		return new PacketPlayOutEntityEquipment(ID,list);
	}
	
	@NotNull
	public static PacketPlayOutEntityEquipment packetArmorNotNulls(int ID, @NotNull Equipment equipment) {
		return packetArmor(ID,equipment,equipment.mainHand() != null,equipment.offHand() != null,equipment.helmet() != null,
				equipment.chestplate() != null,equipment.leggings() != null,equipment.boots() != null);
	}
	
	@NotNull
	public static DataWatcher createDataWatcherArmorStandName(IChatBaseComponent name) {
		DataWatcher data = new DataWatcher(null);
		data.register(new DataWatcherObject<>(2,DataWatcherRegistry.f),Optional.ofNullable(name));
		data.register(new DataWatcherObject<>(3,DataWatcherRegistry.i),true);
		return data;
	}
	
	@NotNull
	public static DataWatcher createDataWatcherArmorStandOptions(boolean invisible, boolean small, boolean arms, boolean plate, boolean marker) {
		DataWatcher data = new DataWatcher(null);
		if (invisible) data.register(new DataWatcherObject<>(0,DataWatcherRegistry.a),(byte) 32);
		int options = 0;
		if (small) options += 1;
		if (arms) options += 4;
		if (!plate) options += 8;
		if (marker) options += 16;
		data.register(new DataWatcherObject<>(15,DataWatcherRegistry.a),(byte) options);
		return data;
	}
	
	@NotNull
	public static DataWatcher createDataWatcherArmorStandRotations(Vector3f head, Vector3f body, Vector3f leftArm, Vector3f rightArm, Vector3f leftLeg, Vector3f rightLeg) {
		DataWatcher data = new DataWatcher(null);
		if (head != null) data.register(new DataWatcherObject<>(16,DataWatcherRegistry.k),head);
		if (body != null) data.register(new DataWatcherObject<>(17,DataWatcherRegistry.k),body);
		if (leftArm != null) data.register(new DataWatcherObject<>(18,DataWatcherRegistry.k),leftArm);
		if (rightArm != null) data.register(new DataWatcherObject<>(19,DataWatcherRegistry.k),rightArm);
		if (leftLeg != null) data.register(new DataWatcherObject<>(20,DataWatcherRegistry.k),leftLeg);
		if (rightLeg != null) data.register(new DataWatcherObject<>(21,DataWatcherRegistry.k),rightLeg);
		return data;
	}
	
	@NotNull
	public static DataWatcher createDataWatcherArmorStandEverything(IChatBaseComponent name, boolean invisible, boolean small, boolean arms, boolean noPlate, boolean marker,
																	Vector3f head, Vector3f body, Vector3f leftArm, Vector3f rightArm, Vector3f leftLeg, Vector3f rightLeg) {
		DataWatcher data = new DataWatcher(null);
		data.register(new DataWatcherObject<>(2,DataWatcherRegistry.f),Optional.ofNullable(name));
		data.register(new DataWatcherObject<>(3,DataWatcherRegistry.i),true);
		if (invisible) data.register(new DataWatcherObject<>(0,DataWatcherRegistry.a),(byte) 32);
		int options = 0;
		if (small) options += 1;
		if (arms) options += 4;
		if (noPlate) options += 8;
		if (marker) options += 16;
		data.register(new DataWatcherObject<>(15,DataWatcherRegistry.a),(byte) options);
		if (head != null) data.register(new DataWatcherObject<>(16,DataWatcherRegistry.k),head);
		if (body != null) data.register(new DataWatcherObject<>(17,DataWatcherRegistry.k),body);
		if (leftArm != null) data.register(new DataWatcherObject<>(18,DataWatcherRegistry.k),leftArm);
		if (rightArm != null) data.register(new DataWatcherObject<>(19,DataWatcherRegistry.k),rightArm);
		if (leftLeg != null) data.register(new DataWatcherObject<>(20,DataWatcherRegistry.k),leftLeg);
		if (rightLeg != null) data.register(new DataWatcherObject<>(21,DataWatcherRegistry.k),rightLeg);
		return data;
	}
	
	@NotNull
	public static PacketPlayOutEntityMetadata packetUpdateEntity(int ID, @NotNull DataWatcher DataWatcher) {
		return new PacketPlayOutEntityMetadata(ID,DataWatcher,true);
	}
	
	@NotNull
	public static PacketPlayOutEntityDestroy packetDestroyEntity(int ... IDs) {
		return new PacketPlayOutEntityDestroy(IDs);
	}
	
	@NotNull
	@Unmodifiable
	public static List<Packet<?>> packetWorldBorder(@NotNull WorldBorder border) {
		return List.of(new ClientboundInitializeBorderPacket(border), new ClientboundSetBorderSizePacket(border));
	}
	
	@NotNull
	@Unmodifiable
	public static List<Packet<?>> packetWorldBorder(@NotNull World world, @NotNull me.DMan16.POPUtils.Classes.Pair<@NotNull Double,@NotNull Double> center, double size, double damageAmount,
													double damageBuffer, int warningDistance, int warningTime) {
		WorldBorder border = new WorldBorder();
		border.world = ReflectionUtils.getHandle(world);
		border.setCenter(center.first(),center.second());
		border.setSize(size);
		border.setDamageAmount(damageAmount);
		border.setDamageBuffer(damageBuffer);
		border.setWarningDistance(warningDistance);
		border.setWarningTime(warningTime);
		return packetWorldBorder(border);
	}
	
	@NotNull
	@Unmodifiable
	public static List<Packet<?>> packetWorldBorder(@NotNull World world, @NotNull me.DMan16.POPUtils.Classes.Pair<@NotNull Double,@NotNull Double> center, double size, int warningDistance) {
		WorldBorder border = ReflectionUtils.getWorldBorder(world);
		return packetWorldBorder(world,center,size,border.getDamageAmount(),border.getDamageBuffer(),warningDistance,border.getWarningTime());
	}
	
	@NotNull
	@Unmodifiable
	public static List<Packet<?>> packetWorldBorder(@NotNull World world, @NotNull me.DMan16.POPUtils.Classes.Pair<@NotNull Double,@NotNull Double> center, double size) {
		WorldBorder border = ReflectionUtils.getWorldBorder(world);
		return packetWorldBorder(world,center,size,border.getDamageAmount(),border.getDamageBuffer(),border.getWarningDistance(),border.getWarningTime());
	}
	
	@NotNull
	public static PacketPlayOutBlockAction changeLidded(@NotNull Block block, boolean open) {
		return new PacketPlayOutBlockAction(new BlockPosition(block.getX(),block.getY(),block.getZ()),CraftUtils.getBlockCraftMagicNumbers(block.getType()),1,open ? 1 : 0);
	}
}