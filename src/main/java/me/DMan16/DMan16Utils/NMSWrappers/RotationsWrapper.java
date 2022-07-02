package me.DMan16.DMan16Utils.NMSWrappers;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;

public sealed class RotationsWrapper permits RotationsWrapper.Safe {
	/**
	 * !null -> safe to cast to {@link net.minecraft.core.Rotations}!</>
	 */
	protected final Object rotations;
	
	public RotationsWrapper(@NotNull Object obj) {
		rotations = (obj instanceof net.minecraft.core.Rotations) ? obj : null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.core.Rotations}!</>
	 */
	@MonotonicNonNull
	public final Object rotations() {
		return rotations;
	}
	
	public final boolean isRotations() {
		return rotations != null;
	}
	
	public static final class Safe extends RotationsWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isRotations()) throw new IllegalArgumentException();
		}
	}
}