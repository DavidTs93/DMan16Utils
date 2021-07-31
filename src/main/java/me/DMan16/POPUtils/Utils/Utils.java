package me.DMan16.POPUtils.Utils;

import me.DMan16.POPUpdater.POPUpdaterMain;
import me.DMan16.POPUtils.Classes.Pair;
import me.DMan16.POPUtils.Listeners.CancelPlayers;
import me.DMan16.POPUtils.POPUtilsMain;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serial;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {
	private static final Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
	private static final Pattern unicode = Pattern.compile("\\\\u\\+[a-fA-F0-9]{4}");
	private static final Set<Long> sessionIDs = new HashSet<>();
	private static List<Material> interactable = null;
	
	static {
		createInteractable();
	}
	
	@NotNull
	public  static String javaVersion() {
		String javaVersion = "";
		Iterator<Entry<Object,Object>> systemProperties = System.getProperties().entrySet().iterator();
		while (systemProperties.hasNext() && javaVersion.isEmpty()) {
			Entry<Object,Object> property = systemProperties.next();
			if (property.getKey().toString().equalsIgnoreCase("java.version")) javaVersion = property.getValue().toString();
		}
		return javaVersion;
	}
	
	/**
	 * @return Strips the string from colors and converts to color code using {@code&}.
	 * 1.16+ HEX colors can be used via {@code&#??????}.
	 */
	@NotNull
	public static String chatColors(@NotNull String str) {
//		str = chatColorsStrip(str);
		Matcher match = unicode.matcher(str);
		while (match.find()) {
			String code = str.substring(match.start(),match.end());
			str = str.replace(code,Character.toString((char) Integer.parseInt(code.replace("\\u+",""),16)));
			match = unicode.matcher(str);
		}
		match = pattern.matcher(str);
		while (match.find()) {
			String color = str.substring(match.start(),match.end());
			str = str.replace(color, ChatColor.of(color.replace("&","")) + "");
			match = pattern.matcher(str);
		}
		return ChatColor.translateAlternateColorCodes('&',str);
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
		return chatColors("&d[" + POPUtilsMain.pluginNameColors + "&d]&r " + str);
	}

	public static void chatColorsPlugin(@NotNull CommandSender sender, @NotNull String str) {
		sender.sendMessage(chatColorsPlugin(str));
	}

	@NotNull
	public static String chatColorsUsage(@NotNull String str) {
		return chatColors("&cUsage: &r/" + POPUtilsMain.pluginNameColors + "&r " + str);
	}

	public static void chatColorsUsage(@NotNull CommandSender sender, @NotNull String str) {
		sender.sendMessage(chatColorsUsage(str));
	}
	
	/**
	 * Revert color codes using {@code&}
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
	
	public static List<String> chatColorsToString(@NotNull List<String> list, @NotNull String colorCode) {
		List<String> newList = new ArrayList<>();
		for (String str : list) if (str != null) {
			if (str.trim().isEmpty()) newList.add("");
			else newList.add(chatColorsToString(str,colorCode));
		}
		return newList;
	}
	
	public static void chatColorsActionBar(@NotNull Player player, @NotNull Component ... components) {
		Component comp = Component.empty();
		for (Component component : components) comp = comp.append(component);
		player.sendActionBar(comp);
	}
	
	@NotNull
	public static String splitCapitalize(String str, String splitReg) {
		return splitCapitalize(str,splitReg,"&");
	}
	
	@NotNull
	public static String splitCapitalize(@Nullable String str, @Nullable String splitReg, @NotNull String colorCode) {
		if (str == null || str.trim().isEmpty()) return "";
		String[] splitName;
		if (splitReg == null || splitReg.trim().isEmpty()) splitName = new String[]{str};
		else splitName = str.split(splitReg);
		StringBuilder newStr = new StringBuilder();
		for (String sub : splitName) {
			boolean found = false;
			int i = 0;
			for (; i < sub.length() - 1; i++) {
				try {
					if (sub.substring(i - 1,i).equalsIgnoreCase(colorCode)) continue;
				} catch (Exception e) {}
				if (sub.substring(i,i+1).matches("[a-zA-Z]+")) {
					found = true;
					break;
				}
			}
			if (found) newStr.append(sub,0,i).append(sub.substring(i,i + 1).toUpperCase()).append(sub.substring(i + 1).toLowerCase()).append(" ");
		}
		Pattern pattern = Pattern.compile(" " + colorCode + "[a-zA-Z0-9]Of ");
		Matcher match = pattern.matcher(newStr.toString());
		while (match.find()) {
			String code = newStr.substring(match.start(),match.end());
			newStr = new StringBuilder(newStr.toString().replace(code, code.replace("Of ", "of ")));
			match = pattern.matcher(newStr.toString());
		}
		pattern = Pattern.compile(" " + colorCode + "[a-zA-Z0-9]The ");
		match = pattern.matcher(newStr.toString());
		while (match.find()) {
			String code = newStr.substring(match.start(),match.end());
			newStr = new StringBuilder(newStr.toString().replace(code, code.replace("The ", "the ")));
			match = pattern.matcher(newStr.toString());
		}
		return newStr.toString().replace(" Of ", " of ").replace(" The ", " the ").trim();
	}
	
	@NotNull
	public static String chatColorsStrip(@NotNull String str) {
		return ChatColor.stripColor(str);
	}
	
	@NotNull
	public static String encode(@NotNull String str, @Nullable String regSplit, @Nullable String regJoin) {
		return String.join(regJoin == null ? "" : regJoin,str.split(regSplit == null ? "" : regSplit));
	}
	
	@NotNull
	public static List<Integer> getPlayerInventorySlots() {
		List<Integer> slots = new ArrayList<>();
		slots.add(-106);
		for (int i = 0; i < 4 * 9; i++) slots.add(i);
		for (int i = 100; i <= 103; i++) slots.add(i);
		return slots;
	}
	
	@Nullable
	public static ItemStack getFromSlot(@NotNull Player player, int slot) {
		ItemStack item = null;
		if (slot == -106) item = player.getInventory().getItemInOffHand();
		else if (slot < 0);
		else if (slot == 100) item = player.getInventory().getBoots();
		else if (slot == 101) item = player.getInventory().getLeggings();
		else if (slot == 102) item = player.getInventory().getChestplate();
		else if (slot == 103) item = player.getInventory().getHelmet();
		else item = player.getInventory().getItem(slot);
		return item;
	}
	
	public static int getSlot(@NotNull Player player, @Nullable EquipmentSlot slot) {
		if (slot == null) return -1;
		else if (slot == EquipmentSlot.HEAD) return 103;
		else if (slot == EquipmentSlot.CHEST) return 102;
		else if (slot == EquipmentSlot.LEGS) return 101;
		else if (slot == EquipmentSlot.FEET) return 100;
		else if (slot == EquipmentSlot.OFF_HAND) return -106;
		return player.getInventory().getHeldItemSlot();
	}
	
	public static void setItemSlot(@NotNull Player player, @Nullable ItemStack item, int slot) {
		if (slot == -106) player.getInventory().setItemInOffHand(item);
		else if (slot == 100) player.getInventory().setBoots(item);
		else if (slot == 101) player.getInventory().setLeggings(item);
		else if (slot == 102) player.getInventory().setChestplate(item);
		else if (slot == 103) player.getInventory().setHelmet(item);
		else if (slot >= 0) player.getInventory().setItem(slot,item);
	}
	
	@Nullable
	public static ItemStack addDamage(@Nullable ItemStack item, int damage) {
		if (isNull(item)) return item;
		return setDamage(item,item.getType().getMaxDurability() + damage);
	}
	
	@Nullable
	public static ItemStack setDamage(@Nullable ItemStack item, int damage) {
		if (isNull(item)) return item;
		int maxDMG = item.getType().getMaxDurability();
		if (maxDMG <= 0) return item;
		Damageable meta = (Damageable) item.getItemMeta();
		if (meta.getDamage() >= maxDMG || damage >= maxDMG) return item;
		meta.setDamage(Math.max(damage,0));
		item.setItemMeta((ItemMeta) meta);
		return item;
	}
	
	/**
	 * @return if the items are the identical besides the amount
	 */
	public static boolean sameItem(@Nullable ItemStack item1, @Nullable ItemStack item2) {
		if (item1 == null || item2 == null) return item1 == item2;
		return item1.isSimilar(item2);
	}
	
	/**
	 * @return if the items are the identical besides the amount, the display name, and the durability
	 */
	public static boolean similarItem(@Nullable ItemStack item1, @Nullable ItemStack item2, boolean ignoreDurability) {
		if (item1 == null || item2 == null) return item1 == item2;
		if (item1.isSimilar(item2)) return true;
		ItemStack cmp1 = item1.clone();
		ItemStack cmp2 = item2.clone();
		if (ignoreDurability) {
			cmp1 = setDamage(cmp1,0);
			cmp2 = setDamage(cmp2,0);
			return similarItem(cmp1,cmp2,false);
		}
		ItemMeta meta1 = cmp1.getItemMeta();
		ItemMeta meta2 = cmp2.getItemMeta();
		meta1.displayName(null);
		meta2.displayName(null);
		cmp1.setItemMeta(meta1);
		cmp2.setItemMeta(meta2);
		return cmp1.isSimilar(cmp2);
	}
	
	/**
	 * Pick up items properly from custom set results, example: Anvil, Smithing Table
	 */
	public static void uniqueCraftingHandle(@NotNull InventoryClickEvent event, int reduce, float pitch) {
		if (!(event.getWhoClicked() instanceof Player player) || isNull(event.getInventory().getItem(0)) || isNull(event.getInventory().getItem(1)) ||
				event.getInventory().getItem(1).getAmount() < reduce || (!event.isShiftClick() && !event.isLeftClick() &&
				!event.isRightClick() && event.getHotbarButton() <= -1)) return;
		if (event.getRawSlot() != 2) return;
		Inventory inv = event.getInventory();
		ItemStack result = inv.getItem(2);
		if (event.isShiftClick()) {
			if (player.getInventory().firstEmpty() == -1) {
				event.setCancelled(true);
				return;
			}
			givePlayer(player,result,false);
		} else if(event.getHotbarButton() != -1) {
			if (!isNull(getFromSlot(player,event.getHotbarButton()))) {
				event.setCancelled(true);
				return;
			}
			setItemSlot(player,result,event.getHotbarButton());
		} else player.setItemOnCursor(result);
		inv.setItem(0,null);
		if (inv.getItem(1).getAmount() > reduce) inv.getItem(1).setAmount(inv.getItem(1).getAmount() - reduce);
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
	
	public static boolean isNull(@Nullable ItemStack item) {
		return item == null || isNull(item.getType());
	}
	
	public static boolean isNull(@Nullable Material material) {
		return material == null || material.isAir();
	}
	
	@NotNull
	public static JString JString(@NotNull String str) {
		return new JString(str);
	}
	
	private static class JString implements java.io.Serializable {
		@Serial
		private static final long serialVersionUID = 1L;
		String value;
		public JString(@NotNull String value) {
			super();
			this.value = value;
		}
		
		public @NotNull String getValue() {
			return value;
		}
		
		public void setValue(@NotNull String value) {
			this.value = value;
		}
		
		@Override
		public @NotNull String toString(){
			return this.value;
		}
	}
	
	public static class PairInt extends Pair<Integer,Integer> {
		public PairInt(int first, int second) {
			super(first,second);
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
	@Nullable
	public static Item givePlayer(@NotNull Player player, ItemStack item, @Nullable Location drop, boolean glow) {
		if (isNull(item)) return null;
		if (!player.isDead() && player.getInventory().addItem(item).isEmpty()) return null;
		if (drop != null) {
			Item droppedItem = dropItem(drop,item);
			droppedItem.setGlowing(glow);
			return droppedItem;
		}
		return null;
	}
	
	/**
	 * Drop an item naturally to the world at a given location
	 * @return the dropped item
	 */
	@NotNull
	public static Item dropItem(@NotNull Location drop, @NotNull ItemStack item) {
		return drop.getWorld().dropItemNaturally(drop,item);
	}
	
	@Nullable
	public static Item givePlayer(@NotNull Player player, ItemStack item, boolean glow) {
		return givePlayer(player,item,player.getLocation(),glow);
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
	
	/**
	 * @param digitsAfterDot >= 0
	 * @return the number rounded to specified digits after the dot
	 */
	public static double roundAfterDot(double num, int digitsAfterDot) {
		if (digitsAfterDot < 0) return num;
		if (digitsAfterDot == 0) return (double) Math.round(num);
		return Double.parseDouble((new DecimalFormat("0." + "0".repeat(digitsAfterDot))).format(num));
	}
	
	/**
	 * @return serialized version
	 */
	@Nullable
	public static String ObjectToBase64(@NotNull Object obj) {
		try {
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
	public static Object ObjectFromBase64(@NotNull String data) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			dataInput.readInt();
			Object obj = dataInput.readObject();
			dataInput.close();
			return obj;
		} catch (Exception e) {}
		return null;
    }
	
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
	public static List<Component> ListStringToListComponent(@NotNull List<String> strs) {
		List<Component> list = new ArrayList<>();
		for (String str : strs) list.add(Component.text(str).decoration(TextDecoration.ITALIC,false));
		return list;
	}
	
	@NotNull
	public static ItemStack cloneChange(@NotNull ItemStack base, @Nullable Component name, @Nullable List<Component> lore, int model, boolean removeFlags, ItemFlag ... flags) {
		ItemStack item = base.clone();
		ItemMeta meta = item.getItemMeta();
		if (name == null) meta.displayName(null);
		else if (!name.equals(Component.empty())) meta.displayName(name);
		if (removeFlags) for (ItemFlag flag : ItemFlag.values()) meta.removeItemFlags(flag);
		for (ItemFlag flag : flags) if (flag != null) meta.addItemFlags(flag);
		if (model > 0) meta.setCustomModelData(model);
		else if (model == 0) meta.setCustomModelData(null);
		if (lore == null) meta.lore(null);
		else if (!lore.isEmpty()) meta.lore(lore);
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
	public static ItemStack makePotion(@Nullable Component name, int model, @Nullable List<Component> lore, boolean hide, Color color, @Nullable PotionData base, PotionEffect... effects) {
		return makePotion(Material.POTION,name,model,lore,hide,color,base,effects);
	}
	
	@NotNull
	public static ItemStack makePotionSplash(@Nullable Component name, int model, @Nullable List<Component> lore, boolean hide, Color color, @Nullable PotionData base, PotionEffect ... effects) {
		return makePotion(Material.SPLASH_POTION,name,model,lore,hide,color,base,effects);
	}
	
	private static @NotNull ItemStack makePotion(@NotNull Material material, @Nullable Component name, int model, @Nullable List<Component> lore, boolean hide, Color color, @Nullable PotionData base, PotionEffect ... effects) {
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
	
	@SafeVarargs
	public static <V> @NotNull List<V> joinLists(List<? extends V> ... lists) {
		List<V> list = new ArrayList<>();
		for (List<? extends V> l : lists) if (l != null) list.addAll(l);
		return list;
	}
	
	@NotNull
	public static Inventory makeInventory(@Nullable InventoryHolder owner, int lines, Component name) {
		if (name == null) return Bukkit.createInventory(owner,lines * 9);
		return Bukkit.createInventory(owner,lines * 9,name);
	}
	
	public static Inventory makeInventory(@Nullable InventoryHolder owner, @NotNull InventoryType type, Component name) {
		if (name == null) return Bukkit.createInventory(owner,type);
		return Bukkit.createInventory(owner,type,name);
	}
	
	/**
	 * Lasts for 10 minutes
	 */
	public static long newSessionID() {
		long id = System.currentTimeMillis();
		while (sessionIDs.contains(id)) id = System.currentTimeMillis();
		long ID = id;
		sessionIDs.add(ID);
		new BukkitRunnable() {
			public void run() {
				sessionIDs.remove(ID);
			}
		}.runTaskLater(POPUtilsMain.getInstance(),10 * 60 * 20);
		return id;
	}
	
	public static boolean isPlayerNPC(@NotNull Player player) {
		if (POPUtilsMain.getCitizensManager() == null) return false;
		return POPUtilsMain.getCitizensManager().isNPC(player);
	}
	
	public static void addCancelledPlayer(@NotNull Player player) {
		CancelPlayers.addPlayer(player);
	}
	
	public static void addCancelledPlayer(@NotNull Player player, boolean allowRotation, boolean disableDamage) {
		CancelPlayers.addPlayer(player,allowRotation,disableDamage);
	}
	
	public static void removeCancelledPlayer(@NotNull Player player) {
		CancelPlayers.removePlayer(player);
	}
	
	public static boolean isPlayerCancelled(@NotNull Player player) {
		return CancelPlayers.isPlayerCancelled(player);
	}
	
	public static void savePlayer(@NotNull Player player) {
		if (isPlayerNPC(player)) return;
		player.saveData();
//		if (Bukkit.getPluginManager().getPlugin("AxInventories") != null) me.DMan16.AxInventories.AxInventories.save(player);
	}
	
	@Nullable
	public static UUID getPlayerUUIDByName(@NotNull String name) {
		return POPUpdaterMain.getPlayerUUIDByName(name);
	}
	
	@Nullable
	public static String getPlayerNameByUUID(@NotNull UUID ID) {
		return POPUpdaterMain.getPlayerNameByUUID(ID);
	}
}