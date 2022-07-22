package me.DMan16.DMan16Utils.Events;

import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RequestObjectsEvent<V,T> extends Event implements Cancellable {
	private final @NotNull List<@NotNull T> objects;
	public final @NotNull Class<@NotNull V> clazz;
	private boolean cancelled;
	
	public RequestObjectsEvent(@NotNull Class<@NotNull V> clazz) {
		this.objects = new ArrayList<>();
		this.clazz = clazz;
		this.cancelled = false;
	}
	
	@NotNull
	@Unmodifiable
	public final List<@NotNull T> objects() {
		return Collections.unmodifiableList(objects);
	}
	
	@NotNull
	public final RequestObjectsEvent<V,T> addObjects(@NotNull List<@NotNull T> objects) {
		this.objects.addAll(objects);
		return this;
	}
	
	@NotNull
	@SafeVarargs
	public final RequestObjectsEvent<V,T> addObjects(@NotNull T ... objects) {
		return addObjects(Arrays.asList(objects));
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}