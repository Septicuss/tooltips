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

	// Preset
	private String id;

	// Content
	private List<String> text;

	// Display
	private Theme theme;
	private String color;
	private int horizontalShift;
	private boolean interruptable;

	// Fade
	private int fadeIn;
	private int stay;
	private int fadeOut;

	// Conditions
	private int checkFrequency;
	private StatementHolder statementHolder;

	// Show
	private ShowProperties showProperties;
	
	// Actions
	private ActionProperties actionProperties;

	// Validatable
	private boolean valid = false;

	public Preset(Tooltips plugin, ConfigurationSection section) {
		var conditionManager = plugin.getConditionManager();
		var themeManager = plugin.getThemeManager();

		// Tooltip
		id = section.getName();

		// Content
		boolean hasText = section.contains("content.text");
		text = (hasText ? section.getStringList("content.text") : Lists.newArrayList());

		// Display
		String themeId = section.getString("display.theme");
		boolean validTheme = themeManager.doesThemeExist(themeId);
		theme = (validTheme ? themeManager.getTheme(themeId) : themeManager.getTheme("default"));
		color = section.getString("display.color", "white");
		horizontalShift = section.getInt("display.horizontal-shift", 0);
		interruptable = section.getBoolean("display.interruptable", false);

		// Fade
		fadeIn = section.getInt("fade.fadein", 0);
		stay = section.getInt("fade.stay", 5 * 20);
		fadeOut = section.getInt("fade.fadeout", 0);

		// Conditions
		var conditionsSection = section.getConfigurationSection("conditions");
		if (conditionsSection != null) {
			var conditionLines = conditionsSection.getStringList("conditions");

			if (conditionLines != null) {
				statementHolder = new StatementHolder();

				for (var line : conditionLines) {
					Statement statement = conditionManager.getStatementParser().parse(id, line);
					statementHolder.addStatement(statement);
				}
			}

			checkFrequency = conditionsSection.getInt("check-frequency", 3);
		}

		// Show
		var showSection = section.getConfigurationSection("conditions.show");
		this.showProperties = ShowProperties.of(showSection);

		// Actions
		var actionsSection = section.getConfigurationSection("conditions.actions");
		this.actionProperties = ActionProperties.of(actionsSection);
		
		// Validate
		
		if (!validTheme) {
			Tooltips.warn(String.format(
					"Preset by the name of \"%s\" is using an unknown theme \"%s\"", id,
					themeId));
			return;
		}

		valid = true;
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

}
