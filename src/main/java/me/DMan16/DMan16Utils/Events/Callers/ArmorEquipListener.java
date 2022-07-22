package me.DMan16.DMan16Utils.Events.Callers;

import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Enums.ArmorSlot;
import me.DMan16.DMan16Utils.Enums.EquipMethod;
import me.DMan16.DMan16Utils.Enums.Tags;
import me.DMan16.DMan16Utils.Events.ArmorEquipEvent;
import me.DMan16.DMan16Utils.Interfaces.Listener;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
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
import org.jetbrains.annotations.Nullable;

public class ArmorEquipListener implements Listener {
	public ArmorEquipListener() {
		register(DMan16UtilsMain.getInstance());
	}
	
	@EventHandler(priority = EventPriority.LOWEST,ignoreCancelled = true)
	public void inventoryClick(InventoryClickEvent event) {
		if (!(event.getInventory() instanceof CraftingInventory) || (event.getView().getType() != InventoryType.CREATIVE && event.getView().getType() != InventoryType.CRAFTING) || !(event.getWhoClicked() instanceof Player player)) return;
		if (event.getSlotType() != SlotType.ARMOR && event.getSlotType() != SlotType.QUICKBAR && event.getSlotType() != SlotType.CONTAINER) return;
		EquipMethod method = null;
		ArmorSlot equipSlot = null;
		ItemStack oldArmor = null;
		ItemStack newArmor = null;
		int hotbar = -1;
		switch (event.getAction()) {
			case PICKUP_ALL,PICKUP_SOME,PICKUP_HALF,PICKUP_ONE,COLLECT_TO_CURSOR -> {
				if (event.getSlotType() == SlotType.ARMOR) {
					method = EquipMethod.PICKUP;
					equipSlot = fromSlot(event.getSlot());
					oldArmor = event.getCurrentItem();
				}
			}
			case PLACE_ALL,PLACE_SOME,PLACE_ONE,SWAP_WITH_CURSOR -> {
				if (event.getSlotType() == SlotType.ARMOR) {
					if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
						method = EquipMethod.CURSOR_SWAP;
					} else method = EquipMethod.PLACE;
					equipSlot = fromSlot(event.getSlot());
					oldArmor = event.getCurrentItem();
					newArmor = event.getCursor();
				}
			}
			case DROP_ONE_SLOT,DROP_ALL_SLOT -> {
				if (event.getSlotType() == SlotType.ARMOR) {
					method = EquipMethod.DROP;
					equipSlot = fromSlot(event.getSlot());
					oldArmor = event.getCurrentItem();
				}
			}
			case HOTBAR_SWAP -> {
				if (event.getSlotType() == SlotType.ARMOR) {
					method = EquipMethod.HOTBAR_SWAP;
					equipSlot = fromSlot(event.getSlot());
					oldArmor = event.getCurrentItem();
					hotbar = event.getHotbarButton();
					newArmor = player.getInventory().getItem(hotbar);
				}
			}
			case MOVE_TO_OTHER_INVENTORY -> {
				method = EquipMethod.SHIFT_CLICK;
				equipSlot = event.getSlotType() == SlotType.ARMOR ? fromSlot(event.getSlot()) : Utils.applyNotNull(event.getCurrentItem(),item -> ArmorSlot.get(item.getType().getEquipmentSlot()));
				oldArmor = event.getSlotType() == SlotType.ARMOR ? event.getCurrentItem() : null;
				newArmor = event.getSlotType() != SlotType.ARMOR ? event.getCurrentItem() : null;
			}
		}
		if (method == null || equipSlot == null) return;
		if (!new ArmorEquipEvent(player,method,equipSlot,oldArmor,newArmor,hotbar).callEventAndDoTasksIfNotCancelled()) event.setCancelled(true);
	}
	
	@Nullable
	public static ArmorSlot fromSlot(int slot) {
		if (slot == 36) return ArmorSlot.BOOTS;
		else if (slot == 37) return ArmorSlot.LEGGINGS;
		else if (slot == 38) return ArmorSlot.CHESTPLATE;
		else if (slot == 39) return ArmorSlot.HELMET;
		return null;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerInteractEvent(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		if (!event.hasItem() || event.useItemInHand().equals(Result.DENY) || Utils.isInteract(event) || Utils.isNull(item)) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
		ArmorSlot slot = ArmorSlot.get(item.getType().getEquipmentSlot());
		if (slot == null || Tags.isNonArmorHelmet(item.getType()) || Utils.notNull(event.getPlayer().getInventory().getItem(slot.equipSlot.equipSlot))) return;
		if (!new ArmorEquipEvent(event.getPlayer(),EquipMethod.RIGHT_CLICK,slot,null,item).callEventAndDoTasksIfNotCancelled()) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST,ignoreCancelled = true)
	public void inventoryDrag(InventoryDragEvent event) {
		if (event.getRawSlots().isEmpty()) return;
		int slotNum = event.getRawSlots().stream().findFirst().orElse(0);
		ArmorSlot slot = ArmorSlot.get(event.getOldCursor().getType().getEquipmentSlot());
		if (slot == null || slotNum != getSlot(slot.equipSlot.equipSlot)) return;
		if (new ArmorEquipEvent((Player) event.getWhoClicked(),EquipMethod.DRAG,slot,null,event.getOldCursor()).callEventAndDoTasksIfNotCancelled()) return;
		event.setResult(Result.DENY);
		event.setCancelled(true);
	}
	
	public static int getSlot(@NotNull EquipmentSlot method) {
		return switch (method) {
			case HEAD -> 5;
			case CHEST -> 6;
			case LEGS -> 7;
			case FEET -> 8;
			default -> -1;
		};
	}

	@EventHandler(priority = EventPriority.LOWEST,ignoreCancelled = true)
	public void itemBreakEvent(PlayerItemBreakEvent event) {
		ArmorSlot slot = ArmorSlot.get(event.getBrokenItem().getType().getEquipmentSlot());
		if (slot == null) return;
		Player player = event.getPlayer();
		ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(player,EquipMethod.BROKE,slot,event.getBrokenItem(),null);
		armorEquipEvent.callEventAndDoTasks();
	}
	
	@EventHandler(priority = EventPriority.LOW,ignoreCancelled = true)
	public void playerDeathEvent(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (event.getKeepInventory()) return;
		ItemStack helmet = player.getInventory().getHelmet();
		ItemStack chestplate = player.getInventory().getChestplate();
		ItemStack leggings = player.getInventory().getLeggings();
		ItemStack boots = player.getInventory().getBoots();
		if (Utils.notNull(helmet)) new ArmorEquipEvent(player,EquipMethod.DEATH,ArmorSlot.HELMET,helmet,null).callEventAndDoTasks();
		if (Utils.notNull(chestplate)) new ArmorEquipEvent(player,EquipMethod.DEATH,ArmorSlot.CHESTPLATE,chestplate,null).callEventAndDoTasks();
		if (Utils.notNull(leggings)) new ArmorEquipEvent(player,EquipMethod.DEATH,ArmorSlot.LEGGINGS,leggings,null).callEventAndDoTasks();
		if (Utils.notNull(boots)) new ArmorEquipEvent(player,EquipMethod.DEATH,ArmorSlot.BOOTS,boots,null).callEventAndDoTasks();
	}
	
	@EventHandler(priority = EventPriority.LOWEST,ignoreCancelled = true)
	public void onDispenseArmorEvent(BlockDispenseArmorEvent event) {
		ArmorSlot slot;
		if (Utils.notNull(event.getItem()) && (slot = ArmorSlot.get(event.getItem().getType().getEquipmentSlot())) != null && (event.getTargetEntity() instanceof Player player))
			if (!new ArmorEquipEvent(player,EquipMethod.DISPENSER,slot,event.getItem(),null).callEventAndDoTasksIfNotCancelled()) event.setCancelled(true);
	}
}