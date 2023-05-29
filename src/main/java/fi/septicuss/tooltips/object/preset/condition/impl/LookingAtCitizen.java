package fi.septicuss.tooltips.object.preset.condition.impl;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import fi.septicuss.tooltips.integrations.IntegratedPlugin;
import fi.septicuss.tooltips.object.preset.condition.Condition;
import fi.septicuss.tooltips.object.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.object.preset.condition.type.MultiString;
import fi.septicuss.tooltips.object.validation.Validity;
import fi.septicuss.tooltips.utils.Utils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;

public class LookingAtCitizen implements Condition {

	private static final String[] DISTANCE = { "distance", "dist", "d" };
	private static final String[] ID = { "id" };

	@Override
	public boolean check(Player player, Arguments args) {

		int distance = 3;

		if (args.has(DISTANCE))
			distance = args.get(DISTANCE).getAsInt();

		Entity entity = Utils.getEntityPlayerIsLookingAt(player, distance, 0, Lists.newArrayList());

		if (entity == null)
			return false;

		MultiString ids = null;

		if (args.has(ID))
			ids = MultiString.of(args.get(ID).getAsString());

		NPCRegistry registry = CitizensAPI.getNPCRegistry();

		if (!registry.isNPC(entity))
			return false;

		// Any Citizen
		if (ids == null)
			return true;

		String id = String.valueOf(registry.getNPC(entity).getId());
		return ids.contains(id);
	}

	@Override
	public Validity valid(Arguments args) {
		if (!IntegratedPlugin.CITIZENS.isEnabled())
			return Validity.of(false, "Citizens is required for this condition.");

		if (CitizensAPI.getNPCRegistry() == null)
			return Validity.of(false, "Citizens is required for this condition.");

		if (args.has(DISTANCE) && !args.isNumber(DISTANCE))
			return Validity.of(false, "Distance must be a number");

		return Validity.TRUE;
	}

}
