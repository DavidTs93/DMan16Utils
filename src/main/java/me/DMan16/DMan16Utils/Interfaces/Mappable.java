package me.DMan16.DMan16Utils.Interfaces;

import me.DMan16.DMan16Utils.Utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface Mappable {
	@NotNull Map<@NotNull String,Object> toMap();
	
	@NotNull String mappableKey();
	
	@NotNull
	default String stringMappable() {
		return mappableKey() + ":" + Utils.getJSONString(toMap());
	}
}