package me.DMan16.DMan16Utils.NMSWrappers;

import me.DMan16.DMan16Utils.Utils.Utils;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Predicate;

public sealed class CriterionTriggerInstanceWrapper permits CriterionTriggerInstanceWrapper.Safe {
	public static final @NotNull Field ABSTRACT_CRITERION_TRIGGER_INSTANCE_COMPOSITE = Objects.requireNonNull(getComposite());
	public static final @NotNull Field COMPOSITE_PREDICATES = Objects.requireNonNull(getCompositePredicates());
	
	@Nullable
	private static Field getComposite() {
		for (Field field : AbstractCriterionTriggerInstance.class.getDeclaredFields()) if (EntityPredicate.Composite.class.isAssignableFrom(field.getType())) return field;
		return null;
	}
	
	@Nullable
	private static Field getCompositePredicates() {
		for (Field field : EntityPredicate.Composite.class.getDeclaredFields()) if (Predicate.class.isAssignableFrom(field.getType())) return field;
		return null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.advancements.CriterionTriggerInstance}!</>
	 */
	protected final Object instance;
	
	public CriterionTriggerInstanceWrapper(@NotNull Object obj) {
		instance = (obj instanceof net.minecraft.advancements.CriterionTriggerInstance) ? obj : null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.advancements.CriterionTriggerInstance}!</>
	 */
	@MonotonicNonNull
	public final Object instance() {
		return instance;
	}
	
	public final boolean isInstance() {
		return instance != null;
	}
	
	@Nullable
	public final Field getItemPredicateField() {
		if (this.instance instanceof net.minecraft.advancements.CriterionTriggerInstance instance) try {
			for (Field field : instance.getClass().getDeclaredFields()) if (ItemPredicate.class.isAssignableFrom(field.getType()) || (field.getType().isArray() && ItemPredicate.class.isAssignableFrom(field.getType().getComponentType()))) return field;
		} catch (Exception e) {e.printStackTrace();}
		return null;
	}
	
	public final boolean applyItemPredicateNBTTag(TagWrapper.CompoundTagWrapper.@NotNull Safe tag) {
		Field field;
		if (!(this.instance instanceof net.minecraft.advancements.CriterionTriggerInstance instance) || (field = getItemPredicateField()) == null) return false;
		try {
			field.setAccessible(true);
			if (!field.getType().isArray()) Utils.applyNotNull(field.get(instance),predicate -> new ItemPredicateWrapper(predicate).setNBTPredicate(tag));
			else for (ItemPredicate predicate : (ItemPredicate[]) field.get(instance)) if (predicate != null) new ItemPredicateWrapper(predicate).setNBTPredicate(tag);
			return true;
		} catch (Exception e) {e.printStackTrace();}
		return false;
	}
	
	public static final class Safe extends CriterionTriggerInstanceWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isInstance()) throw new IllegalArgumentException();
		}
	}
}