package me.DMan16.DMan16Utils.Utils;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
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
	
	public void registerTrait(@NotNull Class<? extends Trait> clazz,@NotNull String name) {
		TraitInfo trait = TraitInfo.create(clazz).withName(name);
		newTraits.add(trait);
		CitizensAPI.getTraitFactory().registerTrait(trait);
	}
	
	public boolean isNPC(@NotNull Entity entity,@Nullable NPCRegistry registry) {
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
	public NPC getNPC(@NotNull Entity entity,@Nullable NPCRegistry registry) {
		if (registry == null) registry = CitizensAPI.getNPCRegistry();
		return registry.getNPC(entity);
	}
	
	public boolean applySkin(@NotNull NPC npc,String name,String signature,String data) {
		if (name != null && signature != null && data != null) try {
			npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(name,signature,data);
			return true;
		} catch (Exception e) {e.printStackTrace();}
		return false;
	}
	
	public boolean applySkin(@NotNull NPC npc,Player player) {
		if (player != null && (npc.getEntity() instanceof Player)) try {
			GameProfile profile = Utils.getProfile(player);
			Property property = Iterables.getFirst(profile.getProperties().get("textures"),null);
			assert property != null;
			applySkin(npc,player.getName(),property.getSignature(),property.getValue());
			return true;
		} catch (Exception e1) {
			e1.printStackTrace();
			try {
				npc.getOrAddTrait(SkinTrait.class).setSkinName(player.getName());
				return true;
			} catch (Exception e2) {}
		}
		return false;
	}
	
	public boolean applySkin(@NotNull NPC npc,String name) {
		if (name != null) try {
			npc.getOrAddTrait(SkinTrait.class).setSkinName(name);
			return true;
		} catch (Exception e2) {}
		return false;
	}
	
	@NotNull
	public NPC addInvisible(@NotNull NPC npc) {
		npc.data().setPersistent("invisible",true);
		return npc;
	}
	
	@NotNull
	public NPC removeInvisible(@NotNull NPC npc) {
		npc.data().setPersistent("invisible",false);
		return npc;
	}
	
	@NotNull
	public NPC addGlow(@NotNull NPC npc) {
		npc.data().setPersistent("glowing",true);
		return npc;
	}
	
	@NotNull
	public NPC removeGlow(@NotNull NPC npc) {
		npc.data().setPersistent("glowing",false);
		return npc;
	}
}