package me.DMan16.DMan16Utils.Classes;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Gradient {
	private final List<TextColor> colorValues = new ArrayList<>();
	
	public Gradient(int @NotNull ... colorValues) {
		for (int num : colorValues) this.colorValues.add(TextColor.color(num));
		if (this.colorValues.isEmpty()) throw new IllegalArgumentException("Cannot initiate with no values!");
	}
	
	public Gradient(TextColor @NotNull ... colors) {
		for (TextColor color : colors) if (color != null) this.colorValues.add(color);
		if (this.colorValues.isEmpty()) throw new IllegalArgumentException("Cannot initiate with no values!");
	}
	
	public Gradient(Color @NotNull ... colors) {
		for (Color color : colors) if (color != null) this.colorValues.add(TextColor.color(color.asRGB()));
		if (this.colorValues.isEmpty()) throw new IllegalArgumentException("Cannot initiate with no values!");
	}
	
	@NotNull
	public List<Integer> getGradientInts(int num) {
		List<Integer> gradient = new ArrayList<>();
		for (TextColor color : getGradientTextColors(num)) gradient.add(color.value());
		return gradient;
	}
	
	@NotNull
	public List<TextColor> getGradientTextColors(int num) {
		List<TextColor> gradient = new ArrayList<>();
		if (num == 0) return gradient;
		if (num == 1) gradient.add(colorValues.get(0));
		else if (num == 2) {
			gradient.add(colorValues.get(0));
			gradient.add(colorValues.get(colorValues.size() - 1));
		} else {
			TextColor current;
			TextColor next;
			current = num > 0 ? colorValues.get(0) : colorValues.get(colorValues.size() - 1);
			for (int i = 1; i < colorValues.size() - 1; i++) {
				int idx = num > 0 ? i : colorValues.size() - 1 - i;
				next = colorValues.get(idx);
				gradient.addAll(makeGradient(current,next,num));
				current = next;
			}
			gradient.add(num > 0 ? colorValues.get(colorValues.size() - 1) : colorValues.get(0));
		}
		return gradient;
	}
	
	@NotNull
	private List<TextColor> makeGradient(TextColor start,TextColor end,int num) {
		List<TextColor> gradient = new ArrayList<>();
		for (int i = 0; i < num; i++) {
			float ratio = (float) i / (float) num;
			int red = Math.round(end.red() * ratio + start.red() * (1 - ratio));
			int green = Math.round(end.green() * ratio + start.green() * (1 - ratio));
			int blue = Math.round(end.blue() * ratio + start.blue() * (1 - ratio));
			gradient.add(TextColor.color(red,green,blue));
		}
		return gradient;
	}
	
	@NotNull
	public List<Color> getGradientColors(int num) {
		return getGradientTextColors(num).stream().map(color -> Color.fromRGB(color.red(),color.green(),color.blue())).collect(Collectors.toList());
	}
}