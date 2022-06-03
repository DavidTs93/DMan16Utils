package me.DMan16.DMan16Utils.NMSWrappers;

import org.jetbrains.annotations.NotNull;

public sealed class ItemStackWrapper permits ItemStackWrapper.Safe {
	/**
	 * !null -> safe to cast to {@link net.minecraft.world.item.ItemStack}!</>
	 */
	protected final Object item;
	
	public ItemStackWrapper(@NotNull Object obj) {
		item = (obj instanceof net.minecraft.world.item.ItemStack) ? obj : null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.world.item.ItemStack}!</>
	 */
	public Object item() {
		return item;
	}
	
	public boolean isItem() {
		return item != null;
	}
	
	public static final class Safe extends ItemStackWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isItem()) throw new IllegalArgumentException();
		}
	}
}