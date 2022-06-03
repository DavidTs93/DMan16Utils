package me.DMan16.DMan16Utils.Effects;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.MathUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class TwoSpiritsEffect extends Effect {
	public TwoSpiritsEffect(EffectManager effectManager) {
		super(effectManager);
		type = EffectType.REPEATING;
		iterations = 500;
		period = 1;
	}
	
	/**
	 * ParticleType of spawned particle
	 */
	public Particle particle = Particle.SPELL_WITCH;
	
	/**
	 * Ball particles total (150)
	 */
	public int particles = 150;
	
	/**
	 * The amount of particles, displayed in one iteration (10)
	 */
	public int particlesPerIteration = 10;
	
	/**
	 * Size of this ball (1)
	 */
	public float size = 1F;
	
	/**
	 * Factors (1, 2, 1)
	 */
	public float xFactor = 1F, yFactor = 2F, zFactor = 1F;
	
	/**
	 * Offsets (0, 0.8, 0)
	 */
	public float xOffset, yOffset = 0.8F, zOffset;
	
	/**
	 * Internal Counter
	 */
	protected int step = 0;
	
	@Override
	public void reset() {
		step = 0;
	}
	
	@Override
	public void onRun() {
		Vector vector1 = new Vector();
		Vector vector2 = new Vector();
		Location location = getLocation();
		for (int i = 0; i < particlesPerIteration; i++) {
			step++;
			
			float s = MathUtils.PI * MathUtils.PI * step / particles;
			vector1.setX(xFactor * size * MathUtils.cos(s) + xOffset);
			vector1.setZ(zFactor * size * MathUtils.sin(s) + zOffset);
			vector1.setY(yOffset);
			vector2.setX(-vector1.getX());
			vector2.setZ(-vector1.getZ());
			vector2.setY(vector1.getY());
			
			display(particle,location.add(vector1));
			location.subtract(vector1);
			display(particle,location.add(vector2));
			location.subtract(vector2);
		}
	}
}