package me.DMan16.POPUtils.Classes;

import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class DefaultCustomEnchantment extends CustomEnchantment {
	protected DefaultCustomEnchantment(@NotNull String key,@Nullable Collection<@NotNull NamespacedKey> conflicts) {
		super(key,conflicts);
	}
	
	protected DefaultCustomEnchantment(@NotNull String key,@NotNull NamespacedKey ... conflicts) {
		super(key,conflicts);
	}
	
	public boolean includes(Material material) {
		 return !Utils.isNull(material) && getItemTarget().includes(material);
	}
}