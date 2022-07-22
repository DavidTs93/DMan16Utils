package me.DMan16.DMan16Utils.Interfaces;

import me.DMan16.DMan16Utils.Classes.Pairs.Pair;
import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Items.Enchantable;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public interface EnchantmentXPHolder {
	int BASE_XP_LEVEL = 10000;
	int MULT_XP_LEVEL = 2;
	int BOOKS_SAME_LEVEL_UP = 4;
	NamespacedKey ENCHANTMENTS_XP_KEY = new NamespacedKey(DMan16UtilsMain.getInstance(),"enchantments_xp");
	
	@Positive
	static int totalXPLevel(@NonNegative int level) {
		int amount = BASE_XP_LEVEL;
		for (int i = 1; i < level; i++) amount *= MULT_XP_LEVEL;
		return amount;
	}
	
	@Positive
	static int XPBook(@NonNegative int level) {
		return totalXPLevel(level) / BOOKS_SAME_LEVEL_UP;
	}
	
	/**
	 * @return 0 = no XP / max level reached
	 */
	@NonNegative
	static int getXPToNextLevel(@NotNull Enchantment enchantment,int level,int xp) {
		return level >= Enchantable.getMaxLevel(enchantment) ? 0 : Math.max(EnchantmentXPHolder.totalXPLevel(level) - xp,0);
	}
	
	/**
	 * Represents percentage until next level
	 */
	@Range(from = 0,to = 100)
	static float enchantmentProgress(@NonNegative int xp,@NonNegative int toNextLevel) {
		return toNextLevel == 0 ? 100f : Utils.roundAfterDot(Utils.divideAsFloat(xp,xp + toNextLevel) * 100,2);
	}
	
	@NotNull
	static Component progressBar(@NotNull Enchantment enchantment,@Range(from = 0,to = 100) float progress) {
		StringBuilder str = new StringBuilder("[");
		int i = 0;
		for (; i < 10 && progress >= 10; i++) {
			str.append('\u2503');
			progress -= 10;
		}
		if (progress >= 5 && i < 10) {
			str.append('\u257b');
			i++;
		}
		for (; i < 10; i++) str.append('\u25aa');
		return Component.text(str.append(']').toString());
	}
	
	/**
	 * @return null = doesn't have this enchantment,0 = no XP / max level reached
	 */
	@Nullable @NonNegative Integer getEnchantmentXP(@NotNull Enchantment enchantment);
	
	@Nullable @NonNegative Integer getXPToNextLevel(@NotNull Enchantment enchantment);
	
	/**
	 * Represents percentage until next level
	 * @return null = doesn't have this enchantment
	 */
	@Nullable
	@NonNegative
	@Range(from = 0,to = 100)
	Float enchantmentProgress(@NotNull Enchantment enchantment);
	
	/**
	 * @return null = doesn't have this enchantment,first = xp used (if 0 and xp isn't 0 then enchantment was at max level),second = did the enchantment level up
	 */
	@Nullable
	Pair<@NotNull Integer,@NotNull Boolean> addEnchantmentXP(@NotNull Enchantment enchantment,@NonNegative int xp);
	
	/**
	 * @return null = doesn't have this enchantment,first = xp used (if 0 then enchantment was at max level),second = did the enchantment level up
	 */
	@Nullable
	default Pair<@NotNull Integer,@NotNull Boolean> addEnchantmentBookXP(@NotNull Enchantment enchantment,@Positive int level) {
		return addEnchantmentXP(enchantment,XPBook(level));
	}
}