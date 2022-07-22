package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.Classes.Pairs.NumberPairs.PairInt;
import me.DMan16.DMan16Utils.Classes.Pairs.Pair;
import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.Interfaces.ItemableAmountable;
import me.DMan16.DMan16Utils.Interfaces.Scoreable;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class ItemableStorage implements Scoreable {
	protected static final @NotNull NamespacedKey STORAGE_KEY = new NamespacedKey(DMan16UtilsMain.getInstance(),"storage");
	
	protected final Itemable<?>@NotNull [] storage;
	
	protected ItemableStorage(@Positive int storageSize) {
		this.storage = new Itemable<?>[storageSize];
	}
	
	@NotNull
	protected final LinkedHashMap<@NotNull Integer,@NotNull String> itemMap() {
		LinkedHashMap<Integer,String> map = new LinkedHashMap<>();
		Itemable<?> item;
		for (int i = 0; i < storage.length; i++) if (Utils.notNull(item = storage[i])) map.put(i,item.stringMappable());
		return map;
	}
	
	@NotNull
	public Map<@NotNull String,Object> toMap() {
		return Utils.runGetOriginal(new HashMap<>(),map -> map.put("Storage",itemMap()));
	}
	
	@NotNull
	protected ItemStack setItemsPDC(@NotNull ItemStack item) {
		return Utils.setKeyPersistentDataContainer(item,STORAGE_KEY,Utils.getJSONString(itemMap()));
	}
	
	@Nullable
	protected static Map<@NotNull @NonNegative Integer,@NotNull Itemable<?>> getItems(Map<?,?> map) {
		if (Utils.isNullOrEmpty(map)) return null;
		HashMap<Integer,Itemable<?>> storage = new HashMap<>();
		Integer index;
		Itemable<?> itemable;
		for (Map.Entry<?,?> entry : map.entrySet()) {
			index = Utils.getInteger(entry.getKey());
			itemable = ItemUtils.of(Utils.getString(entry.getValue()));
			if (index != null && index >= 0 && itemable != null) storage.put(index,itemable);
		}
		return storage;
	}
	
	@Nullable
	protected static Map<@NotNull @NonNegative Integer,@NotNull Itemable<?>> getItemsPDC(@NotNull ItemStack item) {
		return getItems(Utils.getMapFromJSON(Utils.getKeyPersistentDataContainer(item,STORAGE_KEY,PersistentDataType.STRING)));
	}
	
	public boolean isFull() {
		for (Itemable<?> item : storage) if (Utils.isNull(item) || ((item instanceof ItemableAmountable<?> amountable) && !amountable.isMaxSize())) return false;
		return true;
	}
	
	public boolean isEmpty() {
		for (Itemable<?> item : storage) if (!Utils.isNull(item)) return false;
		return true;
	}
	
	protected boolean canSlotHoldItem(@NonNegative int index,@NotNull Itemable<?> item) {
		return true;
	}
	
	@Nullable
	public List<@NotNull Pair<@NotNull @NonNegative Integer,@NotNull @Positive Integer>> addToStorage(@Nullable Itemable<?> item) {
		if (Utils.isNull(item) || isFull()) return null;
		List<Pair<Integer,Integer>> list = new ArrayList<>();
		if (item instanceof ItemableAmountable<?> amountable) for (int i = 0; i < storage.length; i++) {
			if (storage[i] == null) continue;
			if ((storage[i] instanceof NullItemable) || !canSlotHoldItem(i,storage[i])) storage[i] = null;
			else if ((storage[i] instanceof ItemableAmountable<?> storageItem) && !storageItem.isMaxSize() && (storageItem.canPassAsThis(item) || item.canPassAsThis(storageItem))) {
				ItemableAmountable<?> copy = (ItemableAmountable<?>) storageItem.copyAdd(amountable.amount());
				int amount = copy.amount() - storageItem.amount();
				if (amount <= 0) continue;
				list.add(PairInt.of(i,amount));
				amount = amountable.amount() - amount;
				if (amount <= 0) {
					item = null;
					break;
				} else amountable = (ItemableAmountable<?>) amountable.copy(amount);
			}
		}
		if (!Utils.isNull(item)) for (int i = 0; i < storage.length; i++) if (Utils.isNull(storage[i]) && canSlotHoldItem(i,storage[i])) {
			storage[i] = item;
			list.add(PairInt.of(i,(item instanceof ItemableAmountable<?> amountable) ? amountable.amount() : 1));
			break;
		}
		return list.isEmpty() ? null : list;
	}
	
	@Positive
	protected abstract int itemScoreNonScorable(@NotNull Itemable<?> item);
	
	@NonNegative
	protected final int storageScore() {
		long score = 0;
		for (Itemable<?> item : storage) {
			if (Utils.isNull(item)) continue;
			if (item instanceof Scoreable scoreable) score += scoreable.score();
			else score += itemScoreNonScorable(item);
			if (score >= Integer.MAX_VALUE) break;
		}
		return (int) Math.min(score,Integer.MAX_VALUE);
	}
}