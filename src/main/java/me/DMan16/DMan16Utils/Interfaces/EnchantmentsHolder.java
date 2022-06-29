package me.DMan16.DMan16Utils.Interfaces;

import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

public interface EnchantmentsHolder {
	@NotNull @Unmodifiable Map<@NotNull Enchantment,@NotNull Integer> getEnchantments();
}