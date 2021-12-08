package me.DMan16.POPUtils.Classes;

import me.DMan16.POPUtils.Enums.Rarity;
import me.DMan16.POPUtils.Interfaces.InterfacesUtils;
import me.DMan16.POPUtils.Interfaces.Purchasable;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Applicable<V,T> implements Purchasable<V,T> {
	public final int ID;
	public final Rarity rarity;
	public final @NotNull String name;
	public final @NotNull Component defaultName;
	public final @NotNull Component displayName;
	protected final ItemStack displayItem;
	protected final @Nullable V currency;
	protected final @Nullable BigInteger ShopPrice;
	public final boolean isNull;
	
	// Null applicable
	@SuppressWarnings("unchecked")
	protected <P extends Applicable<V,T>> Applicable(@NotNull Component displayName, @Nullable Consumer<P> doFirst) {
		this.ID = 0;
		this.rarity = Rarity.get(0);
		this.name = "null";
		if (doFirst != null) doFirst.accept((P) this);
		this.defaultName = displayName;
		this.displayItem = displayItem(this.defaultName);
		ItemStack bundle = Utils.makeItem(Material.BUNDLE,defaultName,lore(false),ItemFlag.values());
		BundleMeta meta = (BundleMeta) bundle.getItemMeta();
		meta.addItem(this.displayItem);
		bundle.setItemMeta(meta);
		this.displayName = defaultName.hoverEvent(bundle.asHoverEvent());
		this.currency = null;
		this.ShopPrice = null;
		this.isNull = true;
	}
	
	// Null applicable
	protected <P extends Applicable<V,T>> Applicable(@NotNull String displayName, @Nullable Consumer<P> doFirst) {
		this(Utils.stringToComponent(displayName,NamedTextColor.WHITE),doFirst);
	}
	
	@SuppressWarnings("unchecked")
	protected <P extends Applicable<V,T>> Applicable(int ID, int rarity, @NotNull String name, @NotNull String displayName, @Nullable String color, @Nullable V currency,
													 @Nullable BigInteger shopPrice, @Nullable Function<P,@NotNull Boolean> doFirst) throws IllegalArgumentException {
		if (ID <= 0) throw new IllegalArgumentException();
		this.ID = ID;
		this.rarity = Rarity.get(rarity);
		name = Utils.fixKey(name);
		if (name == null) throw new IllegalArgumentException("Empty name!");
		this.name = name;
		if (doFirst != null) if (!doFirst.apply((P) this)) throw new IllegalArgumentException();
		TextColor textColor = Utils.getTextColor(color);
		this.defaultName = Utils.stringToComponent(displayName,textColor);
		this.displayItem = displayItem(defaultName);
		ItemStack bundle = Utils.makeItem(Material.BUNDLE,defaultName,lore(false),ItemFlag.values());
		BundleMeta meta = (BundleMeta) bundle.getItemMeta();
		meta.addItem(this.displayItem);
		bundle.setItemMeta(meta);
		this.displayName = defaultName.hoverEvent(bundle.asHoverEvent());
		this.ShopPrice = shopPrice == null || shopPrice.compareTo(BigInteger.ZERO) < 0 ? null : shopPrice;
		this.currency = currency;
		this.isNull = false;
	}
	
	@NotNull
	public Component giveComponent() {
		return displayName;
	}
	
	public boolean isPurchasable() {
		return this.ShopPrice != null;
	}
	
	@NotNull
	public ItemStack itemCanPurchaseAndAfford(@NotNull Player player, T val) {
		return item(displayName.hoverEvent(null),false,false);
	}
	
	@Nullable
	public BigInteger getPrice(@NotNull Player player, @Nullable T val) {
		return this.ShopPrice;
	}
	
	@NotNull
	public final V getCurrencyType() {
		return currency == null ? getDefaultCurrencyType() : currency;
	}
	
	@NotNull
	protected abstract ItemStack item(@NotNull Component name, boolean skinChooser, boolean chosen);
	
	@NotNull
	public ItemStack item(boolean skinChooser, boolean chosen) {
		return item(displayName,skinChooser,chosen);
	}
	
	@NotNull
	protected ItemStack displayItem(@NotNull Component display) {
		return item(display,false,false);
	}
	
	@NotNull
	protected List<Component> lore(boolean chosen) {
		return lore(chosen,rarity);
	}
	
	@NotNull
	protected static List<Component> lore(boolean chosen, @NotNull Rarity rarity) {
		return new ArrayList<>(chosen ? Arrays.asList(Component.empty(),rarity.displayName(),Component.empty(),InterfacesUtils.CHOSEN) :
				Arrays.asList(Component.empty(),rarity.displayName()));
	}
	
	@NotNull
	public static <V extends Applicable<?,?>> List<V> sortModel(@NotNull List<V> skins, boolean ascending) {
		List<V> sorted = skins.stream().filter(Objects::nonNull).sorted(Comparator.comparing(skin -> skin.ID)).collect(Collectors.toList());
		if (!ascending) Collections.reverse(sorted);
		return sorted;
	}
	
	@NotNull
	public static <V extends Applicable<?,?>> List<V> sortName(@NotNull List<V> skins, boolean ascending) {
		List<V> sorted = skins.stream().filter(Objects::nonNull).sorted(Comparator.comparing(skin -> skin.name)).collect(Collectors.toList());
		if (!ascending) Collections.reverse(sorted);
		return sorted;
	}
	
	@NotNull
	public static <V extends Applicable<?,?>> List<V> sortRarity(@NotNull List<V> skins, boolean ascending) {
		List<V> sorted = sortName(skins,ascending).stream().filter(Objects::nonNull).sorted(Comparator.comparing(skin -> skin.rarity)).collect(Collectors.toList());
		if (!ascending) Collections.reverse(sorted);
		return sorted;
	}
}