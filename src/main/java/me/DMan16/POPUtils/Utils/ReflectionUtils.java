package me.DMan16.POPUtils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SuppressWarnings({"deprecation","ConstantConditions"})
public class ReflectionUtils {
	public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	public static final Class<?> CLASS_CRAFT_ITEM_STACK = getClassCraftBukkit("inventory.CraftItemStack");
	public static final Class<?> CLASS_ITEM_STACK_NMS = getClassNMS("ItemStack","world.item");
	public static final Class<?> CLASS_ITEM_NMS = getClassNMS("Item","world.item");
	public static final Class<?> CLASS_CRAFT_PLAYER = getClassCraftBukkit("entity.CraftPlayer");
	public static final Class<?> CLASS_ENTITY_LIVING_NMS = getClassNMS("EntityLiving","world.entity");
	public static final Class<?> CLASS_MOB_EFFECT_NMS = getClassNMS("MobEffect","world.effect");
	public static final Class<?> CLASS_MOB_EFFECT_LIST_NMS = getClassNMS("MobEffectList","world.effect");
	
	@NotNull
	private static String removeUnnecessaryDots(@NotNull String str) {
		return Arrays.stream(str.split("\\.")).filter(s -> !s.isEmpty()).collect(Collectors.joining("."));
	}
	
	public static Class<?> getClassNMS(@NotNull String name, @NotNull String subPackageNameNewNMS) {
		try {
			return Class.forName(removeUnnecessaryDots("net.minecraft." + (Utils.getVersionInt() >= 17 ? subPackageNameNewNMS : "server." + VERSION) + "." + name));
		} catch (Exception e) {}
		return null;
	}
	
	public static Class<?> getClassCraftBukkit(@NotNull String name) {
		try {
			return Class.forName(removeUnnecessaryDots("org.bukkit.craftbukkit." + VERSION + "." + name));
		} catch (Exception e) {}
		return null;
	}

	/**
	 * @param item Bukkit ItemStack
	 * @return item's material's translatable name, example: Material.APPLE -> "item.minecraft.apple"
	 */
	public static String ItemTranslatableName(@NotNull ItemStack item) {
		try {
			Method getItem = CLASS_ITEM_STACK_NMS.getDeclaredMethod("getItem");
			Method getName = CLASS_ITEM_NMS.getDeclaredMethod("getName");
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
			Method asNMSCopy = CLASS_CRAFT_ITEM_STACK.getDeclaredMethod("asNMSCopy",ItemStack.class);
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
			Method asCraftMirror = CLASS_CRAFT_ITEM_STACK.getDeclaredMethod("asCraftMirror",CLASS_ITEM_STACK_NMS);
			return (ItemStack) asCraftMirror.invoke(null,item);
		} catch (Exception e) {}
		return null;
	}
	
	/**
	 * @param item Item to clone
	 * @return Clone of the original item with NBT tags maintained
	 */
	@NotNull
	public static ItemStack CloneWithNBT(@NotNull ItemStack item) {
		try {
			Method asCraftMirror = CLASS_CRAFT_ITEM_STACK.getDeclaredMethod("asCraftMirror",CLASS_ITEM_STACK_NMS);
			return (ItemStack) asCraftMirror.invoke(null,item);
		} catch (Exception e) {}
		return item.clone();
	}
	
	public static Object getHandle(@NotNull Player player) {
		try {
			Method getHandle = CLASS_CRAFT_PLAYER.getDeclaredMethod("getHandle");
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
			Method fromId = CLASS_MOB_EFFECT_LIST_NMS.getMethod("fromId",int.class);
			return fromId.invoke(null,ID);
		} catch (Exception e) {}
		return null;
	}
	
	public static boolean[] AddEffects(@NotNull Player player, @NotNull Cause cause, @NotNull List<PotionEffect> effects) {
		if (effects.isEmpty()) return null;
		boolean[] result = new boolean[effects.size()];
		Arrays.fill(result,false);
		try {
			Constructor<?> MobEffectConstructor = CLASS_MOB_EFFECT_NMS.getConstructor(CLASS_MOB_EFFECT_LIST_NMS,int.class,int.class,
					boolean.class,boolean.class,boolean.class);
			Method addEffect = CLASS_ENTITY_LIVING_NMS.getMethod("addEffect",CLASS_MOB_EFFECT_NMS,cause.getClass());
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
			Method removeEffect = CLASS_ENTITY_LIVING_NMS.getMethod("removeEffect",CLASS_MOB_EFFECT_LIST_NMS,cause.getClass());
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
	
	public static List<Field> getFields(Class<?> clazz, Class<?> type) {
		List<Field> list = new ArrayList<>();
		if (clazz == null) return list;
		Field[] arr = clazz.getDeclaredFields();
		for (Field field : arr) {
			field.setAccessible(true);
			if (type == null || field.getType() == type) list.add(field);
		}
		return list;
	}
	
	public static Map<String, Object> getStaticFields(Class<?> clazz) {
		Map<String,Object> fields = new HashMap<>();
		if (clazz == null) return fields;
		for (Field field : getFields(clazz,null)) {
			field.setAccessible(true);
			if (Modifier.isStatic(field.getModifiers())) try {
				fields.put(field.getName(),field.get((Object) null));
			} catch (Exception e) {e.printStackTrace();}
		}
		return fields;
	}
	
	public static String buildIChatBaseComponentString(String text, boolean translate, boolean italic, @Nullable String color, boolean bold, Object ... with) {
		if (text == null) return null;
		String str = "{\"";
		str += (translate ? "translate" : "text");
		str += "\":\"" + text + "\"";
		str += ",\"italic\":" + italic;
		if (color != null && !color.isEmpty()) str += ",\"color\":\"" + color + "\"";
		if (translate && with.length > 0) {
			List<String> extras = new ArrayList<>();
			for (Object obj : with) {
				String extra = IChatBaseComponentToString(obj);
				if (extra != null) extras.add(extra);
			}
			if (!extras.isEmpty()) {
				str += ",\"with\":[";
				str += String.join(",",extras);
				str += "]";
			}
		}
		str += "}";
		return str;
	}
	
	public static String buildIChatBaseComponentString(String text, boolean translate, @Nullable String color) {
		return buildIChatBaseComponentString(text,translate,false,color,false);
	}
	
	public static Object buildIChatBaseComponentStringExtra(List<Object> comps) {
		if (comps == null || comps.isEmpty()) return null;
		String str = "{\"extra\":[";
		List<String> components = new ArrayList<>();
		for (Object obj : comps) components.add(IChatBaseComponentToString(obj));
		str += String.join(",",components);
		str += "],\"text\":\"\"}";
		return StringToIChatBaseComponent(str);
	}
	
	public static Object buildIChatBaseComponentStringExtra(Object ... components) {
		return buildIChatBaseComponentStringExtra(Arrays.asList(components));
	}
	
	public static String ChatColorsToIChatBaseComponent(String str) {
		try {
			Class<?> nmsCraftChatMessage = getClassCraftBukkit("util.CraftChatMessage");
			Method methodfromStringOrNull = nmsCraftChatMessage.getDeclaredMethod("fromStringOrNull",String.class);
			return IChatBaseComponentToString(methodfromStringOrNull.invoke(null,str));
		} catch (Exception e) {e.printStackTrace();}
		return null;
	}
	
	public static Object StringToIChatBaseComponent(Object obj) {
		try {
			Class<?> nmsIChatBaseComponent = getClassNMS("IChatBaseComponent","network.chat");
			return nmsIChatBaseComponent.cast(obj);
		} catch (Exception e1) {
			try {
				String str = (String) obj;
				if (str.trim().isEmpty()) return str;
				if (!str.startsWith("{") || !(str.toLowerCase().contains("\"text\":\"") || str.toLowerCase().contains("\"translate\":\"")))
					str = ChatColorsToIChatBaseComponent(str);
				Class<?> nmsChatSerializer = getClassNMS("IChatBaseComponent$ChatSerializer","network.chat");
				Method methodA = nmsChatSerializer.getDeclaredMethod("a",String.class);
				return methodA.invoke(null,str);
			} catch (Exception e2) {e2.printStackTrace();}
		}
		return null;
	}
	
	public static String IChatBaseComponentToString(Object obj) {
		try {
			String str = (String) obj;
			if (str.trim().isEmpty()) return str;
			if (str.startsWith("{") && (str.toLowerCase().contains("\"text\":\"") || str.toLowerCase().contains("\"translate\":\""))) return str;
			return ChatColorsToIChatBaseComponent(str);
		} catch (Exception e1) {
			try {
				Class<?> nmsIChatBaseComponent = getClassNMS("IChatBaseComponent","network.chat");
				Class<?> nmsChatSerializer = getClassNMS("IChatBaseComponent$ChatSerializer","network.chat");
				Method methodA = nmsChatSerializer.getDeclaredMethod("a",nmsIChatBaseComponent);
				return (String) methodA.invoke(null,obj);
			} catch (Exception e2) {e2.printStackTrace();}
		}
		return null;
	}
	
	public static int getNextEntityID() {
		int ID = -1;
		try {
			Class<?> entityClass = getClassNMS("Entity","world.entity");
			Field entityCount;
			try {
				entityCount = entityClass.getDeclaredField("entityCount");
			} catch (Exception e) {
				entityCount = entityClass.getDeclaredField("b");
			}
			entityCount.setAccessible(true);
			if (Utils.getVersionInt() < 14) {
				ID = (int) entityCount.get(null);
				entityCount.set(null,ID + 1);
			} else ID = (int) AtomicInteger.class.getDeclaredMethod("incrementAndGet").invoke(entityCount.get(null));
		} catch (Exception e) {e.printStackTrace();}
		return ID;
	}
}