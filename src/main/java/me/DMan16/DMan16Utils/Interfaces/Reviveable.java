package me.DMan16.DMan16Utils.Interfaces;

import me.DMan16.DMan16Utils.Classes.Pairs.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.TreeMap;

public interface Reviveable<V extends Reviveable<V>> extends Scoreable {
	@NotNull
	default Pair<@NotNull Integer,@NotNull Integer> reviveCost() {
		int upgrade = 0,amount = score();
		float divide = 8;
		while (amount >= divide) {
			amount = Math.round(amount / divide);
			upgrade++;
		}
		return Pair.of(upgrade,amount);
	}
	
	@NotNull
	default TreeMap<@NotNull Integer,@NotNull Integer> reviveCostMap() {
		int upgrade = 0,amount = score(),divide = 8,left;
		TreeMap<@NotNull Integer,@NotNull Integer> map = new TreeMap<>();
		if (amount < divide) map.put(0,amount);
		else while (amount >= divide) {
			left = amount % divide;
			if (left > 0) map.put(upgrade,left);
			amount /= divide;
			upgrade++;
		}
		return map;
	}
}