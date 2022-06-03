package me.DMan16.DMan16Utils.Utils;

import me.DMan16.DMan16Utils.NMSWrappers.ComponentWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NMSUtils {
	
	@Nullable
	@Contract("null -> null")
	public static ComponentWrapper.Safe componentToNMSComponent(net.kyori.adventure.text.Component component) {
		return Utils.applyNotNull(convert(component),ComponentWrapper.Safe::new);
	}
	
	@Nullable
	@Contract("null -> null")
	private static BaseComponent convert(net.kyori.adventure.text.Component component) {
		if (component == null) return null;
		BaseComponent comp;
		String textContent = null;
		if (component instanceof net.kyori.adventure.text.TextComponent text) {
			textContent = text.content();
			comp = new TextComponent(textContent);
		} else if (component instanceof net.kyori.adventure.text.TranslatableComponent translate) comp = translate.args().isEmpty() ? new TranslatableComponent(translate.key()) :
				new TranslatableComponent(translate.key(),translate.args().stream().map(NMSUtils::componentToNMSComponent).filter(Objects::nonNull).toArray(Object[]::new));
		else return null;
		net.kyori.adventure.text.format.TextColor textColor = component.color();
		TextColor color = null;
		if (textColor != null) color = (textColor instanceof net.kyori.adventure.text.format.NamedTextColor named) ? TextColor.fromLegacyFormat(ChatFormatting.getByName(named.toString())) :
				TextColor.fromRgb(textColor.value());
		Style style = Style.EMPTY;
		if (color != null) style = style.withColor(color);
		Boolean bool;
		if ((bool = decorationStateToBoolean(component.decoration(net.kyori.adventure.text.format.TextDecoration.BOLD))) != null) style = style.withBold(bool);
		if ((bool = decorationStateToBoolean(component.decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC))) != null) style = style.withItalic(bool);
		if ((bool = decorationStateToBoolean(component.decoration(net.kyori.adventure.text.format.TextDecoration.OBFUSCATED))) != null) style = style.withObfuscated(bool);
		if ((bool = decorationStateToBoolean(component.decoration(net.kyori.adventure.text.format.TextDecoration.UNDERLINED))) != null) style = style.withUnderlined(bool);
		if ((bool = decorationStateToBoolean(component.decoration(net.kyori.adventure.text.format.TextDecoration.STRIKETHROUGH))) != null) style = style.withStrikethrough(bool);
		comp.setStyle(style);
		if (component.children().isEmpty()) return comp;
		List<BaseComponent> children = component.children().stream().map(NMSUtils::convert).filter(Objects::nonNull).collect(Collectors.toList());
		if (textContent != null && textContent.isEmpty()) {
			if (children.isEmpty()) return null;
			comp = children.get(0);
			children.remove(0);
		}
		children.forEach(comp::append);
		return comp;
	}
	
	@Nullable
	private static Boolean decorationStateToBoolean(@NotNull net.kyori.adventure.text.format.TextDecoration.State state) {
		return switch (state) {
			case TRUE -> true;
			case FALSE -> false;
			default -> null;
		};
	}
}