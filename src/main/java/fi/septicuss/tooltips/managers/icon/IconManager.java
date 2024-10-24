package fi.septicuss.tooltips.managers.icon;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.utils.FileUtils;
import fi.septicuss.tooltips.utils.font.Widths;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IconManager {

    public static final Key ICON_FONT_PLACEHOLDER_KEY = Key.key("tooltips", "placeholder");

    private final Map<String, Icon> icons;

    public IconManager() {
        this.icons = new HashMap<>();
    }

    public void loadFrom(final File iconDirectory) {
        Tooltips.log("Loading icons...");

        int total = 0;
        int valid = 0;
        icons.clear();

        final List<File> iconFiles = FileUtils.getAllYamlFilesFromDirectory(iconDirectory);

        if (!iconFiles.isEmpty()) {
            for (File file : iconFiles) {
                final String relativeName = FileUtils.getRelativeFileName(iconDirectory, file);
                final String fileName = FileUtils.getExtensionlessFileName(file);

                var config = YamlConfiguration.loadConfiguration(file);
                var root = config.getRoot();

                for (String key : root.getKeys(false)) {
                    final String iconPath = relativeName + "/" + key;

                    var section = root.getConfigurationSection(key);
                    var icon = new Icon(iconPath, fileName, section);

                    total++;

                    if (!icon.isValid()) continue;

                    icons.put(iconPath, icon);
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
        return Set.copyOf(this.icons.values());
    }

    public Set<String> getIconPaths() {
        return Set.copyOf(this.icons.keySet());
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

    public Map<String, TagResolver> getIconPlaceholders() {
        Map<String, TagResolver> map = new HashMap<>();

        for (Icon icon : getAllIcons()) {

            final String iconPlaceholder = "icon:"+icon.getPath();

            final Component iconComponent = Component.text(String.valueOf(icon.getUnicode())).font(ICON_FONT_PLACEHOLDER_KEY);
            map.put(iconPlaceholder, Placeholder.component(iconPlaceholder, iconComponent));
        }

        return map;
    }

    private void loadWidths() {
        for (Icon icon : getAllIcons()) {
            try {
                final File texture = new File(Tooltips.getPackAssetsFolder(), icon.getTexturePath().getFullPath());
                final BufferedImage image = ImageIO.read(texture);
                Widths.addIcon(icon.getUnicode(), image, icon.getHeight());

                if (icon.getOverridingWidth() != 0) {
                    Widths.getIconSizedChar(icon.getUnicode()).setOverridingWidth(icon.getOverridingWidth());
                }
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
