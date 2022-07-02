package me.DMan16.DMan16Utils.NMSWrappers;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;

public sealed class ComponentWrapper permits ComponentWrapper.Safe {
	/**
	 * !null -> safe to cast to {@link net.minecraft.network.chat.Component}!</>
	 */
	protected final Object component;
	
	public ComponentWrapper(@NotNull Object obj) {
		component = (obj instanceof net.minecraft.network.chat.Component) ? obj : null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.network.chat.Component}!</>
	 */
	@MonotonicNonNull
	public final Object component() {
		return component;
	}
	
	public final boolean isComponent() {
		return component != null;
	}
	
	public static final class Safe extends ComponentWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isComponent()) throw new IllegalArgumentException();
		}
	}
}