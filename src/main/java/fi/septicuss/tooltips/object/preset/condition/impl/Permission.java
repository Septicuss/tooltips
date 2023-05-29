package fi.septicuss.tooltips.object.preset.condition.impl;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.object.preset.condition.Condition;
import fi.septicuss.tooltips.object.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.object.preset.condition.type.MultiString;
import fi.septicuss.tooltips.object.validation.Validity;

public class Permission implements Condition {

	private static final String[] PERMISSION = { "p", "perm", "permission" };

	@Override
	public boolean check(Player player, Arguments args) {
		MultiString permissions = MultiString.of(args.get(PERMISSION).getAsString());
		for (var permission : permissions.getStrings())
			if (player.hasPermission(permission))
				return true;
		return false;
	}

	@Override
	public Validity valid(Arguments args) {
		if (!args.has(PERMISSION)) {
			return Validity.of(false, "Permission argument is required");
		}

		return Validity.TRUE;
	}

}
