package me.DMan16.POPUtils.Utils;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CitizensManager {
	List<TraitInfo> newTraits;
	
	public CitizensManager() {
		newTraits = new ArrayList<>();
	}
	
	public void registerTrait(@NotNull Class<? extends Trait> clazz, @NotNull String name) {
		TraitInfo trait = TraitInfo.create(clazz).withName(name);
		newTraits.add(trait);
		CitizensAPI.getTraitFactory().registerTrait(trait);
	}
	
	public boolean isNPC(@NotNull Entity entity) {
		return CitizensAPI.getNPCRegistry().isNPC(entity);
	}
}