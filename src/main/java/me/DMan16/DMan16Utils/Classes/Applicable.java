package me.DMan16.DMan16Utils.Classes;

import me.DMan16.DMan16Utils.Enums.Rarity;
import me.DMan16.DMan16Utils.Interfaces.InterfacesUtils;
import me.DMan16.DMan16Utils.Interfaces.Purchasable;
import me.DMan16.DMan16Utils.Utils.Utils;
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

public abstract class Applicable<V,T> implements Purchasable<V,T>,Comparable<Applicable<V,T>> {
	protected static final @NotNull Component NULL_DEFAULT_NAME = Utils.noItalic(Component.translatable("generator.default",NamedTextColor.WHITE));
	
	public final int ID;
	public final Rarity rarity;
	public final @NotNull String key;
	public final @NotNull Component defaultName;
	public final @NotNull Component displayName;
	protected final ItemStack displayItem;
	protected final @Nullable V currency;
	protected final @Nullable BigInteger ShopPrice;
	public final boolean isDefault;
	
	// Null applicable
	@SuppressWarnings("unchecked")
	protected <P extends Applicable<V,T>> Applicable(@NotNull Component displayName,@Nullable Consumer<P> doFirst) {
		this.ID = 0;
		this.rarity = Rarity.get(0);
		this.key = "";
		if (doFirst != null) doFirst.accept((P) this);
		this.defaultName = Utils.noItalic(displayName);
		this.displayItem = displayItem(this.defaultName);
		ItemStack bundle = Utils.makeItem(Material.BUNDLE,defaultName,lore(false),ItemFlag.values());
		BundleMeta meta = (BundleMeta) bundle.getItemMeta();
		meta.addItem(this.displayItem);
		bundle.setItemMeta(meta);
		this.displayName = defaultName.hoverEvent(bundle.asHoverEvent());
		this.currency = null;
		this.ShopPrice = null;
		this.isDefault = true;
	}
	
	// Null applicable
	protected <P extends Applicable<V,T>> Applicable(@NotNull String displayName,@Nullable Consumer<P> doFirst) {
		this(Utils.stringToComponent(displayName,NamedTextColor.WHITE),doFirst);
	}
	
	// Null applicable
	protected <P extends Applicable<V,T>> Applicable(@Nullable Consumer<P> doFirst) {
		this(NULL_DEFAULT_NAME,doFirst);
	}
	
	@SuppressWarnings("unchecked")
	protected <P extends Applicable<V,T>> Applicable(int ID,int rarity,@NotNull String key,@NotNull String displayName,@Nullable String color,@Nullable V currency,@Nullable BigInteger shopPrice,@Nullable Function<P,@NotNull Boolean> doFirst) throws IllegalArgumentException {
		if (ID <= 0) throw new IllegalArgumentException();
		this.ID = ID;
		this.rarity = Rarity.get(rarity);
		key = Utils.fixKey(key);
		if (key == null) throw new IllegalArgumentException("Key is null or empty!");
		this.key = key;
		if (doFirst != null) if (!doFirst.apply((P) this)) throw new IllegalArgumentException();
		TextColor textColor = Utils.getTextColor(color);
		this.defaultName = Utils.noItalic(Utils.stringToComponent(displayName,textColor));
		this.displayItem = displayItem(defaultName);
		ItemStack bundle = Utils.makeItem(Material.BUNDLE,defaultName,lore(false),ItemFlag.values());
		BundleMeta meta = (BundleMeta) bundle.getItemMeta();
		meta.addItem(this.displayItem);
		bundle.setItemMeta(meta);
		this.displayName = defaultName.hoverEvent(bundle.asHoverEvent());
		this.ShopPrice = currency == null || shopPrice == null || shopPrice.compareTo(BigInteger.ZERO) < 0 ? null : shopPrice;
		this.currency = this.ShopPrice == null ? null : currency;
		this.isDefault = false;
	}
	
	@NotNull
	public final String key() {
		return key;
	}
	
	public final boolean isDefault() {
		return isDefault;
	}
	
	@NotNull
	public Component giveComponent() {
		return displayName;
	}
	
	public boolean isPurchasable() {
		return this.ShopPrice != null;
	}
	
	@NotNull
	public ItemStack itemCanPurchaseAndAfford(@NotNull Player player,T val) {
		return item(defaultName.hoverEvent(null),false,false);
	}
	
	@Nullable
	public BigInteger getPrice(@NotNull Player player,@Nullable T val) {
		return this.ShopPrice;
	}
	
	@NotNull
	public final V getCurrencyType() {
		return currency == null ? getDefaultCurrencyType() : currency;
	}
	
	@NotNull
	protected abstract ItemStack item(@NotNull Component name,boolean skinChooser,boolean chosen);
	
	@NotNull
	public ItemStack item(boolean skinChooser,boolean chosen) {
		return item(defaultName,skinChooser,chosen);
	}
	
	@NotNull
	protected ItemStack displayItem(@NotNull Component display) {
		return item(display,false,false);
	}
	
	@NotNull
	protected List<Component> lore(boolean chosen) {
		return lore(chosen,rarity);
	}
	
	public int compareTo(@NotNull Applicable<V,T> applicable) {
		return isDefault() ? (applicable.isDefault() ? 0 : -1) : (applicable.isDefault() ? 1 : key.compareTo(applicable.key));
	}
	
	@NotNull
	protected static List<Component> lore(boolean chosen,@NotNull Rarity rarity) {
		return new ArrayList<>(chosen ? Arrays.asList(Component.empty(),rarity.displayName(),Component.empty(),InterfacesUtils.CHOSEN) : Arrays.asList(Component.empty(),rarity.displayName()));
	}
	
	@NotNull
	public static <V extends Applicable<?,?>> List<V> sortID(@NotNull List<V> skins,boolean ascending) {
		List<V> sorted = skins.stream().filter(Objects::nonNull).sorted(Comparator.comparing(skin -> skin.ID)).collect(Collectors.toList());
		if (!ascending) Collections.reverse(sorted);
		return sorted;
	}
	
	@NotNull
	public static <S extends Applicable<V,T>,V,T> List<S> sortName(@NotNull List<S> skins,boolean ascending) {
		List<S> sorted = skins.stream().filter(Objects::nonNull).sorted(Applicable::compareTo).collect(Collectors.toList());
		if (!ascending) Collections.reverse(sorted);
		return sorted;
	}
	
	@NotNull
	public static <S extends Applicable<V,T>,V,T> List<S> sortRarity(@NotNull List<S> skins,boolean ascending) {
		List<S> sorted = sortName(skins,ascending).stream().filter(Objects::nonNull).sorted(Comparator.comparing(skin -> skin.rarity)).collect(Collectors.toList());
		if (!ascending) Collections.reverse(sorted);
		return sorted;
	}
}