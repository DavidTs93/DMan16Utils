package me.DMan16.DMan16Utils.NMSWrappers;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.CompoundTag;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Objects;

public sealed class ItemPredicateWrapper permits ItemPredicateWrapper.Safe {
	private static final @NotNull Field NBT_PREDICATE = Objects.requireNonNull(getNBTPredicate());
	
	@Nullable
	private static Field getNBTPredicate() {
		for (Field field : ItemPredicate.class.getDeclaredFields()) if (NbtPredicate.class.isAssignableFrom(field.getType())) return field;
		return null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.advancements.critereon.ItemPredicate}!</>
	 */
	protected final Object predicate;
	
	public ItemPredicateWrapper(@NotNull Object obj) {
		predicate = (obj instanceof net.minecraft.advancements.critereon.ItemPredicate) ? obj : null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.advancements.critereon.ItemPredicate}!</>
	 */
	@MonotonicNonNull
	public final Object instance() {
		return predicate;
	}
	
	public final boolean isPredicate() {
		return predicate != null;
	}
	
	public final boolean setNBTPredicate(TagWrapper.CompoundTagWrapper.@NotNull Safe predicateNBT) {
		if (!(this.predicate instanceof ItemPredicate predicate) || !(predicateNBT.tag instanceof CompoundTag tag)) return false;
		try {
			NBT_PREDICATE.setAccessible(true);
			NBT_PREDICATE.set(predicate,new NbtPredicate(tag));
			return true;
		} catch (Exception e) {e.printStackTrace();}
		return false;
	}
	
	public static final class Safe extends ItemPredicateWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isPredicate()) throw new IllegalArgumentException();
		}
	}
}