package me.DMan16.DMan16Utils.Items;

import me.DMan16.DMan16Utils.Classes.Pair;
import me.DMan16.DMan16Utils.Interfaces.Itemable;
import me.DMan16.DMan16Utils.Interfaces.ItemableAmountable;
import me.DMan16.DMan16Utils.Interfaces.Scoreable;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class ItemableStorage implements Scoreable {
	protected final Itemable<?>@NotNull [] storage;
	
	protected ItemableStorage(@Positive int storageSize) {
		this.storage = new Itemable<?>[storageSize];
	}
	
	@NotNull
	protected final LinkedHashMap<@NotNull Integer,@NotNull String> itemMap() {
		LinkedHashMap<Integer,String> map = new LinkedHashMap<>();
		Itemable<?> item;
		for (int i = 0; i < storage.length; i++) if ((item = storage[i]) != null && !(item instanceof NullItemable)) map.put(i,item.stringMappable());
		return map;
	}
	
	@NotNull
	public Map<@NotNull String,Object> toMap() {
		return new HashMap<>() {{
			put("Storage",itemMap());
		}};
	}
	
	public boolean isFull() {
		for (Itemable<?> item : storage) if (Utils.isNull(item) || ((item instanceof ItemableAmountable<?> amountable) && !amountable.isMaxSize())) return false;
		return true;
	}
	
	protected boolean canIndexHoldItem(@NonNegative int index,@NotNull Itemable<?> item) {
		return true;
	}
	
	@Nullable
	public List<@NotNull Pair<@NotNull @NonNegative Integer,@NotNull @Positive Integer>> addToStorage(@Nullable Itemable<?> item) {
		if (Utils.isNull(item) || isFull()) return null;
		List<Pair<Integer,Integer>> list = new ArrayList<>();
		if (item instanceof ItemableAmountable<?> amountable) for (int i = 0; i < storage.length; i++) {
			if (storage[i] == null) continue;
			if ((storage[i] instanceof NullItemable) || !canIndexHoldItem(i,storage[i])) storage[i] = null;
			else if ((storage[i] instanceof ItemableAmountable<?> storageItem) && !storageItem.isMaxSize() && (storageItem.canPassAsThis(item) || item.canPassAsThis(storageItem))) {
				ItemableAmountable<?> copy = (ItemableAmountable<?>) storageItem.copyIncrement(amountable.amount());
				int amount = copy.amount() - storageItem.amount();
				if (amount <= 0) continue;
				list.add(Pair.of(i,amount));
				amount = amountable.amount() - amount;
				if (amount <= 0) {
					item = null;
					break;
				} else amountable = (ItemableAmountable<?>) amountable.copy(amount);
			}
		}
		if (!Utils.isNull(item)) for (int i = 0; i < storage.length; i++) if (Utils.isNull(storage[i]) || !canIndexHoldItem(i,storage[i])) {
			storage[i] = item;
			list.add(Pair.of(i,(item instanceof ItemableAmountable<?> amountable) ? amountable.amount() : 1));
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
			if (item instanceof Scoreable scoreable) score += scoreable.totalScore();
			else score += itemScoreNonScorable(item);
			if (score >= Integer.MAX_VALUE) break;
		}
		return (int) Math.min(score,Integer.MAX_VALUE);
	}
	
	@Nullable
	public Itemable<?> getItem(@NonNegative int index) {
		return index >= storage.length ? null : storage[index];
	}
}