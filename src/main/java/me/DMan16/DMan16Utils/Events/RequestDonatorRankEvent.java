package me.DMan16.DMan16Utils.Events;

import org.bukkit.event.Cancellable;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RequestDonatorRankEvent extends Event implements Cancellable {
	private final UUID ID;
	private boolean cancelled;
	private Integer rank;
	
	public RequestDonatorRankEvent(@NotNull UUID ID) {
		this.ID = ID;
		this.cancelled = false;
		this.rank = null;
	}
	
	@NotNull
	public UUID ID() {
		return ID;
	}
	
	@Nullable
	@NonNegative
	public Integer rank() {
		return rank;
	}
	
	@NotNull
	public RequestDonatorRankEvent rank(int rank) {
		if (rank >= 0) this.rank = rank;
		return this;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}