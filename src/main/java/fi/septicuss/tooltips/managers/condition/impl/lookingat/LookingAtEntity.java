package fi.septicuss.tooltips.managers.condition.impl.lookingat;

import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.Context;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.condition.type.EnumOptions;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.validation.Validity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class LookingAtEntity implements Condition {

	private static final String[] DISTANCE = {"d", "dist", "distance"};
	private static final String[] TYPE = {"entity", "type", "t"};
	
	@Override
	public boolean check(Player player, Arguments args) {
		return this.entity(player, args) != null;
	}

	@Override
	public void writeContext(Player player, Arguments args, Context context) {
		Entity entity = this.entity(player, args);
		if (entity != null)
			context.put("lookingatentity.name", entity.getName());
	}

	@Override
	public Validity valid(Arguments args) {
		
		if (args.has(DISTANCE) && !args.isNumber(DISTANCE)) {
			return Validity.of(false, "Distance integer must be present");
		}
		
		if (args.has(TYPE)) {
			String type = args.get(TYPE).getAsString();
			Validity typeValidity = EnumOptions.validity(EntityType.class, type);
			
			if (!typeValidity.isValid())
				return typeValidity;
		}
		
		return Validity.TRUE;
	}

	private Entity entity(Player player, Arguments args) {
		int distance = 3;
		EnumOptions<EntityType> entities = null;

		if (args.has(DISTANCE))
			distance = args.get(DISTANCE).getAsInt();

		if (args.has(TYPE))
			entities = args.get(TYPE).getAsEnumOptions(EntityType.class);

		var rayTrace = Utils.getRayTraceResult(player, distance);

		if (rayTrace == null || rayTrace.getHitEntity() == null)
			return null;

		if (entities == null)
			return null;

		Entity entity = rayTrace.getHitEntity();

		if (entity == null) return null;
		return entities.contains(entity.getType()) ? entity : null;
	}

	@Override
	public String id() {
		return "lookingatentity";
	}
}
