package me.DMan16.DMan16Utils.Restrictions;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Events.ArmorEquipEvent;
import me.DMan16.DMan16Utils.Interfaces.Listener;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Restrictions {
	public static final Unequippable Unequippable = new Unequippable();
	public static final Unplaceable Unplaceable = new Unplaceable();
	public static final Undroppable Undroppable = new Undroppable();
	public static final Unenchantable Unenchantable = new Unenchantable();
	public static final Uncraftable Uncraftable = new Uncraftable();
	public static final DropRemove DropRemove = new DropRemove();
	public static final Unforgeable Unforgeable = new Unforgeable();
	public static final Ungrindable Ungrindable = new Ungrindable();
	public static final Unfuelable Unfuelable = new Unfuelable();
	public static final Unstackable Unstackable = new Unstackable();
	public static final Untradeable Untradeable = new Untradeable();
	public static final RecipeRemove RecipeRemove = new RecipeRemove();
	public static final RecipeMust RecipeMust = new RecipeMust();
	
	private static final List<Restriction> restrictions = Arrays.asList(Unequippable,Unplaceable,Undroppable,Unenchantable,Uncraftable,DropRemove,Unforgeable,Ungrindable,Unfuelable,Untradeable);
	
	@NotNull
	@Unmodifiable
	public static List<@NotNull Restriction> getRestrictions() {
		return restrictions;
	}
	
	@NotNull
	@Unmodifiable
	public static List<@NotNull Restriction> getRestrictions(ItemStack item) {
		return getRestrictions(Utils.isNull(item) ? null : item.getItemMeta());
	}
	
	@NotNull
	@Unmodifiable
	public static List<@NotNull Restriction> getRestrictions(ItemMeta meta) {
		if (meta == null) return List.of();
		return Restrictions.restrictions.stream().filter(restriction -> restriction.is(meta)).toList();
	}
	
	@Nullable
	@Contract("null -> null")
	public static Restriction byName(@Nullable String name) {
		if ((name = Utils.fixKey(name)) != null) for (Restriction restriction : restrictions) if (restriction.name().equalsIgnoreCase(name)) return restriction;
		return null;
	}
	
	public static ItemStack addRestrictions(ItemStack item,@NotNull Restriction ... restrictions) {
		if (Utils.isNull(item)) return item;
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return item;
		item.setItemMeta(addRestrictions(meta,restrictions));
		return item;
	}
	
	public static ItemMeta addRestrictions(ItemMeta meta,@NotNull Restriction ... restrictions) {
		if (meta != null) for (Restriction restriction : restrictions) meta = restriction.add(meta);
		return meta;
	}
	
	public static ItemStack addRestrictions(ItemStack item,List<@NotNull Restriction> restrictions) {
		if (Utils.isNull(item)) return item;
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return item;
		item.setItemMeta(addRestrictions(meta,restrictions));
		return item;
	}
	
	public static ItemMeta addRestrictions(ItemMeta meta,List<@NotNull Restriction> restrictions) {
		if (meta != null && restrictions != null) for (Restriction restriction : restrictions) meta = restriction.add(meta);
		return meta;
	}
	
	public static ItemStack removeRestrictions(ItemStack item,@NotNull Restriction ... restrictions) {
		if (Utils.isNull(item)) return item;
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return item;
		item.setItemMeta(removeRestrictions(meta,restrictions));
		return item;
	}
	
	public static ItemMeta removeRestrictions(ItemMeta meta,@NotNull Restriction ... restrictions) {
		if (meta != null) for (Restriction restriction : restrictions) meta = restriction.remove(meta);
		return meta;
	}
	
	public static ItemStack removeRestrictions(ItemStack item,List<@NotNull Restriction> restrictions) {
		if (Utils.isNull(item)) return item;
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return item;
		item.setItemMeta(removeRestrictions(meta,restrictions));
		return item;
	}
	
	public static ItemMeta removeRestrictions(ItemMeta meta,List<@NotNull Restriction> restrictions) {
		if (meta != null && restrictions != null) for (Restriction restriction : restrictions) meta = restriction.remove(meta);
		return meta;
	}
	
	public static class Unequippable extends Restriction {
		private Unequippable() {
			super("unequippable");
		}
		
		@EventHandler(ignoreCancelled = true,priority = EventPriority.LOWEST)
		public void onEquip(ArmorEquipEvent event) {
			if (!is(event.getNewArmor())) return;
			restrictionEvent(event,event.getNewArmor(),event.getPlayer(),true);
		}
	}
	
	public static class Unplaceable extends Restriction {
		private Unplaceable() {
			super("unplaceable");
		}
		
		@EventHandler(ignoreCancelled = true,priority = EventPriority.LOWEST)
		public void onPlace(BlockPlaceEvent event) {
			if (!is(event.getItemInHand())) return;
			restrictionEvent(event,event.getItemInHand(),event.getPlayer(),true);
		}
	}
	
	public static class Undroppable extends Restriction {
		private Undroppable() {
			super("undroppable");
		}
		
		@EventHandler(ignoreCancelled = true,priority = EventPriority.LOWEST)
		public void onDropItemMainMenu(PlayerDropItemEvent event) {
			if (!is(event.getItemDrop().getItemStack())) return;
			restrictionEvent(event,event.getItemDrop().getItemStack(),event.getPlayer(),true);
		}
	}
	
	public static class Unenchantable extends Restriction {
		private Unenchantable() {
			super("unenchantable");
		}
		
		@EventHandler(ignoreCancelled = true,priority = EventPriority.LOWEST)
		public void onPrepareEnchant(PrepareItemEnchantEvent event) {
			if (!is(event.getItem())) return;
			restrictionEvent(event,event.getItem(),event.getEnchanter(),true);
		}
	}
	
	public static class Uncraftable extends Restriction {
		private final @NotNull Set<@NotNull NamespacedKey> ignore = new HashSet<>();
		
		private Uncraftable() {
			super("uncraftable");
		}
		
		public boolean addIgnore(@NotNull NamespacedKey key) {
			return ignore.add(key);
		}
		
		public boolean removeIgnore(@NotNull NamespacedKey key) {
			return ignore.remove(key);
		}
		
		public boolean ignore(@NotNull NamespacedKey key) {
			return ignore.contains(key);
		}
		
		@EventHandler(priority = EventPriority.LOWEST)
		public void onCraftItem(PrepareItemCraftEvent event) {
			Recipe recipe = event.getRecipe();
			if (recipe == null || ((recipe instanceof Keyed keyed) && ignore.contains(keyed.getKey())) || Utils.isNull(event.getInventory().getResult())) return;
			for (ItemStack item : event.getInventory().getMatrix()) if (is(item)) if (restrictionEvent(event,item,event.getView().getPlayer(),true)) {
				event.getInventory().setResult(null);
				break;
			}
		}
	}
	
	public static class Unforgeable extends Restriction {
		private Unforgeable() {
			super("unforgeable");
		}
		
		@EventHandler(priority = EventPriority.LOWEST)
		public void onPrepare(PrepareSmithingEvent event) {
			if (is(event.getResult())) if (restrictionEvent(event,event.getResult(),event.getView().getPlayer(),true)) event.setResult(null);
		}
	}
	
	public static class Ungrindable extends Restriction {
		private Ungrindable() {
			super("ungrindable");
		}
		
		@EventHandler(priority = EventPriority.LOWEST)
		public void onPrepare(PrepareResultEvent event) {
			if ((event.getInventory() instanceof GrindstoneInventory grindstone) && is(event.getResult()))
				if (restrictionEvent(event,event.getResult(),event.getView().getPlayer(),true)) event.setResult(null);
		}
	}
	
	public static class Unfuelable extends Restriction {
		private Unfuelable() {
			super("unfuelable");
		}
		
		@EventHandler(ignoreCancelled = true,priority = EventPriority.LOWEST)
		public void onFurnaceItem(InventoryClickEvent event) {
			if ((event.getInventory().getType() != InventoryType.FURNACE && event.getInventory().getType() != InventoryType.BLAST_FURNACE &&
					event.getInventory().getType() != InventoryType.SMOKER)) return;
			int slot = event.getRawSlot();
			if (slot == 1) {
				if (event.getAction() == InventoryAction.HOTBAR_SWAP || event.getAction().name().startsWith("PLACE") || event.getAction() == InventoryAction.SWAP_WITH_CURSOR)
					if (is(event.getCursor())) restrictionEvent(event,event.getCursor(),event.getWhoClicked(),true);
			} else if (slot > 2 && slot <= 38) {
				if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
					if (is(event.getCurrentItem()) && event.getCurrentItem().getType().isBurnable()) restrictionEvent(event,event.getCurrentItem(),event.getWhoClicked(),true);
			}
		}
		
		@EventHandler(priority = EventPriority.LOWEST)
		public void onFurnaceBurn(FurnaceBurnEvent event) {
			if (is(event.getFuel()) && event.getFuel().getType().isBurnable()) restrictionEvent(event,event.getFuel(),null,true);
		}
	}
	
	public static class Unstackable extends Restriction {
		private Unstackable() {
			super("unstackable");
		}
		
		@NotNull
		protected String keyValue() {
			return System.currentTimeMillis() + " + " + ThreadLocalRandom.current().nextInt(1000000) + " = " + UUID.randomUUID();
		}
		
		@EventHandler(ignoreCancelled = true,priority = EventPriority.LOWEST)
		public void onClick(InventoryClickEvent event) {
			if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE && is(event.getCursor())) new BukkitRunnable() {
				public void run() {
					Inventory inventory = event.getClickedInventory();
					if (inventory != null) try {
						ItemStack item = inventory.getItem(event.getSlot());
						if (is(item)) {
							item.setAmount(1);
							inventory.setItem(event.getSlot(),add(remove(item)));
						}
					} catch (Exception e) {}
				}
			}.runTaskLater(DMan16UtilsMain.getInstance(),1);
		}
		
		@EventHandler(ignoreCancelled = true,priority = EventPriority.LOWEST)
		public void onDrag(InventoryDragEvent event) {
			if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE && is(event.getOldCursor())) event.setCancelled(true);
		}
		
		@EventHandler(ignoreCancelled = true,priority = EventPriority.LOWEST)
		public void onDrop(PlayerDropItemEvent event) {
			ItemStack item = event.getItemDrop().getItemStack();
			if (event.getPlayer().getGameMode() != GameMode.CREATIVE || !is(item)) return;
			item.setAmount(1);
			event.getItemDrop().setItemStack(add(remove(item)));
		}
		
		@EventHandler(priority = EventPriority.LOWEST)
		public void onCraft(PrepareItemCraftEvent event) {
			if (event.getRecipe() != null || Utils.notNull(event.getInventory().getResult())) {
				ItemStack item = event.getInventory().getResult();
				if (is(item)) event.getInventory().setResult(add(item));
//				return;
			}
//			ItemStack[] matrix = event.getInventory().getMatrix();
//			ItemStack[] items = new ItemStack[matrix.length];
//			boolean found = false;
//			for (int i = 0; i < matrix.length; i++) {
//				if (is(matrix[i])) found = true;
//				items[i] = Utils.isNull(matrix[i]) ? null : remove(matrix[i].clone());
//			}
//			if (!found) return;
//			Recipe recipe = Bukkit.getCraftingRecipe(items,event.getView().getPlayer().getWorld());
//			if (recipe != null) new BukkitRunnable() {
//				public void run() {
//					event.getInventory().setResult(recipe.getResult());
//				}
//			}.runTaskLater(DMan16UtilsMain.getInstance(),1);
		}
	}
	
	public static class Untradeable extends Restriction {
		private Untradeable() {
			super("untradeable");
		}
	}
	
	public static class DropRemove extends Restriction {
		private DropRemove() {
			super("drop_remove");
		}
		
		@EventHandler(ignoreCancelled = true,priority = EventPriority.MONITOR)
		public void onDrop(PlayerDropItemEvent event) {
			if (is(event.getItemDrop().getItemStack())) if (restrictionEvent(event,event.getItemDrop().getItemStack(),event.getPlayer(),true)) event.getItemDrop().remove();
		}
		
		@EventHandler(ignoreCancelled = true,priority = EventPriority.HIGHEST)
		public void onSpawn(EntitySpawnEvent event) {
			if ((event.getEntity() instanceof Item item) && is(item.getItemStack())) if (restrictionEvent(event,item.getItemStack(),null,true)) item.remove();
		}
	}
	
	public static class RecipeRemove extends Restriction {
		private RecipeRemove() {
			super("RecipeRemove");
		}

		@EventHandler(ignoreCancelled = true,priority = EventPriority.MONITOR)
		public void onCraftItem(CraftItemEvent event) {
			if (!(event.getRecipe() instanceof Keyed keyed) || !is(event.getRecipe().getResult())) return;
			if (restrictionEvent(event,event.getRecipe().getResult(),event.getWhoClicked(),false)) new BukkitRunnable() {
				public void run() {
					event.getWhoClicked().undiscoverRecipe(keyed.getKey());
				}
			}.runTask(DMan16UtilsMain.getInstance());
		}
	}

	public static class RecipeMust extends Restriction {
		private RecipeMust() {
			super("RecipeMust");
		}

		@EventHandler(priority = EventPriority.LOWEST)
		public void onCraftItem(PrepareItemCraftEvent event) {
			if (!(event.getRecipe() instanceof Keyed keyed) || !is(event.getInventory().getResult())) return;
			if (!event.getView().getPlayer().hasDiscoveredRecipe(keyed.getKey())) if (restrictionEvent(event,event.getRecipe().getResult(),event.getView().getPlayer(),true)) event.getInventory().setResult(null);
		}
	}
	
	public static abstract class Restriction implements Listener {
		private final String name;
		
		private Restriction(@NotNull String name) {
			this.name = Utils.fixKey(name);
			register(DMan16UtilsMain.getInstance());
		}
		
		@NotNull
		public String name() {
			return name;
		}
		
		@NotNull
		private NamespacedKey key() {
			return new NamespacedKey(DMan16UtilsMain.getInstance(),name());
		}
		
		@NotNull
		protected String keyValue() {
			return "";
		}
		
		public ItemMeta add(ItemMeta meta) {
			if (meta != null) meta.getPersistentDataContainer().set(key(),PersistentDataType.STRING,keyValue());
			return meta;
		}
		
		public ItemStack add(ItemStack item) {
			if (Utils.isNull(item)) return item;
			ItemMeta meta = item.getItemMeta();
			if (meta != null) item.setItemMeta(add(meta));
			return item;
		}
		
		public ItemMeta remove(ItemMeta meta) {
			if (meta != null) meta.getPersistentDataContainer().remove(key());
			return meta;
		}
		
		public ItemStack remove(ItemStack item) {
			if (Utils.isNull(item)) return item;
			ItemMeta meta = item.getItemMeta();
			if (meta != null) item.setItemMeta(remove(meta));
			return item;
		}
		
		public boolean is(ItemMeta meta) {
			return meta != null && meta.getPersistentDataContainer().has(key(),PersistentDataType.STRING);
		}
		
		@Contract(value = "null -> false",pure = true)
		public boolean is(ItemStack item) {
			if (Utils.isNull(item)) return false;
			return is(item.getItemMeta());
		}
		
		protected boolean restrictionEvent(Event event,ItemStack item,HumanEntity human,boolean cancelIfPossible) {
			if (!new ItemPreRestrictEvent(human,this,item).callEventAndDoTasksIfNotCancelled()) return false;
			if (cancelIfPossible && (event instanceof Cancellable)) ((Cancellable) event).setCancelled(true);
			new ItemPostRestrictEvent(human,this,item).callEventAndDoTasks();
			return true;
		}
	}
}