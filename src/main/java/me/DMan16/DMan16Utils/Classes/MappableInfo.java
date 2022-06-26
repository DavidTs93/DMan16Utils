package me.DMan16.DMan16Utils.Classes;

import me.DMan16.DMan16Utils.Interfaces.Mappable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public class MappableInfo<V extends Mappable,T> {
	private final Class<V> clazz;
	private final Function<Map<String,?>,@Nullable V> fromArguments;
	private final Function<@Nullable T,@Nullable V> fromObject;
	
	public MappableInfo(@NotNull Class<V> clazz,@NotNull Function<@Nullable Map<String,?>,@Nullable V> fromArguments,@Nullable Function<@NotNull T,@Nullable V> fromObject) {
		this.clazz = clazz;
		this.fromArguments = fromArguments;
		this.fromObject = fromObject;
	}
	
	@NotNull
	public Class<V> getMappableClass() {
		return clazz;
	}
	
	@Nullable
	public V fromArguments(@Nullable Map<String,?> arguments) {
		return arguments == null ? null : fromArguments.apply(arguments);
	}
	
	@Nullable
	public V fromObject(@NotNull T obj) {
		return fromObject == null ? null : fromObject.apply(obj);
	}
}