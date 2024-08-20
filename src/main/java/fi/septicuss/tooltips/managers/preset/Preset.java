package fi.septicuss.tooltips.managers.preset;

import com.google.common.collect.Lists;
import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.preset.actions.ActionProperties;
import fi.septicuss.tooltips.managers.preset.condition.ConditionManager;
import fi.septicuss.tooltips.managers.preset.condition.Statement;
import fi.septicuss.tooltips.managers.preset.condition.StatementHolder;
import fi.septicuss.tooltips.managers.preset.show.ShowProperties;
import fi.septicuss.tooltips.managers.theme.Theme;
import fi.septicuss.tooltips.managers.theme.ThemeManager;
import fi.septicuss.tooltips.utils.validation.Validatable;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.List;

public class Preset implements Validatable {

    private static final int DEFAULT_STAY = (5 * 20); // 5 seconds

    /**
     * PRESET
     */
    private String id;
    private List<String> text;

    /**
     * DISPLAY
     */
    private Theme theme;
    private String color = "white";
    private int horizontalShift = 0;

    /**
     * FADE
     */
    private int fadeIn;
    private int stay;
    private int fadeOut;

    /**
     * CONDITIONS
     */
    private StatementHolder statementHolder;

    /**
     * PROPERTIES
     */
    private ShowProperties showProperties;
    private ActionProperties actionProperties;

    /**
     * VALIDATION
     */
    private boolean valid = false;

    private ConfigurationSection section;

    public Preset(Tooltips plugin, String path, Preset parent, ConfigurationSection section) {

        final ThemeManager themeManager = plugin.getThemeManager();
        final ConditionManager conditionManager = plugin.getConditionManager();

        this.section = section;

        /* PARENT */
        if (parent != null) {
            this.text = parent.getText();
            this.theme = parent.getTheme();
            this.color = parent.getColor();
            this.horizontalShift = parent.getHorizontalShift();
            this.fadeIn = parent.getFadeIn();
            this.stay = parent.getStay();
            this.fadeOut = parent.getFadeOut();
            this.statementHolder = parent.getStatementHolder();
            this.showProperties = parent.getShowProperties();
            this.actionProperties = parent.getActionProperties();
        }

        /* TOOLTIP */
        id = path;

        /* TEXT */
        final var contentSection = section.getConfigurationSection("content");

        if (contentSection != null && contentSection.contains("text"))
            text = contentSection.getStringList("text");

        if (section.contains("text"))
            text = section.getStringList("text");

        if (text == null)
            text = Lists.newArrayList();

        /* DISPLAY */
        final var displaySection = section.getConfigurationSection("display");

        /* Theme */
        String themeName = null;

        if (displaySection != null && displaySection.contains("theme"))
            themeName = displaySection.getString("theme");

        if (section.contains("theme"))
            themeName = section.getString("theme");

        if (themeName == null && theme == null) {
            Tooltips.warn(String.format("Preset by the name of \"%s\" does not define a theme.", id));
            return;
        }

        if (!themeManager.doesThemeExist(themeName) && theme == null) {
            Tooltips.warn(String.format("Preset by the name of \"%s\" is using an unknown theme \"%s\"", id, themeName));
            return;
        }

        if (themeName != null)
            if (themeManager.doesThemeExist(themeName))
                theme = themeManager.getTheme(themeName);

        if (displaySection != null) {
            /* Other */
            color = displaySection.getString("color", color);
            horizontalShift = displaySection.getInt("horizontal-shift", (horizontalShift == 0 ? 0 : horizontalShift));
        }

        /* FADE */
        fadeIn = section.getInt("fade.fadein", (fadeIn == 0 ? 0 : fadeIn));
        stay = section.getInt("fade.stay", (stay == 0 ? DEFAULT_STAY : stay));
        fadeOut = section.getInt("fade.fadeout", (fadeOut == 0 ? 0 : fadeOut));


        /* CONDITIONS */
        List<String> conditionLines = null;

        if (section.contains("conditions.conditions"))
            conditionLines = section.getStringList("conditions.conditions");
        if (section.contains("conditions") && !section.isConfigurationSection("conditions"))
            conditionLines = section.getStringList("conditions");

        if (conditionLines != null) {
            statementHolder = new StatementHolder();

            for (var line : conditionLines) {
                Statement statement = conditionManager.getStatementParser().parse(id, line);
                statementHolder.addStatement(statement);
            }
        }

        // Show
        ConfigurationSection showSection = null;

        if (section.contains("conditions.show"))
            showSection = section.getConfigurationSection("conditions.show");
        if (section.contains("show"))
            showSection = section.getConfigurationSection("show");

        if (showSection != null || showProperties == null)
            showProperties = ShowProperties.of(showSection);

        // Actions
        ConfigurationSection actionsSection = null;

        if (section.contains("conditions.actions"))
            actionsSection = section.getConfigurationSection("conditions.actions");
        if (section.contains("actions"))
            actionsSection = section.getConfigurationSection("actions");

        if (actionsSection != null || actionProperties == null)
            actionProperties = ActionProperties.of(actionsSection);

        valid = true;
    }

    public Preset(Tooltips plugin, String path, ConfigurationSection section) {
        this(plugin, path, null, section);
    }

    public String getId() {
        return id;
    }

    public List<String> getText() {
        return Collections.unmodifiableList(text);
    }

    public Theme getTheme() {
        return theme;
    }

    public String getColor() {
        return color;
    }

    public int getHorizontalShift() {
        return horizontalShift;
    }

    public StatementHolder getStatementHolder() {
        return statementHolder;
    }

    public boolean hasStatementHolder() {
        if (statementHolder == null)
            return false;
        return !statementHolder.getStatements().isEmpty();
    }

    public int getFadeIn() {
        return fadeIn;
    }

    public int getStay() {
        return stay;
    }

    public int getFadeOut() {
        return fadeOut;
    }

    public ShowProperties getShowProperties() {
        return showProperties;
    }

    public ActionProperties getActionProperties() {
        return actionProperties;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    public ConfigurationSection getSection() {
        return section;
    }

}
