package fi.septicuss.tooltips.object.icon;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.bukkit.configuration.file.FileConfiguration;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.utils.font.Widths;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class IconManager {

	public static final String ICON_FONT_PLACEHOLDER = "tooltips:placeholder";

	private Map<String, Icon> icons;

	public IconManager() {
		this.icons = new HashMap<>();
	}

	public void loadFrom(Tooltips plugin, List<FileConfiguration> iconConfigs) {
		Tooltips.log("Loading icons...");

		int total = 0;
		int valid = 0;
		icons.clear();

		if (!iconConfigs.isEmpty()) {
			for (FileConfiguration config : iconConfigs) {
				var root = config.getRoot();

				for (String name : root.getKeys(false)) {
					var section = root.getConfigurationSection(name);
					var icon = new Icon(section);

					total++;

					if (!icon.isValid()) continue;

					icons.put(name, icon);
					valid++;
				}
			}
		}

		generateUnicodes();
		loadWidths();

		final int invalid = total - valid;
		final String loadedAmount = valid + ((invalid > 0) ? (" (out of " + total + ")") : (""));
		final String noun = (total == 1) ? "icon" : "icons";
		final String message = String.format("Loaded " + loadedAmount + " " + noun + ".");

		Tooltips.log(ChatColor.GREEN + message);
	}

	public Set<Icon> getAllIcons() {
		return Collections.unmodifiableSet(Set.copyOf(this.icons.values()));
	}

	public char getUnicodeFor(String name) {
		return icons.get(name).getUnicode();
	}

	public Icon getIcon(String name) {
		return icons.get(name);
	}

	public boolean hasIcon(String name) {
		return icons.containsKey(name);
	}

	public Map<String, TextComponent> getIconPlaceholders() {
		Map<String, TextComponent> map = new HashMap<>();

		for (Icon icon : getAllIcons()) {
			final String iconPlaceholder = "{" + icon.getName() + "}";

			final TextComponent iconComponent = new TextComponent(String.valueOf(icon.getUnicode()));
			iconComponent.setFont(IconManager.ICON_FONT_PLACEHOLDER);
			map.put(iconPlaceholder, iconComponent);
		}

		return map;
	}

	private void loadWidths() {
		for (Icon icon : getAllIcons()) {
			try {
				final File texture = new File(Tooltips.getPackAssetsFolder(), icon.getPath().getFullPath());
				final BufferedImage image = ImageIO.read(texture);
				Widths.add(icon.getUnicode(), image, icon.getHeight());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void generateUnicodes() {
		char current = '\uF000';
		int index = 0;

		for (Map.Entry<String, Icon> entry : icons.entrySet()) {
			char character = (char) (current + index);
			entry.getValue().setUnicode(character);
			index++;
		}
	}

}
