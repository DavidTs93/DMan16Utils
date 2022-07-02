package me.DMan16.DMan16Utils.NMSWrappers;

import me.DMan16.DMan16Utils.Utils.Utils;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public sealed class CriterionWrapper permits CriterionWrapper.Safe {
	/**
	 * !null -> safe to cast to {@link net.minecraft.advancements.Criterion}!</>
	 */
	protected final Object criterion;
	
	public CriterionWrapper(@NotNull Object obj) {
		criterion = (obj instanceof net.minecraft.advancements.Criterion) ? obj : null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.advancements.Criterion}!</>
	 */
	@MonotonicNonNull
	public final Object criterion() {
		return criterion;
	}
	
	@Nullable
	public final CriterionTriggerInstanceWrapper.Safe criterionTriggerInstance() {
		return (this.criterion instanceof net.minecraft.advancements.Criterion criterion) ? Utils.applyNotNull(criterion.getTrigger(),CriterionTriggerInstanceWrapper.Safe::new) : null;
	}
	
	public final boolean isCriterion() {
		return criterion != null;
	}
	
	public static final class Safe extends CriterionWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isCriterion()) throw new IllegalArgumentException();
		}
	}
}