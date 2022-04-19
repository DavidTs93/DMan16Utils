package me.DMan16.POPUtils.Classes;

import me.DMan16.POPUtils.Interfaces.Itemable;
import me.DMan16.POPUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class BasicItemable implements Itemable<BasicItemable> {
	public final Material material;
	public final Component name;
	public final Integer model;
	public final String skin;
	public final Function<ItemStack,ItemStack> alterItem;
	
	
	public BasicItemable(@NotNull Material material,@Nullable Component name,@Nullable Integer model,@Nullable String skin,@Nullable Function<ItemStack,ItemStack> alterItem) {
		this.material = material;
		this.name = Utils.noItalic(name);
		this.model = model;
		this.skin = skin;
		this.alterItem = alterItem;
	}
	
	public BasicItemable(@NotNull Material material,@Nullable Component name,@Nullable Integer model,@Nullable String skin) {
		this(material,name,model,skin,null);
	}
	
	public BasicItemable(@NotNull Material material,@Nullable Component name,@Nullable Integer model,@Nullable Function<ItemStack,ItemStack> alterItem) {
		this(material,name,model,null,alterItem);
	}
	
	public BasicItemable(@NotNull Material material,@Nullable Component name,@Nullable Integer model) {
		this(material,name,model,null,null);
	}
	
	public BasicItemable(@NotNull Material material,@Nullable Component name,@Nullable String skin,@Nullable Function<ItemStack,ItemStack> alterItem) {
		this(material,name,null,skin,alterItem);
	}
	
	public BasicItemable(@NotNull Material material,@Nullable Component name,@Nullable Function<ItemStack,ItemStack> alterItem) {
		this(material,name,null,null,alterItem);
	}
	
	public BasicItemable(@NotNull Material material,@Nullable Component name,@Nullable String skin) {
		this(material,name,null,skin,null);
	}
	
	public BasicItemable(@NotNull Material material,@Nullable Component name) {
		this(material,name,null,null,null);
	}
	
	@NotNull
	@Contract(pure = true)
	public BasicItemable copy() {
		return new BasicItemable(material,name,model,skin,alterItem);
	}
	
	@NotNull
	@Contract(pure = true)
	public BasicItemable copy(@NotNull Material material) {
		return new BasicItemable(material,name,model,skin,alterItem);
	}
	
	@NotNull
	@Contract(pure = true)
	public BasicItemable copy(@Nullable Component name) {
		return new BasicItemable(material,name,model,skin,alterItem);
	}
	
	@NotNull
	@Contract(pure = true)
	public BasicItemable copy(@Nullable Integer model) {
		return new BasicItemable(material,name,model,skin,alterItem);
	}
	
	@NotNull
	@Contract(pure = true)
	public BasicItemable copy(@Nullable String skin) {
		return new BasicItemable(material,name,model,skin,alterItem);
	}
	
	@NotNull
	@Contract(pure = true)
	public BasicItemable copy(@Nullable Function<ItemStack,ItemStack> alterItem) {
		return new BasicItemable(material,name,model,skin,alterItem);
	}
	
	@NotNull
	@Contract(pure = true)
	public BasicItemable copyAddAlterItem(@Nullable Function<ItemStack,ItemStack> alterItem) {
		return new BasicItemable(material,name,model,skin,this.alterItem == null ? alterItem : (alterItem == null ? this.alterItem : this.alterItem.andThen(alterItem)));
	}
	
	@NotNull
	@Contract(pure = true)
	public BasicItemable copyChangeNameIf(@NotNull Function<Component,Component> changeName,@NotNull Function<Component,@NotNull Boolean> ifName) {
		return ifName.apply(name) ? copy(changeName.apply(name)) : this;
	}
	
	@NotNull
	public static Function<ItemStack,ItemStack> setLoreFunction(@Nullable List<Component> lore) {
		return (ItemStack item) -> Utils.runGetOriginal(item,i -> i.setItemMeta(Utils.runGetOriginal(i.getItemMeta(),meta -> meta.lore(lore))));
	}
	
	@NotNull
	public Material material() {
		return material;
	}
	
	@NotNull
	public ItemStack asItem() {
		ItemStack item = null;
		if (skin != null) {
			item = Utils.makeItem(Material.PLAYER_HEAD,name,ItemFlag.values());
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			if (Utils.setSkin(meta,skin,null)) item.setItemMeta(meta);
		}
		if (item == null) item = Utils.makeItem(material(),name,model,ItemFlag.values());
		if (alterItem != null && !Utils.isNull(item)) item = alterItem.apply(item);
		return item;
	}
	
	@NotNull
	public Component giveComponent() {
		return name == null ? Component.text("Basic:" + Utils.thisOrThatOrNull(Utils.getJSONString(toMap()),"")) : name;
	}
	
	@NotNull
	public Map<@NotNull String,Object> toMap() {
		HashMap<String,Object> map = new HashMap<>();
		map.put("material",material.name());
		if (model != null) map.put("model",model);
		if (skin != null) map.put("skin",skin);
		return map;
	}
	
	@NotNull
	public String ItemableKey() {
		return "basic_itemable";
	}
}