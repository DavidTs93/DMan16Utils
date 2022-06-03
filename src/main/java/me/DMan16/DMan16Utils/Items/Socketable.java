package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.Classes.AttributesInfo;
import me.DMan16.DMan16Utils.Classes.Engraving;
import me.DMan16.DMan16Utils.Classes.Pair;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.enchantments.Enchantment;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Socketable<V extends Socketable<V>> extends Enchantable<V> {
	private static final Component EMPTY_LINE = Utils.noItalic(Component.translatable("menu.socket_empty",NamedTextColor.AQUA).append(Component.translatable("menu.empty_socket",NamedTextColor.GRAY)));
	
	protected Socketable(@NotNull AttributesInfo info,boolean isDefault) {
		super(info,isDefault);
	}
	
	@Positive
	public int maxPossibleSockets() {
		return 5;
	}
	
	@Positive
	public int itemScore() {
		int score = 1 + extraScore() + attributesScore();
		if (getEngraving() != null) score += 4;
		if (maxPossibleSockets() == maxEnchantments()) score += 4;
		score += Utils.thisOrThatOrNull(Engraving.getEngravingExtraScore(this),0) * 2;
		for (Map.Entry<Enchantment,Integer> entry : enchantments.entrySet()) {
			if (entry.getKey().isCursed()) score -= 2;
			else {
				score += 2;
				if (entry.getKey().getMaxLevel() <= entry.getValue()) score += entry.getValue() - entry.getKey().getMaxLevel() + 1;
			}
		}
		return Math.max(score,1);
	}
	
	@Override
	public boolean canRevive() {
		return true;
	}
	
	@NotNull
	public Pair<@NotNull Integer,@NotNull Integer> reviveCost() {
		int upgrade = 0,amount = itemScore();
		float divide = 8;
		while (amount >= divide) {
			amount = Math.round(amount / divide);
			upgrade++;
		}
		return Pair.of(upgrade,amount);
	}
	
	@NotNull
	public TreeMap<@NotNull Integer,@NotNull Integer> reviveCostMap() {
		int upgrade = 0,amount = itemScore(),divide = 8,left;
		TreeMap<@NotNull Integer,@NotNull Integer> map = new TreeMap<>();
		if (amount < divide) map.put(0,amount);
		else while (amount >= divide) {
			left = amount % divide;
			if (left > 0) map.put(upgrade,left);
			amount /= divide;
			upgrade++;
//			if (divide < 64) divide *= 2;
		}
		return map;
	}
	
	protected int extraScore() {
		return 0;
	}
	
	protected int attributesScore() {
		return info.score();
	}
	
	@Override
	@NotNull
	protected List<Component> enchantmentsLore(@NotNull HashMap<@NotNull Enchantment,@NotNull @Positive Integer> enchantmentsMap) {
		List<Component> lore = new ArrayList<>();
		int count = 0;
		for (Map.Entry<Enchantment,Integer> ench : enchantments.entrySet()) if (!(ench.getKey() instanceof Engraving)) {
			lore.add(Utils.enchantmentsLoreLine(ench.getKey(),ench.getValue()));
			count++;
		}
		for (; count < maxEnchantments; count++) lore.add(EMPTY_LINE);
		if (engraving != null) {
			lore.add(0,Component.empty());
			lore.add(Utils.enchantmentsLoreLine(engraving,1));
		}
		return lore;
	}
	
	@Override
	protected V setMaxEnchantmentSlots(@Positive int maxEnchantments) {
		return super.setMaxEnchantmentSlots(Math.min(maxEnchantments,maxPossibleSockets()));
	}
	
	public boolean addEnchantmentSlot() {
		int old = maxEnchantments;
		return old != setMaxEnchantmentSlots(old + 1).maxEnchantments;
	}
	
	@Override
	@Positive
	protected int initialMaxEnchantments() {
		return ThreadLocalRandom.current().nextInt(1,maxPossibleSockets());
	}
}