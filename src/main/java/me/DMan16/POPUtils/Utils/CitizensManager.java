package me.DMan16.POPUtils.Utils;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CitizensManager {
	private final List<TraitInfo> newTraits;
	
	public CitizensManager() {
		newTraits = new ArrayList<>();
	}
	
	@NotNull
	@Unmodifiable
	public List<TraitInfo> getNewTraits() {
		return Collections.unmodifiableList(newTraits);
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