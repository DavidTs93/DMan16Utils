package me.DMan16.DMan16Utils.Effects;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.AnimatedBallEffect;
import de.slikey.effectlib.util.MathUtils;
import de.slikey.effectlib.util.VectorUtils;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class CircleFlatEffect extends AnimatedBallEffect {
	public CircleFlatEffect(EffectManager effectManager) {
		super(effectManager);
	}
	
	@Override
	public void onRun() {
		Vector vector = new Vector();
		Location location = getLocation();
		for (int i = 0; i < particlesPerIteration; i++) {
			step++;
			
			float s = MathUtils.PI * MathUtils.PI * step / particles;
			vector.setX(xFactor * size * MathUtils.cos(s) + xOffset);
			vector.setZ(zFactor * size * MathUtils.sin(s) + zOffset);
			vector.setY(yOffset);
			
			VectorUtils.rotateVector(vector,xRotation,yRotation,zRotation);
			
			display(particle,location.add(vector));
			location.subtract(vector);
		}
	}
}