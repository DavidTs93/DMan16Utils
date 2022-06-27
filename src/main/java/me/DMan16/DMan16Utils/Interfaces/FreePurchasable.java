package me.DMan16.DMan16Utils.Interfaces;

import me.DMan16.DMan16Utils.Classes.Empty;
import me.DMan16.DMan16Utils.Classes.Pair;
import me.DMan16.DMan16Utils.Classes.Trio;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;

public interface FreePurchasable<V> extends QuickPurchasable<V> {
	@NotNull
	default BigInteger getPrice(@NotNull Player player,Empty val) {
		return BigInteger.ZERO;
	}
	
	default void purchasePaid(@NotNull Player player,@NotNull Trio<@NotNull Pair<@NotNull V,@NotNull BigInteger>,@Nullable Pair<@NotNull V,@NotNull BigInteger>,@Nullable Pair<@NotNull V,@NotNull BigInteger>> prices,@Nullable Runnable onSuccess,@Nullable Runnable onFail,Empty val) {}
}