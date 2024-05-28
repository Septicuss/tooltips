package fi.septicuss.tooltips.object.preset;

import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import com.google.common.collect.Lists;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.object.preset.actions.ActionProperties;
import fi.septicuss.tooltips.object.preset.condition.Statement;
import fi.septicuss.tooltips.object.preset.condition.StatementHolder;
import fi.septicuss.tooltips.object.preset.show.ShowProperties;
import fi.septicuss.tooltips.object.theme.Theme;
import fi.septicuss.tooltips.object.validation.Validatable;

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
	private String color;
	private int horizontalShift;
	private boolean interruptable = false;

	/**
	 * FADE
	 */
	private int fadeIn;
	private int stay;
	private int fadeOut;

	/**
	 * CONDITIONS
	 */
	private int checkFrequency;
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

	public Preset(Tooltips plugin, Preset parent, ConfigurationSection section) {

		this.section = section;

		/* PARENT */
		if (parent != null) {
			this.text = parent.getText();
			this.theme = parent.getTheme();
			this.color = parent.getColor();
			this.horizontalShift = parent.getHorizontalShift();
			this.interruptable = parent.interruptable;
			this.fadeIn = parent.getFadeIn();
			this.stay = parent.getStay();
			this.fadeOut = parent.getFadeOut();
			this.checkFrequency = parent.getConditionCheckFrequency();
			this.statementHolder = parent.getStatementHolder();
			this.showProperties = parent.getShowProperties();
			this.actionProperties = parent.getActionProperties();
		}

		/* TOOLTIP */
		id = section.getName();

		/* TEXT */
		final var contentSection = section.getConfigurationSection("content");

		if (contentSection != null && contentSection.contains("text"))
			text = contentSection.getStringList("text");
		
		if (text == null)
			text = Lists.newArrayList();
		
		/* DISPLAY */
		final var displaySection = section.getConfigurationSection("display");
		
		if (displaySection == null && theme == null) {
			Tooltips.warn(String.format("Preset by the name of \"%s\" does not define a theme.", id));
			return;
		}

		if (displaySection != null) {
			/* Theme */
			String themeName = displaySection.getString("theme", null);
			var themeManager = plugin.getThemeManager();

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
			
			/* Other */
			color = displaySection.getString("color", (color == null ? "white" : color));
			horizontalShift = displaySection.getInt("horizontal-shift", (horizontalShift == 0 ? 0 : horizontalShift));
			interruptable = displaySection.getBoolean("display.interruptable", interruptable);
		}
		
		/* FADE */
		fadeIn = section.getInt("fade.fadein", (fadeIn == 0 ? 0 : fadeIn));
		stay = section.getInt("fade.stay", (stay == 0 ? DEFAULT_STAY : stay));
		fadeOut = section.getInt("fade.fadeout", (fadeOut == 0 ? 0 : fadeOut));

		
		/* CONDITIONS */
		var conditionsSection = section.getConfigurationSection("conditions");
		if (conditionsSection != null) {
			var conditionLines = conditionsSection.getStringList("conditions");

			if (conditionLines != null) {
				statementHolder = new StatementHolder();

				for (var line : conditionLines) {
					Statement statement = plugin.getConditionManager().getStatementParser().parse(id, line);
					statementHolder.addStatement(statement);
				}
			}

			checkFrequency = conditionsSection.getInt("check-frequency", 3);
		}

		// Show
		var showSection = section.getConfigurationSection("conditions.show");
		if (showSection != null || showProperties == null)
			showProperties = ShowProperties.of(showSection);
		
		// Actions
		var actionsSection = section.getConfigurationSection("conditions.actions");
		if (actionsSection != null || actionProperties == null)
			actionProperties = ActionProperties.of(actionsSection);

		valid = true;
	}

	public Preset(Tooltips plugin, ConfigurationSection section) {
		this(plugin, null, section);
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

	public boolean isInterruptable() {
		return interruptable;
	}

	public int getConditionCheckFrequency() {
		return checkFrequency;
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

	public ConfigurationSection getSection() { return section; }

}
