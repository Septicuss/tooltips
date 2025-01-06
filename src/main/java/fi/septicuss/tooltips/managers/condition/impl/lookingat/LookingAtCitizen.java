package fi.septicuss.tooltips.managers.condition.impl.lookingat;

import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.Context;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.condition.type.MultiString;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.validation.Validity;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class LookingAtCitizen implements Condition {

	private static final String[] DISTANCE = { "distance", "dist", "d" };
	private static final String[] ID = { "id" };

	@Override
	public boolean check(Player player, Arguments args) {

		MultiString ids = null;

		if (args.has(ID))
			ids = MultiString.of(args.get(ID).getAsString());

		final NPC citizen = this.getCitizen(player, args);

		if (citizen == null) {
			return false;
		}

		// Any citizen
		if (ids == null) {
			return true;
		}

		// Specific ID citizen
		final String id = String.valueOf(citizen.getId());
		return ids.contains(id);
	}

	@Override
	public void writeContext(Player player, Arguments args, Context context) {
		final NPC citizen = this.getCitizen(player, args);

		if (citizen == null) {
			return;
		}

		context.put("citizen.id", citizen.getId());
	}

	private NPC getCitizen(Player player, Arguments args) {
		int distance = 3;

		if (args.has(DISTANCE))
			distance = args.get(DISTANCE).getAsInt();

		var rayTrace = Utils.getRayTraceResult(player, distance);

		if (rayTrace == null || rayTrace.getHitEntity() == null)
			return null;

		Entity entity = rayTrace.getHitEntity();

		NPCRegistry registry = CitizensAPI.getNPCRegistry();

		if (!registry.isNPC(entity))
			return null;

		return registry.getNPC(entity);
	}

	@Override
	public Validity valid(Arguments args) {
		if (!Bukkit.getPluginManager().isPluginEnabled("Citizens"))
			return Validity.of(false, "Citizens is required for this condition.");

		if (CitizensAPI.getNPCRegistry() == null)
			return Validity.of(false, "Citizens is required for this condition.");

		if (args.has(DISTANCE) && !args.isNumber(DISTANCE))
			return Validity.of(false, "Distance must be a number");

		return Validity.TRUE;
	}

	@Override
	public String id() {
		return "lookingatcitizen";
	}
}
