package me.DMan16.POPUtils.Restrictions;

import me.DMan16.POPUtils.Events.ArmorEquipEvent;
import me.DMan16.POPUtils.Listeners.Listener;
import me.DMan16.POPUtils.POPUtils;
import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Restrictions {
	public static final Restriction Unequippable = new Unequippable();
	public static final Restriction Unplaceable = new Unplaceable();
	public static final Restriction Undroppable = new Undroppable();
	public static final Restriction Unenchantable = new Unenchantable();
	public static final Restriction Uncraftable = new Uncraftable();
	public static final Restriction DropRemove = new DropRemove();
	public static final Restriction Unforgeable = new Unforgeable();
	public static final Restriction Ungrindable = new Ungrindable();
	public static final Restriction Unfuelable = new Unfuelable();
	public static final Restriction Unstackable = new Unstackable();
	
	private static final List<Restriction> restrictions = Arrays.asList(Unequippable,Unplaceable,Undroppable,Unenchantable,Uncraftable,DropRemove,Unforgeable,Ungrindable,Unfuelable);
	
	@NotNull
	@Unmodifiable
	public static List<@NotNull Restriction> getRestrictions() {
		return restrictions;
	}
	
	@NotNull
	@Unmodifiable
	public static List<@NotNull Restriction> getRestrictions(ItemStack item) {
		if (Utils.isNull(item)) return Arrays.asList();
		return Restrictions.restrictions.stream().filter(restriction -> restriction.is(item)).toList();
	}
	
	@Nullable
	public static Restriction byName(@NotNull String name) {
		if (!name.isEmpty()) for (Restriction restriction : restrictions) if (restriction.name().equalsIgnoreCase(name.replace(" ","_"))) return restriction;
		return null;
	}
	
	private static class Unequippable extends Restriction {
		public Unequippable() {
			super("Unequippable");
		}
		
		@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
		public void onEquip(ArmorEquipEvent event) {
			if (!is(event.getNewArmor())) return;
			restrictionEvent(event,event.getNewArmor(),event.getPlayer(),true);
		}
	}
	
	private static class Unplaceable extends Restriction {
		public Unplaceable() {
			super("Unplaceable");
		}
		
		@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
		public void onPlace(BlockPlaceEvent event) {
			if (!is(event.getItemInHand())) return;
			restrictionEvent(event,event.getItemInHand(),event.getPlayer(),true);
		}
	}
	
	private static class Undroppable extends Restriction {
		public Undroppable() {
			super("Undroppable");
		}
		
		@EventHandler(priority = EventPriority.LOWEST)
		public void onDropItemMainMenu(PlayerDropItemEvent event) {
			if (event.isCancelled() || !is(event.getItemDrop().getItemStack())) return;
			restrictionEvent(event,event.getItemDrop().getItemStack(),event.getPlayer(),true);
		}
	}
	
	private static class Unenchantable extends Restriction {
		public Unenchantable() {
			super("Unenchantable");
		}
		
		@EventHandler(priority = EventPriority.LOWEST)
		public void onEnchantItem(EnchantItemEvent event) {
			if (event.isCancelled() || !is(event.getItem())) return;
			restrictionEvent(event,event.getItem(),event.getEnchanter(),true);
		}
	}
	
	private static class Uncraftable extends Restriction {
		public Uncraftable() {
			super("Uncraftable");
		}
		
		@EventHandler(priority = EventPriority.LOWEST)
		public void onCraftItem(PrepareItemCraftEvent event) {
			if (event.getRecipe() == null || Utils.isNull(event.getInventory().getResult())) return;
			for (ItemStack item : event.getInventory().getMatrix()) if (is(item)) if (restrictionEvent(event,item,event.getView().getPlayer(),true)) {
				event.getInventory().setResult(null);
				break;
			}
		}
	}
	
	private static class Unforgeable extends Restriction {
		public Unforgeable() {
			super("Unforgeable");
		}
		
		@EventHandler(priority = EventPriority.LOWEST)
		public void onForgeItem(InventoryClickEvent event) {
			if (event.isCancelled() || event.getInventory().getType() != InventoryType.ANVIL) return;
			int slot = event.getRawSlot();
			if (slot == 0 || slot == 1) {
				if (event.getAction() == InventoryAction.HOTBAR_SWAP || event.getAction().name().startsWith("PLACE") || event.getAction() == InventoryAction.SWAP_WITH_CURSOR)
					if (is(event.getCursor())) restrictionEvent(event,event.getCursor(),event.getWhoClicked(),true);
			} else if (slot > 2 && slot <= 38) {
				if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
					if (is(event.getCurrentItem())) restrictionEvent(event,event.getCurrentItem(),event.getWhoClicked(),true);
			}
		}
	}
	
	private static class Ungrindable extends Restriction {
		public Ungrindable() {
			super("Ungrindable");
		}
		
		@EventHandler(priority = EventPriority.LOWEST)
		public void onGrindItem(InventoryClickEvent event) {
			if (event.isCancelled() || event.getInventory().getType() != InventoryType.GRINDSTONE) return;
			int slot = event.getRawSlot();
			if (slot == 0 || slot == 1) {
				if (event.getAction() == InventoryAction.HOTBAR_SWAP || event.getAction().name().startsWith("PLACE") || event.getAction() == InventoryAction.SWAP_WITH_CURSOR)
					if (is(event.getCursor())) restrictionEvent(event,event.getCursor(),event.getWhoClicked(),true);
			} else if (slot > 2 && slot <= 38) {
				if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
					if (is(event.getCurrentItem())) restrictionEvent(event,event.getCurrentItem(),event.getWhoClicked(),true);
			}
		}
	}
	
	private static class Unfuelable extends Restriction {
		public Unfuelable() {
			super("Unfuelable");
		}
		
		@EventHandler(priority = EventPriority.LOWEST)
		public void onFurnaceItem(InventoryClickEvent event) {
			if (event.isCancelled() || (event.getInventory().getType() != InventoryType.FURNACE && event.getInventory().getType() != InventoryType.BLAST_FURNACE &&
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
	
	private static class Unstackable extends Restriction {
		public Unstackable() {
			super("Unstackable");
		}
		
		@NotNull
		protected String keyValue() {
			return System.currentTimeMillis() + " + " + ThreadLocalRandom.current().nextInt(1000000) + " = " + UUID.randomUUID();
		}
	}
	
	private static class DropRemove extends Restriction {
		public DropRemove() {
			super("DropRemove");
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onDropItemMainMenu(PlayerDropItemEvent event) {
			if (!event.isCancelled() && is(event.getItemDrop().getItemStack())) event.getItemDrop().remove();
		}
	}
	
	private static class RecipeRemove extends Restriction {
		public RecipeRemove() {
			super("RecipeRemove");
		}

		@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
		public void onCraftItem(CraftItemEvent event) {
			if (!is(event.getRecipe().getResult())) return;
			if (restrictionEvent(event,event.getRecipe().getResult(),event.getWhoClicked(),false)) new BukkitRunnable() {
				public void run() {
					event.getWhoClicked().undiscoverRecipe((event.getRecipe() instanceof ShapedRecipe) ? ((ShapedRecipe) event.getRecipe()).getKey() :
							((ShapelessRecipe) event.getRecipe()).getKey());
				}
			}.runTask(POPUtils.getInstance());
		}
	}

	private static class RecipeMust extends Restriction {
		public RecipeMust() {
			super("RecipeMust");
		}

		@EventHandler(priority = EventPriority.LOWEST)
		public void onCraftItem(PrepareItemCraftEvent event) {
			if (event.getRecipe() == null || !is(event.getInventory().getResult())) return;
			if (!event.getView().getPlayer().hasDiscoveredRecipe((event.getRecipe() instanceof ShapedRecipe) ? ((ShapedRecipe) event.getRecipe()).getKey() :
					((ShapelessRecipe) event.getRecipe()).getKey()))
				if (restrictionEvent(event,event.getRecipe().getResult(),event.getView().getPlayer(),true)) event.getInventory().setResult(null);
		}
	}
	
	public static abstract class Restriction extends Listener {
		private final String name;
		
		Restriction(@NotNull String name) {
			this.name = Utils.splitCapitalize(name.toLowerCase().replace("_"," ")).replace(" ","");
			register(POPUtils.getInstance());
		}
		
		public String name() {
			return name;
		}
		
		private NamespacedKey key() {
			return new NamespacedKey(POPUtils.getInstance(),name());
		}
		
		@NotNull
		protected String keyValue() {
			return "";
		}
		
		public ItemStack add(ItemStack item) {
			if (Utils.isNull(item)) return item;
			ItemMeta meta = item.getItemMeta();
			if (meta == null) return item;
			meta.getPersistentDataContainer().set(key(),PersistentDataType.STRING,keyValue());
			item.setItemMeta(meta);
			return item;
		}
		
		public ItemStack remove(ItemStack item) {
			if (Utils.isNull(item)) return item;
			ItemMeta meta = item.getItemMeta();
			if (meta == null) return item;
			meta.getPersistentDataContainer().remove(key());
			item.setItemMeta(meta);
			return item;
		}
		
		@Contract(value = "null -> false",pure = true)
		public boolean is(ItemStack item) {
			if (Utils.isNull(item)) return false;
			ItemMeta meta = item.getItemMeta();
			if (meta == null) return false;
			return meta.getPersistentDataContainer().has(key(),PersistentDataType.STRING);
		}
		
		protected boolean restrictionEvent(Event event, ItemStack item, HumanEntity player, boolean cancelIfPossible) {
			ItemPreRestrictEvent preRestrictEvent = new ItemPreRestrictEvent(this,player,item);
			if (!preRestrictEvent.callEvent()) return false;
			preRestrictEvent.immediateTasks().forEach(Runnable::run);
			preRestrictEvent.delayedTasks().forEach(Runnable::run);
			if (cancelIfPossible && (event instanceof Cancellable)) ((Cancellable) event).setCancelled(true);
			ItemPostRestrictEvent postRestrictEvent = new ItemPostRestrictEvent(this,player,item);
			postRestrictEvent.callEvent();
			postRestrictEvent.immediateTasks().forEach(Runnable::run);
			postRestrictEvent.delayedTasks().forEach(Runnable::run);
			return true;
		}
	}
}