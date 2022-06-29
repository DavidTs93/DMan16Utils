package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.Classes.AttributesInfo;
import me.DMan16.DMan16Utils.Classes.Engraving;
import me.DMan16.DMan16Utils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.enchantments.Enchantment;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Socketable<V extends Socketable<V>> extends Enchantable<V> {
	private static final @NotNull Component EMPTY_LINE = Utils.noItalic(Component.translatable("menu.socket_empty",NamedTextColor.AQUA).append(Component.translatable("menu.empty_socket",NamedTextColor.GRAY)));
	private static final @NotNull Component SOCKET_LINE = Utils.noItalic(Component.translatable("menu.socket_full",NamedTextColor.AQUA));
	
	protected Socketable(@NotNull AttributesInfo info,boolean isDefault) {
		super(info,isDefault);
	}
	
	@Positive
	public int maxPossibleSockets() {
		return 5;
	}
	
	@Override
	@NotNull
	protected List<Component> enchantmentsLore(@NotNull HashMap<@NotNull Enchantment,@NotNull @Positive Integer> enchantmentsMap) {
		List<Component> lore = new ArrayList<>();
		int count = 0;
		for (Map.Entry<Enchantment,Integer> ench : enchantments.entrySet()) if (!(ench.getKey() instanceof Engraving)) {
			lore.add(SOCKET_LINE.append(enchantmentsLoreLine(ench.getKey(),ench.getValue())));
			count++;
		}
		for (; count < maxEnchantments; count++) lore.add(EMPTY_LINE);
		if (engraving != null) {
			lore.add(0,Component.empty());
			lore.add(enchantmentsLoreLine(engraving,1));
		}
		return lore;
	}
	
	@NotNull
	protected Component enchantmentsLoreLine(@NotNull Enchantment enchantment,int lvl) {
		return Utils.enchantmentsLoreLine(enchantment,lvl);
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
	protected final int randomMaxEnchantments(@Positive int limit) {
		return ThreadLocalRandom.current().nextInt(0,Math.min(limit,maxPossibleSockets())) + 1;
	}
	
	@NotNull
	public final V setRandomMaxEnchantments() {
		return setRandomMaxEnchantments(maxInitialEnchantments());
	}
	
	protected int maxInitialEnchantments() {
		return maxPossibleSockets() - 1;
	}
}