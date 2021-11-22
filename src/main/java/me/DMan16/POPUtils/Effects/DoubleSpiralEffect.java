package me.DMan16.POPUtils.Effects;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.AnimatedBallEffect;
import de.slikey.effectlib.util.MathUtils;
import de.slikey.effectlib.util.VectorUtils;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class DoubleSpiralEffect extends AnimatedBallEffect {
	public DoubleSpiralEffect(EffectManager effectManager) {
		super(effectManager);
		particlesPerIteration = 5;
	}
	
	@Override
	public void onRun() {
		Vector vector1 = new Vector();
		Vector vector2 = new Vector();
		Location location = getLocation();
		for (int i = 0; i < particlesPerIteration; i++) {
			step++;
			int quarter = (((step - 1) % particles) * 2f / particles >= 1 ? 2 : 1) + (((step - 1) / particles) % 2 == 1 ? 2 : 0);
			float t = (MathUtils.PI / particles) * step;
			float r = MathUtils.sin(t) * size;
			if (r < 0) r = -r;
			if (quarter == 2 || quarter == 3) r = 2 * size - r;
			float s = 2 * MathUtils.PI * t;
			vector1.setX(xFactor * r * MathUtils.cos(s) + xOffset);
			vector1.setZ(zFactor * r * MathUtils.sin(s) + zOffset);
			vector1.setY(yFactor * size * MathUtils.cos(t) + yOffset);
			vector2.setX(-vector1.getX());
			vector2.setZ(-vector1.getZ());
			vector2.setY(vector1.getY());
			
			VectorUtils.rotateVector(vector1,xRotation,yRotation,zRotation);
			
			display(particle,location.add(vector1));
			location.subtract(vector1);
			display(particle,location.add(vector2));
			location.subtract(vector2);
		}
	}
}