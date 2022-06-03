package me.DMan16.DMan16Utils.Utils;

import me.DMan16.DMan16Utils.NMSWrappers.Entities.*;
import me.DMan16.DMan16Utils.NMSWrappers.ItemStackWrapper;
import me.DMan16.DMan16Utils.NMSWrappers.WorldWrapper;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@SuppressWarnings("deprecation")
public class ReflectionUtils {
	
	@NotNull
	public static ItemStackWrapper.Safe asNMSCopy(@NotNull ItemStack item) {
		 return new ItemStackWrapper.Safe(CraftUtils.asNMSCopy().apply(item));
	}
	
	public static PlayerWrapper.Safe getHandle(@NotNull Player player) {
		 return new PlayerWrapper.Safe(CraftUtils.toCraft(player).getHandle());
	}
	
	@NotNull
	public static HumanWrapper.Safe getHandle(@NotNull HumanEntity human) {
		return new HumanWrapper.Safe(CraftUtils.toCraft(human).getHandle());
	}
	
	@NotNull
	public static MobWrapper.Safe getHandle(@NotNull Mob entity) {
		return new MobWrapper.Safe(CraftUtils.toCraft(entity).getHandle());
	}
	
	@NotNull
	public static LivingEntityWrapper.Safe getHandle(@NotNull LivingEntity entity) {
		return new LivingEntityWrapper.Safe(CraftUtils.toCraft(entity).getHandle());
	}
	
	@NotNull
	public static EntityWrapper.Safe getHandle(@NotNull Entity entity) {
		return new EntityWrapper.Safe(CraftUtils.toCraft(entity).getHandle());
	}
	
	@NotNull
	public static WorldWrapper.Safe getHandle(@NotNull World world) {
		return new WorldWrapper.Safe(CraftUtils.toCraft(world).getHandle());
	}
	
	public static boolean[] addEffects(@NotNull LivingEntity entity, @NotNull Cause cause, @NotNull List<PotionEffect> effects) {
		if (effects.isEmpty()) return new boolean[0];
		boolean[] result = new boolean[effects.size()];
		Arrays.fill(result,false);
		for (int i = 0; i < effects.size(); i++) {
			PotionEffect effect = effects.get(i);
			net.minecraft.world.effect.MobEffect mobEffect = net.minecraft.world.effect.MobEffect.byId(effect.getType().getId());
			if (mobEffect == null) continue;
			net.minecraft.world.effect.MobEffectInstance mobEffectInstance = new net.minecraft.world.effect.MobEffectInstance(mobEffect,effect.getDuration() * 20,effect.getAmplifier(),
					effect.isAmbient(),effect.hasParticles(),effect.hasIcon());
			result[i] = ((net.minecraft.world.entity.LivingEntity) getHandle(entity).livingEntity()).addEffect(mobEffectInstance,cause);
		}
		return result;
	}
	
	public static boolean[] addEffects(@NotNull LivingEntity entity, @NotNull Cause cause, @NotNull PotionEffect ... effects) {
		return addEffects(entity,cause,Arrays.asList(effects));
	}
	
	public static boolean[] RemoveEffects(@NotNull Player player, @NotNull Cause cause, @NotNull List<PotionEffectType> effects) {
		if (effects.isEmpty()) return new boolean[0];
		boolean[] result = new boolean[effects.size()];
		Arrays.fill(result,false);
		for (int i = 0; i < effects.size(); i++) {
			PotionEffectType effect = effects.get(i);
			net.minecraft.world.effect.MobEffect mobEffectList = net.minecraft.world.effect.MobEffect.byId(effect.getId());
			if (mobEffectList != null) result[i] = ((net.minecraft.server.level.ServerPlayer) getHandle(player).player()).removeEffect(mobEffectList,cause);
		}
		return result;
	}
	
	public static boolean[] RemoveEffects(@NotNull Player player, @NotNull Cause cause, @NotNull PotionEffectType ... effects) {
		getStaticFields(Player.class,Cause.class,false);
		return RemoveEffects(player,cause,Arrays.asList(effects));
	}
	
	@NotNull
	@Unmodifiable
	public static <V extends Class<?>> List<@NotNull Field> getFields(@NotNull Class<?> clazz, V type, boolean exact) {
		List<Field> list = new ArrayList<>();
		Field[] arr = clazz.getDeclaredFields();
		if (type == null) return List.of(arr);
		for (Field field : arr) {
			field.setAccessible(true);
			if ((exact && field.getType() == type) || type.isAssignableFrom(field.getType())) list.add(field);
		}
		return Collections.unmodifiableList(list);
	}
	
	@NotNull
	public static <V> Map<String,V> getStaticFields(@NotNull Class<?> clazz, @NotNull Class<V> type, boolean exact) {
		Map<String,V> fields = new HashMap<>();
		for (Field field : getFields(clazz,type,exact)) {
			field.setAccessible(true);
			if (Modifier.isStatic(field.getModifiers())) try {
				fields.put(field.getName(),type.cast(field.get(null)));
			} catch (Exception e) {e.printStackTrace();}
		}
		return fields;
	}
}