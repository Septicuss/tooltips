package fi.septicuss.tooltips.managers.integration.impl.papi;

import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.utils.variable.Variables;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TooltipsExpansion extends PlaceholderExpansion {

	@Override
	public @NotNull String getIdentifier() {
		return "tooltips";
	}

	@Override
	public @NotNull String getAuthor() {
		return "Septicuss";
	}

	@Override
	public @NotNull String getVersion() {
		return "1.1";
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {

		String[] paramArgs = params.split("_");
		String firstParam = paramArgs[0];

		boolean variable = false;
		boolean persistent = false;
		boolean global = false;

		char[] chars = firstParam.toCharArray();
		int length = chars.length;

		if (length > 3) {
			return null;
		}

		for (char c : firstParam.toCharArray()) {
			if (c == 'v') {
				variable = true;
				continue;
			}

			if (c == 'p') {
				persistent = true;
				continue;
			}

			if (c == 'g') {
				global = true;
				continue;
			}

			return null;
		}

		if (!variable) {
			return null;
		}

		if (params.length() < length + 1) {
			return null;
		}

		String variableName = params.substring(length + 1);

		if (variableName.isBlank()) {
			return null;
		}

		Argument argument = null;

		if (persistent) {
			if (global) {
				argument = Variables.PERSISTENT.getVar(variableName);
			} else {
				argument = Variables.PERSISTENT.getVar(player, variableName);
			}
		} else {
			if (global) {
				argument = Variables.LOCAL.getVar(variableName);
			} else {
				argument = Variables.LOCAL.getVar(player, variableName);
			}
		}

		if (argument == null || argument.getAsString() == null) {
			return null;
		}

		return argument.getAsString();
	}

}
