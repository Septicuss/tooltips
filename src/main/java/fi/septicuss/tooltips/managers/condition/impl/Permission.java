package fi.septicuss.tooltips.managers.condition.impl;

import fi.septicuss.tooltips.utils.Text;
import org.bukkit.entity.Player;

import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.condition.type.MultiString;
import fi.septicuss.tooltips.utils.validation.Validity;

public class Permission implements Condition {

	private static final String[] PERMISSION = { "p", "perm", "permission" };

	@Override
	public boolean check(Player player, Arguments args) {
		MultiString permissions = MultiString.of(args.get(PERMISSION).getAsString());
		for (var permission : permissions.getStrings())
			if (player.hasPermission(Text.processText(player, permission)))
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

	@Override
	public String id() {
		return "permission";
	}
}
