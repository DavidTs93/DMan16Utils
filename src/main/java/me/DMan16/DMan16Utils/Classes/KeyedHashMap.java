package me.DMan16.DMan16Utils.Classes;

import me.DMan16.DMan16Utils.Utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Keys are normalized using {@link Utils#fixKey(String)}
 * Any usage with a String that return null from {@link Utils#fixKey(String)} will result in a {@link NullPointerException}
 */
public class KeyedHashMap<V> extends HashMap<String,V> {
	@Override
	public V get(Object key) {
		try {
			return super.get(fixKey(key));
		} catch (Exception e) {}
		return null;
	}
	
	@Override
	public boolean containsKey(Object key) {
		try {
			return super.containsKey(fixKey(key));
		} catch (Exception e) {}
		return false;
	}
	
	@Override
	public V put(String key, V value) {
		return super.put(fixKey(key),value);
	}
	
	@Override
	public V remove(Object key) {
		return super.remove(fixKey(key));
	}
	
	@Override
	public V getOrDefault(Object key, V defaultValue) {
		return super.getOrDefault(fixKey(key),defaultValue);
	}
	
	@Override
	public V putIfAbsent(String key, V value) {
		return super.putIfAbsent(fixKey(key),value);
	}
	
	@Override
	public boolean remove(Object key, Object value) {
		return super.remove(fixKey(key),value);
	}
	
	@Override
	public boolean replace(String key, V oldValue, V newValue) {
		return super.replace(fixKey(key),oldValue,newValue);
	}
	
	@Override
	public V replace(String key, V value) {
		return super.replace(fixKey(key),value);
	}
	
	@Override
	public V computeIfAbsent(String key, @NotNull Function<? super String, ? extends V> mappingFunction) {
		return super.computeIfAbsent(fixKey(key),mappingFunction);
	}
	
	@Override
	public V computeIfPresent(String key, @NotNull BiFunction<? super String, ? super V, ? extends V> remappingFunction) {
		return super.computeIfPresent(fixKey(key),remappingFunction);
	}
	
	@Override
	public V compute(String key, @NotNull BiFunction<? super String, ? super V, ? extends V> remappingFunction) {
		return super.compute(fixKey(key),remappingFunction);
	}
	
	@Override
	public V merge(String key, @NotNull V value, @NotNull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		return super.merge(fixKey(key),value,remappingFunction);
	}
	
	@NotNull
	private static String fixKey(String key) {
		return Objects.requireNonNull(Utils.fixKey(key));
	}
	
	private static Object fixKey(Object obj) {
		return (obj instanceof String key) ? fixKey(key) : obj;
	}
}