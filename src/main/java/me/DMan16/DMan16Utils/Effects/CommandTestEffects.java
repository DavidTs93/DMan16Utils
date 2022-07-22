package me.DMan16.DMan16Utils.Effects;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.*;
import me.DMan16.DMan16Utils.DMan16UtilsMain;
import me.DMan16.DMan16Utils.Utils.Utils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandTestEffects implements CommandExecutor,TabCompleter {
	private static final List<String> EFFECTS = Arrays.asList("animated_ball","arc","atom","big_bang","circle","cone","cube","cuboid","cylinder","disco","donut","earth","equation",
			"fountain","grid","helix","hill","jump","plot","pyramid","shield","sky_rocket","sphere","square","star","tornado","trace","turn","vortex","warp","wave",
			"spiral1","spiral2","spiral3","circle2","circle3");
	
	private final EffectManager manager;
	
	public CommandTestEffects() {
		PluginCommand command = DMan16UtilsMain.getInstance().getCommand("test_effect");
		assert command != null;
		command.setExecutor(this);
		manager = new EffectManager(DMan16UtilsMain.getInstance());
	}
	
	void disable() {
		manager.dispose();
	}
	
	public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label,String[] args) {
		if (args.length == 1 && (sender instanceof Player player)) {
			Effect effect = effect(EFFECTS.indexOf(args[0].toLowerCase()));
			if (effect != null) {
				Location loc = player.getLocation().clone()/*.add(0, 4, 0)*/;
				loc.setYaw(0);
				loc.setPitch(0);
				effect.setLocation(loc);
				effect.start();
			}
		}
		return true;
	}
	
	@Nullable
	private Effect effect(int idx) {
		return idx < 0 ? null : switch (idx) {
			case 0 -> new AnimatedBallEffect(manager);
			case 1 -> new ArcEffect(manager);
			case 2 -> new AtomEffect(manager);
			case 3 -> new BigBangEffect(manager);
			case 4 -> circle();
			case 5 -> cone();
			case 6 -> new CubeEffect(manager);
			case 7 -> new CuboidEffect(manager);
			case 8 -> new CylinderEffect(manager);
			case 9 -> new DiscoBallEffect(manager);
			case 10 -> new DonutEffect(manager);
			case 11 -> new EarthEffect(manager);
			case 12 -> new EquationEffect(manager);
			case 13 -> new FountainEffect(manager);
			case 14 -> new GridEffect(manager);
			case 15 -> new HelixEffect(manager);
			case 16 -> new HillEffect(manager);
			case 17 -> new JumpEffect(manager);
			case 18 -> new PlotEffect(manager);
			case 19 -> new PyramidEffect(manager);
			case 20 -> new ShieldEffect(manager);
			case 21 -> new SkyRocketEffect(manager);
			case 22 -> new SphereEffect(manager);
			case 23 -> new SquareEffect(manager);
			case 24 -> new StarEffect(manager);
			case 25 -> new TornadoEffect(manager);
			case 26 -> new TraceEffect(manager);
			case 27 -> new TurnEffect(manager);
			case 28 -> new VortexEffect(manager);
			case 29 -> new WarpEffect(manager);
			case 30 -> new WaveEffect(manager);
			case 31 -> spiral1();
			case 32 -> spiral2();
			case 33 -> spiral3();
			case 34 -> circle2();
			case 35 -> circle3();
			default -> null;
		};
	}
	
	@NotNull
	private Effect circle() {
		CircleFlatEffect effect = new CircleFlatEffect(manager);
		effect.particle = Particle.REDSTONE;
		effect.colorList = Arrays.asList(Color.AQUA,Color.FUCHSIA);
		effect.size = 1.5f;
		effect.particles = 500;
		effect.yOffset = 0.1f;
		return effect;
	}
	
	@NotNull
	private Effect circle2() {
		CircleFlatEffect effect = new CircleFlatEffect(manager);
		effect.particle = Particle.REDSTONE;
		effect.colorList = Arrays.asList(Color.AQUA,Color.FUCHSIA);
		effect.size = 1.5f;
		effect.particles = 500;
		effect.yOffset = 0.1f;
		effect.period = 2;
		return effect;
	}
	
	@NotNull
	private Effect circle3() {
		CircleFlatEffect effect = new CircleFlatEffect(manager);
		effect.particle = Particle.REDSTONE;
		effect.colorList = Arrays.asList(Color.AQUA,Color.FUCHSIA);
		effect.size = 1.5f;
		effect.particles = 1000;
		effect.yOffset = 0.1f;
		effect.period = 1;
		return effect;
	}
	
	@NotNull
	private Effect spiral1() {
		SpiralEffect effect = new SpiralEffect(manager);
		effect.particle = Particle.REDSTONE;
		effect.colorList = Arrays.asList(Color.PURPLE,Color.YELLOW);
		effect.size = 1.3f;
		effect.particles = 500;
		effect.xFactor = 0.75f;
		effect.yFactor = 0.5f;
		effect.zFactor = 0.75f;
		effect.yOffset = 0.675f;
		return effect;
	}
	
	@NotNull
	private Effect spiral2() {
		SpiralEffect effect = new SpiralEffect(manager);
		effect.particle = Particle.REDSTONE;
		effect.colorList = Arrays.asList(Color.BLUE,Color.GREEN);
		effect.size = 1.3f;
		effect.particles = 500;
		effect.xFactor = 1;
		effect.yFactor = 0.5f;
		effect.zFactor = 1;
		effect.yOffset = 0.675f;
		return effect;
	}
	
	@NotNull
	private Effect spiral3() {
		SpiralEffect effect = new SpiralEffect(manager);
		effect.particle = Particle.REDSTONE;
		effect.colorList = Arrays.asList(Color.BLUE,Color.GREEN);
		effect.size = 1.3f;
		effect.particles = 500;
		effect.xFactor = 1.25f;
		effect.yFactor = 0.5f;
		effect.zFactor = 1.25f;
		effect.yOffset = 0.675f;
		return effect;
	}
	
	@NotNull
	private Effect cone() {
		ConeEffect effect = new ConeEffect(manager);
		effect.particle = Particle.REDSTONE;
		effect.colorList = Arrays.asList(Color.GRAY,Color.LIME);
		effect.particles = 50;
		effect.iterations = -1;
		effect.particlesCone = 300;
		effect.lengthGrow = 0.025f;
		effect.radiusGrow = 0.02f;
		return effect;
	}
	
	public List<String> onTabComplete(@NotNull CommandSender sender,@NotNull Command command,@NotNull String alias,@NotNull String[] args) {
		if (args.length == 1) return EFFECTS.stream().filter(cmd -> Utils.containsTabComplete(args[0],cmd)).map(String::toLowerCase).collect(Collectors.toList());
		return new ArrayList<>();
	}
}