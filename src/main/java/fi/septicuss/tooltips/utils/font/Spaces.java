package fi.septicuss.tooltips.utils.font;

import com.google.common.collect.Maps;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class Spaces {

	public static final Key OFFSET_FONT_KEY = Key.key("tooltips", "space");
	private static final Map<Integer, Character> OFFSET_MAP;

	// Inside each theme line
	public static final char NEGATIVE_ONE = '\uE000';
	public static final char POSITIVE_ONE = '\uE001';

	static {
		OFFSET_MAP = Maps.newHashMap();

		OFFSET_MAP.put(-1, '\uE001');
		OFFSET_MAP.put(-2, '\uE002');
		OFFSET_MAP.put(-4, '\uE003');
		OFFSET_MAP.put(-8, '\uE004');
		OFFSET_MAP.put(-16, '\uE005');
		OFFSET_MAP.put(-32, '\uE006');
		OFFSET_MAP.put(-64, '\uE007');
		OFFSET_MAP.put(-128, '\uE008');

		OFFSET_MAP.put(1, '\uE009');
		OFFSET_MAP.put(2, '\uE010');
		OFFSET_MAP.put(4, '\uE011');
		OFFSET_MAP.put(8, '\uE012');
		OFFSET_MAP.put(16, '\uE013');
		OFFSET_MAP.put(32, '\uE014');
		OFFSET_MAP.put(64, '\uE015');
		OFFSET_MAP.put(128, '\uE016');
	}

	public static Component getOffset(int pixels) {
		return Component.text(getOffsetText(pixels)).font(OFFSET_FONT_KEY);
	}

	public static String getOffsetText(int pixels) {
		final StringBuilder builder = new StringBuilder();
		if (pixels == 0)
			return builder.toString(); // Return empty string

		final boolean negative = Integer.signum(pixels) == -1;
		pixels = Math.abs(pixels); // Get negative pixels absolute value

		while (pixels > 0) {
			int highestBit = Integer.highestOneBit(pixels);
			if (highestBit > 128)
				highestBit = 128; // Max is 128
			builder.append(OFFSET_MAP.get(negative ? -highestBit : highestBit));

			pixels -= highestBit;
		}

		return builder.toString();
	}

	public static Set<Map.Entry<Integer, Character>> getOffsetMapEntries() {
		return Collections.unmodifiableSet(OFFSET_MAP.entrySet());
	}

}
