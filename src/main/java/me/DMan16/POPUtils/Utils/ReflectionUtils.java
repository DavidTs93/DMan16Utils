package me.DMan16.POPUtils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class ReflectionUtils {
	public static final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	public static final Class<?> ClassCraftItemStack = getClassCraftBukkit("inventory.CraftItemStack");
	public static final Class<?> ClassItemStackNMS = getClassNMS("ItemStack","world.item");
	public static final Class<?> ClassItemNMS = getClassNMS("Item","world.item");
	public static final Class<?> ClassCraftPlayer = getClassCraftBukkit("entity.CraftPlayer");
	public static final Class<?> ClassEntityLivingNMS = getClassNMS("EntityLiving","world.entity");
	public static final Class<?> ClassMobEffectNMS = getClassNMS("MobEffect","world.effect");
	public static final Class<?> ClassMobEffectListNMS = getClassNMS("MobEffectList","world.effect");
	
	@NotNull
	private static String removeUnnecessaryDots(@NotNull String str) {
		return Arrays.stream(str.split("\\.")).filter(s -> !s.isEmpty()).collect(Collectors.joining("."));
	}
	
	public static Class<?> getClassNMS(@NotNull String name, @NotNull String subPackageNameNewNMS) {
		try {
			return Class.forName(removeUnnecessaryDots("net.minecraft." + (Utils.getVersionInt() >= 17 ? subPackageNameNewNMS : "server." + version) + "." + name));
		} catch (Exception e) {}
		return null;
	}
	
	public static Class<?> getClassCraftBukkit(@NotNull String name) {
		try {
			return Class.forName(removeUnnecessaryDots("org.bukkit.craftbukkit." + version + "." + name));
		} catch (Exception e) {}
		return null;
	}

	/**
	 * @param item Bukkit ItemStack
	 * @return item's material's translatable name, example: Material.APPLE -> "item.minecraft.apple"
	 */
	public static String ItemTranslatableName(@NotNull ItemStack item) {
		try {
			Method getItem = ClassItemStackNMS.getDeclaredMethod("getItem");
			Method getName = ClassItemNMS.getDeclaredMethod("getName");
			return (String) getName.invoke(getItem.invoke(ItemAsNMSCopy(item)));
		} catch (Exception e) {}
		return null;
	}
	
	/**
	 * @param item Bukkit ItemStack
	 * @return CraftBukkit ItemStack
	 */
	public static Object ItemAsNMSCopy(@NotNull ItemStack item) {
		try {
			Method asNMSCopy = ClassCraftItemStack.getDeclaredMethod("asNMSCopy",ItemStack.class);
			return asNMSCopy.invoke(null,item);
		} catch (Exception e) {}
		return null;
	}
	
	/**
	 * @param item CraftBukkit ItemStack
	 * @return Bukkit ItemStack
	 */
	public static ItemStack ItemAsBukkitCopy(@NotNull Object item) {
		try {
			Method asCraftMirror = ClassCraftItemStack.getDeclaredMethod("asCraftMirror",ClassItemStackNMS);
			return (ItemStack) asCraftMirror.invoke(null,item);
		} catch (Exception e) {}
		return null;
	}
	
	/**
	 * @param item Item to clone
	 * @return Clone of the original item with NBT tags maintained
	 */
	public static ItemStack CloneWithNBT(@NotNull ItemStack item) {
		try {
			Method asCraftMirror = ClassCraftItemStack.getDeclaredMethod("asCraftMirror", ClassItemStackNMS);
			return (ItemStack) asCraftMirror.invoke(null,item);
		} catch (Exception e) {}
		return item.clone();
	}
	
	/*public static ItemStack getTridentAsItemStack(Trident trident) {
		try {
			Class<?> craftClass = getClassCraftBukkit("inventory.CraftItemStack");
			Class<?> itemClass = getClassNMS("ItemStack","world.item");
			Class<?> tridentClass = getClassCraftBukkit("entity.CraftTrident");
			Method methodBukkitCopy = craftClass.getMethod("asBukkitCopy",itemClass);
			Method methodGetHandle = tridentClass.getDeclaredMethod("getHandle");
			Object castTrident = tridentClass.cast(trident);
			Object tridentEntity = methodGetHandle.invoke(castTrident);
			Field field = tridentEntity.getClass().getDeclaredField("trident");
			field.setAccessible(true);
			Object tridentHandle = field.get(tridentEntity);
			Object obj = methodBukkitCopy.invoke(tridentHandle.getClass(),tridentHandle);
			return (ItemStack) obj;
		} catch (Exception e) {}
		return null;
	}*/
	
	public static Object getHandle(@NotNull Player player) {
		try {
			Method getHandle = ClassCraftPlayer.getDeclaredMethod("getHandle");
			return getHandle.invoke(player);
		} catch (Exception e) {}
		return null;
	}
	
	public static Object getHandle(@NotNull HumanEntity human) {
		try {
			Method getHandle =getClassCraftBukkit("entity.CraftHumanEntity").getDeclaredMethod("getHandle");
			return getHandle.invoke(human);
		} catch (Exception e) {}
		return null;
	}
	
	public static Object getHandle(@NotNull LivingEntity entity) {
		try {
			Method getHandle = getClassCraftBukkit("entity.CraftLivingEntity").getDeclaredMethod("getHandle");
			return getHandle.invoke(entity);
		} catch (Exception e) {}
		return null;
	}
	
	public static Object getHandle(@NotNull Entity entity) {
		try {
			Method getHandle = getClassCraftBukkit("entity.CraftEntity").getDeclaredMethod("getHandle");
			return getHandle.invoke(entity);
		} catch (Exception e) {}
		return null;
	}
	
	public static Object MobEffectFromID(int ID) {
		try {
			Method fromId = ClassMobEffectListNMS.getMethod("fromId",int.class);
			return fromId.invoke(null,ID);
		} catch (Exception e) {}
		return null;
	}
	
	public static boolean[] AddEffects(@NotNull Player player, @NotNull Cause cause, @NotNull List<PotionEffect> effects) {
		if (effects.isEmpty()) return null;
		boolean[] result = new boolean[effects.size()];
		Arrays.fill(result,false);
		try {
			Constructor<?> MobEffectConstructor = ClassMobEffectNMS.getConstructor(ClassMobEffectListNMS,int.class,int.class,
					boolean.class,boolean.class,boolean.class);
			Method addEffect = ClassEntityLivingNMS.getMethod("addEffect",ClassMobEffectNMS,cause.getClass());
			for (int i = 0; i < effects.size(); i++) {
				PotionEffect effect = effects.get(i);
				try {
					Object MobEffect = MobEffectConstructor.newInstance(MobEffectFromID(effect.getType().getId()),effect.getDuration() * 20,
							effect.getAmplifier(),effect.isAmbient(),effect.hasParticles(),effect.hasIcon());
					result[i] = (boolean) addEffect.invoke(getHandle(player),MobEffect,cause);
				} catch (Exception e1) {}
			}
		} catch (Exception e) {}
		return result;
	}
	
	public static boolean[] AddEffects(@NotNull Player player, @NotNull Cause cause, @NotNull PotionEffect ... effects) {
		return AddEffects(player,cause,Arrays.asList(effects));
	}
	
	public static boolean[] RemoveEffects(@NotNull Player player, @NotNull Cause cause, @NotNull List<PotionEffectType> effects) {
		if (effects.isEmpty()) return null;
		boolean[] result = new boolean[effects.size()];
		Arrays.fill(result,false);
		try {
			Method removeEffect = ClassEntityLivingNMS.getMethod("removeEffect",ClassMobEffectListNMS,cause.getClass());
			for (int i = 0; i < effects.size(); i++) {
				PotionEffectType effect = effects.get(i);
				try {
					result[i] = (boolean) removeEffect.invoke(getHandle(player),MobEffectFromID(effect.getId()),cause);
				} catch (Exception e1) {}
			}
		} catch (Exception e) {}
		return result;
	}
	
	public static boolean[] RemoveEffects(@NotNull Player player, @NotNull Cause cause, @NotNull PotionEffectType ... effects) {
		return RemoveEffects(player,cause,Arrays.asList(effects));
	}
}