package fi.septicuss.tooltips.managers.condition.impl.lookingat;

import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.condition.type.MultiString;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.validation.Validity;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class LookingAtCitizen implements Condition {

	private static final String[] DISTANCE = { "distance", "dist", "d" };
	private static final String[] ID = { "id" };

	@Override
	public boolean check(Player player, Arguments args) {

		int distance = 3;

		if (args.has(DISTANCE))
			distance = args.get(DISTANCE).getAsInt();

		var rayTrace = Utils.getRayTraceResult(player, distance);
		
		if (rayTrace == null || rayTrace.getHitEntity() == null)
			return false;
		
		Entity entity = rayTrace.getHitEntity();
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
