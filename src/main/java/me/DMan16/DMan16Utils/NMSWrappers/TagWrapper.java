package me.DMan16.DMan16Utils.NMSWrappers;

import me.DMan16.DMan16Utils.Utils.Utils;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.Material;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;

public sealed class TagWrapper permits TagWrapper.Safe,TagWrapper.CompoundTagWrapper {
	/**
	 * !null -> safe to cast to {@link net.minecraft.nbt.Tag}!</>
	 */
	protected final Object tag;
	
	public TagWrapper(@NotNull Object obj) {
		tag = (obj instanceof net.minecraft.nbt.Tag) ? obj : null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.nbt.Tag}!</>
	 */
	@MonotonicNonNull
	public final Object tag() {
		return tag;
	}
	
	public final boolean isTag() {
		return tag != null;
	}
	
	public static final class Safe extends TagWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isTag()) throw new IllegalArgumentException();
		}
	}
	
	public sealed static class CompoundTagWrapper extends TagWrapper permits CompoundTagWrapper.Safe {
		public CompoundTagWrapper(@NotNull Object obj) {
			super((obj instanceof net.minecraft.nbt.CompoundTag) ? obj : Material.AIR);
		}
		
		public static final class Safe extends CompoundTagWrapper {
			public Safe(@NotNull Object obj) {
				super(obj);
				if (!isTag()) throw new IllegalArgumentException();
			}
			
			@NotNull
			public static CompoundTagWrapper.Safe originalCompoundTag() {
				return new CompoundTagWrapper.Safe(Utils.runGetOriginal(new CompoundTag(),tag -> tag.put("Original",ByteTag.ONE)));
			}
		}
	}
}