package fi.septicuss.tooltips.managers.preset.actions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public class ActionProperties {

	private final Map<String, List<String>> actionCommands;
	
	private ActionProperties(ConfigurationSection actionsSection) {
		
		this.actionCommands = new HashMap<>();
		
		if (actionsSection == null) {
			return;
		}


		for (String key : actionsSection.getKeys(false)) {
			this.actionCommands.put(key, actionsSection.getStringList(key));
		}

	}
	
	public boolean hasAnyActions() {
		return !actionCommands.isEmpty();
	}
	
	public boolean hasAction(String action) {
		return actionCommands.containsKey(action);
	}
	
	public List<String> getCommandsForAction(String action) {
		return Collections.unmodifiableList(actionCommands.get(action));
	}
	
	public static ActionProperties of(ConfigurationSection actionsSection) {
		return new ActionProperties(actionsSection);
	}
	
}
