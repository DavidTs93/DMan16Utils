package me.DMan16.POPUtils.Utils;

import me.DMan16.POPUtils.Classes.Equipment;
import me.DMan16.POPUtils.NMSWrappers.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("deprecation")
public class PacketUtils {
	public static final RotationsWrapper.@NotNull Safe ROTATIONS0 = rotations(0,0,0);
	
	public static void sendPackets(@NotNull Player player, PacketWrapper ... packets) {
		if (packets.length <= 0) return;
		ServerPlayer handle = (ServerPlayer) ReflectionUtils.getHandle(player).player();
		for (PacketWrapper packet : packets) if (packet != null && packet.isPacket()) handle.connection.send((Packet<?>) packet.packet());
	}
	
	public static void sendPackets(@NotNull Player player, Collection<? extends PacketWrapper> packets) {
		if (packets == null) return;
		ServerPlayer handle = (ServerPlayer) ReflectionUtils.getHandle(player).player();
		for (PacketWrapper packet : packets) if (packet != null && packet.isPacket()) handle.connection.send((Packet<?>) packet.packet());
	}
	
	public static void sendPackets(@NotNull Collection<@NotNull Player> players, PacketWrapper ... packets) {
		if (players.size() > 0 && packets.length > 0) players.forEach(player -> sendPackets(player,packets));
	}
	
	public static void sendPackets(@NotNull Collection<@NotNull Player> players, Collection<? extends PacketWrapper> packets) {
		if (players.size() > 0 && packets != null) players.forEach(player -> sendPackets(player,packets));
	}
	
	@NotNull
	public static PacketWrapper.Safe packetChangeGameMode(@NotNull GameMode newGameMode) {
		return new PacketWrapper.Safe(new ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE,newGameMode.getValue()));
	}
	
	@NotNull
	public static RotationsWrapper.Safe rotations(float a,float b,float c) {
		return new RotationsWrapper.Safe(new Rotations(a,b,c));
	}
	
	@NotNull
	public static SafeArmorStandWrapper createArmorStand(@NotNull Location loc,ComponentWrapper name,boolean invisible,boolean small,boolean arms,boolean plate,boolean marker,
														 RotationsWrapper head,RotationsWrapper body,RotationsWrapper leftArm,RotationsWrapper rightArm,RotationsWrapper leftLeg,
														 RotationsWrapper rightLeg) {
		ArmorStand stand = new ArmorStand(((ServerLevel) ReflectionUtils.getHandle(loc.getWorld()).world()),loc.getX(),loc.getY(),loc.getZ());
		if (name != null && name.isComponent()) {
			stand.setCustomName((Component) name.component());
			stand.setCustomNameVisible(true);
		}
		stand.setInvisible(invisible);
		stand.setSmall(small);
		stand.setShowArms(arms);
		stand.setNoBasePlate(!plate);
		stand.setMarker(true);
		if (head != null && head.isRotations()) stand.setHeadPose((Rotations) head.rotations());
		if (body != null && body.isRotations()) stand.setBodyPose((Rotations) body.rotations());
		if (leftArm != null && leftArm.isRotations()) stand.setLeftArmPose((Rotations) leftArm.rotations());
		if (rightArm != null && rightArm.isRotations()) stand.setRightArmPose((Rotations) rightArm.rotations());
		if (leftLeg != null && leftLeg.isRotations()) stand.setLeftLegPose((Rotations) leftLeg.rotations());
		if (rightLeg != null && rightLeg.isRotations()) stand.setRightLegPose((Rotations) rightLeg.rotations());
		return new SafeArmorStandWrapper(stand);
	}
	
	@NotNull
	public static SafeArmorStandWrapper createArmorStand(@NotNull Location loc, ComponentWrapper name, boolean invisible, boolean small, boolean arms, boolean plate, boolean marker) {
		return createArmorStand(loc,name,invisible,small,arms,plate,marker,null,null,null,null,null,null);
	}
	
	@NotNull
	public static SafeArmorStandWrapper createArmorStand(@NotNull Location loc, ComponentWrapper name, boolean invisible, boolean small, boolean arms, boolean plate,
														 boolean marker, RotationsWrapper head, RotationsWrapper body, RotationsWrapper leftArm, RotationsWrapper rightArm,
														 RotationsWrapper leftLeg, RotationsWrapper rightLeg, @NotNull Equipment equipment, boolean mainHand, boolean offHand,
														 boolean helmet, boolean chestplate, boolean leggings, boolean boots) {
		ArmorStand stand = (ArmorStand) createArmorStand(loc,name,invisible,small,arms,plate,marker,head,body,leftArm,rightArm,leftLeg,rightLeg).armorStand();
		stand.setYRot(loc.getYaw());	// set rotation
		if (mainHand) stand.setItemSlot(EquipmentSlot.MAINHAND,(ItemStack) ReflectionUtils.asNMSCopy(equipment.mainHand()).item());
		if (offHand) stand.setItemSlot(EquipmentSlot.OFFHAND,(ItemStack) ReflectionUtils.asNMSCopy(equipment.offHand()).item());
		if (helmet) stand.setItemSlot(EquipmentSlot.HEAD,(ItemStack) ReflectionUtils.asNMSCopy(equipment.helmet()).item());
		if (chestplate) stand.setItemSlot(EquipmentSlot.CHEST,(ItemStack) ReflectionUtils.asNMSCopy(equipment.chestplate()).item());
		if (leggings) stand.setItemSlot(EquipmentSlot.LEGS,(ItemStack) ReflectionUtils.asNMSCopy(equipment.leggings()).item());
		if (boots) stand.setItemSlot(EquipmentSlot.FEET,(ItemStack) ReflectionUtils.asNMSCopy(equipment.boots()).item());
		return new SafeArmorStandWrapper(stand);
	}
	
	@NotNull
	public static SafeArmorStandWrapper createArmorStand(@NotNull Location loc, ComponentWrapper name, boolean invisible, boolean small, boolean arms, boolean plate,
														 boolean marker, @NotNull Equipment equipment) {
		return createArmorStand(loc,name,invisible,small,arms,plate,marker,null,null,null,null,null,null,equipment,
				equipment.mainHand() != null,equipment.offHand() != null,equipment.helmet() != null,
				equipment.chestplate() != null,equipment.leggings() != null,equipment.boots() != null);
	}
	
	@NotNull
	public static SafeArmorStandWrapper createArmorStand(@NotNull Location loc, ComponentWrapper name, boolean invisible, boolean small, boolean arms, boolean plate, boolean marker,
														 RotationsWrapper head, RotationsWrapper body, RotationsWrapper leftArm, RotationsWrapper rightArm, RotationsWrapper leftLeg,
														 RotationsWrapper rightLeg, @NotNull Equipment equipment) {
		return createArmorStand(loc,name,invisible,small,arms,plate,marker,head,body,leftArm,rightArm,leftLeg,rightLeg,equipment,
				equipment.mainHand() != null,equipment.offHand() != null,equipment.helmet() != null,
				equipment.chestplate() != null,equipment.leggings() != null,equipment.boots() != null);
	}
	
	@NotNull
	public static PacketWrapper.Safe packetCreateArmorStand(@NotNull Location loc,ComponentWrapper name,boolean invisible,boolean small,boolean arms,boolean plate,boolean marker,
															RotationsWrapper head,RotationsWrapper body,RotationsWrapper leftArm,RotationsWrapper rightArm,RotationsWrapper leftLeg,
															RotationsWrapper rightLeg,@NotNull Equipment equipment) {
		return new PacketWrapper.Safe(new ClientboundAddMobPacket((ArmorStand) createArmorStand(loc,name,invisible,small,arms,plate,marker,head,body,leftArm,rightArm,leftLeg,
				rightLeg,equipment).armorStand()));
	}
	
	@NotNull
	public static PacketWrapper.Safe packetArmor(int ID,@NotNull Equipment equipment,boolean mainHand,boolean offHand,boolean helmet,boolean chestplate,boolean leggings,boolean boots) {
		List<com.mojang.datafixers.util.Pair<EquipmentSlot,ItemStack>> list = new ArrayList<>();
		if (mainHand) list.add(com.mojang.datafixers.util.Pair.of(EquipmentSlot.MAINHAND,(ItemStack) ReflectionUtils.asNMSCopy(equipment.mainHand()).item()));
		if (offHand) list.add(com.mojang.datafixers.util.Pair.of(EquipmentSlot.OFFHAND,(ItemStack) ReflectionUtils.asNMSCopy(equipment.offHand()).item()));
		if (helmet) list.add(com.mojang.datafixers.util.Pair.of(EquipmentSlot.HEAD,(ItemStack) ReflectionUtils.asNMSCopy(equipment.helmet()).item()));
		if (chestplate) list.add(com.mojang.datafixers.util.Pair.of(EquipmentSlot.CHEST,(ItemStack) ReflectionUtils.asNMSCopy(equipment.chestplate()).item()));
		if (leggings) list.add(com.mojang.datafixers.util.Pair.of(EquipmentSlot.LEGS,(ItemStack) ReflectionUtils.asNMSCopy(equipment.leggings()).item()));
		if (boots) list.add(com.mojang.datafixers.util.Pair.of(EquipmentSlot.FEET,(ItemStack) ReflectionUtils.asNMSCopy(equipment.boots()).item()));
		return new PacketWrapper.Safe(new ClientboundSetEquipmentPacket(ID,list));
	}
	
	@NotNull
	public static PacketWrapper.Safe packetArmorNotNulls(int ID, @NotNull Equipment equipment) {
		return packetArmor(ID,equipment,equipment.mainHand() != null,equipment.offHand() != null,equipment.helmet() != null,
				equipment.chestplate() != null,equipment.leggings() != null,equipment.boots() != null);
	}
	
	@NotNull
	public static SynchedEntityDataWrapper.Safe createDataWatcherName(ComponentWrapper name) {
		SynchedEntityData data = new SynchedEntityData(null);
		data.define(new EntityDataAccessor<>(2,EntityDataSerializers.OPTIONAL_COMPONENT),Optional.ofNullable(name == null || !name.isComponent() ? null :
				(Component) name.component()));
		data.define(new EntityDataAccessor<>(3,EntityDataSerializers.BOOLEAN),true);
		return new SynchedEntityDataWrapper.Safe(data);
	}
	
	@NotNull
	public static SynchedEntityDataWrapper.Safe createDataWatcherArmorStandOptions(boolean invisible, boolean small, boolean arms, boolean plate, boolean marker) {
		SynchedEntityData data = new SynchedEntityData(null);
		if (invisible) data.define(new EntityDataAccessor<>(0,EntityDataSerializers.BYTE),(byte) 32);
		int options = 0;
		if (small) options += 1;
		if (arms) options += 4;
		if (!plate) options += 8;
		if (marker) options += 16;
		data.define(new EntityDataAccessor<>(15,EntityDataSerializers.BYTE),(byte) options);
		return new SynchedEntityDataWrapper.Safe(data);
	}
	
	@NotNull
	public static SynchedEntityDataWrapper.Safe createDataWatcherArmorStandRotations(RotationsWrapper head, RotationsWrapper body, RotationsWrapper leftArm, RotationsWrapper rightArm,
																					 RotationsWrapper leftLeg, RotationsWrapper rightLeg) {
		SynchedEntityData data = new SynchedEntityData(null);
		if (head != null && head.isRotations()) data.define(new EntityDataAccessor<>(16,EntityDataSerializers.ROTATIONS),(Rotations) head.rotations());
		if (body != null && body.isRotations()) data.define(new EntityDataAccessor<>(17,EntityDataSerializers.ROTATIONS),(Rotations) body.rotations());
		if (leftArm != null && leftArm.isRotations()) data.define(new EntityDataAccessor<>(18,EntityDataSerializers.ROTATIONS),(Rotations) leftArm.rotations());
		if (rightArm != null && rightArm.isRotations()) data.define(new EntityDataAccessor<>(19,EntityDataSerializers.ROTATIONS),(Rotations) rightArm.rotations());
		if (leftLeg != null && leftLeg.isRotations()) data.define(new EntityDataAccessor<>(20,EntityDataSerializers.ROTATIONS),(Rotations) leftLeg.rotations());
		if (rightLeg != null && rightLeg.isRotations()) data.define(new EntityDataAccessor<>(21,EntityDataSerializers.ROTATIONS),(Rotations) rightLeg.rotations());
		return new SynchedEntityDataWrapper.Safe(data);
	}
	
	@NotNull
	public static SynchedEntityDataWrapper.Safe createDataWatcherArmorStandEverything(ComponentWrapper name, boolean invisible, boolean small, boolean arms, boolean noPlate, boolean marker,
																					  RotationsWrapper head, RotationsWrapper body, RotationsWrapper leftArm, RotationsWrapper rightArm,
																					  RotationsWrapper leftLeg, RotationsWrapper rightLeg) {
		SynchedEntityData data = (SynchedEntityData) createDataWatcherArmorStandRotations(head,body,leftArm,rightArm,leftLeg,rightLeg).data();
		data.define(new EntityDataAccessor<>(2,EntityDataSerializers.OPTIONAL_COMPONENT),Optional.ofNullable(name == null || !name.isComponent() ? null :
				(Component) name.component()));
		data.define(new EntityDataAccessor<>(3,EntityDataSerializers.BOOLEAN),true);
		if (invisible) data.define(new EntityDataAccessor<>(0,EntityDataSerializers.BYTE),(byte) 32);
		int options = 0;
		if (small) options += 1;
		if (arms) options += 4;
		if (noPlate) options += 8;
		if (marker) options += 16;
		data.define(new EntityDataAccessor<>(15,EntityDataSerializers.BYTE),(byte) options);
		return new SynchedEntityDataWrapper.Safe(data);
	}
	
	@NotNull
	public static PacketWrapper.Safe packetUpdateEntity(int ID, @NotNull SynchedEntityDataWrapper data) {
		return new PacketWrapper.Safe(new ClientboundSetEntityDataPacket(ID,(SynchedEntityData) data.data(),true));
	}
	
	@NotNull
	public static PacketWrapper.Safe packetDestroyEntity(int ... IDs) {
		return new PacketWrapper.Safe(new ClientboundRemoveEntitiesPacket(IDs));
	}
	
//	@NotNull
//	@Unmodifiable
//	public static List<Packet<?>> packetWorldBorder(@NotNull WorldBorder border) {
//		return List.of(new ClientboundInitializeBorderPacket(border), new ClientboundSetBorderSizePacket(border));
//	}
//
//	@NotNull
//	@Unmodifiable
//	public static List<Packet<?>> packetWorldBorder(@NotNull World world, @NotNull me.DMan16.POPUtils.Classes.Pair<@NotNull Double,@NotNull Double> center, double size, double damageAmount,
//													double damageBuffer, int warningDistance, int warningTime) {
//		WorldBorder border = new WorldBorder();
//		border.world = ReflectionUtils.getHandle(world);
//		border.setCenter(center.first(),center.second());
//		border.setSize(size);
//		border.setDamagePerBlock(damageAmount);
//		border.setDamageSafeZone(damageBuffer);
//		border.setWarningBlocks(warningDistance);
//		border.setWarningTime(warningTime);
//		return packetWorldBorder(border);
//	}
//
//	@NotNull
//	@Unmodifiable
//	public static List<Packet<?>> packetWorldBorder(@NotNull World world, @NotNull me.DMan16.POPUtils.Classes.Pair<@NotNull Double,@NotNull Double> center, double size, int warningDistance) {
//		WorldBorder border = ReflectionUtils.getWorldBorder(world);
//		return packetWorldBorder(world,center,size,border.getDamagePerBlock(),border.getDamageSafeZone(),warningDistance,border.getWarningTime());
//	}
//
//	@NotNull
//	@Unmodifiable
//	public static List<Packet<?>> packetWorldBorder(@NotNull World world, @NotNull me.DMan16.POPUtils.Classes.Pair<@NotNull Double,@NotNull Double> center, double size) {
//		WorldBorder border = ReflectionUtils.getWorldBorder(world);
//		return packetWorldBorder(world,center,size,border.getDamagePerBlock(),border.getDamageSafeZone(),border.getWarningBlocks(),border.getWarningTime());
//	}
	
	@NotNull
	public static PacketWrapper.Safe changeLidded(@NotNull Block block,boolean open) {
		return new PacketWrapper.Safe(new ClientboundBlockEventPacket(new BlockPos(block.getX(),block.getY(),block.getZ()),CraftUtils.getBlockCraftMagicNumbers(block.getType()),1,open ? 1 : 0));
	}
	
	public static final class SafeArmorStandWrapper {
		private final Object stand;
		
		private SafeArmorStandWrapper(@NotNull ArmorStand stand) {
			this.stand = stand;
		}
		
		@NotNull
		public Object armorStand() {
			return stand;
		}
	}
}