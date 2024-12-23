package fi.septicuss.tooltips.managers.preset.actions.command.impl;

import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.preset.actions.command.ActionCommand;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.validation.Validity;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class SoundCommand implements ActionCommand {

	@Override
	public void run(Player player, Arguments arguments) {
		String sound = arguments.get("1").getAsString().toLowerCase();
		SoundCategory category = SoundCategory.valueOf(arguments.get("2").getAsString().toUpperCase());
		float volume = arguments.get("3").getAsFloat();
		float pitch = arguments.get("4").getAsFloat();
		
		player.playSound(player.getLocation(), sound, category, volume, pitch);
	}

	@Override
	public Validity validity(Arguments arguments) {

		if (!arguments.has("4"))
			return Validity.of(false, "Not enough arguments: sound (sound) (category) (volume) (pitch)");

		String category = arguments.get("2").getAsString();
		
		if (!Utils.enumExists(SoundCategory.class, category))
			return Validity.of(false, "Unknown sound category " + Utils.quote(category));
			
		if (!arguments.isNumber("3") || !arguments.isNumber("4"))
			return Validity.of(false, "Volume and pitch must be floats");
		
		return Validity.TRUE;
	}

}
