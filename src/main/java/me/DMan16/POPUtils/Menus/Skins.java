package me.DMan16.POPUtils.Menus;

import me.DMan16.POPUtils.Interfaces.Backable;
import me.DMan16.POPUtils.Interfaces.Purchasable;
import me.DMan16.POPUtils.Interfaces.Sortable;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Skins<V extends Skins.Skin<?,?>> extends ListenerInventoryPages implements Sortable {
	protected int slotSort;
	protected int slotResetSkin;
	protected V currentSkin;
	protected int currentSort;
	protected boolean ascending;
	protected List<V> skins;
	
	public Skins(@NotNull Player player, @NotNull Component menuName, @NotNull JavaPlugin plugin, @NotNull List<V> skins, @Nullable V currentSkin, Object ... objs) {
		super(player,player,5,menuName,plugin,skins,currentSkin,objs);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void first(Object ... objs) {
		resetWithBorder = true;
		this.slotSort = 4;
		this.slotResetSkin = 8;
		this.currentSort = 0;
		this.ascending = true;
		this.skins = (List<V>) objs[0];
		this.currentSkin = objs[1] == null ? null : (V) objs[1];
		firstMore((Object[]) objs[2]);
	}
	
	@Override
	protected void otherSlot(@NotNull InventoryClickEvent event, int slot, ItemStack slotItem, @NotNull ClickType click) {
		if ((this instanceof Backable) && slot == slotBack()) ((Backable) this).goBack();
		else if (slot == slotSort) {
			if (event.isRightClick()) ascending = !ascending;
			else currentSort = (currentSort + 1) % 3;
			sort();
		} else if (!otherSlots(event,slot,slotItem)) {
			V skin;
			int idx;
			if (slot == slotResetSkin) skin = null;
			else if ((idx = getIndex(slot)) >= 0 && idx < skins.size()) skin = skins.get(idx);
			else return;
			if (skin != currentSkin && setSkin(skin)) {
				currentSkin = skin;
				setPage(currentPage);
			}
		}
	}
	
	public void sort() {
		if (currentSort == 0) skins = Skin.sortModel(skins,ascending);
		else if (currentSort == 1) skins = Skin.sortRarity(skins,ascending);
		else skins = Skin.sortName(skins,ascending);
		setPage(currentPage);
	}
	
	@Override
	protected boolean secondSlotCheck(int slot, @NotNull ClickType click) {
		return super.secondSlotCheck(slot,click) || slot >= size;
	}
	
	@Override
	public int maxPage() {
		return Math.max(1,(int) Math.ceil(skins.size() / 28.0));
	}
	
	@Override
	protected void setPageContents() {
		int idx;
		V skin;
		for (int i = 0; i < size; i++) if (!isBorder(i) && (idx = getIndex(i)) >= 0 && idx < skins.size()) inventory.setItem(i,(skin = skins.get(idx)).item(true,skin == currentSkin));
		inventory.setItem(slotSort,SORTS.get((currentSort * 2) + (ascending ? 0 : 1)));
		ItemStack resetSkinItem = resetSkinItem();
		if (resetSkinItem != null) inventory.setItem(slotResetSkin,resetSkinItem);
		setPageContentsMore();
	}
	
	@Nullable
	protected ItemStack resetSkinItem() {
		return null;
	}
	
	private int getIndex(int slot) {
		return (currentPage - 1) * 7 * 4 + ((slot / 9) - 1) * 7 + (slot % 9) - 1;
	}
	
	protected boolean otherSlots(@NotNull InventoryClickEvent event, int slot, ItemStack slotItem) {
		return false;
	}
	
	protected void setPageContentsMore() {}
	
	protected void firstMore(Object ... objs) {}
	
	protected abstract boolean setSkin(@Nullable V skin);
	
	public abstract static class Skin<V,T> implements Purchasable<V,T> {
		protected static final String translatable = "translatable: ";
		protected static final Component chosenSkin = Component.translatable("menu.prisonpop.chosen",NamedTextColor.GREEN).decoration(TextDecoration.ITALIC,false);
		
		public final int model;
		public final int rarity;
		@NotNull public final String name;
		@NotNull public final Component displayName;
		protected final ItemStack displayItem;
		@Nullable protected final BigInteger ShopPrice;
		
		// NullSkin
		protected Skin(@NotNull String displayName, Object ... objs) {
			this.model = 0;
			this.rarity = 0;
			this.name = "null";
			Component display = (displayName.toLowerCase().startsWith(translatable) ? Component.translatable(displayName.substring(translatable.length()),NamedTextColor.WHITE) : Component.text(Utils.chatColors(displayName),NamedTextColor.WHITE)).decoration(TextDecoration.ITALIC,false);
			this.displayItem = displayItem(model,rarity,name,display,objs);
			ItemStack bundle = Utils.makeItem(Material.BUNDLE,display,lore(false,0),ItemFlag.values());
			BundleMeta meta = (BundleMeta) bundle.getItemMeta();
			meta.addItem(this.displayItem);
			bundle.setItemMeta(meta);
			this.displayName = display.hoverEvent(bundle.asHoverEvent());
			this.ShopPrice = null;
		}
		
		protected Skin(int model, int rarity, @NotNull String name, @NotNull String displayName, @Nullable String color, @Nullable BigInteger ShopPrice, Object ... objs) throws IllegalArgumentException {
			if (model <= 0 || rarity < 0) throw new IllegalArgumentException();
			this.model = model;
			this.rarity = rarity;
			this.name = name.toLowerCase();
			TextColor textColor = Utils.getColor(color);
			Component display = (displayName.toLowerCase().startsWith(translatable) ? Component.translatable(displayName.substring(translatable.length()),textColor) : Component.text(Utils.chatColors(displayName),textColor)).decoration(TextDecoration.ITALIC,false);
			this.displayItem = displayItem(model,rarity,name,display,objs);
			ItemStack bundle = Utils.makeItem(Material.BUNDLE,display,lore(false,rarity),ItemFlag.values());
			BundleMeta meta = (BundleMeta) bundle.getItemMeta();
			meta.addItem(this.displayItem);
			bundle.setItemMeta(meta);
			this.displayName = display.hoverEvent(bundle.asHoverEvent());
			this.ShopPrice = ShopPrice == null || ShopPrice.compareTo(BigInteger.ZERO) < 0 ? null : ShopPrice;
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
		protected abstract ItemStack displayItem(int model, int rarity, @NotNull String name, @NotNull Component display, Object ... objs);
		
		protected static List<Component> lore(boolean chosen, int rarity) {
			return chosen ? Arrays.asList(Component.empty(),SkinRarity.get(rarity).displayName(),Component.empty(),chosenSkin) : Arrays.asList(Component.empty(), SkinRarity.get(rarity).displayName());
		}
		
		@NotNull
		public static <V extends Skin<?,?>> List<V> sortModel(@NotNull List<V> skins, boolean ascending) {
			List<V> sorted = skins.stream().filter(Objects::nonNull).sorted(Comparator.comparing(skin -> skin.model)).collect(Collectors.toList());
			if (!ascending) Collections.reverse(sorted);
			return sorted;
		}
		
		@NotNull
		public static <V extends Skin<?,?>> List<V> sortName(@NotNull List<V> skins, boolean ascending) {
			List<V> sorted = skins.stream().filter(Objects::nonNull).sorted(Comparator.comparing(skin -> skin.name)).collect(Collectors.toList());
			if (!ascending) Collections.reverse(sorted);
			return sorted;
		}
		
		@NotNull
		public static <V extends Skin<?,?>> List<V> sortRarity(@NotNull List<V> skins, boolean ascending) {
			List<V> sorted = sortName(skins,ascending).stream().filter(Objects::nonNull).sorted(Comparator.comparing(skin -> skin.rarity)).collect(Collectors.toList());
			if (!ascending) Collections.reverse(sorted);
			return sorted;
		}
	}
	
	public enum SkinRarity {
		COMMON(0, NamedTextColor.WHITE),
		UNCOMMON(20,NamedTextColor.GRAY),
		RARE(50,NamedTextColor.YELLOW),
		EPIC(100,NamedTextColor.BLUE),
		MYTHICAL(200,NamedTextColor.RED),
		LEGENDARY(500,NamedTextColor.LIGHT_PURPLE),
		GOD(1000,TextColor.color(112,51,173));
		
		private static final String prefix = "menu.prisonpop.rarity.";
		
		private final int level;
		private final TextColor color;
		
		SkinRarity(int level, TextColor color) {
			this.level = level;
			this.color = color;
		}
		
		Component displayName() {
			return Component.translatable(prefix + name().toLowerCase(),color).decoration(TextDecoration.ITALIC,false);
		}
		
		static SkinRarity get(int level) {
			SkinRarity rarity = COMMON;
			for (SkinRarity val : values()) {
				if (level >= val.level) rarity = val;
				else break;
			}
			return rarity;
		}
	}
}