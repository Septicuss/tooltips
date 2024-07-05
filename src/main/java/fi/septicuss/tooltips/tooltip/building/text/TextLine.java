package fi.septicuss.tooltips.tooltip.building.text;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.icon.IconManager;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.font.Spaces;
import fi.septicuss.tooltips.utils.placeholder.Placeholders;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.TextComponent;

public class TextLine {

	private static FormatRetention RETENTION = FormatRetention.NONE;
	private static Map<String, TextComponent> REPLACEABLES;

	private ComponentBuilder lineComponents;
	private String processedText;
	private boolean centered;

	public TextLine(Player player, IconManager iconManager, String unprocessedText) {

		if (unprocessedText.isEmpty()) {
			this.lineComponents = new ComponentBuilder();
			this.processedText = "";
			return;
		}
		
		if (unprocessedText.startsWith("||") && unprocessedText.endsWith("||")) {
			this.centered = true;
			unprocessedText = unprocessedText.substring(2, unprocessedText.length() - 2);
		}

		if (REPLACEABLES == null || REPLACEABLES.isEmpty()) {
			REPLACEABLES = new HashMap<>();
			REPLACEABLES.putAll(iconManager.getIconPlaceholders());
		}

		String line = Utils.color(Placeholders.replacePlaceholders(player, unprocessedText));
		
		if (!Tooltips.get().isUseSpaces())
			line = line.replace(" ", "{+2}");
		
		lineComponents = new ComponentBuilder();

		for (BaseComponent baseComponent : TextComponent.fromLegacyText(line, ChatColor.WHITE)) {
			final String plain = baseComponent.toPlainText();
			final boolean hasBrackets = (plain.contains("}") && plain.contains("{"));

			// No need to replace anything
			if (!hasBrackets) {
				lineComponents.append(baseComponent, RETENTION);
				continue;
			}

			StringBuilder stringLineBuilder = new StringBuilder();
			final int length = plain.length();

			in: for (int j = 0; j < length; j++) {
				char character = plain.charAt(j);

				if (character != '{') {
					stringLineBuilder.append(character);
					continue;
				}

				String subs = plain.substring(j + 1);

				int nextOpening = subs.indexOf('{');
				int nextClosure = subs.indexOf('}');

				boolean bracketsExist = (nextClosure != -1 && nextOpening == -1);
				boolean openingBeforeClosure = (nextOpening > nextClosure);

				if (bracketsExist || openingBeforeClosure) {
					String sub = plain.substring(j, j + nextClosure + 2);
					String withoutBrackets = sub.substring(1, sub.length() - 1);
					
					BaseComponent replacement = null;
					
					// Replace dynamic offsets
					if (withoutBrackets.startsWith("+") || withoutBrackets.startsWith("-")) {
						String amountStr = withoutBrackets.substring(1);

						if (Utils.isInteger(amountStr)) {
							int pixels = Integer.parseInt(withoutBrackets);
							replacement = Spaces.getOffset(pixels);
						}
					}
					
					// Replace other placeholders like icons
					if (REPLACEABLES.containsKey(sub)) {
						var replaceable = REPLACEABLES.get(sub);
						var replaceWith = replaceable.duplicate();
						replaceWith.copyFormatting(baseComponent, false);
						replacement = replaceWith;
					}
					
					// If replacement exists
					if (replacement != null) {
						String textUntilNow = stringLineBuilder.toString();

						if (!textUntilNow.isEmpty()) {
							var text = new TextComponent(textUntilNow);
							text.copyFormatting(baseComponent, false);
							lineComponents.append(text, RETENTION);
						}

						lineComponents.append(replacement, RETENTION);
						stringLineBuilder.setLength(0);
						
						j = j + nextClosure + 1;
						continue in;
					}

				}

				stringLineBuilder.append(character);
			}

			// Remaining
			final String remainingText = stringLineBuilder.toString();

			if (!remainingText.isEmpty()) {
				final TextComponent text = new TextComponent(remainingText);
				text.copyFormatting(baseComponent);
				lineComponents.append(text, RETENTION);
			}
		}

		final StringBuilder lineBuilder = new StringBuilder();

		for (BaseComponent base : lineComponents.getParts()) {
			lineBuilder.append(base.toPlainText());
		}

		processedText = lineBuilder.toString();
	}
	
	public ComponentBuilder getLineComponents() {
		return lineComponents;
	}

	public String getProcessedText() {
		return processedText;
	}
	
	public boolean isCentered() {
		return centered;
	}

	public static void clearReplaceables() {
		REPLACEABLES = null;
	}
}