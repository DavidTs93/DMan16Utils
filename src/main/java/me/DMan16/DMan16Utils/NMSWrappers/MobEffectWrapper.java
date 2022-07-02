package me.DMan16.DMan16Utils.NMSWrappers;

import net.minecraft.world.effect.MobEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("deprecation")
public sealed class MobEffectWrapper permits MobEffectWrapper.Safe {
	/**
	 * !null -> safe to cast to {@link net.minecraft.world.effect.MobEffect}!</>
	 */
	protected final Object mobEffect;
	
	public MobEffectWrapper(@NotNull Object obj) {
		int id;
		mobEffect = (obj instanceof net.minecraft.world.effect.MobEffect effect) ? ((id = MobEffect.getId(effect)) > 0 && id <= PotionEffectType.values().length ? obj : null) : null;
	}
	
	/**
	 * !null -> safe to cast to {@link net.minecraft.world.effect.MobEffect}!</>
	 */
	@MonotonicNonNull
	public final Object mobEffect() {
		return mobEffect;
	}
	
	public final boolean isMobEffect() {
		return mobEffect != null;
	}
	
	@MonotonicNonNull
	public PotionEffectType toPotionEffectType() {
		if (!(mobEffect instanceof net.minecraft.world.effect.MobEffect effect)) return null;
		int id = MobEffect.getId(effect);
		return id > 0 ? PotionEffectType.getById(id) : null;
	}
	
	@NotNull
	public static MobEffectWrapper.Safe fromPotionEffectType(@NotNull PotionEffectType type) {
		return new Safe(Objects.requireNonNull(MobEffect.byId(type.getId())));
	}
	
	public static final class Safe extends MobEffectWrapper {
		public Safe(@NotNull Object obj) {
			super(obj);
			if (!isMobEffect()) throw new IllegalArgumentException();
		}
		
		@NotNull
		public PotionEffectType toPotionEffectType() {
			return super.toPotionEffectType();
		}
	}
}