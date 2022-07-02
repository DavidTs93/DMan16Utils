package me.DMan16.DMan16Utils.Utils;

import me.DMan16.DMan16Utils.NMSWrappers.*;
import me.DMan16.DMan16Utils.NMSWrappers.Entities.*;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.nbt.Tag;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

public class ReflectionUtils {
	public static final @NotNull Field CRITERIA = Objects.requireNonNull(getCriteria());
	public static final @NotNull Field GET_PLAYER_PREDICATE_ABSTRACT_CRITERION_TRIGGER_INSTANCE = Objects.requireNonNull(getPlayerPredicate());
	
	@Nullable
	private static Field getCriteria() {
		for (Field field : net.minecraft.advancements.Advancement.class.getDeclaredFields()) if (Map.class.isAssignableFrom(field.getType())) return field;
		return null;
	}
	
	@Nullable
	private static Field getPlayerPredicate() {
		for (Field field : AbstractCriterionTriggerInstance.class.getDeclaredFields()) if (EntityPredicate.Composite.class.isAssignableFrom(field.getType())) return field;
		return null;
	}
	
	@NotNull
	public static ItemStackWrapper.Safe asNMSCopy(@NotNull ItemStack item) {
		 return new ItemStackWrapper.Safe(CraftUtils.asNMSCopy().apply(item));
	}
	
	@NotNull
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
	
	public static boolean[] addEffects(@NotNull LivingEntity entity,@NotNull Cause cause,@NotNull List<PotionEffect> effects) {
		if (effects.isEmpty()) return new boolean[0];
		boolean[] result = new boolean[effects.size()];
		for (int i = 0; i < effects.size(); i++) {
			PotionEffect effect = effects.get(i);
			net.minecraft.world.effect.MobEffect mobEffect = (net.minecraft.world.effect.MobEffect) MobEffectWrapper.fromPotionEffectType(effect.getType()).mobEffect();
			net.minecraft.world.effect.MobEffectInstance mobEffectInstance = new net.minecraft.world.effect.MobEffectInstance(mobEffect,effect.getDuration(),effect.getAmplifier(),effect.isAmbient(),effect.hasParticles(),effect.hasIcon());
			result[i] = ((net.minecraft.world.entity.LivingEntity) getHandle(entity).livingEntity()).addEffect(mobEffectInstance,cause);
		}
		return result;
	}
	
	public static boolean[] addEffects(@NotNull LivingEntity entity,@NotNull Cause cause,@NotNull PotionEffect ... effects) {
		return addEffects(entity,cause,Arrays.asList(effects));
	}
	
	public static boolean[] removeEffects(@NotNull Player player,@NotNull Cause cause,@NotNull List<PotionEffectType> effects) {
		if (effects.isEmpty()) return new boolean[0];
		boolean[] result = new boolean[effects.size()];
		for (int i = 0; i < effects.size(); i++) {
			PotionEffectType effect = effects.get(i);
			net.minecraft.world.effect.MobEffect mobEffect = (net.minecraft.world.effect.MobEffect) MobEffectWrapper.fromPotionEffectType(effect).mobEffect();
			result[i] = ((net.minecraft.server.level.ServerPlayer) getHandle(player).player()).removeEffect(mobEffect,cause);
		}
		return result;
	}
	
	public static boolean[] removeEffects(@NotNull Player player,@NotNull Cause cause,@NotNull PotionEffectType ... effects) {
		return removeEffects(player,cause,Arrays.asList(effects));
	}
	
	@NotNull
	@Unmodifiable
	public static <V extends Class<?>> List<@NotNull Field> getFields(@NotNull Class<?> clazz,V type,boolean exact) {
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
	public static <V> Map<String,V> getStaticFields(@NotNull Class<?> clazz,@NotNull Class<V> type,boolean exact) {
		Map<String,V> fields = new HashMap<>();
		for (Field field : getFields(clazz,type,exact)) {
			field.setAccessible(true);
			if (Modifier.isStatic(field.getModifiers())) try {
				fields.put(field.getName(),type.cast(field.get(null)));
			} catch (Exception e) {e.printStackTrace();}
		}
		return fields;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean alterAdvancementCriterion(@NotNull Advancement advancement,@NotNull Function<@NotNull LinkedHashMap<@NotNull String,CriterionWrapper.@NotNull Safe>,@Nullable Map<@NotNull String,CriterionWrapper.@NotNull Safe>> alter) {
		try {
			CRITERIA.setAccessible(true);
			net.minecraft.advancements.Advancement advancementNMS = CraftUtils.toCraft(advancement).getHandle();
			Map<String,Criterion> criteria = (Map<String,Criterion>) CRITERIA.get(advancementNMS);
			Map<String,CriterionWrapper.Safe> map = new LinkedHashMap<>();
			for (Map.Entry<String,Criterion> entry : criteria.entrySet()) map.put(entry.getKey(),new CriterionWrapper.Safe(entry.getValue()));
			map = alter.apply((LinkedHashMap<String,CriterionWrapper.Safe>) map);
			if (map == null) return false;
			criteria = new LinkedHashMap<>();
			for (Map.Entry<String,CriterionWrapper.Safe> entry : map.entrySet()) if (entry.getValue().criterion() instanceof Criterion criterion) criteria.put(entry.getKey(),criterion);
			CRITERIA.set(advancementNMS,Collections.unmodifiableMap(criteria));
			return true;
		} catch (Exception e) {}
		return false;
	}
	
	@NotNull
	@Contract(value = "_,_,_ -> new",pure = true)
	public static ItemStack addNBTTag(@NotNull ItemStack item,@NotNull String name,TagWrapper.@NotNull Safe tagWrapper) {
		return CraftUtils.asCraftMirror(new ItemStackWrapper.Safe(Utils.runGetOriginal((net.minecraft.world.item.ItemStack) asNMSCopy(item).item(),i -> i.addTagElement(name,(Tag) tagWrapper.tag()))));
	}
	
	@NotNull
	@Contract(value = "_,_,_ -> new",pure = true)
	public static ItemStack addNBTTag(@NotNull ItemStack item,@NotNull String name,TagWrapper.CompoundTagWrapper.@NotNull Safe tagWrapper) {
		return addNBTTag(item,name,new TagWrapper.Safe(tagWrapper.tag()));
	}
}