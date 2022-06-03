package me.DMan16.DMan16Utils.NMSWrappers;

import org.jetbrains.annotations.NotNull;

public sealed class ItemWrapper permits ItemWrapper.Safe {
	/**
	 * !null -> safe to cast to {@link net.minecraft.world.item.Item}!</>
	 */
	protected final Object item;
	
	public ItemWrapper(@NotNull Object obj) {
		item = (obj instanceof net.minecraft.world.item.Item) ? obj : null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.world.item.Item}!</>
	 */
	public Object item() {
		return item;
	}
	
	public boolean isItem() {
		return item != null;
	}
	
	public static final class Safe extends ItemWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isItem()) throw new IllegalArgumentException();
		}
	}
}