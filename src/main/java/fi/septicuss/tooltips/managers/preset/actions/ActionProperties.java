package fi.septicuss.tooltips.managers.preset.actions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public class ActionProperties {

	private Map<TooltipAction, List<String>> actionCommands;
	
	private ActionProperties(ConfigurationSection actionsSection) {
		
		this.actionCommands = new HashMap<>();
		
		if (actionsSection == null) {
			return;
		}
		
		for (var action : TooltipAction.values()) {
			String readableAction = action.toString().toLowerCase().replace("_", "-");
			
			if (!actionsSection.contains(readableAction)) {
				continue;
			}
			

			this.actionCommands.put(action, actionsSection.getStringList(readableAction));
		}
		
	}
	
	public boolean hasAnyActions() {
		return !actionCommands.isEmpty();
	}
	
	public boolean hasAction(TooltipAction action) {
		return actionCommands.containsKey(action);
	}
	
	public List<String> getCommandsForAction(TooltipAction action) {
		return Collections.unmodifiableList(actionCommands.get(action));
	}
	
	public static ActionProperties of(ConfigurationSection actionsSection) {
		return new ActionProperties(actionsSection);
	}
	
	public enum TooltipAction {
		LEFT_CLICK,
		LEFT_CLICK_BLOCK,
		LEFT_CLICK_AIR,
		LEFT_CLICK_ENTITY,
		RIGHT_CLICK,
		RIGHT_CLICK_BLOCK,
		RIGHT_CLICK_AIR,
		RIGHT_CLICK_ENTITY,
		ON_SHOW,
		ON_STOP_SHOWING;
	}




}
