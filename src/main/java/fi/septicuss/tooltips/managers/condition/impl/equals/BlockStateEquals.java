package fi.septicuss.tooltips.managers.condition.impl.equals;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.validation.Validity;

public class BlockStateEquals implements Condition {

	private static final String[] DISTANCE = {"distance", "dist", "d"};
	private static final String[] KEY = {"key", "k", "id"};
	private static final String[] VALUE = {"value", "val", "v"};
	
	@Override
	public boolean check(Player player, Arguments args) {
		int distance = 3;
		
		if (args.has(DISTANCE))
			distance = args.get(DISTANCE).getAsInt();

		var rayTrace = Utils.getRayTraceResult(player, distance);

		if (rayTrace == null || rayTrace.getHitBlock() == null) {
			return false;
		}

		Block hit = rayTrace.getHitBlock();
		
		if (hit.getBlockData() == null) {
			return false;
		}
		
		String blockDataString = hit.getBlockData().getAsString();
		Map<String, String> stateMap = getBlockStateMap(blockDataString);
		
		String key = args.get(KEY).getAsString();
		String comparableValue = args.get(VALUE).getAsString();
		
		if (!stateMap.containsKey(key)) {
			return false;
		}
		
		String value = stateMap.get(key);
		return (value.equals(comparableValue));
	}

	@Override
	public Validity valid(Arguments args) {
		
		if (args.has(DISTANCE) && !args.isNumber(DISTANCE))
			return Validity.of(false, "Distance must be a number");

		if (!args.has(KEY))
			return Validity.of(false, "Key argument is required");
		
		if (!args.has(VALUE))
			return Validity.of(false, "Value argument is required");
		
		return Validity.TRUE;
	}

	@Override
	public String id() {
		return "blockstateequals";
	}

	private Map<String, String> getBlockStateMap(String blockDataString) {
		// blockDataString is of the following format:
		// minecraft:chest[waterlogged=false]
		
		Map<String, String> map = new HashMap<>();

		int firstSquareBracket = blockDataString.indexOf("[");

		if (firstSquareBracket == -1) {
			return map;
		}

		String states = blockDataString.substring(firstSquareBracket + 1, blockDataString.length() - 1);
		
		for (var state : states.split(",")) {
			var keyValue = state.split("=");
			var key = keyValue[0];
			var value = keyValue[1];
			map.put(key, value);
		}
		
		return map;
	}

}
