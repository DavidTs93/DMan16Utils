package me.DMan16.POPUtils.Items;

import me.DMan16.POPUtils.Interfaces.Itemable;
import me.DMan16.POPUtils.Utils.Utils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class ItemUtils {
	private static final HashMap<@NotNull String,@NotNull Class<? extends Itemable<?>>> KEY_MAP = new HashMap<>();
	private static final HashMap<@NotNull Class<? extends Itemable<?>>,@NotNull Function<Map<String,?>,? extends Itemable<?>>> CLASS_MAP = new HashMap<>();
	
	@Nullable
	public static Itemable<?> of(@NotNull String key, @Nullable Map<String,?> arguments) {
		try {
			return Objects.requireNonNull(getInfo(key)).apply(arguments);
		} catch (Exception e) {}
		return null;
	}
	
	@Nullable
	@Contract("null -> null")
	public static Itemable<?> of(@Nullable String str) {
		if (str == null || str.isEmpty()) return null;
		String key = null, arguments = null;
		if (str.contains(":")) {
			String[] arr = str.split(":",2);
			if (arr.length == 2) {
				key = arr[0];
				arguments = arr[1].isEmpty() ? null : arr[1];
			}
		} else key = str;
		if (key != null) try {
			return of(key,Utils.getMapFromJSON(arguments));
		} catch (Exception e) {}
		return null;
	}
	
	@Nullable
	private static Function<Map<String,?>,? extends Itemable<?>> getInfo(@NotNull String key) {
		Class<? extends Itemable<?>> clazz = KEY_MAP.get(key.toLowerCase());
		return clazz == null ? null : CLASS_MAP.get(clazz);
	}
	
	@Nullable
	private static Function<Map<String,?>,? extends Itemable<?>> getInfo(@NotNull Class<? extends Itemable<?>> clazz) {
		return CLASS_MAP.get(clazz);
	}
	
	@Nullable
	@SuppressWarnings("unchecked")
	public static <V extends Itemable<?>> V of(@NotNull Class<V> clazz, @Nullable Map<String,?> arguments) {
		try {
			return (V) Objects.requireNonNull(getInfo(clazz)).apply(arguments);
		} catch (Exception e) {}
		return null;
	}
	
	public static <V extends Itemable<?>> boolean registerItemable(@NotNull String key, Class<V> clazz, @NotNull Function<Map<String,?>,@Nullable V> fromArguments) {
		key = key.toLowerCase();
		if (KEY_MAP.containsKey(key) || CLASS_MAP.containsKey(clazz)) return false;
		KEY_MAP.put(key.toLowerCase(),clazz);
		CLASS_MAP.put(clazz,fromArguments);
		return true;
	}
}