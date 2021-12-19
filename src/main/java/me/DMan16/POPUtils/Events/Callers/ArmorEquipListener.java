package me.DMan16.POPUtils.Events.Callers;

import me.DMan16.POPUtils.Interfaces.Listener;
import me.DMan16.POPUtils.Enums.EquipMethod;
import me.DMan16.POPUtils.Events.ArmorEquipEvent;
import me.DMan16.POPUtils.POPUtilsMain;
import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ArmorEquipListener implements Listener {
	public ArmorEquipListener() {
		register(POPUtilsMain.getInstance());
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void inventoryClick(InventoryClickEvent event) {
		if (!(event.getInventory() instanceof CraftingInventory) || (event.getView().getType() != InventoryType.CREATIVE && event.getView().getType() != InventoryType.CRAFTING) ||
				!(event.getWhoClicked() instanceof Player player)) return;
		if (event.getSlotType() != SlotType.ARMOR && event.getSlotType() != SlotType.QUICKBAR && event.getSlotType() != SlotType.CONTAINER) return;
		EquipMethod method = null;
		EquipmentSlot equipSlot = null;
		ItemStack oldArmor = null;
		ItemStack newArmor = null;
		int hotbar = -1;
		switch (event.getAction()) {
			case PICKUP_ALL:
			case PICKUP_SOME:
			case PICKUP_HALF:
			case PICKUP_ONE:
			case PLACE_ALL:
			case PLACE_SOME:
			case PLACE_ONE:
			case SWAP_WITH_CURSOR:
				if (event.getSlotType() == SlotType.ARMOR) {
					method = EquipMethod.PLACE;
					equipSlot = fromSlot(event.getSlot());
					oldArmor = event.getCurrentItem();
					newArmor = event.getCursor();
				}
				break;
			case HOTBAR_SWAP:
				if (event.getSlotType() == SlotType.ARMOR) {
					method = EquipMethod.HOTBAR_SWAP;
					equipSlot = fromSlot(event.getSlot());
					oldArmor = event.getCurrentItem();
					hotbar = event.getHotbarButton();
					newArmor = player.getInventory().getItem(hotbar);
				}
				break;
			case MOVE_TO_OTHER_INVENTORY:
				method = EquipMethod.SHIFT_CLICK;
				equipSlot = event.getSlotType() == SlotType.ARMOR ? fromSlot(event.getSlot()) : event.getCurrentItem().getType().getEquipmentSlot();
				oldArmor = event.getSlotType() == SlotType.ARMOR ? event.getCurrentItem() : null;
				newArmor = event.getSlotType() != SlotType.ARMOR ? event.getCurrentItem() : null;
				break;
			default: return;
		}
		if (method == null || equipSlot == null || oldArmor == null || newArmor == null) return;
		if (!new ArmorEquipEvent(player,method,equipSlot,oldArmor,newArmor,hotbar).callEventAndDoTasksIfNotCancelled()) event.setCancelled(true);
	}
	
	private EquipmentSlot fromSlot(int slot) {
		if (slot == 36) return EquipmentSlot.FEET;
		else if (slot == 37) return EquipmentSlot.LEGS;
		else if (slot == 38) return EquipmentSlot.CHEST;
		else if (slot == 39) return EquipmentSlot.HEAD;
		return null;
	}
	
	/*@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void inventoryClick(InventoryClickEvent event) {
		if (event.isCancelled() || event.getAction() == InventoryAction.NOTHING || event.getClick() == ClickType.MIDDLE || !(event.getInventory() instanceof CraftingInventory) ||
				(event.getView().getType() != InventoryType.CREATIVE && event.getView().getType() != InventoryType.CRAFTING) || !(event.getWhoClicked() instanceof Player)) return;
		if (event.getSlotType() != SlotType.ARMOR && event.getSlotType() != SlotType.QUICKBAR && event.getSlotType() != SlotType.CONTAINER) return;
		if (event.getClick() == ClickType.CREATIVE && event.getSlotType() != SlotType.ARMOR) return;
		boolean shift = event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT;
		if ((event.getSlotType() == SlotType.CONTAINER || event.getSlotType() == SlotType.QUICKBAR) && !shift) return;
		Player player = (Player) event.getWhoClicked();
		ItemStack current = event.getCurrentItem();
		int slot = event.getSlot();
		if (slot == 40) slot = -106;
		else if (slot >= 36) slot += 64;
		EquipmentSlot equipSlot;
		if (slot == 100) equipSlot = EquipmentSlot.FEET;
		else if (slot == 101) equipSlot = EquipmentSlot.LEGS;
		else if (slot == 102) equipSlot = EquipmentSlot.CHEST;
		else if (slot == 103) equipSlot = EquipmentSlot.HEAD;
		else equipSlot = current.getType().getEquipmentSlot();
		if (equipSlot == null) return;
		boolean hotbar = event.getClick() == ClickType.NUMBER_KEY;
		boolean offhand = event.getClick() == ClickType.SWAP_OFFHAND;
		boolean drop = event.getClick() == ClickType.DROP || event.getClick() == ClickType.CONTROL_DROP;
		if (hotbar && slot < 100) return;
		ItemStack oldArmor;
		ItemStack newArmor;
		EquipMethod method;
		if (shift) {
			oldArmor = slot >= 100 ? Utils.getFromSlot(player,slot) : null;
			newArmor = slot >= 100 ? null : Utils.getFromSlot(player,slot);
			method = EquipMethod.SHIFT_CLICK;
		} else if (hotbar) {
			oldArmor = Utils.getFromSlot(player,slot);
			newArmor = Utils.getFromSlot(player,event.getHotbarButton());
			method = EquipMethod.HOTBAR_SWAP;
		} else if (offhand) {
			oldArmor = Utils.getFromSlot(player,slot);
			newArmor = Utils.getFromSlot(player,-106);
			method = EquipMethod.OFFHAND_SWAP;
		} else if (drop) {
			oldArmor = Utils.getFromSlot(player,slot);
			newArmor = null;
			method = EquipMethod.DROP;
		} else {
			oldArmor = Utils.getFromSlot(player,slot);
			newArmor = event.getCursor();
			method = event.getClick() == ClickType.CREATIVE ? EquipMethod.CREATIVE : EquipMethod.PICK_DROP;
		}
		ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(player,method,equipSlot,oldArmor,newArmor);
		Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
		if (armorEquipEvent.isCancelled()) event.setCancelled(true);
	}*/
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void playerInteractEvent(PlayerInteractEvent event) {
		if (!event.hasItem() || event.useItemInHand().equals(Result.DENY)) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && Utils.isInteract(event.getClickedBlock().getType(),player)) return;
		ItemStack item = event.getItem();
		if (Utils.isNull(item)) return;
		EquipmentSlot method = item.getType().getEquipmentSlot();
		if (method != EquipmentSlot.HEAD && method != EquipmentSlot.CHEST && method != EquipmentSlot.LEGS && method != EquipmentSlot.FEET) return;
		if (nonArmorHelmet(item.getType())) return;
		if (!Utils.isNull(player.getInventory().getItem(method))) return;
		if (!new ArmorEquipEvent(event.getPlayer(),EquipMethod.RIGHT_CLICK,method,null,item).callEventAndDoTasksIfNotCancelled()) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void inventoryDrag(InventoryDragEvent event) {
		if (event.getRawSlots().isEmpty()) return;
		int slot = event.getRawSlots().stream().findFirst().orElse(0);
		EquipmentSlot method = event.getOldCursor().getType().getEquipmentSlot();
		if (slot != getSlot(method)) return;
		if (!new ArmorEquipEvent((Player) event.getWhoClicked(),EquipMethod.DRAG,method,null,event.getOldCursor()).callEventAndDoTasksIfNotCancelled()) {
			event.setResult(Result.DENY);
			event.setCancelled(true);
		}
	}
	
	private int getSlot(@NotNull EquipmentSlot method) {
		if (method == EquipmentSlot.HEAD) return 5;
		if (method == EquipmentSlot.CHEST) return 6;
		if (method == EquipmentSlot.LEGS) return 7;
		if (method == EquipmentSlot.FEET) return 8;
		return -1;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void itemBreakEvent(PlayerItemBreakEvent event) {
		EquipmentSlot method = event.getBrokenItem().getType().getEquipmentSlot();
		Player player = event.getPlayer();
		ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(player,EquipMethod.BROKE,method,event.getBrokenItem(),null);
		armorEquipEvent.callEventAndDoTasks();
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void playerDeathEvent(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (event.getKeepInventory()) return;
		ItemStack helmet = player.getInventory().getHelmet();
		ItemStack chestplate = player.getInventory().getChestplate();
		ItemStack leggings = player.getInventory().getLeggings();
		ItemStack boots = player.getInventory().getBoots();
		ArmorEquipEvent armorEquipEvent;
		if (!Utils.isNull(helmet)) new ArmorEquipEvent(player,EquipMethod.DEATH,EquipmentSlot.HEAD,helmet,null).callEventAndDoTasks();
		if (!Utils.isNull(chestplate)) new ArmorEquipEvent(player,EquipMethod.DEATH,EquipmentSlot.CHEST,chestplate,null).callEventAndDoTasks();
		if (!Utils.isNull(leggings)) new ArmorEquipEvent(player,EquipMethod.DEATH,EquipmentSlot.LEGS,leggings,null).callEventAndDoTasks();
		if (!Utils.isNull(boots)) new ArmorEquipEvent(player,EquipMethod.DEATH,EquipmentSlot.FEET,boots,null).callEventAndDoTasks();
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDispenseArmorEvent(BlockDispenseArmorEvent event) {
		if (Utils.isNull(event.getItem())) return;
		if (!(event.getTargetEntity() instanceof Player player)) return;
		if (!new ArmorEquipEvent(player,EquipMethod.DISPENSER,event.getItem().getType().getEquipmentSlot(),event.getItem(),null).callEventAndDoTasksIfNotCancelled())
			event.setCancelled(true);
	}
	
	/*public static EquipmentSlot getEquipSlot(Material material) {
		if (material == null) return null;
		return material.getEquipmentSlot();
		if (nonArmorHelmet(material) || Tags.HELMETS.contains(material)) return EquipmentSlot.HEAD;
		if (Tags.CHESTPLATES.contains(material) || Tags.EXTRAARMORY.contains(material)) return EquipmentSlot.CHEST;
		if (Tags.LEGGINGS.contains(material)) return EquipmentSlot.LEGS;
		if (Tags.BOOTS.contains(material)) return EquipmentSlot.FEET;
		return null;
	}*/
	
	public static boolean nonArmorHelmet(@NotNull Material material) {
		return material.getEquipmentSlot() == EquipmentSlot.HEAD && !material.name().endsWith("_HELMET");
	}
}