package me.DMan16.DMan16Utils.NMSWrappers;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;

public sealed class SynchedEntityDataWrapper permits SynchedEntityDataWrapper.Safe {
	/**
	 * !null -> safe to cast to {@link net.minecraft.network.syncher.SynchedEntityData}!</>
	 */
	protected final Object data;
	
	public SynchedEntityDataWrapper(@NotNull Object obj) {
		data = (obj instanceof net.minecraft.network.syncher.SynchedEntityData) ? obj : null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.network.syncher.SynchedEntityData}!</>
	 */
	@MonotonicNonNull
	public final Object data() {
		return data;
	}
	
	public final boolean isData() {
		return data != null;
	}
	
	public static final class Safe extends SynchedEntityDataWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isData()) throw new IllegalArgumentException();
		}
	}
}
