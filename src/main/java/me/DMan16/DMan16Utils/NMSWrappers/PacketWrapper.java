package me.DMan16.DMan16Utils.NMSWrappers;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;

public sealed class PacketWrapper permits PacketWrapper.Safe {
	/**
	 * !null -> safe to cast to {@link net.minecraft.network.protocol.Packet}!</>
	 */
	protected final Object packet;
	
	public PacketWrapper(@NotNull Object obj) {
		packet = (obj instanceof net.minecraft.network.protocol.Packet<?>) ? obj : null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.network.protocol.Packet}!</>
	 */
	@MonotonicNonNull
	public final Object packet() {
		return packet;
	}
	
	public final boolean isPacket() {
		return packet != null;
	}
	
	public static final class Safe extends PacketWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isPacket()) throw new IllegalArgumentException();
		}
	}
}