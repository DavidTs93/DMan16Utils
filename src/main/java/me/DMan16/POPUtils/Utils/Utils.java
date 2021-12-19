package me.DMan16.POPUtils.Utils;

import com.comphenix.protocol.ProtocolManager;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.DMan16.POPUpdater.POPUpdaterMain;
import me.DMan16.POPUtils.Classes.Pair;
import me.DMan16.POPUtils.Classes.Trio;
import me.DMan16.POPUtils.Enums.Rarity;
import me.DMan16.POPUtils.Events.PlayerRequestSaveEvent;
import me.DMan16.POPUtils.Interfaces.InterfacesUtils;
import me.DMan16.POPUtils.Interfaces.Itemable;
import me.DMan16.POPUtils.Items.ItemUtils;
import me.DMan16.POPUtils.Listeners.CancelPlayers;
import me.DMan16.POPUtils.Listeners.PlayerVersionLogger;
import me.DMan16.POPUtils.POPUtilsMain;
import me.DMan16.POPUtils.Restrictions.Restrictions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ScopedComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Utils {
	public static final Pattern COLOR_PATTERN = Pattern.compile("&(#[a-fA-F0-9]{6})");
	public static final Function<String,ChatColor> COLOR_FUNCTION = ChatColor::of;
	public static final Pattern UNICODE_PATTERN = Pattern.compile("\\\\u\\+([a-fA-F0-9]{4})");
	public static final Function<String,Character> UNICODE_FUNCTION = str -> (char) Integer.parseInt(str,16);
	public static final @NotNull TextComponent KICK_MESSAGE = noItalic(Component.text("An error occurred, please try to reconnect",NamedTextColor.RED));
	public static final @NotNull TranslatableComponent NOT_FINISHED_LOADING_MESSAGE = noItalic(Component.translatable("multiplayer.disconnect.server_shutdown",NamedTextColor.RED));
	public static final @NotNull TranslatableComponent PLAYER_NOT_FOUND = noItalic(Component.translatable("multiplayer.prisonpop.player_not_found",NamedTextColor.RED));
	public static final @NotNull TranslatableComponent COMING_SOON = noItalic(Component.translatable("menu.prisonpop.coming_soon",NamedTextColor.GOLD,TextDecoration.BOLD));
	public static final @Unmodifiable List<String> NUMBER_SUFFIXES = List.of("k","m","b","t","q","qn","s","sp","oc","n","d","u","dd");
	public static final BigDecimal THOUSAND = BigDecimal.valueOf(1000);
	public static final BigInteger THOUSAND_INT = BigInteger.valueOf(1000);
	public static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
	public static final BigInteger HUNDRED_INT = BigInteger.valueOf(100);
	private static final Set<Long> SESSION_IDS = new HashSet<>();
	private static final @Unmodifiable List<Integer> PLAYER_STORAGE_SLOTS;
	private static final @Unmodifiable List<Integer> PLAYER_HOLDING_SLOTS;
	private static final @Unmodifiable List<Integer> PLAYER_INVENTORY_SLOTS;
	public static final Gson GSON = new GsonBuilder().create();
	private static List<Material> interactable = null;
	private static Set<@NotNull Recipe> removedRecipes = null;
	
	static {
		createInteractable();
		PLAYER_HOLDING_SLOTS = IntStream.range(0,4 * 9).boxed().toList();
		List<Integer> slots = new ArrayList<>();
		slots.add(-106);
		slots.addAll(PLAYER_HOLDING_SLOTS);
		PLAYER_STORAGE_SLOTS = Collections.unmodifiableList(slots);
		for (int i = 100; i <= 103; i++) slots.add(i);
		PLAYER_INVENTORY_SLOTS = Collections.unmodifiableList(slots);
	}
	
	@NotNull
	public static String javaVersion() {
		String javaVersion = "";
		Iterator<Entry<Object,Object>> systemProperties = System.getProperties().entrySet().iterator();
		while (systemProperties.hasNext() && javaVersion.isEmpty()) {
			Entry<Object,Object> property = systemProperties.next();
			if (property.getKey().toString().equalsIgnoreCase("java.version")) javaVersion = property.getValue().toString();
		}
		return javaVersion;
	}
	
	@NotNull
	public static String matchAndReplace(@NotNull final String str, @NotNull Pattern pattern, @NotNull Function<String,?> replace) {
		int lastIndex = 0;
		StringBuilder output = new StringBuilder();
		Matcher matcher = pattern.matcher(str);
		Object replaced;
		while (matcher.find()) {
			replaced = replace.apply(matcher.group(1));
			if (replaced == null) continue;
			output.append(str,lastIndex,matcher.start()).append(replaced);
			lastIndex = matcher.end();
		}
		if (lastIndex < str.length()) output.append(str,lastIndex,str.length());
		return output.toString();
	}
	
	@NotNull
	public static <V> String matchAndReplace(@NotNull final String str, @NotNull Pattern pattern, @NotNull BiFunction<String,V,?> replace, V val) {
		int lastIndex = 0;
		StringBuilder output = new StringBuilder();
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			output.append(str,lastIndex,matcher.start()).append(replace.apply(matcher.group(1),val));
			lastIndex = matcher.end();
		}
		if (lastIndex < str.length()) output.append(str,lastIndex,str.length());
		return output.toString();
	}
	
	@NotNull
	public static String unicode(@NotNull String str) {
		return matchAndReplace(str,UNICODE_PATTERN,UNICODE_FUNCTION);
	}
	
	@NotNull
	public static String colors(@NotNull String str) {
		return ChatColor.translateAlternateColorCodes('&',matchAndReplace(str,COLOR_PATTERN,COLOR_FUNCTION));
	}
	
	/**
	 * @return Converts to color code using &.
	 * 1.16+ HEX colors can be used via &#??????.
	 */
	@NotNull
	public static String chatColors(@NotNull String str) {
		return colors(unicode(str));
	}
	
	@NotNull
	public static List<String> chatColors(@NotNull List<String> list) {
		List<String> newList = new ArrayList<>();
		for (String str : list) if (str != null)
			if (str.trim().isEmpty()) newList.add("");
			else newList.add(chatColors(str));
		return newList;
	}
	
	public static void chatColors(@NotNull CommandSender sender, @NotNull String str) {
		sender.sendMessage(chatColors(str));
	}
	
	public static void chatColorsLogPlugin(@NotNull String str) {
		Bukkit.getLogger().info(chatColorsPlugin(str));
	}
	
	public static void chatLogPlugin(@NotNull String str) {
		Bukkit.getLogger().info(chatColorsPlugin("") + str);
	}
	
	@NotNull
	public static String chatColorsPlugin(@NotNull String str) {
		return chatColors("&d[&bP&ar&di&#d552ccs&#df88eco&en&#fe51e2P&bO&#cd7979P&d]&r " + str);
	}

	public static void chatColorsPlugin(@NotNull CommandSender sender, @NotNull String str) {
		sender.sendMessage(chatColorsPlugin(str));
	}
	
	/**
	 * Revert color codes using &
	 */
	@NotNull
	public static String chatColorsToString(@NotNull String str) {
		return chatColorsToString(str,"&");
	}
	
	@NotNull
	public static String chatColorsToString(@NotNull String str, @NotNull String colorCode) {
		Pattern unicode = Pattern.compile("§[xX](§[a-fA-F0-9]){6}");
		Matcher match = unicode.matcher(str);
		while (match.find()) {
			String code = str.substring(match.start(),match.end());
			str = str.replace(code,"§" + code.replaceAll("§[xX]","#").replace("§",""));
			match = unicode.matcher(str);
		}
		return str.replace("§",colorCode);
	}
	
	@NotNull
	public static List<String> chatColorsToString(@NotNull List<String> list) {
		return chatColorsToString(list,"&");
	}
	
	@NotNull
	@Unmodifiable
	public static List<String> chatColorsToString(@NotNull List<String> list, @NotNull String colorCode) {
		List<String> newList = new ArrayList<>();
		for (String str : list) if (str != null) {
			if (str.trim().isEmpty()) newList.add("");
			else newList.add(chatColorsToString(str,colorCode));
		}
		return Collections.unmodifiableList(newList);
	}
	
	public static Component combineComponents(List<@Nullable Component> comps) {
		Component combined = null;
		if (comps != null) for (Component comp : comps) if (comp != null) {
			if (combined == null) combined = comp;
			else combined = combined.append(comp);
		}
		return combined;
	}
	
	public static Component combineComponents(@Nullable Component ... comps) {
		Component combined = null;
		for (Component comp : comps) if (comp != null) {
			if (combined == null) combined = comp;
			else combined = combined.append(comp);
		}
		return combined;
	}
	
	public static Component joinComponents(@NotNull Component delimiter, List<@Nullable Component> comps) {
		if (comps == null || comps.isEmpty()) return null;
		Component combined = comps.get(0);
		Component comp;
		for (int i = 1; i < comps.size(); i++) {
			comp = comps.get(i);
			if (comp != null) {
				if (combined == null) combined = comp;
				else combined = combined.append(delimiter).append(comp);
			}
		}
		return combined;
	}
	
	public static Component joinComponents(@NotNull Component delimiter, @Nullable Component ... comps) {
		if (comps == null || comps.length == 0) return null;
		Component combined = comps[0];
		Component comp;
		for (int i = 1; i < comps.length; i++) {
			comp = comps[i];
			if (comp != null) {
				if (combined == null) combined = comp;
				else combined = combined.append(delimiter).append(comp);
			}
		}
		return combined;
	}
	
	public static void sendActionBar(@NotNull Player player, Component ... components) {
		Component comp = combineComponents(components);
		if (comp != null) player.sendActionBar(comp);
	}
	
	@Nullable
	@Contract("null -> null; !null -> !null")
	public static String splitCapitalize(@Nullable String str) {
		return splitCapitalize(str,null,'&');
	}
	
	@Nullable
	@Contract("null,_ -> null; !null,_ -> !null")
	public static String splitCapitalize(@Nullable String str, String splitReg) {
		return splitCapitalize(str,splitReg,'&');
	}
	
	@Nullable
	@Contract("null,_,_ -> null; !null,_,_ -> !null")
	public static String splitCapitalize(@Nullable String str, @Nullable String splitReg, @Nullable Character colorCode) {
		if (str == null) return null;
		String[] splitName;
		if (splitReg == null || splitReg.trim().isEmpty()) splitName = new String[]{str};
		else splitName = str.split(splitReg);
		StringBuilder newStr = new StringBuilder();
		char[] arr;
		char c,d;
		for (String sub : splitName) {
			arr = sub.toCharArray();
			for (int i = 0; i < arr.length; i++) {
				c = arr[i];
				if (colorCode != null && colorCode == c) {
					newStr.append(colorCode);
					if (i < arr.length - 1) {
						d = arr[i + 1];
						if (ChatColor.translateAlternateColorCodes(colorCode,colorCode.toString() + d).charAt(0) != colorCode) {
							newStr.append(d);
							i++;
						}
					}
				} else if (Character.isLetter(c)){
					newStr.append(Character.toUpperCase(c));
					while (++i < arr.length) {
						c = arr[i];
						if (Character.isLetter(c)) newStr.append(Character.toLowerCase(c));
						else {
							newStr.append(c);
							break;
						}
					}
				} else newStr.append(c);
			}
		}
		return newStr.toString();
	}
	
	@NotNull
	public static String chatColorsStrip(@NotNull String str) {
		return ChatColor.stripColor(str);
	}
	
	@NotNull
	public static String encode(@NotNull String str, @Nullable String regSplit, @Nullable String regJoin) {
		return String.join(regJoin == null ? "" : regJoin,str.split(regSplit == null ? "" : regSplit));
	}
	
	@Nullable
	public static TextColor getTextColor(String str) {
		TextColor color = null;
		if (str != null) try {
			str = str.trim();
			if (str.startsWith("#")) color = TextColor.fromHexString(str);
			else if ("0123456789".contains(str.substring(0,1))) color = TextColor.fromHexString("#" + str);
			else {
				Rarity rarity = Rarity.get(str);
				color = rarity == null ? NamedTextColor.NAMES.value(str.replace(" ","_").toLowerCase()) : rarity.color;
			}
		} catch (Exception e) {}
		return color;
	}
	
	@Nullable
	public static Color getColor(String str) {
		Color color = null;
		if (str != null) try {
			str = str.trim();
			if (str.startsWith("#")) str = str.replaceFirst("#","");
			if ("0123456789".contains(str.substring(0,1))) color = Color.fromRGB(Integer.parseInt(str));
			else {
				Rarity rarity = Rarity.get(str);
				color = rarity == null ? ReflectionUtils.getStaticFields(Color.class,Color.class,true).get(str.replace(" ","_").toUpperCase()) :
						Color.fromRGB(rarity.color.value());
			}
		} catch (Exception e) {}
		return color;
	}
	
	@NotNull
	@Unmodifiable
	public static List<@NotNull Integer> getPlayerStorageSlots() {
		return PLAYER_STORAGE_SLOTS;
	}
	
	@NotNull
	@Unmodifiable
	public static List<@NotNull Integer> getPlayerHoldingSlots() {
		return PLAYER_HOLDING_SLOTS;
	}
	
	@NotNull
	@Unmodifiable
	public static List<@NotNull Integer> getPlayerInventorySlots() {
		return PLAYER_INVENTORY_SLOTS;
	}
	
	public static int getSlot(@NotNull Player player, EquipmentSlot slot) {
		if (slot == null) return -1;
		else if (slot == EquipmentSlot.HEAD) return 103;
		else if (slot == EquipmentSlot.CHEST) return 102;
		else if (slot == EquipmentSlot.LEGS) return 101;
		else if (slot == EquipmentSlot.FEET) return 100;
		else if (slot == EquipmentSlot.OFF_HAND) return -106;
		return player.getInventory().getHeldItemSlot();
	}
	
	@Nullable
	public static ItemStack getFromSlot(@NotNull Player player, int slot) {
		if (!PLAYER_INVENTORY_SLOTS.contains(slot)) return null;
		ItemStack item;
		if (slot == -106) item = player.getInventory().getItemInOffHand();
		else if (slot == 100) item = player.getInventory().getBoots();
		else if (slot == 101) item = player.getInventory().getLeggings();
		else if (slot == 102) item = player.getInventory().getChestplate();
		else if (slot == 103) item = player.getInventory().getHelmet();
		else item = player.getInventory().getItem(slot);
		return item;
	}
	
	@Nullable
	public static ItemStack getFromSlot(@NotNull Player player, @NotNull EquipmentSlot slot) {
		if (slot == EquipmentSlot.HEAD) return player.getInventory().getHelmet();
		else if (slot == EquipmentSlot.CHEST) return player.getInventory().getChestplate();
		else if (slot == EquipmentSlot.LEGS) return player.getInventory().getLeggings();
		else if (slot == EquipmentSlot.FEET) return player.getInventory().getBoots();
		else if (slot == EquipmentSlot.OFF_HAND) return player.getInventory().getItemInOffHand();
		return player.getInventory().getItemInMainHand();
	}
	
	public static void setSlot(@NotNull Player player, @Nullable ItemStack item, int slot) {
		if (slot == -106) player.getInventory().setItemInOffHand(item);
		else if (slot == 100) player.getInventory().setBoots(item);
		else if (slot == 101) player.getInventory().setLeggings(item);
		else if (slot == 102) player.getInventory().setChestplate(item);
		else if (slot == 103) player.getInventory().setHelmet(item);
		else if (slot >= 0) player.getInventory().setItem(slot,item);
	}
	
	public static void setSlot(@NotNull Player player, @Nullable ItemStack item, EquipmentSlot slot) {
		if (slot == EquipmentSlot.HEAD) player.getInventory().setHelmet(item);
		else if (slot == EquipmentSlot.CHEST) player.getInventory().setChestplate(item);
		else if (slot == EquipmentSlot.LEGS) player.getInventory().setLeggings(item);
		else if (slot == EquipmentSlot.FEET) player.getInventory().setBoots(item);
		else if (slot == EquipmentSlot.OFF_HAND) player.getInventory().setItemInOffHand(item);
		else if (slot == EquipmentSlot.HAND) player.getInventory().setItemInMainHand(item);
	}
	
	@Nullable
	public static ItemStack addDamage(@Nullable ItemStack item, int damage) {
		if (isNull(item)) return item;
		return setDamage(item,item.getType().getMaxDurability() + damage);
	}
	
	@Nullable
	@Contract("null,_ -> null; !null,_ -> !null")
	public static ItemStack setDamage(@Nullable ItemStack item, int damage) {
		if (isNull(item)) return item;
		int maxDMG = item.getType().getMaxDurability();
		if (maxDMG <= 0) return item;
		Damageable meta = (Damageable) item.getItemMeta();
		if (meta.getDamage() >= maxDMG || damage >= maxDMG) return item;
		meta.setDamage(Math.max(damage,0));
		item.setItemMeta(meta);
		return item;
	}
	
	/**
	 * @return if the items are the identical besides the amount
	 */
	public static boolean sameItem(@Nullable ItemStack item1, @Nullable ItemStack item2) {
		if (isNull(item1) || isNull(item2)) return item1 == item2;
		item1 = Restrictions.Unstackable.remove(item1.clone());
		item2 = Restrictions.Unstackable.remove(item2.clone());
		if (Objects.equals(mapComponent(item1.getItemMeta().displayName()),mapComponent(item2.getItemMeta().displayName()))) {
			ItemMeta meta1 = item1.getItemMeta();
			ItemMeta meta2 = item2.getItemMeta();
			meta1.displayName(null);
			meta2.displayName(null);
			item1.setItemMeta(meta1);
			item2.setItemMeta(meta2);
		}
		return item1.isSimilar(item2);
	}
	
	/**
	 * @return if the items are the identical besides the amount, the display name, and the durability
	 */
	public static boolean similarItem(@Nullable ItemStack item1, @Nullable ItemStack item2, boolean ignoreDurability, boolean ignoreFlags) {
		if (isNull(item1) || isNull(item2)) return item1 == item2;
		item1 = Restrictions.Unstackable.remove(item1.clone());
		item2 = Restrictions.Unstackable.remove(item2.clone());
		if (ignoreDurability) {
			item1 = setDamage(item1,0);
			item2 = setDamage(item2,0);
		}
		ItemMeta meta1 = item1.getItemMeta();
		ItemMeta meta2 = item2.getItemMeta();
		meta1.displayName(null);
		meta2.displayName(null);
		if (ignoreFlags) {
			meta1.removeItemFlags(ItemFlag.values());
			meta2.removeItemFlags(ItemFlag.values());
		}
		item1.setItemMeta(meta1);
		item2.setItemMeta(meta2);
		return item1.isSimilar(item2);
	}
	
	/**
	 * @return if the items are the identical besides the amount, the display name, and the durability
	 */
	public static boolean similarItem(@Nullable ItemStack item1, @Nullable ItemStack item2, boolean ignoreDurability) {
		return similarItem(item1,item2,ignoreDurability,false);
	}
	
	/**
	 * Pick up items properly from custom set results, example: Anvil, Smithing Table
	 */
	public static void uniqueCraftingHandle(@NotNull InventoryClickEvent event, int reduce, float pitch) {
		Inventory inv = event.getInventory();
		ItemStack item1 = inv.getItem(1);
		if (!(event.getWhoClicked() instanceof Player player) || isNull(inv.getItem(0)) || isNull(item1) ||
				item1.getAmount() < reduce || (!event.isShiftClick() && !event.isLeftClick() &&
				!event.isRightClick() && event.getHotbarButton() <= -1)) return;
		if (event.getRawSlot() != 2) return;
		ItemStack result = inv.getItem(2);
		if (event.isShiftClick()) {
			if (player.getInventory().firstEmpty() == -1) {
				event.setCancelled(true);
				return;
			}
			givePlayer(player,player.getLocation(),false,result);
		} else if(event.getHotbarButton() != -1) {
			if (!isNull(getFromSlot(player,event.getHotbarButton()))) {
				event.setCancelled(true);
				return;
			}
			setSlot(player,result,event.getHotbarButton());
		} else player.setItemOnCursor(result);
		inv.setItem(0,null);
		if (item1.getAmount() > reduce) item1.setAmount(item1.getAmount() - reduce);
		else inv.setItem(1,null);
		inv.setItem(2,null);
		player.updateInventory();
		if (inv.getType() == InventoryType.ANVIL) player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE,1,pitch);
		else if (inv.getType() == InventoryType.SMITHING) player.playSound(player.getLocation(),Sound.BLOCK_SMITHING_TABLE_USE,1,pitch);
	}
	
	/**
	 * @return stored Enchantments in an Enchanted Book
	 */
	public static Map<Enchantment,Integer> getStoredEnchants(ItemStack item) {
		if (isNull(item)) return null;
		if (item.getType() != Material.ENCHANTED_BOOK) return null;
		return ((EnchantmentStorageMeta) item.getItemMeta()).getStoredEnchants();
	}
	
	/**
	 * @return number as Roman numerals
	 */
	@NotNull
	public static String toRoman(int num) {
		int[] values = {1000,900,500,400,100,90,50,40,10,9,5,4,1};
		String[] romanLiterals = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};
		StringBuilder roman = new StringBuilder();
		for(int i = 0 ; i < values.length; i++)
			while (num >= values[i]) {
				num -= values[i];
				roman.append(romanLiterals[i]);
			}
		return roman.toString();
	}
	
	@SuppressWarnings("deprecation")
	@NotNull
	public static NamespacedKey namespacedKey(@NotNull String prefix, @NotNull String name) {
		return new NamespacedKey(prefix,name);
	}

	@Nullable
	public static NamespacedKey toNamespacedKey(@NotNull String str) {
		String[] splitKey = str.split(":");
		if (splitKey.length == 2 && !splitKey[0].isEmpty() && !splitKey[1].isEmpty()) return namespacedKey(splitKey[0],splitKey[1]);
		return null;
	}
	
	@Contract(value = "null -> true",pure = true)
	public static boolean isNull(@Nullable ItemStack item) {
		return item == null || isNull(item.getType()) || item.getAmount() <= 0;
	}
	
	@Contract(value = "null -> true",pure = true)
	public static boolean isNull(@Nullable Material material) {
		return material == null || material.isAir();
	}
	
	public static class PairInt extends Pair<Integer,Integer> {
		private PairInt(int first, int second) {
			super(first,second);
		}
		
		public static PairInt of(int first, int second) {
			return new PairInt(first,second);
		}
		
		public PairInt add(@NotNull PairInt add) {
			return add(add.first(),add.second());
		}
		
		public @NotNull PairInt add(int first, int second) {
			return new PairInt(this.first() + first,this.second() + second);
		}
	}
	
	/**
	 * Give an item to a player.
	 * If their inventory is full, drops the item at the given location.
	 */
	@NotNull
	public static List<@NotNull Item> givePlayer(@NotNull Player player, @Nullable Location drop, boolean glow, ItemStack ... items) {
		return givePlayer(player,drop,glow,Arrays.asList(items));
	}
	
	@NotNull
	public static List<@NotNull Item> givePlayer(@NotNull Player player, @Nullable Location drop, boolean glow, @NotNull List<ItemStack> items) {
		if (player.isDead()) return new ArrayList<>();
		List<ItemStack> leftovers = addItems(player,items);
		if (drop == null) return new ArrayList<>();
		return dropItems(drop,glow,leftovers);
	}
	
	@NotNull
	public static List<@NotNull ItemStack> addItems(@NotNull LivingEntity entity, ItemStack ... items) {
		return addItems(entity,Arrays.asList(items));
	}
	
	@NotNull
	public static List<@NotNull ItemStack> addItems(@NotNull LivingEntity entity, List<ItemStack> items) {
		if (items == null || items.isEmpty()) return new ArrayList<>();
		if (!(entity instanceof InventoryHolder e)) return items;
		return new ArrayList<>(e.getInventory().addItem(items.stream().filter(item -> !isNull(item)).toArray(ItemStack[]::new)).values());
	}
	
	/**
	 * Drop an item naturally to the world at a given location
	 * @return the dropped item
	 */
	@NotNull
	public static List<Item> dropItems(@NotNull Location loc, boolean glow, @NotNull ItemStack ... items) {
		return dropItems(loc,glow,Arrays.asList(items));
	}
	
	@NotNull
	public static List<Item> dropItems(@NotNull Location loc, boolean glow, @NotNull List<ItemStack> items) {
		if (items.isEmpty()) return new ArrayList<>();
		items = new ArrayList<>(items);
		ListIterator<ItemStack> iter = items.listIterator();
		ItemStack item;
		List<Item> drops = new ArrayList<>();
		Item drop;
		while (iter.hasNext()) {
			item = iter.next();
			int amount = item.getAmount();
			item.setAmount(1);
			while (amount > 0) {
				drop = loc.getWorld().dropItemNaturally(loc,item);
				amount--;
				if (drop.isDead()) continue;
				if (glow) drop.setGlowing(true);
				drops.add(drop);
			}
		}
		return drops;
	}
	
	/**
	 * @return the Minecraft version the server is running on
	 */
	@NotNull
	public static String getVersion() {
		return Bukkit.getServer().getVersion().split("\\(MC:")[1].split("\\)")[0].trim().split(" ")[0].trim();
	}
	
	/**
	 * @return the main number of the server's version (1)
	 */
	public static int getVersionMain() {
		return Integer.parseInt(getVersion().split("\\.")[0]);
	}

	/**
	 * @return the number of the server's version (14,15,16,etc.)
	 */
	public static int getVersionInt() {
		return Integer.parseInt(getVersion().split("\\.")[1]);
	}
	
	@Nullable
	@Contract("null -> null; !null -> !null")
	public static Long round(@Nullable Double num) {
		return num == null ? null : Math.round(num);
	}
	
	@Nullable
	@Contract("null -> null; !null -> !null")
	public static Integer round(@Nullable Float num) {
		return num == null ? null : Math.round(num);
	}
	
	/**
	 * @param digitsAfterDot >= 0
	 * @return the number rounded to specified digits after the dot
	 */
	public static Double roundAfterDot(@Nullable Double num, int digitsAfterDot) {
		if (num == null) return null;
		if (digitsAfterDot < 0) return num;
		if (digitsAfterDot == 0) return (double) Math.round(num);
		return Double.parseDouble((new DecimalFormat("0." + "0".repeat(digitsAfterDot))).format(num));
	}
	
	/**
	 * @param digitsAfterDot >= 0
	 * @return the number rounded to specified digits after the dot
	 */
	public static Float roundAfterDot(@Nullable Float num, int digitsAfterDot) {
		if (num == null) return null;
		if (digitsAfterDot < 0) return num;
		if (digitsAfterDot == 0) return (float) Math.round(num);
		return Float.parseFloat((new DecimalFormat("0." + "0".repeat(digitsAfterDot))).format(num));
	}
	
	/**
	 * @return serialized version
	 */
	@Nullable
	@Contract("null -> null")
	public static String ObjectToBase64(@Nullable Object obj) {
		if (obj != null) try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			dataOutput.writeInt(1);
			dataOutput.writeObject(obj);
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray()).replace("\n","").replace("\r","");
        } catch (Exception e) {}
		return null;
    }
	
	/**
	 * @return deserialized version
	 */
	@Nullable
	@Contract("null -> null")
	public static Object ObjectFromBase64(@Nullable String data) {
		if (data != null) try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			dataInput.readInt();
			Object obj = dataInput.readObject();
			dataInput.close();
			return obj;
		} catch (Exception e) {}
		return null;
    }
	
	@Nullable
	@Contract("null -> null")
	public static ItemStack toItem(@Nullable String str) {
		Itemable<?> item = ItemUtils.of(str);
		if (item != null) return item.asItem();
		try {
			return (ItemStack) ObjectFromBase64(str);
		} catch (Exception e) {}
		return null;
	}
	
	@Nullable
	@Contract("null -> null")
	public static String toString(@Nullable ItemStack item) {
		if (isNull(item)) return null;
		Itemable<?> itemable = ItemUtils.of(item);
		if (itemable != null) return itemable.ItemableString();
		if (!isNull(item)) try {
			return ObjectToBase64(item);
		} catch (Exception e) {}
		return null;
	}
	
	@Contract(pure = true)
	public static Object Null() {
		return null;
	}
	
	/**
	 * @return online player from name, null if not found
	 */
	@Nullable
	public static Player getOnlinePlayer(@NotNull String name) {
		return Bukkit.getPlayer(name);
	}
	
	public static boolean isUndead(@NotNull LivingEntity entity) {
		return entity instanceof Zombie || entity instanceof ZombieHorse || entity instanceof Skeleton || entity instanceof SkeletonHorse ||
				entity instanceof Zoglin || entity instanceof Phantom || entity instanceof Wither;
	}
	
	@NotNull
	public static List<Component> listStringToListComponent(@NotNull List<String> strs) {
		List<Component> list = new ArrayList<>();
		for (String str : strs) list.add(Component.text(str).decoration(TextDecoration.ITALIC,false));
		return list;
	}
	
	@NotNull
	public static ItemStack cloneChange(@NotNull ItemStack base, boolean changeName, @Nullable Component name, boolean changeLore, @Nullable List<Component> lore,
										int model, boolean removeFlags, ItemFlag ... flags) {
		ItemStack item = base.clone();
		ItemMeta meta = item.getItemMeta();
		if (changeName) meta.displayName(name);
		if (removeFlags) for (ItemFlag flag : ItemFlag.values()) meta.removeItemFlags(flag);
		for (ItemFlag flag : flags) if (flag != null) meta.addItemFlags(flag);
		if (model > 0) meta.setCustomModelData(model);
		else if (model == 0) meta.setCustomModelData(null);
		if (changeLore) meta.lore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	@NotNull
	public static ItemStack makeItem(@NotNull Material material, @Nullable Component name, ItemFlag ... itemflag) {
		return makeItem(material,name,null,0,itemflag);
	}
	
	@NotNull
	public static ItemStack makeItem(@NotNull Material material, @Nullable Component name, int model, ItemFlag ... itemflag) {
		return makeItem(material,name,null,model,itemflag);
	}
	
	@NotNull
	public static ItemStack makeItem(@NotNull Material material, @Nullable Component name, @Nullable List<Component> lore, ItemFlag ... itemflag) {
		return makeItem(material,name,lore,0,itemflag);
	}
	
	@NotNull
	public static ItemStack makeItem(@NotNull Material material, @Nullable Component name, @Nullable List<Component> lore, int model, ItemFlag ... itemflag) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		if (name != null) meta.displayName(name);
		if (lore != null) meta.lore(lore);
		for (ItemFlag flag : itemflag) meta.addItemFlags(flag);
		if (model > 0) meta.setCustomModelData(model);
		item.setItemMeta(meta);
		return item;
	}
	
	@NotNull
	public static ItemStack makePotion(@Nullable Component name, int model, @Nullable List<Component> lore, boolean hide, Color color,
									   @Nullable PotionData base, PotionEffect... effects) {
		return makePotion(Material.POTION,name,model,lore,hide,color,base,effects);
	}
	
	@NotNull
	public static ItemStack makePotionSplash(@Nullable Component name, int model, @Nullable List<Component> lore, boolean hide, Color color,
											 @Nullable PotionData base, PotionEffect ... effects) {
		return makePotion(Material.SPLASH_POTION,name,model,lore,hide,color,base,effects);
	}
	
	private static @NotNull ItemStack makePotion(@NotNull Material material, @Nullable Component name, int model, @Nullable List<Component> lore, boolean hide, Color color,
												 @Nullable PotionData base, PotionEffect ... effects) {
		ItemStack item = hide ? makeItem(material,name,lore,model,ItemFlag.HIDE_POTION_EFFECTS) : makeItem(material,name,lore,model);
		PotionMeta meta = (PotionMeta) item.getItemMeta();
		if (base != null) meta.setBasePotionData(base);
		for (PotionEffect effect : effects) meta.addCustomEffect(effect,true);
		if (color != null) meta.setColor(color);
		item.setItemMeta(meta);
		return item;
	}
	
	public static void broadcast(@NotNull Component component) {
		Bukkit.getServer().sendMessage(component);
	}
	
	public static boolean isInteractable(@NotNull Material material) {
		if (interactable == null) createInteractable();
		return interactable.contains(material);
	}
	
	public static boolean isInteract(@NotNull Material material, @NotNull Player player) {
		if (isInteractable(material)) return !player.isSneaking();
		return false;
	}
	
	public static boolean isInteract(@NotNull PlayerInteractEvent event) {
		return event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && isInteract(event.getClickedBlock().getType(),event.getPlayer());
	}
	
	private static void createInteractable() {
		interactable = new ArrayList<>();
		List<Material> initialInteractable = Stream.of("MINECART","CHEST_MINECART","FURNACE_MINECART","HOPPER_MINECART","CHEST","ENDER_CHEST","TRAPPED_CHEST",
				"NOTE_BLOCK","CRAFTING_TABLE","FURNACE","BLAST_FURNACE","LEVER","ENCHANTING_TABLE","BEACON","DAYLIGHT_DETECTOR","HOPPER","DROPPER","REPEATER",
				"COMPARATOR","COMPOSTER","CAKE","BREWING_STAND","LOOM","BARREL","SMOKER","CARTOGRAPHY_TABLE","SMITHING_TABLE","GRINDSTONE",
				"LECTERN","STONECUTTER","DISPENSER","BELL","FLOWER_POT").map(Material::getMaterial).filter(Objects::nonNull).collect(Collectors.toList());
		addInteractable(initialInteractable);
		addInteractable(Tag.ANVIL.getValues());
		addInteractable(Tag.BUTTONS.getValues());
		addInteractable(Tag.FENCE_GATES.getValues());
		addInteractable(Tag.TRAPDOORS.getValues());
		addInteractable(Tag.SHULKER_BOXES.getValues());
		addInteractable(Tag.DOORS.getValues());
		addInteractable(Tag.BEDS.getValues());
	}
	
	public static void addInteractable(Material ... materials) {
		addInteractable(Arrays.asList(materials));
	}
	
	public static void addInteractable(Collection<Material> materials) {
		addMaterials(interactable,materials);
	}
	
	private static void addMaterials(List<Material> list, Collection<Material> materials) {
		if (list != null && materials != null && !materials.isEmpty()) materials.stream().filter(Objects::nonNull).forEach(list::add);
	}
	
	@NotNull
	@SafeVarargs
	public static <V> List<V> joinLists(List<? extends V> ... lists) {
		List<V> list = new ArrayList<>();
		for (List<? extends V> l : lists) if (l != null) list.addAll(l);
		return list;
	}
	
	@NotNull
	public static <V> List<V> joinLists(Collection<? extends List<? extends V>> lists) {
		List<V> list = new ArrayList<>();
		for (List<? extends V> l : lists) if (l != null) list.addAll(l);
		return list;
	}
	
	@NotNull
	@SafeVarargs
	public static <V> Set<V> joinSets(Set<? extends V> ... sets) {
		Set<V> set = new HashSet<>();
		for (Set<? extends V> l : sets) if (l != null) set.addAll(l);
		return set;
	}
	
	@NotNull
	public static <V> Set<V> joinSets(Collection<? extends Set<? extends V>> sets) {
		Set<V> set = new HashSet<>();
		for (Set<? extends V> l : sets) if (l != null) set.addAll(l);
		return set;
	}
	
	@NotNull
	public static Inventory makeInventory(@Nullable InventoryHolder owner, int lines, Component name) {
		if (name == null) return Bukkit.createInventory(owner,lines * 9);
		return Bukkit.createInventory(owner,lines * 9,name);
	}
	
	@NotNull
	public static Inventory makeInventory(@Nullable InventoryHolder owner, @NotNull InventoryType type, Component name) {
		if (name == null) return Bukkit.createInventory(owner,type);
		return Bukkit.createInventory(owner,type,name);
	}
	
	/**
	 * Lasts for 10 minutes
	 */
	public static long newSessionID() {
		long id = System.currentTimeMillis();
		while (SESSION_IDS.contains(id)) id = System.currentTimeMillis();
		long ID = id;
		SESSION_IDS.add(ID);
		new BukkitRunnable() {
			public void run() {
				SESSION_IDS.remove(ID);
			}
		}.runTaskLater(POPUtilsMain.getInstance(),10 * 60 * 20);
		return id;
	}
	
	public static boolean isPlayerNPC(@NotNull Player player) {
		if (getCitizensManager() == null) return false;
		return getCitizensManager().isNPC(player);
	}
	
	public static void addCancelledPlayer(@NotNull Player player) {
		getCancelPlayers().addPlayer(player);
	}
	
	public static void addCancelledPlayer(@NotNull Player player, boolean allowRotation, boolean disableDamage) {
		getCancelPlayers().addPlayer(player,allowRotation,disableDamage);
	}
	
	public static void addCancelledPlayer(@NotNull Player player, boolean allowRotation, boolean disableDamage, boolean disableInventoryClicks) {
		getCancelPlayers().addPlayer(player,allowRotation,disableDamage,disableInventoryClicks);
	}
	
	public static void removeCancelledPlayer(@NotNull Player player) {
		getCancelPlayers().removePlayer(player);
	}
	
	public static void removeCancelledPlayer(@NotNull Player player, boolean allowRotation, boolean disableDamage) {
		getCancelPlayers().removePlayer(player,allowRotation,disableDamage);
	}
	
	public static void removeCancelledPlayer(@NotNull Player player, boolean allowRotation, boolean disableDamage, boolean disableInventoryClicks) {
		getCancelPlayers().removePlayer(player,allowRotation,disableDamage,disableInventoryClicks);
	}
	
	public static boolean isPlayerCancelled(@NotNull Player player) {
		return getCancelPlayers().isPlayerCancelled(player);
	}
	
	public static void savePlayer(@NotNull Player player) {
		if (isPlayerNPC(player)) return;
		player.saveData();
		new PlayerRequestSaveEvent(player).callEventAndDoTasks();
	}
	
	@Nullable
	@Contract("null -> null")
	public static UUID getPlayerUUIDByName(String name) {
		return name == null ? null : POPUpdaterMain.getPlayerUUIDByName(name);
	}
	
	@Nullable
	@Contract("null -> null")
	public static String getPlayerNameByUUID(UUID ID) {
		return ID == null ? null : POPUpdaterMain.getPlayerNameByUUID(ID);
	}
	
	@NotNull
	public static List<@NotNull UUID> getAllPlayersByUUID() {
		return POPUpdaterMain.getAllPlayersByUUID();
	}
	
	@NotNull
	public static List<@NotNull String> getAllPlayersByName() {
		return POPUpdaterMain.getAllPlayersByName();
	}
	
	@NotNull
	public static HashMap<@NotNull UUID,@NotNull String> getAllPlayers() {
		return POPUpdaterMain.getAllPlayers();
	}
	
	public static boolean setSkin(@NotNull SkullMeta meta, @NotNull String skin, @Nullable String name) {
		try {
			Method setProfileMethod = meta.getClass().getDeclaredMethod("setProfile",GameProfile.class);
			setProfileMethod.setAccessible(true);
			UUID id = new UUID(skin.substring(skin.length() - 20).hashCode(),skin.substring(skin.length() - 10).hashCode());
			GameProfile profile = new GameProfile(id,name == null ? "D" : name);
			profile.getProperties().put("textures", new Property("textures",skin));
			setProfileMethod.invoke(meta,profile);
			return true;
		} catch (Exception e) {}
		return false;
	}
	
	@NotNull
	public static SkullMeta setSkinGetMeta(@NotNull SkullMeta meta, @NotNull String skin, @Nullable String name) {
		setSkin(meta,skin,name);
		return meta;
	}
	
	public static void setSkin(@NotNull SkullMeta meta, @NotNull Player player) {
		meta.setOwningPlayer(player);
	}
	
	@NotNull
	public static SkullMeta setSkinGetMeta(@NotNull SkullMeta meta, @NotNull Player player) {
		setSkin(meta,player);
		return meta;
	}
	
	public static boolean setSkin(@NotNull ItemStack item, @NotNull Player player) {
		if (item.getType() != Material.PLAYER_HEAD && item.getType() != Material.PLAYER_WALL_HEAD) return false;
		item.setItemMeta(setSkinGetMeta((SkullMeta) item.getItemMeta(),player));
		return true;
	}
	
	@NotNull
	public static ItemStack setSkinGetItem(@NotNull ItemStack item, @NotNull Player player) {
		setSkin(item,player);
		return item;
	}
	
	public static boolean setSkin(@NotNull ItemStack item, @NotNull String skin, @Nullable String name) {
		if (item.getType() != Material.PLAYER_HEAD && item.getType() != Material.PLAYER_WALL_HEAD) return false;
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		if (!setSkin(meta,skin,name)) return false;
		item.setItemMeta(meta);
		return true;
	}
	
	@NotNull
	public static ItemStack setSkinGetItem(@NotNull ItemStack item, @NotNull String skin, @Nullable String name) {
		setSkin(item,skin,name);
		return item;
	}
	
	@Nullable
	public static GameProfile getProfile(@NotNull SkullMeta meta) {
		try {
			Field profileField = meta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			return (GameProfile) profileField.get(meta);
		} catch (Exception e) {}
		return null;
	}
	
	@NotNull
	public static GameProfile getProfile(@NotNull Player player) throws InvocationTargetException,IllegalAccessException,NoSuchMethodException {
		return (GameProfile) player.getClass().getDeclaredMethod("getProfile").invoke(player);
	}
	
	@NotNull
	public static Pair<@Nullable String,@Nullable String> getSkin(@NotNull GameProfile profile) {
		Property property = profile.getProperties().get("textures").stream().findFirst().orElse(null);
		if (property == null) return Pair.of(null,null);
		String data = property.getValue();
		if (data != null) try {
			HashMap<String,Object> map = getMapFromJSON(data);
			assert map != null;
			map.remove("signatureRequired");
			data = getJSONString(map);
		} catch (Exception e) {}
		return Pair.of(data,property.getSignature());
	}
	
	@NotNull
	public static Pair<@Nullable String,@Nullable String> getSkin(@NotNull UUID ID) {
		try (Statement statement = getConnection().createStatement();
			 ResultSet result = statement.executeQuery("SELECT SkinData,SkinSignature FROM PrisonPOP_Players WHERE UUID='" + ID + "';")) {
			result.next();
			String skin,signature;
			skin = result.getString("SkinData");
			signature = result.getString("SkinSignature");
			return Pair.of(skin,signature);
		} catch (Exception e) {}
		return Pair.of(null,null);
	}
	
	public static String toString(double var) {
		double floor = Math.floor(var);
		return floor == var ? Double.toString(floor).replace(".0","") : Double.toString(var);
	}
	
	@NotNull
	public static Connection getConnection() throws SQLException {
		return POPUpdaterMain.getConnection();
	}
	
	public static WorldGuardManager getWorldGuardManager() {
		return POPUtilsMain.getInstance().getWorldGuardManager();
	}
	
	public static PlaceholderManager getPAPIManager() {
		return POPUtilsMain.getInstance().getPAPIManager();
	}
	
	public static CitizensManager getCitizensManager() {
		return POPUtilsMain.getInstance().getCitizensManager();
	}
	
	public static ProtocolManager getProtocolManager() {
		return POPUtilsMain.getInstance().getProtocolManager();
	}
	
	public static CancelPlayers getCancelPlayers() {
		return POPUtilsMain.getInstance().getCancelPlayers();
	}
	
	public static PlayerVersionLogger getPlayerVersionLogger() {
		return POPUtilsMain.getInstance().getPlayerVersionLogger();
	}
	
	public static boolean containsTabComplete(String arg1, String arg2) {
		return (arg1 == null || arg1.isEmpty() || arg2.toLowerCase().contains(arg1.toLowerCase()));
	}
	
	@Nullable
	@Contract("null,null -> null; !null,_ -> !null; _,!null -> !null")
	public static <V> V thisOrThatOrNull(@Nullable V obj1, @Nullable V obj2) {
		return obj1 != null ? obj1 : obj2;
	}
	
	@Nullable
	@Contract("null,null -> null")
	public static Material thisOrThatOrNull(@Nullable Material material1, @Nullable Material material2) {
		return !isNull(material1) ? material1 : (isNull(material2) ? null : material2);
	}
	
	@Nullable
	@Contract("null,null -> null")
	public static ItemStack thisOrThatOrNull(@Nullable ItemStack item1, @Nullable ItemStack item2) {
		return !isNull(item1) ? item1 : (isNull(item2) ? null : item2);
	}
	
	@Nullable
	@Contract("!null,_ -> !null; null,_ -> null")
	public static Component textToComponent(@Nullable String text, @Nullable String color) {
		return textToComponent(text,getTextColor(color));
	}
	
	@Nullable
	@Contract("!null,_ -> !null; null,_ -> null")
	public static Component textToComponent(@Nullable String text, @Nullable TextColor color) {
		return text == null ? null : (text.trim().isEmpty() ? Component.empty() : noItalic(text.toLowerCase().startsWith(InterfacesUtils.TRANSLATABLE) ?
				Component.translatable(text.substring(InterfacesUtils.TRANSLATABLE.length()),color) : Component.text(chatColors(text),color)));
	}
	
	@Nullable
	@Contract("!null,_ -> !null; null,_ -> null")
	public static Component stringToComponent(@Nullable String text, @Nullable TextColor color) {
		if (text == null) return null;
		Component comp = mapToComponent(getMapFromJSON(text));
		if (comp == null) comp = mapToComponent(getListFromJSON(text));
		return comp != null ? comp.colorIfAbsent(color) : (text.trim().isEmpty() ? Component.empty() : noItalic(text.toLowerCase().startsWith(InterfacesUtils.TRANSLATABLE) ?
				Component.translatable(text.substring(InterfacesUtils.TRANSLATABLE.length()),color) : Component.text(chatColors(text),color)));
	}
	
	@Nullable
	@Contract("!null,_ -> !null; null,_ -> null")
	public static Component stringToComponent(@Nullable String text, @Nullable String color) {
		return stringToComponent(text,getTextColor(color));
	}
	
	@Nullable
	@Contract("!null -> !null; null -> null")
	public static Component stringToComponent(@Nullable String text) {
		return stringToComponent(text,(TextColor) null);
	}
	
	@Nullable
	@Contract("!null -> !null; null -> null")
	public static String textColorToString(@Nullable TextColor color) {
		return color == null ? null : (color instanceof NamedTextColor c ? c.toString() : color.asHexString());
	}
	
	@Nullable
	@Contract("null,_ -> null; !null,_ -> !null")
	public static ItemStack setDisplayName(@Nullable ItemStack item, @Nullable Component name) {
		if (!isNull(item)) {
			ItemMeta meta = item.getItemMeta();
			meta.displayName(name);
			item.setItemMeta(meta);
		}
		return item;
	}
	
	@Nullable
	@Contract("null,_ -> null; !null,_ -> !null")
	public static ItemStack setLore(@Nullable ItemStack item, @Nullable List<Component> lore) {
		if (!isNull(item)) {
			ItemMeta meta = item.getItemMeta();
			meta.lore(lore);
			item.setItemMeta(meta);
		}
		return item;
	}
	
	@SuppressWarnings("unchecked")
	@NotNull
	public static <V> V [] listToArray(@NotNull List<V> list) {
		V[] arr = (V[]) new Object[list.size()];
		for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
		return arr;
	}
	
	@Nullable
	public static <T> T getKeyPersistentDataContainer(ItemStack item, @NotNull NamespacedKey key, @NotNull PersistentDataType<T,T> type) {
		return getKeyPersistentDataContainer(isNull(item) ? null : item.getItemMeta(),key,type);
	}
	
	public static <T> ItemStack setKeyPersistentDataContainer(ItemStack item, @NotNull NamespacedKey key, @NotNull PersistentDataType<T,T> type, T value) {
		if (!isNull(item)) item.setItemMeta(setKeyPersistentDataContainer(item.getItemMeta(),key,type,value));
		return item;
	}
	
	public static <T> ItemStack setKeyPersistentDataContainer(ItemStack item, @NotNull NamespacedKey key) {
		if (!isNull(item)) item.setItemMeta(setKeyPersistentDataContainer(item.getItemMeta(),key));
		return item;
	}
	
	public static <T> ItemStack setKeyPersistentDataContainer(ItemStack item, @NotNull NamespacedKey key, @NotNull PersistentDataType<T,T> type, T value, boolean force) {
		if (!isNull(item)) item.setItemMeta(setKeyPersistentDataContainer(item.getItemMeta(),key,type,value,force));
		return item;
	}
	
	public static <T> ItemStack setKeyPersistentDataContainer(ItemStack item, @NotNull NamespacedKey key, boolean force) {
		if (!isNull(item)) item.setItemMeta(setKeyPersistentDataContainer(item.getItemMeta(),key,force));
		return item;
	}
	
	public static <T> boolean setKeyPersistentDataContainerResult(ItemStack item, @NotNull NamespacedKey key, @NotNull PersistentDataType<T,T> type, T value) {
		if (isNull(item)) return false;
		ItemMeta meta = item.getItemMeta();
		boolean result = setKeyPersistentDataContainerResult(meta,key,type,value);
		item.setItemMeta(meta);
		return result;
	}
	
	@Nullable
	public static <T> T getKeyPersistentDataContainer(ItemMeta meta, @NotNull NamespacedKey key, @NotNull PersistentDataType<T,T> type) {
		return meta == null ? null : (meta.getPersistentDataContainer().has(key,type) ? meta.getPersistentDataContainer().get(key,type) : null);
	}
	
	public static <T> ItemMeta setKeyPersistentDataContainer(ItemMeta meta, @NotNull NamespacedKey key, @NotNull PersistentDataType<T,T> type, T value) {
		return setKeyPersistentDataContainer(meta,key,type,value,false);
	}
	
	public static <T> ItemMeta setKeyPersistentDataContainer(ItemMeta meta, @NotNull NamespacedKey key) {
		return setKeyPersistentDataContainer(meta,key,false);
	}
	
	public static <T> ItemMeta setKeyPersistentDataContainer(ItemMeta meta, @NotNull NamespacedKey key, @NotNull PersistentDataType<T,T> type, T value, boolean force) {
		if (meta != null && (force || !meta.getPersistentDataContainer().has(key,type))) meta.getPersistentDataContainer().set(key,type,value);
		return meta;
	}
	
	public static <T> ItemMeta setKeyPersistentDataContainer(ItemMeta meta, @NotNull NamespacedKey key, boolean force) {
		if (meta != null && (force || !meta.getPersistentDataContainer().has(key,PersistentDataType.STRING))) meta.getPersistentDataContainer().set(key,PersistentDataType.STRING,"");
		return meta;
	}
	
	public static <T> boolean setKeyPersistentDataContainerResult(ItemMeta meta, @NotNull NamespacedKey key, @NotNull PersistentDataType<T,T> type, T value) {
		if (meta != null && !meta.getPersistentDataContainer().has(key,type)) {
			meta.getPersistentDataContainer().set(key,type,value);
			return true;
		}
		return false;
	}
	
	@Nullable
	@Contract("null -> null")
	public static String fixKey(@Nullable String key) {
		if (key == null) return null;
		key = key.trim().toLowerCase().replace(" ","_");
		return key.isEmpty() ? null : key;
	}
	
	@Nullable
	public static Boolean getBoolean(@NotNull ResultSet result, @NotNull String name) {
		try {
			boolean val = result.getBoolean(name);
			if (!result.wasNull()) return val;
		} catch (Exception e) {}
		return null;
	}
	
	@NotNull
	public static String timeSecondsToString(long time, boolean includeZeros, @Nullable String prefix, boolean zeroBelow10, @NotNull String separator,
											 @NotNull String daysSuffix, @NotNull String hoursSuffix, @NotNull String minutesSuffix, @NotNull String secondsSuffix) {
		StringBuilder str = new StringBuilder();
		if (prefix != null) str.append(prefix);
		if (time == 0) {
			str.append(time).append(secondsSuffix);
			return str.toString();
		}
		long days,hours,minutes,seconds;
		days = TimeUnit.SECONDS.toDays(time);
		time -= TimeUnit.DAYS.toSeconds(days);
		hours = TimeUnit.SECONDS.toHours(time);
		time -= TimeUnit.HOURS.toSeconds(hours);
		minutes = TimeUnit.SECONDS.toMinutes(time);
		time -= TimeUnit.MINUTES.toSeconds(minutes);
		seconds = time;
		appendNumIf(str,includeZeros,zeroBelow10,days,daysSuffix,separator);
		appendNumIf(str,includeZeros,zeroBelow10,hours,hoursSuffix,separator);
		appendNumIf(str,includeZeros,zeroBelow10,minutes,minutesSuffix,separator);
		appendNumIf(str,includeZeros,zeroBelow10,seconds,secondsSuffix,null);
		return str.toString();
	}
	
	private static void appendNumIf(@NotNull StringBuilder str, boolean includeZeros, boolean zeroBelow10, long num, @NotNull String suffix1, @Nullable String suffix2) {
		if (num == 0 && includeZeros) return;
		if (num < 0) str.append("-");
		num = Math.abs(num);
		if (num < 10 && zeroBelow10) str.append(0);
		str.append(num);
		str.append(suffix1);
		if (suffix2 != null) str.append(suffix2);
	}
	
	@Nullable
	@Contract("null -> null; !null -> !null")
	public static Component noItalic(@Nullable Component comp) {
		return comp == null || comp.equals(Component.empty()) || comp.hasDecoration(TextDecoration.ITALIC) ? comp : comp.decoration(TextDecoration.ITALIC,false);
	}
	
	@Nullable
	@Contract("null -> null; !null -> !null")
	public static <V extends ScopedComponent<V>> V noItalic(@Nullable V comp) {
		if (comp == null || comp.hasDecoration(TextDecoration.ITALIC)) return null;
		return comp.decoration(TextDecoration.ITALIC,false);
	}
	
	@Nullable
	@Contract("null,_,_ -> null; !null,_,_ -> !null")
	public static ItemStack addEnchantment(@Nullable ItemStack item, @NotNull Enchantment enchantment, int level) {
		if (isNull(item)) return item;
		item.addUnsafeEnchantment(enchantment,level);
		return item;
	}
	
	@Nullable
	@Contract("null -> null")
	public static String getString(@Nullable Object obj) {
		if (obj instanceof String) return (String) obj;
		return null;
	}
	
	@Nullable
	public static BigDecimal formatNumber(String str) {
		if (str == null) return null;
		try {
			return new BigDecimal(str);
		} catch (Exception e) {}
		String suffix,lower = str.toLowerCase();
		if (!lower.contains("e")) for (int i = 0; i < NUMBER_SUFFIXES.size(); i++) if (lower.endsWith(NUMBER_SUFFIXES.get(i))) try {
			return new BigDecimal(str.substring(0,str.length() - NUMBER_SUFFIXES.get(i).length())).multiply(BigDecimal.valueOf(1000).pow(i + 1));
		} catch (Exception e) {}
		return null;
	}
	
	@Nullable
	public static <V extends Number> V getNumber(@Nullable Object obj, @NotNull Function<@NotNull BigDecimal,@NotNull V> getValue) {
		if (obj == null || (obj instanceof Character)) return null;
		if (obj instanceof String str) try {
			return getValue.apply(Objects.requireNonNull(formatNumber(str)));
		} catch (Exception e) {
			return null;
		}
		if (obj instanceof BigInteger big) return getValue.apply(new BigDecimal(big));
		if (obj instanceof BigDecimal big) return getValue.apply(big);
		try {
			if ((obj instanceof Byte) || (obj instanceof Short) || (obj instanceof Integer) || (obj instanceof Long)) return getValue.apply(BigDecimal.valueOf(((Number) obj).longValue()));
			V num;
			if ((obj instanceof Float f)) {
				num = getValue.apply(BigDecimal.valueOf(f));
				if (num.doubleValue() == f) return num;
			} else if ((obj instanceof Double d)) return getValue.apply(BigDecimal.valueOf(d));
		} catch (Exception e) {}
		return null;
	}
	
	@Nullable
	@Contract("null -> null")
	public static Byte getByte(@Nullable Object obj) {
		return getNumber(obj,Number::byteValue);
	}
	
	@Nullable
	@Contract("null -> null")
	public static Short getShort(@Nullable Object obj) {
		return getNumber(obj,Number::shortValue);
	}
	
	@Nullable
	@Contract("null -> null")
	public static Integer getInteger(@Nullable Object obj) {
		return getNumber(obj,Number::intValue);
	}
	
	@Nullable
	@Contract("null -> null")
	public static Long getLong(@Nullable Object obj) {
		return getNumber(obj,Number::longValue);
	}
	
	@Nullable
	@Contract("null -> null")
	public static Float getFloat(@Nullable Object obj) {
		return getNumber(obj,Number::floatValue);
	}
	
	@Nullable
	@Contract("null -> null")
	public static Double getDouble(@Nullable Object obj) {
		return getNumber(obj,Number::doubleValue);
	}
	
	@Nullable
	@Contract("null -> null")
	public static Boolean getBoolean(@Nullable Object obj) {
		if (obj == null) return null;
		try {
			return (boolean) obj;
		} catch (Exception e) {}
		try {
			String str = getString(obj);
			if (str.equalsIgnoreCase("true")) return true;
			return str.equalsIgnoreCase("false") ? false : null;
		} catch (Exception e) {}
		return null;
	}
	
	@Nullable
	@Contract("null -> null")
	public static Character getCharacter(@Nullable Object obj) {
		if (obj == null) return null;
		try {
			return (char) obj;
		} catch (Exception e) {}
		try {
			String str = getString(obj);
			if (str.length() == 1) return str.charAt(0);
		} catch (Exception e) {}
		return null;
	}
	
	@Nullable
	@Contract("null -> null")
	public static Map<@NotNull String,Object> getMap(@Nullable Object obj) {
		if (obj == null) return null;
		try {
			Map<?,?> initial = (Map<?,?>) obj;
			Map<@NotNull String,Object> map = new HashMap<>();
			String key;
			for (Map.Entry<?,?> entry : initial.entrySet()) {
				key = getString(entry.getKey());
				if (key != null) map.put(key,entry.getValue());
			}
			return map;
		} catch (Exception e) {}
		return null;
	}
	
	@Nullable
	@Contract("null,_ -> null")
	@SuppressWarnings("unchecked")
	public static <V> List<V> getListFromArray(@Nullable Object obj, @NotNull Class<@NotNull V> clazz) {
		if (obj != null && obj.getClass().isArray()) try {
			List<Object> asList = (List<Object>) Arrays.class.getDeclaredMethod("asList",Object[].class).invoke(null,obj);
			V val;
			Function<Object,?> function;
			List<V> list = new ArrayList<>();
			for (Object o : asList) {
				if (o == null) list.add(null);
				else {
					if (clazz == byte.class || clazz == Byte.class) function = Utils::getByte;
					else if (clazz == short.class || clazz == Short.class) function = Utils::getShort;
					else if (clazz == int.class || clazz == Integer.class) function = Utils::getInteger;
					else if (clazz == long.class || clazz == Long.class) function = Utils::getLong;
					else if (clazz == float.class || clazz == Float.class) function = Utils::getFloat;
					else if (clazz == double.class || clazz == Double.class) function = Utils::getDouble;
					else if (clazz == boolean.class || clazz == Boolean.class) function = Utils::getBoolean;
					else if (clazz == char.class || clazz == Character.class) function = Utils::getCharacter;
					else if (clazz == String.class) function = Utils::getString;
					else function = null;
					if (function == null) val = clazz.cast(o);
					else val = (V) function.apply(o);
					if (val != null) list.add(val);
				}
			}
			return list;
		} catch (Exception e) {}
		return null;
	}
	
	@Contract("null -> null")
	public static byte[] toPrimitiveArray(Byte[] arr) {
		if (arr == null) return null;
		List<Byte> list = Arrays.stream(arr).filter(Objects::nonNull).toList();
		byte[] primitiveArr = new byte[list.size()];
		for (int i = 0; i < list.size(); i++) primitiveArr[i] = list.get(i);
		return primitiveArr;
	}
	
	@Contract("null -> null")
	public static short[] toPrimitiveArray(Short[] arr) {
		if (arr == null) return null;
		List<Short> list = Arrays.stream(arr).filter(Objects::nonNull).toList();
		short[] primitiveArr = new short[list.size()];
		for (int i = 0; i < list.size(); i++) primitiveArr[i] = list.get(i);
		return primitiveArr;
	}
	
	@Contract("null -> null")
	public static int[] toPrimitiveArray(Integer[] arr) {
		if (arr == null) return null;
		List<Integer> list = Arrays.stream(arr).filter(Objects::nonNull).toList();
		int[] primitiveArr = new int[list.size()];
		for (int i = 0; i < list.size(); i++) primitiveArr[i] = list.get(i);
		return primitiveArr;
	}
	
	@Contract("null -> null")
	public static long[] toPrimitiveArray(Long[] arr) {
		if (arr == null) return null;
		List<Long> list = Arrays.stream(arr).filter(Objects::nonNull).toList();
		long[] primitiveArr = new long[list.size()];
		for (int i = 0; i < list.size(); i++) primitiveArr[i] = list.get(i);
		return primitiveArr;
	}
	
	@Contract("null -> null")
	public static float[] toPrimitiveArray(Float[] arr) {
		if (arr == null) return null;
		List<Float> list = Arrays.stream(arr).filter(Objects::nonNull).toList();
		float[] primitiveArr = new float[list.size()];
		for (int i = 0; i < list.size(); i++) primitiveArr[i] = list.get(i);
		return primitiveArr;
	}
	
	@Contract("null -> null")
	public static double[] toPrimitiveArray(Double[] arr) {
		if (arr == null) return null;
		List<Double> list = Arrays.stream(arr).filter(Objects::nonNull).toList();
		double[] primitiveArr = new double[list.size()];
		for (int i = 0; i < list.size(); i++) primitiveArr[i] = list.get(i);
		return primitiveArr;
	}
	
	@Contract("null -> null")
	public static boolean[] toPrimitiveArray(Boolean[] arr) {
		if (arr == null) return null;
		List<Boolean> list = Arrays.stream(arr).filter(Objects::nonNull).toList();
		boolean[] primitiveArr = new boolean[list.size()];
		for (int i = 0; i < list.size(); i++) primitiveArr[i] = list.get(i);
		return primitiveArr;
	}
	
	@Contract("null -> null")
	public static char[] toPrimitiveArray(Character[] arr) {
		if (arr == null) return null;
		List<Character> list = Arrays.stream(arr).filter(Objects::nonNull).toList();
		char[] primitiveArr = new char[list.size()];
		for (int i = 0; i < list.size(); i++) primitiveArr[i] = list.get(i);
		return primitiveArr;
	}
	
	@Contract("null -> null")
	public static Byte[] toObjectArray(byte[] arr) {
		if (arr == null) return null;
		Byte[] objectArr = new Byte[arr.length];
		for (int i = 0; i < arr.length; i++) objectArr[i] = arr[i];
		return objectArr;
	}
	
	@Contract("null -> null")
	public static Short[] toObjectArray(short[] arr) {
		if (arr == null) return null;
		Short[] objectArr = new Short[arr.length];
		for (int i = 0; i < arr.length; i++) objectArr[i] = arr[i];
		return objectArr;
	}
	
	@Contract("null -> null")
	public static Integer[] toObjectArray(int[] arr) {
		if (arr == null) return null;
		Integer[] objectArr = new Integer[arr.length];
		for (int i = 0; i < arr.length; i++) objectArr[i] = arr[i];
		return objectArr;
	}
	
	@Contract("null -> null")
	public static Long[] toObjectArray(long[] arr) {
		if (arr == null) return null;
		Long[] objectArr = new Long[arr.length];
		for (int i = 0; i < arr.length; i++) objectArr[i] = arr[i];
		return objectArr;
	}
	
	@Contract("null -> null")
	public static Float[] toObjectArray(float[] arr) {
		if (arr == null) return null;
		Float[] objectArr = new Float[arr.length];
		for (int i = 0; i < arr.length; i++) objectArr[i] = arr[i];
		return objectArr;
	}
	
	@Contract("null -> null")
	public static Double[] toObjectArray(double[] arr) {
		if (arr == null) return null;
		Double[] objectArr = new Double[arr.length];
		for (int i = 0; i < arr.length; i++) objectArr[i] = arr[i];
		return objectArr;
	}
	
	@Contract("null -> null")
	public static Boolean[] toObjectArray(boolean[] arr) {
		if (arr == null) return null;
		Boolean[] objectArr = new Boolean[arr.length];
		for (int i = 0; i < arr.length; i++) objectArr[i] = arr[i];
		return objectArr;
	}
	
	@Contract("null -> null")
	public static Character[] toObjectArray(char[] arr) {
		if (arr == null) return null;
		Character[] objectArr = new Character[arr.length];
		for (int i = 0; i < arr.length; i++) objectArr[i] = arr[i];
		return objectArr;
	}
	
	@Nullable
	@Contract("null -> null")
	public static HashMap<@NotNull String,Object> getMapFromJSON(String str) {
		if (str != null) try {
			HashMap<String,Object> map = GSON.fromJson(str, new TypeToken<HashMap<String,Object>>() {}.getType());
			map.remove(null);
			return map;
		} catch (Exception e) {}
		return null;
	}
	
	@Nullable
	@Contract("null -> null")
	public static List<@NotNull Object> getListFromJSON(String str) {
		if (str != null) try {
			List<Object> map = GSON.fromJson(str, new TypeToken<ArrayList<Object>>() {}.getType());
			map.remove(null);
			return map;
		} catch (Exception e) {}
		return null;
	}
	
	@Nullable
	@Contract("null -> null")
	public static String getJSONString(Object obj) {
		return obj == null ? null : GSON.toJson(obj);
	}
	
	@Nullable
	@Contract("null,_ -> null; !null,_ -> !null")
	public static BigInteger toBigInteger(@Nullable BigDecimal num, boolean round) {
		if (num == null) return null;
		return round ? num.setScale(0,RoundingMode.HALF_UP).toBigInteger() : num.toBigInteger();
	}
	
	@Nullable
	@Contract("null -> null; !null -> !null")
	public static BigInteger toBigInteger(@Nullable BigDecimal num) {
		return toBigInteger(num,true);
	}
	
	@Nullable
	@Contract("null -> null")
	public static List<@NotNull HashMap<@NotNull String,?>> mapComponent(Component component) {
		if (component == null) return null;
		HashMap<@NotNull String,@NotNull Object> map = new HashMap<>();
		String textContent = null;
		if (component instanceof TextComponent text) {
			if (text.equals(Component.newline())) textContent = "\n";
			else {
				textContent = text.content();
				if (textContent.contains("\n") && !textContent.equalsIgnoreCase("\n")) {
					TextComponent comp = Component.empty(), empty = text.children(List.of());
					int lastIndex = 0;
					Matcher matcher = Pattern.compile("\n").matcher(textContent);
					while (matcher.find()) {
						comp = comp.append(empty.content(textContent.substring(lastIndex,matcher.start())));
						lastIndex = matcher.end();
						comp = comp.append(Component.newline().style(empty.style()));
					}
					if (lastIndex < textContent.length()) comp = comp.append(empty.content(textContent.substring(lastIndex)));
					return mapComponent(comp.children(joinLists(comp.children(),text.children())));
				}
			}
			map.put("text",textContent);
		} else if (component instanceof TranslatableComponent translate) {
			map.put("translate",translate.key());
			if (!translate.args().isEmpty()) {
				List<HashMap<String,?>> args = joinLists(translate.args().stream().map(Utils::mapComponent).filter(Objects::nonNull).collect(Collectors.toList()));
				if (!args.isEmpty()) map.put("args",args);
			}
		} else return null;
		TextColor color = component.color();
		if (color != null) map.put("color",(color instanceof NamedTextColor named) ? named.toString() : color.asHexString());
		TextDecoration.State state;
		boolean decorations = false;
		for (TextDecoration decoration : TextDecoration.values()) {
			state = component.decoration(decoration);
			if (state != TextDecoration.State.NOT_SET) decorations = true;
			if (state == TextDecoration.State.TRUE) map.put(decoration.toString().toLowerCase(),true);
			else if (state == TextDecoration.State.FALSE) map.put(decoration.toString().toLowerCase(),false);
		}
		if (component.children().isEmpty()) return new ArrayList<>(List.of(map));
		List<HashMap<String,?>> children = joinLists(component.children().stream().map(Utils::mapComponent).filter(Objects::nonNull).collect(Collectors.toList()));
		return textContent != null && textContent.isEmpty() && color == null && !decorations ? (children.isEmpty() ? null : children) : joinLists(List.of(map),children);
	}
	
	@Nullable
	@Contract("null -> null")
	@SuppressWarnings("unchecked")
	public static Component mapToComponent(Object obj) {
		if (obj == null) return null;
		try {
			return listToComponent((List<?>) obj);
		} catch (Exception e) {}
		Map<String,?> map;
		try {
			map = (Map<String,?>) obj;
		} catch (Exception e1) {
			try {
				return textToComponent(Objects.requireNonNull(getString(obj)),(TextColor) null);
			} catch (Exception e2) {
				return null;
			}
		}
		Component comp;
		String str = getString(map.get("text"));
		if (str != null) {
			if (str.isEmpty()) comp = Component.empty();
			else if (str.equalsIgnoreCase("\n")) comp = Component.newline();
			else comp = Component.text(str);
		} else if ((str = getString(map.get("translate"))) != null) {
			TranslatableComponent translate = Component.translatable(str);
			try {
				translate = translate.args(Objects.requireNonNull(mapToComponent(map.get("args"))));
			} catch (Exception e) {
				List<Component> args = mapToListComponent(map.get("args"));
				if (args != null && !args.isEmpty()) translate = translate.args(args);
			}
			comp = translate;
		} else return null;
		TextColor color = getTextColor(getString(map.get("color")));
		if (color != null) comp = comp.color(color);
		Boolean bool;
		for (TextDecoration decoration : TextDecoration.values()) {
			bool = getBoolean(map.get(decoration.toString().toLowerCase()));
			if (decoration == TextDecoration.ITALIC && bool == null) bool = false;
			if (bool != null) comp = comp.decoration(decoration,bool);
		}
		return comp;
	}
	
	@Nullable
	public static List<Component> mapToListComponent(Object obj) {
		if (obj != null) try {
			List<Component> list = new ArrayList<>();
			for (Object o : (List<?>) obj) try {
				list.add(listToComponent((List<?>) o));
			} catch (Exception e1) {
				try {
					list.add(Objects.requireNonNull(mapToComponent(o)));
				} catch (Exception e) {}
			}
			return list.isEmpty() ? null : list;
		} catch (Exception e1) {
			try {
				return Arrays.stream(String.join("\n",Objects.requireNonNull(getString(obj)).split("\\n")).split("\n")).map(Utils::mapToComponent).toList();
			} catch (Exception e2) {}
		}
		return null;
	}
	
	@Nullable
	public static Component listToComponent(List<?> list) {
		if (list == null) return null;
		return combineComponents(list.stream().map(Utils::mapToComponent).toList());
	}
	
	@Contract("null,_ -> null; !null,_ -> !null")
	public static ItemMeta addBeforeLore(ItemMeta meta, List<Component> add) {
		if (meta == null || add == null) return meta;
		List<Component> lore = new ArrayList<>(add);
		if (meta.hasLore()) {
			List<Component> oldLore = meta.lore();
			if (oldLore != null) lore.addAll(oldLore);
		}
		meta.lore(lore);
		return meta;
	}
	
	@Contract("null,_,_ -> null; !null,_,_ -> !null")
	public static ItemMeta addInsideLore(ItemMeta meta, List<Component> add, int idx) {
		if (meta == null || add == null) return meta;
		List<Component> lore = new ArrayList<>();
		if (meta.hasLore()) {
			List<Component> oldLore = meta.lore();
			if (oldLore != null) lore.addAll(oldLore);
		}
		if (lore.isEmpty()) idx = 0;
		else if (idx > lore.size()) while (idx > lore.size()) idx -= lore.size();
		else while (idx < 0) idx += lore.size();
		lore.addAll(idx,add);
		meta.lore(lore);
		return meta;
	}
	
	@Contract("null,_ -> null; !null,_ -> !null")
	public static ItemMeta addAfterLore(ItemMeta meta, List<Component> add) {
		if (meta == null || add == null) return meta;
		List<Component> lore = new ArrayList<>();
		if (meta.hasLore()) {
			List<Component> oldLore = meta.lore();
			if (oldLore != null) lore.addAll(oldLore);
		}
		lore.addAll(add);
		meta.lore(lore);
		return meta;
	}
	
	@Contract("null,_ -> null")
	public static ItemStack addBeforeLore(ItemStack item, List<Component> add) {
		if (!isNull(item)) item.setItemMeta(addBeforeLore(item.getItemMeta(),add));
		return item;
	}
	
	@Contract("null,_,_ -> null")
	public static ItemStack addInsideLore(ItemStack item, List<Component> add, int idx) {
		if (!isNull(item)) item.setItemMeta(addInsideLore(item.getItemMeta(),add,idx));
		return item;
	}
	
	@Contract("null,_ -> null")
	public static ItemStack addAfterLore(ItemStack item, List<Component> add) {
		if (!isNull(item)) item.setItemMeta(addAfterLore(item.getItemMeta(),add));
		return item;
	}
	
	@Nullable
	@Contract("null -> null")
	public static <V extends Collection<?>> V nullIfEmpty(@Nullable V collection) {
		return collection == null || collection.isEmpty() ? null : collection;
	}
	
	public static <V> V random(@NotNull List<V> list) {
		return list.size() == 1 ? list.get(0) : list.get(ThreadLocalRandom.current().nextInt(list.size()));
	}
	
	public static int getPlayerEXP(@NotNull Player player) {
		int level = player.getLevel();
		int amount = Math.round(level == 0 ? 0 : (level <= 16 ? level * (level + 6) : (level < 32 ? (2.5f * level * level - 40.5f * level + 360) :
				(4.5f * level * level - 162.5f * level + 2220))));
		amount += Math.round(player.getExp() * player.getExpToLevel());
		return amount;
	}
	
	public static void setPlayerEXP(@NotNull Player player, int amount) {
		if (!player.isOnline() || amount < 0) return;
		player.giveExp(-getPlayerEXP(player));
		if (amount > 0) player.giveExp(amount);
	}
	
	/**
	 * @param toEmpty These slots will be emptied if the item is added!
	 * @param toRemove These slots will remove the amounts specified if the item is added!
	 * @return A HashMap containing the slots the item was added to and the respective amounts. Empty = nothing added = fail!
	 */
	@NotNull
	public static List<@NotNull HashMap<@NotNull Integer,@NotNull Integer>> addFully(@NotNull Player player, @NotNull List<ItemStack> items,
																					 @Nullable Map<@NotNull Integer,@NotNull Integer> toRemove, int ... toEmpty) {
		List<HashMap<Integer,Integer>> maps = new ArrayList<>();
		List<@NotNull Integer> empty = Arrays.stream(toEmpty).boxed().toList();
		if (toRemove == null) toRemove = new HashMap<>();
		else toRemove = new HashMap<>(toRemove);
		toRemove.entrySet().removeIf(entry -> !PLAYER_INVENTORY_SLOTS.contains(entry.getKey()));
		if (items.stream().filter(item -> !isNull(item)).toList().isEmpty()) return new ArrayList<>();
		HashMap<Integer,Integer> map;
		for (ItemStack item : items) {
			map = new HashMap<>();
			if (isNull(item)) {
				maps.add(map);
				continue;
			}
			int amount = item.getAmount();
			ItemStack itemStack;
			int added;
			for (int slot : PLAYER_HOLDING_SLOTS) {
				itemStack = subtract(applyIfNotNull(getFromSlot(player,slot),ItemStack::clone),thisOrThatOrNull(toRemove.get(slot),0));
				if (empty.contains(slot) || isNull(itemStack)) added = item.getMaxStackSize();
				else if (itemStack.isSimilar(item)) added = Math.max(0,item.getMaxStackSize() - itemStack.getAmount());
				else continue;
				if (added <= 0) continue;
				map.put(slot,added);
				if ((amount -= added) <= 0) break;
			}
			if (amount > 0) return new ArrayList<>();
			maps.add(map);
		}
		toRemove.forEach((slot,remove) -> setSlot(player,subtract(getFromSlot(player,slot),remove),slot));
		empty.forEach(slot -> setSlot(player,null,slot));
		player.getInventory().addItem(items.stream().filter(item -> !isNull(item)).toList().toArray(new ItemStack[0]));
		return maps;
	}
	
	/**
	 * @param toEmpty These slots will be emptied if the item is added!
	 * @param toRemove These slots will remove the amounts specified if the item is added!
	 * @return A HashMap containing the slots the item was added to and the respective amounts. Empty = nothing added = fail!
	 */
	@NotNull
	public static HashMap<@NotNull Integer,@NotNull Integer> addFully(@NotNull Player player, ItemStack item, @Nullable Map<@NotNull Integer,@NotNull Integer> toRemove, int ... toEmpty) {
		List<HashMap<Integer,Integer>> maps = addFully(player,List.of(item),toRemove,toEmpty);
		return maps.isEmpty() ? new HashMap<>() : maps.get(0);
	}
	
	public static int getWeightedRandomIndexDouble(@NotNull List<@Nullable Double> chances) {
		if (chances.isEmpty()) return -1;
		int last = -1;
		Double sum = 0d, chance;
		for (int i = 0; i < chances.size(); i++) {
			chance = chances.get(i);
			if (chance == null) continue;
			last = i;
			if (chance < 0) return -1;
			else sum += chance;
		}
		if (last < 0) return -1;
		double random = sum * ThreadLocalRandom.current().nextDouble();
		sum = 0d;
		for (int i = 0; i < chances.size(); i++) {
			chance = chances.get(i);
			if (chance == null) continue;
			sum += chance;
			if (sum.compareTo(random) >= 0) return i;
		}
		return last;
	}
	
	public static int getWeightedRandomIndexDecimal(@NotNull List<@Nullable BigDecimal> chances) {
		if (chances.isEmpty()) return -1;
		int last = -1;
		BigDecimal sum = BigDecimal.ZERO, chance;
		for (int i = 0; i < chances.size(); i++) {
			chance = chances.get(i);
			if (chance == null) continue;
			last = i;
			if (chance.compareTo(BigDecimal.ZERO) < 0) return -1;
			else sum = sum.add(chance);
		}
		if (last < 0) return -1;
		BigDecimal random = sum.multiply(BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble()));
		sum = BigDecimal.ZERO;
		for (int i = 0; i < chances.size(); i++) {
			chance = chances.get(i);
			if (chance == null) continue;
			sum = sum.add(chance);
			if (sum.compareTo(random) >= 0) return i;
		}
		return last;
	}
	
	@Nullable
	@Contract("null,_ -> null")
	public static ItemStack add(ItemStack item, int amount) {
		return isNull(item) ? null : item.add(amount);
	}
	
	@Nullable
	@Contract("null,_ -> null")
	public static ItemStack subtract(ItemStack item, int amount) {
		if (isNull(item)) return null;
		if (item.getAmount() > item.getMaxStackSize()) item.setAmount(item.getMaxStackSize());
		else if (item.getAmount() <= 0) return null;
		if (amount <= 0) return item;
		return item.getAmount() <= amount ? null : item.subtract(amount);
	}
	
	@Nullable
	public static <V,T> T applyIfNotNull(@Nullable V obj, @NotNull Function<@NotNull V,T> apply) {
		return obj == null ? null : apply.apply(obj);
	}
	
	public static <V> void runIfNotNull(@Nullable V obj, @NotNull Consumer<@NotNull V> apply) {
		if (obj != null) apply.accept(obj);
	}
	
	@NotNull
	public static <V> V runGetOriginal(@NotNull V obj, @NotNull Consumer<@NotNull V> apply) {
		apply.accept(obj);
		return obj;
	}
	
	@NotNull
	public static <V> V runGetOriginalIf(@NotNull V obj, @NotNull Consumer<@NotNull V> apply, boolean arg) {
		if (arg) apply.accept(obj);
		return obj;
	}
	
	@NotNull
	public static List<@NotNull ItemStack> asAmount(ItemStack item, int amount) {
		List<ItemStack> items = new ArrayList<>();
		if (isNull(item) || amount <= 0) return items;
		ItemStack clone;
		while (amount > 0) {
			clone = item.asQuantity(0).add(amount);
			items.add(clone);
			amount -= clone.getAmount();
		}
		return items;
	}
	
	public static int modulo(int num1, int num2) {
		int result = num1 % num2;
		return result < 0 ? result + num2 : result;
	}
	
	public static long modulo(long num1, long num2) {
		long result = num1 % num2;
		return result < 0 ? result + num2 : result;
	}
	
	@NotNull
	public static Trio<@NotNull BigInteger,@NotNull Integer,@NotNull Integer> formatNumber(BigInteger number, int rounding, @Nullable Integer limitExponent) {
		if (number == null) number = BigInteger.ZERO;
		BigDecimal num = new BigDecimal(number.abs()).setScale(rounding,RoundingMode.FLOOR);
		if (num.compareTo(THOUSAND) < 0) return Trio.of(number,0,0);
		if (limitExponent != null && limitExponent < 1) limitExponent = null;
		int exp = 0;
		while (num.compareTo(THOUSAND) >= 0 && limitExponent != null && exp < limitExponent - 1) {
			num = num.divide(THOUSAND,RoundingMode.FLOOR);
			exp++;
		}
		num = num.multiply(HUNDRED);
		BigInteger[] divide = toBigInteger(num,true).divideAndRemainder(HUNDRED_INT);
		BigInteger big = divide[0];
		if (number.compareTo(BigInteger.ZERO) < 0) big = big.multiply(BigInteger.valueOf(-1));
		int small = divide[1].intValue();
		return Trio.of(big,small,exp * 3);
	}
	
	@Nullable
	@Contract("null -> null")
	public static Material getMaterial(@Nullable String name) {
		return name == null ? null : Material.getMaterial(name.toUpperCase());
	}
	
	public static void setRemovedRecipes(@NotNull Set<@NotNull Recipe> recipes) {
		if (removedRecipes == null) removedRecipes = Collections.unmodifiableSet(recipes);
	}
	
	@NotNull
	@Unmodifiable
	public static Set<@NotNull Recipe> removedRecipes() {
		return removedRecipes;
	}
	
	@NotNull
	public static Component healthComponent(@NotNull Player player) {
		AttributeInstance health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		String max = health == null ? "?" : toString(health.getValue());
		return noItalic(Component.translatable("character.attribute.health",NamedTextColor.RED).
				append(Component.translatable("character.attribute.of_x_x",NamedTextColor.WHITE).
						args(Component.text(toString(player.getHealth()),NamedTextColor.AQUA),Component.text(max,NamedTextColor.GREEN))).
				append(Component.text(" \u2665",NamedTextColor.RED)));
	}
	
	@NotNull
	public static Component armorComponent(@NotNull Player player) {
		AttributeInstance armor = player.getAttribute(Attribute.GENERIC_ARMOR);
		String val = armor == null ? "?" : toString(armor.getValue());
		return noItalic(Component.translatable("attribute.name.generic.armor",NamedTextColor.DARK_GRAY).
				append(Component.translatable("character.attribute.of_x",NamedTextColor.WHITE).args(Component.text(val,NamedTextColor.AQUA))));
	}
	
	@NotNull
	public static Component armorToughnessComponent(@NotNull Player player) {
		AttributeInstance armor = player.getAttribute(Attribute.GENERIC_ARMOR);
		String val = armor == null ? "?" : toString(armor.getValue());
		return noItalic(Component.translatable("attribute.name.generic.armor_toughness",NamedTextColor.GRAY).
				append(Component.translatable("character.attribute.of_x",NamedTextColor.WHITE).args(Component.text(val,NamedTextColor.AQUA))));
	}
	
	@NotNull
	public static Component pingComponent(@NotNull Player player) {
		return noItalic(Component.translatable("chat.player.ping_x",NamedTextColor.GRAY).
				args(Component.translatable("multiplayer.status.ping",NamedTextColor.GREEN).args(Component.text(player.getPing(),NamedTextColor.AQUA))));
	}
	
	@NotNull
	private static List<Component> playerInfoHeadLore(@NotNull Player player) {
		return List.of(healthComponent(player),armorComponent(player),armorToughnessComponent(player));
	}
	
	@NotNull
	public static ItemStack playerInfoHead(@NotNull Player player) {
		return setSkinGetItem(makeItem(Material.PLAYER_HEAD,noItalic(player.teamDisplayName()),playerInfoHeadLore(player),ItemFlag.values()),player);
	}
	
	@NotNull
	public static ItemStack playerInfoHead(@NotNull Player player, @NotNull String skin, @Nullable String name) {
		return setSkinGetItem(makeItem(Material.PLAYER_HEAD,noItalic(player.teamDisplayName()),playerInfoHeadLore(player),ItemFlag.values()),skin,name);
	}
}