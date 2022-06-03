package me.DMan16.DMan16Utils.Effects;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.AnimatedBallEffect;
import de.slikey.effectlib.util.MathUtils;
import de.slikey.effectlib.util.VectorUtils;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class HalfSpiralEffect extends AnimatedBallEffect {
	public HalfSpiralEffect(EffectManager effectManager) {
		super(effectManager);
		particlesPerIteration = 5;
	}
	
	@Override
	public void onRun() {
		Vector vector = new Vector();
		Location location = getLocation();
		for (int i = 0; i < particlesPerIteration; i++) {
			step++;
			int quarter = (((step - 1) % particles) * 2f / particles >= 1 ? 2 : 1) + (((step - 1) / particles) % 2 == 1 ? 2 : 0);
			float t = (MathUtils.PI / particles) * step;
			float r = MathUtils.sin(t) * size;
			if (r < 0) r = -r;
			r = (r + 1) / 2;
			if (quarter == 2 || quarter == 3) r = 2 * size - r;
			float s = 2 * MathUtils.PI * t;
			vector.setX(xFactor * r * MathUtils.cos(s) + xOffset);
			vector.setZ(zFactor * r * MathUtils.sin(s) + zOffset);
			vector.setY(yFactor * size * MathUtils.cos(t) + yOffset);
			
			VectorUtils.rotateVector(vector,xRotation,yRotation,zRotation);
			
			display(particle,location.add(vector));
			location.subtract(vector);
		}
	}
}