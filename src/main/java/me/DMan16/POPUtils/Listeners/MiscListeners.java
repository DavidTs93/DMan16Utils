package me.DMan16.POPUtils.Listeners;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import me.DMan16.POPUtils.Classes.AdvancedRecipe;
import me.DMan16.POPUtils.Classes.Pair;
import me.DMan16.POPUtils.Classes.Trio;
import me.DMan16.POPUtils.Events.SuccessfulPrepareAnvilEvent;
import me.DMan16.POPUtils.Events.SuccessfulPrepareSmithingEvent;
import me.DMan16.POPUtils.Interfaces.Itemable;
import me.DMan16.POPUtils.Interfaces.Listener;
import me.DMan16.POPUtils.Items.ItemUtils;
import me.DMan16.POPUtils.Items.ItemableStack;
import me.DMan16.POPUtils.POPUtilsMain;
import me.DMan16.POPUtils.Utils.Utils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MiscListeners implements Listener {
	private static final HashMap<@NotNull AnvilInventory,@NotNull SuccessfulPrepareInfo<AdvancedRecipe<AnvilInventory>>> ANVIL_RECIPE_EVENTS = new HashMap<>();
	private static final HashMap<@NotNull SmithingInventory,@NotNull SuccessfulPrepareInfo<AdvancedRecipe<SmithingInventory>>> SMITHING_RECIPE_EVENTS = new HashMap<>();
	private static final Set<@NotNull Material> DISABLED_GIVE_MATERIALS = new HashSet<>();
	
	public MiscListeners() {
		register(POPUtilsMain.getInstance());
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
	public void onGiveTool(PlayerCommandPreprocessEvent event) {
		if (DISABLED_GIVE_MATERIALS.isEmpty()) return;
		String cmd = event.getMessage();
		if (cmd.startsWith("/")) cmd = cmd.replaceFirst("/","");
		String[] split = cmd.split(" ",3);
		if (split.length < 2) return;
		String command = split[0];
		if (command.contains(":")) command = command.split(":")[1];
		if (!command.equalsIgnoreCase("give")) return;
		Material material = Utils.getMaterial(split[1].split("\\{",2)[0]);
		if (material == null || !DISABLED_GIVE_MATERIALS.contains(material)) return;
		event.setCancelled(true);
		Utils.chatColors(event.getPlayer(),"&cMaterial disabled for giving!");
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPrepare(PrepareResultEvent event) {
		if (!(event.getInventory() instanceof GrindstoneInventory grindstone) || Utils.isNull(event.getResult())) return;
		if (Utils.isNull(grindstone.getUpperItem()) || Utils.isNull(grindstone.getLowerItem())) {
			Itemable<?> result = ItemUtils.of(event.getResult());
			if (result != null) event.setResult(result.asItem());
		} else if (grindstone.getUpperItem().getType() == grindstone.getLowerItem().getType()) event.setResult(null);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onClickCraft(InventoryClickEvent event) {
		if (event.getRawSlot() != 2) return;
		if ((event.getInventory() instanceof AnvilInventory anvilInventory) && !Utils.isNull(anvilInventory.getResult())) {
			event.setCancelled(true);
			SuccessfulPrepareInfo<AdvancedRecipe<AnvilInventory>> info = ANVIL_RECIPE_EVENTS.get(anvilInventory);
			if (info == null) anvilInventory.setResult(null);
			else if ((event.getWhoClicked() instanceof Player player) && (event.getClick().isShiftClick() ||
					((event.getClick().isRightClick() || event.getClick().isLeftClick()) && Utils.isNull(event.getCursor())))) new BukkitRunnable() {
				public void run() {
					if (event.getClick().isShiftClick()) {
						if (Utils.addFully(player,anvilInventory.getResult(),null).isEmpty()) return;
					} else player.setItemOnCursor(anvilInventory.getResult());
					event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(),Sound.BLOCK_ANVIL_USE,1,1);
					anvilInventory.setResult(null);
					anvilInventory.setFirstItem(info.firstAfter == null ? null : info.firstAfter.asItem());
					anvilInventory.setSecondItem(info.secondAfter == null ? null : info.secondAfter.asItem());
				}
			}.runTask(POPUtilsMain.getInstance());
		} else if ((event.getInventory() instanceof SmithingInventory smithingInventory) && !Utils.isNull(smithingInventory.getResult())) {
			event.setCancelled(true);
			SuccessfulPrepareInfo<AdvancedRecipe<SmithingInventory>> info = SMITHING_RECIPE_EVENTS.get(smithingInventory);
			if (info == null) smithingInventory.setResult(null);
			else if ((event.getWhoClicked() instanceof Player player) && (event.getClick().isShiftClick() ||
					((event.getClick().isRightClick() || event.getClick().isLeftClick()) && Utils.isNull(event.getCursor())))) new BukkitRunnable() {
				public void run() {
					if (event.getClick().isShiftClick()) {
						if (Utils.addFully(player,smithingInventory.getResult(),null).isEmpty()) return;
					} else player.setItemOnCursor(smithingInventory.getResult());
					event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(),Sound.BLOCK_SMITHING_TABLE_USE,1,1);
					smithingInventory.setResult(null);
					smithingInventory.setInputEquipment(info.firstAfter == null ? null : info.firstAfter.asItem());
					smithingInventory.setInputMineral(info.secondAfter == null ? null : info.secondAfter.asItem());
				}
			}.runTask(POPUtilsMain.getInstance());
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onCloseAnvil(InventoryCloseEvent event) {
		if (event.getInventory() instanceof AnvilInventory anvilInventory) ANVIL_RECIPE_EVENTS.remove(anvilInventory);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPrepareAnvil(PrepareAnvilEvent event) {
		ANVIL_RECIPE_EVENTS.remove(event.getInventory());
		event.getInventory().setRepairCost(0);
		if (!Utils.isNull(event.getInventory().getFirstItem()) && event.getInventory().getFirstItem().getType() == Material.NAME_TAG && Utils.isNull(event.getInventory().getSecondItem()))
			return;
		Itemable<?> first = ItemUtils.of(event.getInventory().getFirstItem());
		ItemableStack second = ItemUtils.of(ItemableStack.class,event.getInventory().getSecondItem());
		Itemable<?> originalResult = ItemUtils.of(event.getResult());
		Pair<@NotNull AdvancedRecipe<AnvilInventory>,@NotNull Trio<@Nullable Itemable<?>,@Nullable ItemableStack,@NotNull Itemable<?>>> result =
				Utils.getAdvancedAnvilRecipes().getResult(first,second,originalResult);
		SuccessfulPrepareAnvilEvent newEvent = new SuccessfulPrepareAnvilEvent(event,first,second,result == null ? null : result.first,result == null ? null : result.second.third,
				result == null ? null : result.second.first,result == null ? null : result.second.second,originalResult);
		if (result == null) newEvent.setCancelled(true);
		event.setResult(result == null ? null : result.second.third.asItem());
		if (!newEvent.callEventAndDoTasksIfNotCancelled() || newEvent.result() == null || newEvent.first == null || newEvent.recipe == null) event.setResult(null);
		else {
			ANVIL_RECIPE_EVENTS.put(event.getInventory(),
					new SuccessfulPrepareInfo<>(newEvent.first,newEvent.second,newEvent.recipe,newEvent.result(),newEvent.firstAfter(),newEvent.secondAfter()));
			event.setResult(newEvent.result().asItem());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPrepareSmithing(PrepareSmithingEvent event) {
		SMITHING_RECIPE_EVENTS.remove(event.getInventory());
		Itemable<?> first = ItemUtils.of(event.getInventory().getInputEquipment());
		ItemableStack second = ItemUtils.of(ItemableStack.class,event.getInventory().getInputMineral());
		Itemable<?> originalResult = ItemUtils.of(event.getResult());
		Pair<@NotNull AdvancedRecipe<SmithingInventory>,@NotNull Trio<@Nullable Itemable<?>,@Nullable ItemableStack,@NotNull Itemable<?>>> result =
				Utils.getAdvancedSmithingRecipes().getResult(first,second,originalResult);
		SuccessfulPrepareSmithingEvent newEvent = new SuccessfulPrepareSmithingEvent(event,first,second,result == null ? null : result.first,result == null ? null : result.second.third,
				result == null ? null : result.second.first,result == null ? null : result.second.second,originalResult);
		if (result == null) newEvent.setCancelled(true);
		event.setResult(result == null ? null : result.second.third.asItem());
		if (!newEvent.callEventAndDoTasksIfNotCancelled() || newEvent.result() == null || newEvent.first == null || newEvent.recipe == null) event.setResult(null);
		else {
			SMITHING_RECIPE_EVENTS.put(event.getInventory(),
					new SuccessfulPrepareInfo<>(newEvent.first,newEvent.second,newEvent.recipe,newEvent.result(),newEvent.firstAfter(),newEvent.secondAfter()));
			event.setResult(newEvent.result().asItem());
		}
	}
	
	private record SuccessfulPrepareInfo<V>(@NotNull Itemable<?> first, @Nullable Itemable<?> second, @NotNull V recipe,
											@NotNull Itemable<?> result, @Nullable Itemable<?> firstAfter, @Nullable Itemable<?> secondAfter) {}
}