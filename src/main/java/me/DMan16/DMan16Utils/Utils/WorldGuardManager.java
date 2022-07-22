package me.DMan16.DMan16Utils.Utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WorldGuardManager {
	public final FlagRegistry flagRegistry;
	
	public WorldGuardManager() {
		flagRegistry = WorldGuard.getInstance().getFlagRegistry();
	}
	
	@NotNull
	public List<ProtectedRegion> getRegions(@NotNull Location loc) {
		return sortRegionsByPriority(getRegionSet(loc));
	}
	
	@NotNull
	public Map<String,ProtectedRegion> getRegions(@NotNull World world) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(Objects.requireNonNull(world)));
		return regions == null ? new HashMap<>() : regions.getRegions();
	}
	
	@NotNull
	public List<ProtectedRegion> getRegions(@NotNull ProtectedRegion region,@NotNull World world) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(world));
		return regions == null ? new ArrayList<>() : sortRegionsByPriority(regions.getApplicableRegions(region));
	}
	
	@Nullable
	public ApplicableRegionSet getRegionSet(@NotNull Location loc) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(loc.getWorld()));
		return regions == null ? null : regions.getApplicableRegions(BlockVector3.at(loc.getX(),loc.getY(),loc.getZ()));
	}
	
	@NotNull
	public List<ProtectedRegion> sortRegionsByPriority(@Nullable ApplicableRegionSet regions) {
		return sortRegionsByPriority(regions == null ? null : new ArrayList<>(regions.getRegions()));
	}
	
	@NotNull
	public List<ProtectedRegion> sortRegionsByPriority(@Nullable List<ProtectedRegion> regions) {
		List<ProtectedRegion> regionList = new ArrayList<>();
		if (regions != null && regions.size() > 0) {
			regionList.addAll(regions);
			regionList.sort(Comparator.comparingInt(ProtectedRegion::getPriority));
		}
		return regionList;
	}
	
	@Nullable
	public StateFlag newStateFlag(@NotNull String name,boolean defaultValue) {
		try {
			StateFlag flag = new StateFlag(name,defaultValue);
			flagRegistry.register(flag);
			return flag;
		} catch (Exception e) {
			Flag<?> flag = flagRegistry.get(name);
			if (flag instanceof StateFlag) return (StateFlag) flag;
		}
		return null;
	}
	
	@Nullable
	public StringFlag newStringFlag(@NotNull String name,String defaultValue) {
		try {
			StringFlag flag = new StringFlag(name,defaultValue);
			flagRegistry.register(flag);
			return flag;
		} catch (Exception e) {
			Flag<?> flag = flagRegistry.get(name);
			if (flag instanceof StringFlag) return (StringFlag) flag;
		}
		return null;
	}
	
	@Nullable
	public IntegerFlag newIntegerFlag(@NotNull String name) {
		try {
			IntegerFlag flag = new IntegerFlag(name);
			flagRegistry.register(flag);
			return flag;
		} catch (Exception e) {
			Flag<?> flag = flagRegistry.get(name);
			if (flag instanceof IntegerFlag) return (IntegerFlag) flag;
		}
		return null;
	}
}