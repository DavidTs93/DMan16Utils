package me.DMan16.DMan16Utils.Interfaces;

import me.DMan16.DMan16Utils.Classes.AttributesInfo;
import me.DMan16.DMan16Utils.Classes.Engraving;
import me.DMan16.DMan16Utils.Items.Enchantable;
import me.DMan16.DMan16Utils.Items.Socketable;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;

public abstract class SocketableReviveable<V extends Socketable<V> & ReviveableItemable<V>> extends Socketable<V> implements ReviveableItemable<V> {
	public static final int DURABILITY_JUMP = 20;
	
	protected SocketableReviveable(@NotNull AttributesInfo info,boolean isDefault) {
		super(info,isDefault);
	}
	
	@Positive
	public int totalScore() {
		int score = 1 + extraScore() + attributesScore() + engravingScore() + socketsScore() + enchantmentsScore();
		if (shouldBreak()) score = Utils.round(score * brokenScoreMultiplier());
		return Math.max(score,1);
	}
	
	protected int extraScore() {
		return Utils.round(Utils.divideAsFloat(maxDurability(),DURABILITY_JUMP) - 0.25f);
	}
	
	protected int attributesScore() {
		return info.score();
	}
	
	protected int engravingScore() {
		return getEngraving() != null ? 4 + Utils.thisOrThatOrNull(Engraving.getEngravingExtraScore(this),0) * 2 : 0;
	}
	
	protected int socketsScore() {
		return 2 * (maxEnchantments() + (maxEnchantments() == maxPossibleSockets() ? 1 : 0));
	}
	
	protected int enchantmentsScore() {
		return enchantments.entrySet().stream().map(entry -> entry.getKey().isCursed() ? -2 * entry.getValue() : 2 * (entry.getValue() + (entry.getValue() >= Enchantable.getMaxLevel(entry.getKey()) ? 1 : 0))).reduce(0,Integer::sum) + (enchantments.size() == maxEnchantments() ? 2 : 0);
	}
	
	protected float brokenScoreMultiplier() {
		return 0.75f;
	}
}