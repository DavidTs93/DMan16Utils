package me.DMan16.POPUtils.Items;

import me.DMan16.POPUtils.Interfaces.Addable;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public record InteractableItem(@NotNull String key, @Nullable Consumer<@NotNull PlayerInteractEvent> rightClick, @Nullable Consumer<@NotNull PlayerInteractEvent> leftClick) {
	public InteractableItem(@NotNull String key, @Nullable Consumer<@NotNull PlayerInteractEvent> rightClick, @Nullable Consumer<@NotNull PlayerInteractEvent> leftClick) {
		this.key = Utils.fixKey(key);
		this.rightClick = rightClick;
		this.leftClick = leftClick;
	}
	
	@NotNull
	public InteractableItem rightClick(@NotNull PlayerInteractEvent event) {
		if (rightClick != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) rightClick.accept(event);
		return this;
	}
	
	@NotNull
	public InteractableItem leftClick(@NotNull PlayerInteractEvent event) {
		if (leftClick != null && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) leftClick.accept(event);
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof InteractableItem other)) return false;
		return other.key().equalsIgnoreCase(key());
	}
	
	@NotNull
	public static <V> Consumer<@NotNull PlayerInteractEvent> createOpenMenuConsumer(@NotNull Function<@Nullable ItemStack,@Nullable V> of,
																					@NotNull BiConsumer<@NotNull Player,@NotNull V> openMenu) {
		return (@NotNull PlayerInteractEvent event) -> {
			V thing = of.apply(event.getItem());
			if (thing == null) return;
			event.setCancelled(true);
			openMenu.accept(event.getPlayer(),thing);
		};
	}
	
	@NotNull
	public static <V> Consumer<@NotNull PlayerInteractEvent> createOpenMenuConsumer(@NotNull Function<@Nullable ItemStack,@Nullable V> of,
																					@NotNull TriConsumer<@NotNull Player,@NotNull V,@NotNull Integer> openMenu) {
		return (@NotNull PlayerInteractEvent event) -> {
			V thing = of.apply(event.getItem());
			if (thing == null) return;
			event.setCancelled(true);
			openMenu.accept(event.getPlayer(),thing,event.getHand() == EquipmentSlot.OFF_HAND ? -106 : event.getPlayer().getInventory().getHeldItemSlot());
		};
	}
	
	@NotNull
	public static <V extends Addable> Consumer<@NotNull PlayerInteractEvent> createAddableConsumer(@NotNull Function<@Nullable ItemStack,@Nullable V> get, boolean owned) {
		if (owned) return (@NotNull PlayerInteractEvent event) -> {
			try {
				V thing = get.apply(event.getItem());
				if (thing != null && !thing.add(event.getPlayer()))
					event.getPlayer().sendMessage(Utils.noItalic(Component.translatable("menu.prisonpop.skin_owned", NamedTextColor.GREEN)));
			} catch (Exception e) {}
		};
		return (@NotNull PlayerInteractEvent event) -> {
			try {
				V thing = get.apply(event.getItem());
				if (thing != null) thing.add(event.getPlayer());
			} catch (Exception e) {}
		};
	}
}