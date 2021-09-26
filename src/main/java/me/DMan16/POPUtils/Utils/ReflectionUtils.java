package me.DMan16.POPUtils.Utils;

import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
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

//@SuppressWarnings({"deprecation","ConstantConditions"})
@SuppressWarnings("deprecation")
public class ReflectionUtils {
//	public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
//	public static final Class<?> CLASS_CRAFT_ITEM_STACK = getClassCraftBukkit("inventory.CraftItemStack");
//	public static final Class<?> CLASS_ITEM_STACK_NMS = getClassNMS("ItemStack","world.item");
//	public static final Class<?> CLASS_ITEM_NMS = getClassNMS("Item","world.item");
//	public static final Class<?> CLASS_CRAFT_PLAYER = getClassCraftBukkit("entity.CraftPlayer");
//	public static final Class<?> CLASS_ENTITY_LIVING_NMS = getClassNMS("EntityLiving","world.entity");
//	public static final Class<?> CLASS_MOB_EFFECT_NMS = getClassNMS("MobEffect","world.effect");
//	public static final Class<?> CLASS_MOB_EFFECT_LIST_NMS = getClassNMS("MobEffectList","world.effect");
//	public static final Method AS_NMS_COPY = asNMSCopy();
//	public static final Method AS_CRAFT_MIRROR = asCraftMirror();
//
//	private static Method asNMSCopy() {
//		try {
//			return CLASS_CRAFT_ITEM_STACK.getDeclaredMethod("asNMSCopy",ItemStack.class);
//		} catch (Exception e) {}
//		return null;
//	}
//
//	private static Method asCraftMirror() {
//		try {
//			return CLASS_CRAFT_ITEM_STACK.getDeclaredMethod("asCraftMirror",CLASS_ITEM_STACK_NMS);
//		} catch (Exception e) {}
//		return null;
//	}
//
//	@NotNull
//	private static String removeUnnecessaryDots(@NotNull String str) {
//		return Arrays.stream(str.split("\\.")).filter(s -> !s.isEmpty()).collect(Collectors.joining("."));
//	}
//
//	public static Class<?> getClassNMS(@NotNull String name, @NotNull String subPackageNameNewNMS) {
//		try {
//			return Class.forName(removeUnnecessaryDots("net.minecraft." + (Utils.getVersionInt() >= 17 ? subPackageNameNewNMS : "server." + VERSION) + "." + name));
//		} catch (Exception e) {}
//		return null;
//	}
//
//	public static Class<?> getClassCraftBukkit(@NotNull String name) {
//		try {
//			return Class.forName(removeUnnecessaryDots("org.bukkit.craftbukkit." + VERSION + "." + name));
//		} catch (Exception e) {}
//		return null;
//	}
//
//	/**
//	 * @param item Bukkit ItemStack
//	 * @return item's material's translatable name, example: Material.APPLE -> "item.minecraft.apple"
//	 */
//	public static String ItemTranslatableName(@NotNull ItemStack item) {
//
//		try {
//			CraftItemStack.asNMSCopy(item).getName()
//			Method getItem = CLASS_ITEM_STACK_NMS.getDeclaredMethod("getItem");
//			Method getName = CLASS_ITEM_NMS.getDeclaredMethod("getName");
//			return (String) getName.invoke(getItem.invoke(ItemAsNMSCopy(item)));
//		} catch (Exception e) {}
//		return null;
//	}
	
	@NotNull
	public static net.minecraft.world.item.ItemStack ItemAsNMSCopy(@NotNull ItemStack item) {
		 return CraftUtils.asNMSCopy(item);
	}
	
	@NotNull
	public static ItemStack ItemAsBukkitCopy(@NotNull net.minecraft.world.item.ItemStack item) {
		return CraftUtils.asCraftMirror(item);
	}
	
	@NotNull
	public static ItemStack CloneWithNBT(@NotNull ItemStack item) {
		return CraftUtils.asCraftCopy(item);
	}
	
	public static EntityPlayer getHandle(@NotNull Player player) {
		 return CraftUtils.toCraft(player).getHandle();
	}
	
	public static EntityHuman getHandle(@NotNull HumanEntity human) {
		return CraftUtils.toCraft(human).getHandle();
	}
	
	public static EntityLiving getHandle(@NotNull LivingEntity entity) {
		return CraftUtils.toCraft(entity).getHandle();
	}
	
	public static WorldServer getHandle(@NotNull World world) {
		return CraftUtils.toCraft(world).getHandle();
	}
	
	public static net.minecraft.world.entity.Entity getHandle(@NotNull Entity entity) {
		return CraftUtils.toCraft(entity).getHandle();
	}
	
	public static MobEffectList MobEffectFromID(int ID) {
		return MobEffectList.fromId(ID);
	}
	
	public static boolean[] AddEffects(@NotNull Player player, @NotNull Cause cause, @NotNull List<PotionEffect> effects) {
		if (effects.isEmpty()) return null;
		boolean[] result = new boolean[effects.size()];
		Arrays.fill(result,false);
		for (int i = 0; i < effects.size(); i++) {
			PotionEffect effect = effects.get(i);
			MobEffectList mobEffectList = MobEffectFromID(effect.getType().getId());
			if (mobEffectList == null) continue;
			MobEffect mobEffect = new MobEffect(mobEffectList, effect.getDuration() * 20, effect.getAmplifier(), effect.isAmbient(), effect.hasParticles(), effect.hasIcon());
			result[i] = getHandle(player).addEffect(mobEffect,cause);
		}
		return result;
	}
	
	public static boolean[] AddEffects(@NotNull Player player, @NotNull Cause cause, @NotNull PotionEffect ... effects) {
		return AddEffects(player,cause,Arrays.asList(effects));
	}
	
	public static boolean[] RemoveEffects(@NotNull Player player, @NotNull Cause cause, @NotNull List<PotionEffectType> effects) {
		if (effects.isEmpty()) return null;
		boolean[] result = new boolean[effects.size()];
		Arrays.fill(result,false);
		for (int i = 0; i < effects.size(); i++) {
			PotionEffectType effect = effects.get(i);
			MobEffectList mobEffectList = MobEffectFromID(effect.getId());
			if (mobEffectList != null) result[i] = getHandle(player).removeEffect(mobEffectList);
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

//	public static String buildIChatBaseComponentString(String text, boolean translate, boolean italic, @Nullable String color, boolean bold, Object ... with) {
//		if (text == null) return null;
//		String str = "{\"";
//		str += (translate ? "translate" : "text");
//		str += "\":\"" + text + "\"";
//		str += ",\"italic\":" + italic;
//		if (color != null && !color.isEmpty()) str += ",\"color\":\"" + color + "\"";
//		if (translate && with.length > 0) {
//			List<String> extras = new ArrayList<>();
//			for (Object obj : with) {
//				String extra = IChatBaseComponentToString(obj);
//				if (extra != null) extras.add(extra);
//			}
//			if (!extras.isEmpty()) {
//				str += ",\"with\":[";
//				str += String.join(",",extras);
//				str += "]";
//			}
//		}
//		str += "}";
//		return str;
//	}
//
//	public static String buildIChatBaseComponentString(String text, boolean translate, @Nullable String color) {
//		return buildIChatBaseComponentString(text,translate,false,color,false);
//	}
//
//	public static Object buildIChatBaseComponentStringExtra(List<Object> comps) {
//		if (comps == null || comps.isEmpty()) return null;
//		String str = "{\"extra\":[";
//		List<String> components = new ArrayList<>();
//		for (Object obj : comps) components.add(IChatBaseComponentToString(obj));
//		str += String.join(",",components);
//		str += "],\"text\":\"\"}";
//		return StringToIChatBaseComponent(str);
//	}
//
//	public static Object buildIChatBaseComponentStringExtra(Object ... components) {
//		return buildIChatBaseComponentStringExtra(Arrays.asList(components));
//	}
//
//	public static String ChatColorsToIChatBaseComponent(String str) {
//		try {
//			Class<?> nmsCraftChatMessage = getClassCraftBukkit("util.CraftChatMessage");
//			Method methodfromStringOrNull = nmsCraftChatMessage.getDeclaredMethod("fromStringOrNull",String.class);
//			return IChatBaseComponentToString(methodfromStringOrNull.invoke(null,str));
//		} catch (Exception e) {e.printStackTrace();}
//		return null;
//	}
//
//	public static Object StringToIChatBaseComponent(Object obj) {
//		try {
//			Class<?> nmsIChatBaseComponent = getClassNMS("IChatBaseComponent","network.chat");
//			return nmsIChatBaseComponent.cast(obj);
//		} catch (Exception e1) {
//			try {
//				String str = (String) obj;
//				if (str.trim().isEmpty()) return str;
//				if (!str.startsWith("{") || !(str.toLowerCase().contains("\"text\":\"") || str.toLowerCase().contains("\"translate\":\"")))
//					str = ChatColorsToIChatBaseComponent(str);
//				Class<?> nmsChatSerializer = getClassNMS("IChatBaseComponent$ChatSerializer","network.chat");
//				Method methodA = nmsChatSerializer.getDeclaredMethod("a",String.class);
//				return methodA.invoke(null,str);
//			} catch (Exception e2) {e2.printStackTrace();}
//		}
//		return null;
//	}
//
//	public static String IChatBaseComponentToString(Object obj) {
//		try {
//			String str = (String) obj;
//			if (str.trim().isEmpty()) return str;
//			if (str.startsWith("{") && (str.toLowerCase().contains("\"text\":\"") || str.toLowerCase().contains("\"translate\":\""))) return str;
//			return ChatColorsToIChatBaseComponent(str);
//		} catch (Exception e1) {
//			try {
//				Class<?> nmsIChatBaseComponent = getClassNMS("IChatBaseComponent","network.chat");
//				Class<?> nmsChatSerializer = getClassNMS("IChatBaseComponent$ChatSerializer","network.chat");
//				Method methodA = nmsChatSerializer.getDeclaredMethod("a",nmsIChatBaseComponent);
//				return (String) methodA.invoke(null,obj);
//			} catch (Exception e2) {e2.printStackTrace();}
//		}
//		return null;
//	}
}