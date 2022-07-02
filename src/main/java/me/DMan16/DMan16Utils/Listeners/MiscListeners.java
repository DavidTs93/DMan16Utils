package me.DMan16.DMan16Utils.Listeners;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import me.DMan16.DMan16Utils.Classes.*;
import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Events.SuccessfulPrepareAnvilEvent;
import me.DMan16.DMan16Utils.Events.SuccessfulPrepareGrindstoneEvent;
import me.DMan16.DMan16Utils.Events.SuccessfulPrepareSmithingEvent;
import me.DMan16.DMan16Utils.Interfaces.*;
import me.DMan16.DMan16Utils.Items.Enchantable;
import me.DMan16.DMan16Utils.Items.ItemUtils;
import me.DMan16.DMan16Utils.Items.NullItemable;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MiscListeners implements Listener {
	private static final HashMap<@NotNull AnvilInventory,@NotNull SuccessfulPrepareInfo<AdvancedRecipe<AnvilInventory>>> ANVIL_RECIPE_EVENTS = new HashMap<>();
	private static final HashMap<@NotNull SmithingInventory,@NotNull SuccessfulPrepareInfo<AdvancedRecipe<SmithingInventory>>> SMITHING_RECIPE_EVENTS = new HashMap<>();
	private static final HashMap<@NotNull GrindstoneInventory,@NotNull SuccessfulPrepareInfo<AdvancedRecipe<GrindstoneInventory>>> GRINDSTONE_RECIPE_EVENTS = new HashMap<>();
	private static final Set<@NotNull Material> DISABLED_GIVE_MATERIALS = new HashSet<>();
	
	public MiscListeners() {
		register(DMan16UtilsMain.getInstance());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onDeathNPC(PlayerDeathEvent event) {
		if (Utils.isPlayerNPC(event.getPlayer())) event.deathMessage(null);
	}
	
	public static void addDisabledGiveMaterial(@NotNull Material ... materials) {
		DISABLED_GIVE_MATERIALS.addAll(Arrays.asList(materials));
	}
	
	public static void addDisabledGiveMaterial(@NotNull Collection<@NotNull Material> materials) {
		DISABLED_GIVE_MATERIALS.addAll(materials);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		onCommand(event,event.getMessage(),event.getPlayer());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onCommand(ServerCommandEvent event) {
		onCommand(event,event.getCommand(),event.getSender());
	}
	
	private <V extends Event & Cancellable> void onCommand(V event, String cmd, CommandSender sender) {
		if (cmd.startsWith("/")) cmd = cmd.replaceFirst("/","");
		String[] split = cmd.split(" ",3);
		if (split.length < 2) return;
		String command = split[0];
		if (command.contains(":")) command = command.split(":")[1];
		else if (command.equalsIgnoreCase("enchant")) return;
		if (!command.equalsIgnoreCase("give") || DISABLED_GIVE_MATERIALS.isEmpty()) return;
		Material material = Utils.getMaterial(split[1].split("\\{",2)[0]);
		if (material == null || !DISABLED_GIVE_MATERIALS.contains(material)) return;
		event.setCancelled(true);
		Utils.chatColors(sender,"&cMaterial disabled for giving!");
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onItemDamage(PlayerItemDamageEvent event) {
		if (event.getDamage() <= 0) return;
		ItemStack item = event.getItem();
		short max = item.getType().getMaxDurability();
		Itemable<?> itemable = ItemUtils.of(event.getItem());
		if (itemable instanceof Repairable enchantable) {
			if (enchantable.shouldBreak()) {
				event.setDamage(0);
				event.setCancelled(true);
				return;
			}
			int dmg = enchantable.damageItemStack();
			if (enchantable.addDamage(event.getDamage()).shouldBreak()) {
				if (!(enchantable instanceof ReviveableItemable<?>)) event.setDamage(max);
				else {
					event.setDamage(0);
					event.getItem().setItemMeta(itemable.asItem().getItemMeta());
					event.getPlayer().playSound(event.getPlayer().getLocation(),itemable.material() == Material.SHIELD ? Sound.ITEM_SHIELD_BREAK : Sound.ENTITY_ITEM_BREAK,1,1);
				}
			} else {
				event.setDamage(enchantable.damageItemStack() - dmg);
				event.getItem().setItemMeta(Enchantable.damageItem(event.getItem(),enchantable.damage(),false).getItemMeta());
			}
		} else if (item.getType().getMaxDurability() > 0) try {		// !!!!
			item = Utils.addDurabilityLore((((Damageable) item.getItemMeta()).getDamage() <= 0) ? Utils.setLore(item,null) : item,max,event.getDamage(),true);
			if (Utils.isNull(item)) event.setDamage(max);
			else event.getItem().setItemMeta(item.getItemMeta());
		} catch (Exception e) {}
	}
	
	private record SuccessfulPrepareInfo<V>(@NotNull Itemable<?> first, @Nullable Itemable<?> second, @NotNull V recipe,@NotNull Itemable<?> result, @Nullable Itemable<?> firstAfter, @Nullable Itemable<?> secondAfter) {}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onClickCraft(InventoryClickEvent event) {
		if (event.getRawSlot() != 2) return;
		if ((event.getInventory() instanceof AnvilInventory anvilInventory) && Utils.notNull(anvilInventory.getResult())) {
			event.setCancelled(true);
			SuccessfulPrepareInfo<AdvancedRecipe<AnvilInventory>> info = ANVIL_RECIPE_EVENTS.get(anvilInventory);
			if (info == null) anvilInventory.setResult(null);
			else if ((event.getWhoClicked() instanceof Player player) && (event.getClick().isShiftClick() || ((event.getClick().isRightClick() || event.getClick().isLeftClick()) && Utils.isNull(event.getCursor())))) new BukkitRunnable() {
				public void run() {
					if (event.getClick().isShiftClick()) {
						if (Utils.addFully(player,anvilInventory.getResult(),null).isEmpty()) return;
					} else player.setItemOnCursor(anvilInventory.getResult());
					event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(),Sound.BLOCK_ANVIL_USE,1,1);
					anvilInventory.setResult(null);
					anvilInventory.setFirstItem(info.firstAfter == null ? null : info.firstAfter.asItem());
					anvilInventory.setSecondItem(info.secondAfter == null ? null : info.secondAfter.asItem());
				}
			}.runTask(DMan16UtilsMain.getInstance());
		} else if ((event.getInventory() instanceof SmithingInventory smithingInventory) && Utils.notNull(smithingInventory.getResult())) {
			event.setCancelled(true);
			SuccessfulPrepareInfo<AdvancedRecipe<SmithingInventory>> info = SMITHING_RECIPE_EVENTS.get(smithingInventory);
			if (info == null) smithingInventory.setResult(null);
			else if ((event.getWhoClicked() instanceof Player player) && (event.getClick().isShiftClick() || ((event.getClick().isRightClick() || event.getClick().isLeftClick()) && Utils.isNull(event.getCursor())))) new BukkitRunnable() {
				public void run() {
					if (event.getClick().isShiftClick()) {
						if (Utils.addFully(player,smithingInventory.getResult(),null).isEmpty()) return;
					} else player.setItemOnCursor(smithingInventory.getResult());
					event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(),Sound.BLOCK_SMITHING_TABLE_USE,1,1);
					smithingInventory.setResult(null);
					smithingInventory.setInputEquipment(info.firstAfter == null ? null : info.firstAfter.asItem());
					smithingInventory.setInputMineral(info.secondAfter == null ? null : info.secondAfter.asItem());
				}
			}.runTask(DMan16UtilsMain.getInstance());
		} else if ((event.getInventory() instanceof GrindstoneInventory grindstoneInventory) && Utils.notNull(grindstoneInventory.getResult())) {
			event.setCancelled(true);
			SuccessfulPrepareInfo<AdvancedRecipe<GrindstoneInventory>> info = GRINDSTONE_RECIPE_EVENTS.get(grindstoneInventory);
			if (info == null) grindstoneInventory.setResult(null);
			else if ((event.getWhoClicked() instanceof Player player) && (event.getClick().isShiftClick() || ((event.getClick().isRightClick() || event.getClick().isLeftClick()) && Utils.isNull(event.getCursor())))) new BukkitRunnable() {
				public void run() {
					if (event.getClick().isShiftClick()) {
						if (Utils.addFully(player,grindstoneInventory.getResult(),null).isEmpty()) return;
					} else player.setItemOnCursor(grindstoneInventory.getResult());
					event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(),Sound.BLOCK_GRINDSTONE_USE,1,1);
					grindstoneInventory.setResult(null);
					grindstoneInventory.setUpperItem(info.firstAfter == null ? null : info.firstAfter.asItem());
					grindstoneInventory.setLowerItem(info.secondAfter == null ? null : info.secondAfter.asItem());
				}
			}.runTask(DMan16UtilsMain.getInstance());
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onCloseResultInventory(InventoryCloseEvent event) {
		if (event.getInventory() instanceof AnvilInventory anvilInventory) ANVIL_RECIPE_EVENTS.remove(anvilInventory);
		else if (event.getInventory() instanceof SmithingInventory smithingInventory) SMITHING_RECIPE_EVENTS.remove(smithingInventory);
		else if (event.getInventory() instanceof GrindstoneInventory grindstoneInventory) GRINDSTONE_RECIPE_EVENTS.remove(grindstoneInventory);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPrepareAnvil(PrepareAnvilEvent event) {
		ANVIL_RECIPE_EVENTS.remove(event.getInventory());
		event.getInventory().setRepairCost(0);
		Itemable<?> first = ItemUtils.ofOrSubstituteOrHolder(event.getInventory().getFirstItem());
		Itemable<?> second = ItemUtils.ofOrSubstituteOrHolder(event.getInventory().getSecondItem());
		Itemable<?> originalResult = ItemUtils.ofOrSubstituteOrHolder(event.getResult());
		Pair<@NotNull AdvancedRecipe<AnvilInventory>,@NotNull Trio<@Nullable Itemable<?>,@Nullable Itemable<?>,@NotNull Itemable<?>>> result = Utils.advancedAnvilRecipes().getResult(first,second,originalResult);
		SuccessfulPrepareAnvilEvent newEvent = new SuccessfulPrepareAnvilEvent(event,first,second,result == null ? null : result.first,result == null ? null : result.second.third,result == null ? null : result.second.first,result == null ? null : result.second.second,originalResult);
		if (result == null) newEvent.setCancelled(true);
		event.setResult(result == null ? null : result.second.third.asItem());
		if (!newEvent.callEventAndDoTasksIfNotCancelled() || newEvent.result() == null || newEvent.first == null || newEvent.recipe == null) event.setResult(null);
		else {
			ANVIL_RECIPE_EVENTS.put(event.getInventory(), new SuccessfulPrepareInfo<>(newEvent.first,newEvent.second,newEvent.recipe,newEvent.result(),newEvent.firstAfter(),newEvent.secondAfter()));
			event.setResult(newEvent.result().asItem());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPrepareSmithing(PrepareSmithingEvent event) {
		SMITHING_RECIPE_EVENTS.remove(event.getInventory());
		Itemable<?> first = ItemUtils.ofOrSubstituteOrHolder(event.getInventory().getInputEquipment());
		Itemable<?> second = ItemUtils.ofOrSubstituteOrHolder(event.getInventory().getInputMineral());
		Itemable<?> originalResult = ItemUtils.ofOrSubstituteOrHolder(event.getResult());
		Pair<@NotNull AdvancedRecipe<SmithingInventory>,@NotNull Trio<@Nullable Itemable<?>,@Nullable Itemable<?>,@NotNull Itemable<?>>> result = Utils.advancedSmithingRecipes().getResult(first,second,originalResult);
		SuccessfulPrepareSmithingEvent newEvent = new SuccessfulPrepareSmithingEvent(event,first,second,result == null ? null : result.first,result == null ? null : result.second.third,result == null ? null : result.second.first,result == null ? null : result.second.second,originalResult);
		if (result == null) newEvent.setCancelled(true);
		event.setResult(result == null ? null : result.second.third.asItem());
		if (!newEvent.callEventAndDoTasksIfNotCancelled() || newEvent.result() == null || newEvent.first == null || newEvent.recipe == null) event.setResult(null);
		else {
			SMITHING_RECIPE_EVENTS.put(event.getInventory(),new SuccessfulPrepareInfo<>(newEvent.first,newEvent.second,newEvent.recipe,newEvent.result(),newEvent.firstAfter(),newEvent.secondAfter()));
			event.setResult(newEvent.result().asItem());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPrepareGrindstone(PrepareResultEvent event) {
		if (!(event.getInventory() instanceof GrindstoneInventory grindstoneInventory)) return;
		GRINDSTONE_RECIPE_EVENTS.remove(grindstoneInventory);
		Itemable<?> first = ItemUtils.ofOrSubstituteOrHolder(grindstoneInventory.getUpperItem());
		Itemable<?> second = ItemUtils.ofOrSubstituteOrHolder(grindstoneInventory.getLowerItem());
		Itemable<?> originalResult = ItemUtils.ofOrSubstituteOrHolder(event.getResult());
		Pair<@NotNull AdvancedRecipe<GrindstoneInventory>,@NotNull Trio<@Nullable Itemable<?>,@Nullable Itemable<?>,@NotNull Itemable<?>>> result = Utils.advancedGrindstoneRecipes().getResult(first,second,originalResult);
		SuccessfulPrepareGrindstoneEvent newEvent = new SuccessfulPrepareGrindstoneEvent(event,first,second,result == null ? null : result.first,result == null ? null : result.second.third,result == null ? null : result.second.first,result == null ? null : result.second.second,originalResult);
		if (result == null) newEvent.setCancelled(true);
		event.setResult(result == null ? null : result.second.third.asItem());
		if (!newEvent.callEventAndDoTasksIfNotCancelled() || newEvent.result() == null || newEvent.first == null || newEvent.recipe == null) event.setResult(null);
		else {
			GRINDSTONE_RECIPE_EVENTS.put(grindstoneInventory,new SuccessfulPrepareInfo<>(newEvent.first,newEvent.second,newEvent.recipe,newEvent.result(),newEvent.firstAfter(),newEvent.secondAfter()));
			event.setResult(newEvent.result().asItem());
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onLootGenerateFixItems(LootGenerateEvent event) {
		if (event.getLoot().isEmpty()) return;
		List<ItemStack> loot = new ArrayList<>(event.getLoot());
		Pair<Function<ItemStack,Itemable<?>>,Itemable<?>> info;
		for (int i = 0; i < loot.size(); i++) {
			info = LootGenerationItemUpdater.getResult(loot.get(i));
			if (info != null) loot.set(i,info.second() instanceof NullItemable ? null : info.second().asItem());
		}
		event.setLoot(loot);
	}
	
	@EventHandler(ignoreCancelled = true,priority = EventPriority.MONITOR)
	public void onCreativeDropFixItem(PlayerDropItemEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) return;
		Pair<BiFunction<Integer,ItemStack,Itemable<?>>,Itemable<?>> result = CreativeMenuItemUpdater.getResult(null,event.getItemDrop().getItemStack());
		if (result == null) return;
		if (result.second() instanceof NullItemable) event.setCancelled(true);
		else event.getItemDrop().setItemStack(result.second().asItem());
	}
	
	@EventHandler(ignoreCancelled = true,priority = EventPriority.MONITOR)
	public void onCreativeClickFixItem(InventoryClickEvent event) {
		if (event.getClick() == ClickType.CREATIVE && event.getRawSlot() >= 0) Utils.runNotNull(CreativeMenuItemUpdater.getResult(event.getRawSlot(),event.getCursor()),result -> new BukkitRunnable() {
			public void run() {
				event.getWhoClicked().getInventory().setItem(event.getSlot(),result.second() instanceof NullItemable ? null : result.second().asItem());
			}
		}.runTaskLater(DMan16UtilsMain.getInstance(),1));
	}
}