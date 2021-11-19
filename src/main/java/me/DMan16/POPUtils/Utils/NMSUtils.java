package me.DMan16.POPUtils.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.EnumChatFormat;
import net.minecraft.network.chat.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class NMSUtils {
	
	@Nullable
	@Contract("null -> null")
	public static ChatBaseComponent componentToIChatBaseComponent(Component component) {
		if (component == null) return null;
		ChatBaseComponent comp;
		String textContent = null;
		if (component instanceof TextComponent text) {
			textContent = text.content();
			comp = new ChatComponentText(textContent);
		} else if (component instanceof TranslatableComponent translate) comp = translate.args().isEmpty() ? new ChatMessage(translate.key()) :
				new ChatMessage(translate.key(),translate.args().stream().map(NMSUtils::componentToIChatBaseComponent).filter(Objects::nonNull).toArray(Object[]::new));
		else return null;
		TextColor textColor = component.color();
		ChatHexColor color = null;
		if (textColor != null) color = (textColor instanceof NamedTextColor named) ? ChatHexColor.a(EnumChatFormat.b(named.toString())) : ChatHexColor.a(textColor.value());
		ChatModifier modifier = ChatModifier.a;
		if (color != null) modifier = modifier.setColor(color);
		if (component.hasDecoration(TextDecoration.BOLD)) modifier = modifier.setBold(true);
		if (component.hasDecoration(TextDecoration.ITALIC)) modifier = modifier.setItalic(true);
		if (component.hasDecoration(TextDecoration.OBFUSCATED)) modifier = modifier.setRandom(true);
		if (component.hasDecoration(TextDecoration.UNDERLINED)) modifier = modifier.setUnderline(true);
		if (component.hasDecoration(TextDecoration.STRIKETHROUGH)) modifier = modifier.setStrikethrough(true);
		comp.setChatModifier(modifier);
		if (component.children().isEmpty()) return comp;
		List<ChatBaseComponent> children = component.children().stream().map(NMSUtils::componentToIChatBaseComponent).filter(Objects::nonNull).collect(Collectors.toList());
		if (textContent != null && textContent.isEmpty()) {
			if (children.isEmpty()) return null;
			comp = children.get(0);
			children.remove(0);
		}
		children.forEach(comp::addSibling);
		return comp;
	}
}