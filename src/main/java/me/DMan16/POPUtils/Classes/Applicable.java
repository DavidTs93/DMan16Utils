package me.DMan16.POPUtils.Classes;

import me.DMan16.POPUtils.Interfaces.InterfacesUtils;
import me.DMan16.POPUtils.Interfaces.Purchasable;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
	@NotNull public final String name;
	@NotNull protected final Component defaultName;
	@NotNull public final Component displayName;
	protected final ItemStack displayItem;
	@Nullable protected final BigInteger ShopPrice;
	public final boolean isNull;
	
	// Null applicable
	@SuppressWarnings("unchecked")
	protected <P extends Applicable<V,T>> Applicable(@NotNull String displayName, @Nullable Consumer<P> doFirst) {
		this.ID = 0;
		this.rarity = Rarity.get(0);
		this.name = "null";
		if (doFirst != null) doFirst.accept((P) this);
		this.defaultName = (displayName.toLowerCase().startsWith(InterfacesUtils.TRANSLATABLE) ?
				Component.translatable(displayName.substring(InterfacesUtils.TRANSLATABLE.length()),NamedTextColor.WHITE) :
				Component.text(Utils.chatColors(displayName),NamedTextColor.WHITE)).decoration(TextDecoration.ITALIC,false);
		this.displayItem = displayItem(defaultName);
		ItemStack bundle = Utils.makeItem(Material.BUNDLE,defaultName,lore(false,this.rarity), ItemFlag.values());
		BundleMeta meta = (BundleMeta) bundle.getItemMeta();
		meta.addItem(this.displayItem);
		bundle.setItemMeta(meta);
		this.displayName = defaultName.hoverEvent(bundle.asHoverEvent());
		this.ShopPrice = null;
		this.isNull = true;
	}
	
	@SuppressWarnings("unchecked")
	protected <P extends Applicable<V,T>> Applicable(int ID, int rarity, @NotNull String name, @NotNull String displayName, @Nullable String color, @Nullable BigInteger shopPrice,
													 @Nullable Function<P,@NotNull Boolean> doFirst)
			throws IllegalArgumentException {
		if (ID <= 0) throw new IllegalArgumentException();
		this.ID = ID;
		this.rarity = Rarity.get(rarity);
		this.name = name.trim().replace(" ","_").toLowerCase();
		if (this.name.isEmpty()) throw new IllegalArgumentException("Empty name!");
		if (doFirst != null) if (!doFirst.apply((P) this)) throw new IllegalArgumentException();
		TextColor textColor = Utils.getTextColor(color);
		this.defaultName = (displayName.toLowerCase().startsWith(InterfacesUtils.TRANSLATABLE) ?
				Component.translatable(displayName.substring(InterfacesUtils.TRANSLATABLE.length()),textColor) :
				Component.text(Utils.chatColors(displayName),textColor)).decoration(TextDecoration.ITALIC,false);
		this.displayItem = displayItem(defaultName);
		ItemStack bundle = Utils.makeItem(Material.BUNDLE,defaultName,lore(false,this.rarity),ItemFlag.values());
		BundleMeta meta = (BundleMeta) bundle.getItemMeta();
		meta.addItem(this.displayItem);
		bundle.setItemMeta(meta);
		this.displayName = defaultName.hoverEvent(bundle.asHoverEvent());
		this.ShopPrice = shopPrice == null || shopPrice.compareTo(BigInteger.ZERO) < 0 ? null : shopPrice;
		this.isNull = false;
	}
	
	public boolean isPurchasable() {
		return this.ShopPrice != null;
	}
	
	@Override
	@Nullable
	public ItemStack itemCantPurchase(@NotNull Player player, T val) {
		return null;
	}
	
	@NotNull
	public ItemStack itemCanPurchaseAndAfford(@NotNull Player player, T val) {
		return item(null,false,false);
	}
	
	@Nullable
	public BigInteger getPrice(@NotNull Player player, @Nullable T val) {
		return this.ShopPrice;
	}
	
	@NotNull
	public abstract ItemStack item(@Nullable Component name, boolean skinChooser, boolean chosen);
	
	@NotNull
	public ItemStack item(boolean skinChooser, boolean chosen) {
		return item(displayName,skinChooser,chosen);
	}
	
	@NotNull
	protected ItemStack displayItem(@NotNull Component display) {
		return item(null,false,false);
	}
	
	protected static List<Component> lore(boolean chosen, @NotNull Rarity rarity) {
		return chosen ? Arrays.asList(Component.empty(),rarity.displayName(),Component.empty(), InterfacesUtils.CHOSEN) :
				Arrays.asList(Component.empty(),rarity.displayName());
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