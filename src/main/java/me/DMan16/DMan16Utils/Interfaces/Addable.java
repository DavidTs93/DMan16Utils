package me.DMan16.DMan16Utils.Interfaces;

import me.DMan16.DMan16Utils.DMan16UtilsMain;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface Addable {
	default boolean addASync() {
		return false;
	}
	
	boolean add(@NotNull Player player) throws Exception;
	
	/**
	 * @param onSuccess - will run Sync
	 * @param onFail - will run ASync/Sync
	 */
	default void add(@NotNull Player player,@Nullable Consumer<@NotNull Boolean> onSuccess,@Nullable Runnable onFail) {
		if (addASync()) new BukkitRunnable() {
			public void run() {
				try {
					boolean result = add(player);
					if (onSuccess != null) new BukkitRunnable() {
						public void run() {
							onSuccess.accept(result);
						}
					}.runTask(DMan16UtilsMain.getInstance());
				} catch (Exception e) {
					if (onFail != null) onFail.run();
				}
			}
		}.runTaskAsynchronously(DMan16UtilsMain.getInstance());
		else try {
			boolean result = add(player);
			if (onSuccess != null) onSuccess.accept(result);
		} catch (Exception e) {
			if (onFail != null) onFail.run();
		}
	}
}