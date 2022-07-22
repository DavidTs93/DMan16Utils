package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.Interfaces.Addable;
import me.DMan16.DMan16Utils.Utils.Utils;
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

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public record InteractableItem(@NotNull String key,@Nullable Consumer<@NotNull PlayerInteractEvent> rightClick,@Nullable Consumer<@NotNull PlayerInteractEvent> leftClick) {
	public static final @NotNull Component OWNED = Utils.noItalic(Component.translatable("menu.owned",NamedTextColor.GREEN));
	
	public InteractableItem(@NotNull String key,@Nullable Consumer<@NotNull PlayerInteractEvent> rightClick,@Nullable Consumer<@NotNull PlayerInteractEvent> leftClick) {
		this.key = Objects.requireNonNull(Utils.fixKey(key));
		this.rightClick = rightClick;
		this.leftClick = leftClick;
	}
	
	@NotNull
	public InteractableItem rightClick(@NotNull PlayerInteractEvent event) {
		if (rightClick != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && !Utils.isInteract(event)) rightClick.accept(event);
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
	
	public int hashCode() {
		return key.hashCode();
	}
	
	@NotNull
	public static <V> Consumer<@NotNull PlayerInteractEvent> createOpenMenuConsumer(@NotNull Function<@Nullable ItemStack,@Nullable V> of,@NotNull BiConsumer<@NotNull Player,@NotNull V> openMenu) {
		return (@NotNull PlayerInteractEvent event) -> {
			V thing = of.apply(event.getItem());
			if (thing == null) return;
			event.setCancelled(true);
			openMenu.accept(event.getPlayer(),thing);
		};
	}
	
	@NotNull
	public static <V> Consumer<@NotNull PlayerInteractEvent> createOpenMenuConsumer(@NotNull Function<@Nullable ItemStack,@Nullable V> of,@NotNull TriConsumer<@NotNull Player,@NotNull V,@NotNull Integer> openMenu) {
		return (@NotNull PlayerInteractEvent event) -> {
			V thing = of.apply(event.getItem());
			if (thing == null) return;
			event.setCancelled(true);
			openMenu.accept(event.getPlayer(),thing,Utils.getSlot(event.getPlayer(),event.getHand() == EquipmentSlot.OFF_HAND ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND));
		};
	}
	
	@NotNull
	public static <V> Consumer<@NotNull PlayerInteractEvent> createAddConsumer(@NotNull Function<@NotNull ItemStack,@Nullable V> get,@NotNull BiFunction<@NotNull Player,@NotNull V,@NotNull Boolean> add,@Nullable Component failMessage) {
		return (@NotNull PlayerInteractEvent event) -> {
			try {
				ItemStack item = event.getItem();
				if (item == null) return;
				V thing = get.apply(event.getItem());
				if (thing == null) return;
				event.setCancelled(true);
				if (!add.apply(event.getPlayer(),thing)) {
					if (failMessage != null) event.getPlayer().sendMessage(failMessage);
					return;
				}
				Utils.setSlot(event.getPlayer(),Utils.subtract(item,1),event.getHand() == EquipmentSlot.OFF_HAND ? -106 : event.getPlayer().getInventory().getHeldItemSlot());
				Utils.savePlayer(event.getPlayer());
			} catch (Exception e) {}
		};
	}
	
	@NotNull
	public static <V> Consumer<@NotNull PlayerInteractEvent> createAddConsumer(@NotNull Function<@NotNull ItemStack,@Nullable V> get,@NotNull BiFunction<@NotNull Player,@NotNull V,@NotNull Boolean> add,boolean ownedMessageOnFailedAdd) {
		return createAddConsumer(get,add,ownedMessageOnFailedAdd ? OWNED : null);
	}
	
	@NotNull
	public static <V extends Addable> Consumer<@NotNull PlayerInteractEvent> createAddableConsumer(@NotNull Function<@NotNull ItemStack,@Nullable V> get,@Nullable Component failMessage) {
		return (@NotNull PlayerInteractEvent event) -> {
			ItemStack item = event.getItem();
			if (item == null) return;
			V thing = get.apply(event.getItem());
			if (thing == null) return;
			event.setCancelled(true);
			Player player = event.getPlayer();
			thing.add(player,result -> {
				if (!result) {
					if (failMessage != null) player.sendMessage(failMessage);
					return;
				}
				Utils.setSlot(player,Utils.subtract(item,1),event.getHand() == EquipmentSlot.OFF_HAND ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
				Utils.savePlayer(player);
			},null);
		};
	}
	
	@NotNull
	public static <V extends Addable> Consumer<@NotNull PlayerInteractEvent> createAddableConsumer(@NotNull Function<@NotNull ItemStack,@Nullable V> get,boolean ownedMessageOnFailedAdd) {
		return createAddableConsumer(get,ownedMessageOnFailedAdd ? OWNED : null);
	}
}