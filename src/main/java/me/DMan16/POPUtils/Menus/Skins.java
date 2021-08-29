package me.DMan16.POPUtils.Menus;

import me.DMan16.POPUtils.Interfaces.Purchasable;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
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
import java.util.stream.IntStream;

public abstract class Skins<V extends Skins.Skin<?>> extends ListenerInventoryPages {
	private static final Material SORT_MATERIAL = Material.PAPER;
	private static final TranslatableComponent SORT_NAME = Component.translatable("pickaxe.prisonpop.sort",NamedTextColor.GOLD).decoration(TextDecoration.ITALIC,false);
	private static final TextColor SORT_COLOR = NamedTextColor.AQUA;
	private static final List<ItemStack> sort;
	
	static {
		sort = IntStream.range(0,6).mapToObj(i -> Utils.makeItem(SORT_MATERIAL, SORT_NAME,Arrays.asList(
				Component.empty(),
				Component.translatable("generator.default",(i / 2) == 0 ? SORT_COLOR : NamedTextColor.WHITE).decoration(TextDecoration.ITALIC,false),
				Component.translatable("pickaxe.prisonpop.rarity",(i / 2) == 1 ? SORT_COLOR : NamedTextColor.WHITE).decoration(TextDecoration.ITALIC,false),
				Component.translatable("pickaxe.prisonpop.name",(i / 2) == 2 ? SORT_COLOR : NamedTextColor.WHITE).decoration(TextDecoration.ITALIC,false),
				Component.empty(),
				Component.translatable("pickaxe.prisonpop.ascending",(i % 2) == 0 ? SORT_COLOR : NamedTextColor.WHITE).decoration(TextDecoration.ITALIC,false),
				Component.translatable("pickaxe.prisonpop.descending",(i % 2) == 1 ? SORT_COLOR : NamedTextColor.WHITE).decoration(TextDecoration.ITALIC,false)
		),ItemFlag.values())).toList();
	}
	
	protected int backSlot = 0;
	protected int sortSlot = 4;
	protected int resetSkinSlot = 4;
	protected boolean canGoBack = true;
	
	protected V currentSkin = null;
	protected int currentSort = 0;
	protected boolean ascending = true;
	protected List<V> skins = null;
	
	public Skins(@NotNull Player player, @NotNull Component menuName, @NotNull JavaPlugin plugin, @NotNull List<V> skins, Object ... objs) {
		super(player,player,5,menuName,plugin,skins,objs);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void first(Object ... objs) {
		resetWithBorder = true;
		this.skins = (List<V>) objs[0];
		firstMore(objs[1].getClass().isArray() ? (Object[]) objs[1] : objs[1]);
	}
	
	@Override
	protected void otherSlot(@NotNull InventoryClickEvent event, int slot, ItemStack slotItem, @NotNull ClickType click) {
		if (canGoBack && slot == backSlot) goBack();
		else if (slot == sortSlot) {
			if (event.isRightClick()) ascending = !ascending;
			else currentSort = (currentSort + 1) % 3;
			if (currentSort == 0) skins = Skin.sortModel(skins,ascending);
			else if (currentSort == 1) skins = Skin.sortRarity(skins,ascending);
			else skins = Skin.sortName(skins, ascending);
			setPage(currentPage);
		} else if (!otherSlots(event,slot,slotItem)) {
			V skin;
			int idx;
			if (slot == resetSkinSlot) skin = null;
			else if ((idx = getIndex(slot)) >= 0 && idx < skins.size()) skin = skins.get(idx);
			else return;
			if (skin != currentSkin && setSkin(skin)) {
				currentSkin = skin;
				setPage(currentPage);
			}
		}
	}
	
	@Override
	protected boolean secondSlotCheck(int slot, @NotNull ClickType click) {
		return super.secondSlotCheck(slot,click) || slot >= size || slot < 0;
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
		if (canGoBack) inventory.setItem(backSlot,BACK);
		inventory.setItem(sortSlot,sort.get((currentSort * 2) + (ascending ? 0 : 1)));
		ItemStack resetSkinItem = resetSkinItem();
		if (resetSkinItem != null) inventory.setItem(resetSkinSlot,resetSkinItem);
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
	
	protected void goBack() {}
	
	protected abstract void firstMore(Object ... objs);
	
	protected abstract boolean setSkin(@Nullable V skin);
	
	public abstract static class Skin<V> implements Purchasable<V> {
		protected static final String translatable = "translatable: ";
		protected static final Component chosenSkin = Component.translatable("menu.prisonpop.chosen",NamedTextColor.GREEN);
		
		public final int model;
		public final int rarity;
		@NotNull public final String name;
		@NotNull public final Component displayName;
		protected final ItemStack displayItem;
		@Nullable protected final BigInteger ShopPrice;
		
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
		
		@NotNull
		public ItemStack asPurchaseItem(@NotNull Player player) {
			return item(null,false,false);
		}
		
		@Nullable
		public BigInteger getPrice() {
			return this.ShopPrice;
		}
		
		@NotNull
		public abstract ItemStack item(@Nullable Component name, boolean skinChooser, boolean chosen);
		
		@NotNull
		public ItemStack item(boolean skinChooser, boolean chosen) {
			return item(null,skinChooser,chosen);
		}
		
		@NotNull
		protected abstract ItemStack displayItem(int model, int rarity, @NotNull String name, @NotNull Component display, Object ... objs);
		
		protected static List<Component> lore(boolean chosen, int rarity) {
			return chosen ? Arrays.asList(Component.empty(), SkinRarity.get(rarity).displayName(),Component.empty(),chosenSkin) : Arrays.asList(Component.empty(), SkinRarity.get(rarity).displayName());
		}
		
		@NotNull
		public static <V extends Skin> List<V> sortModel(@NotNull List<V> skins, boolean ascending) {
			List<V> sorted = skins.stream().filter(Objects::nonNull).sorted(Comparator.comparing(skin -> skin.model)).collect(Collectors.toList());
			if (!ascending) Collections.reverse(sorted);
			return sorted;
		}
		
		@NotNull
		public static <V extends Skin> List<V> sortName(@NotNull List<V> skins, boolean ascending) {
			List<V> sorted = skins.stream().filter(Objects::nonNull).sorted(Comparator.comparing(skin -> skin.name)).collect(Collectors.toList());
			if (!ascending) Collections.reverse(sorted);
			return sorted;
		}
		
		@NotNull
		public static <V extends Skin> List<V> sortRarity(@NotNull List<V> skins, boolean ascending) {
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