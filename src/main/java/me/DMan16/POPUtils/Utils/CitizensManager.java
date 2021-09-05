package me.DMan16.POPUtils.Utils;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
	
	public boolean isNPC(@NotNull Entity entity, @Nullable NPCRegistry registry) {
		return getNPC(entity,registry) != null;
	}
	
	public boolean isNPC(@NotNull Entity entity) {
		return getNPC(entity) != null;
	}
	
	@Nullable
	public NPC getNPC(@NotNull Entity entity) {
		for (NPCRegistry registry : CitizensAPI.getNPCRegistries()) {
			if (registry == null) continue;
			NPC npc = registry.getNPC(entity);
			if (npc != null) return npc;
		}
		return null;
	}
	
	@Nullable
	public NPC getNPC(@NotNull Entity entity, @Nullable NPCRegistry registry) {
		if (registry == null) registry = CitizensAPI.getNPCRegistry();
		return registry.getNPC(entity);
	}
	
	public boolean applySkin(@NotNull NPC npc, String name, String signature, String data) {
		if (name != null && signature != null && data != null && (npc.getEntity() instanceof Player)) try {
			npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(name,signature,data);
			return true;
		} catch (Exception e) {}
		return false;
	}
}