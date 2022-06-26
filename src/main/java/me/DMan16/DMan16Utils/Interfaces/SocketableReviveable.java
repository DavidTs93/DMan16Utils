package me.DMan16.DMan16Utils.Interfaces;

import me.DMan16.DMan16Utils.Classes.AttributesInfo;
import me.DMan16.DMan16Utils.Classes.Engraving;
import me.DMan16.DMan16Utils.Items.Enchantable;
import me.DMan16.DMan16Utils.Items.Socketable;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.enchantments.Enchantment;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class SocketableReviveable<V extends Socketable<V> & Reviveable> extends Socketable<V> implements Reviveable {
	protected SocketableReviveable(@NotNull AttributesInfo info,boolean isDefault) {
		super(info,isDefault);
	}
	
	@Positive
	public int totalScore() {
		int score = 1 + extraScore() + attributesScore();
		if (getEngraving() != null) score += 4;
		if (maxPossibleSockets() == maxEnchantments()) score += 4;
		score += Utils.thisOrThatOrNull(Engraving.getEngravingExtraScore(this),0) * 2;
		for (Map.Entry<Enchantment,Integer> entry : enchantments.entrySet()) {
			if (entry.getKey().isCursed()) score -= 2 * entry.getValue();
			else {
				score += 2;
				if (Enchantable.getMaxLevel(entry.getKey()) <= entry.getValue()) score += entry.getValue() - Enchantable.getMaxLevel(entry.getKey()) + 1;
			}
		}
		return Math.max(score,1);
	}
	
	protected int extraScore() {
		return 0;
	}
	
	protected int attributesScore() {
		return info.score();
	}
}