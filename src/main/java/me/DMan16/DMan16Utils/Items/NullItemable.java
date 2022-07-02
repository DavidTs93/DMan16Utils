package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.Interfaces.Itemable;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class NullItemable implements Itemable<NullItemable> {
	public static final @NotNull NullItemable NullItemable = new NullItemable();
	
	private NullItemable() {}
	
	@NotNull
	public ItemStack asItem() {
		return new ItemStack(material());
	}
	
	@Override
	public void give(@NotNull Player player,@Nullable Runnable onSuccess,@Nullable Runnable onFail,@Nullable Map<@NotNull Integer,@NotNull Integer> toRemove,int ... toEmpty) {}
	
	@NotNull
	public Material material() {
		return Material.AIR;
	}
	
	@NotNull
	public Component giveComponent() {
		return Component.empty();
	}
	
	@NotNull
	public NullItemable copy() {
		return NullItemable;
	}
	
	@NotNull
	public Map<@NotNull String,Object> toMap() {
		return new HashMap<>();
	}
	
	@NotNull
	public String mappableKey() {
		return "null";
	}
}