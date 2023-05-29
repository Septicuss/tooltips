package fi.septicuss.tooltips.api.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fi.septicuss.tooltips.object.preset.condition.Condition;

/**
 * This event runs before all preset conditions are parsed. The plugin registers
 * its own native conditions at the lowest priority, so they can be easily
 * removed or overridden if needed.
 */
public class ConditionRegisterEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();
	private Map<String, Condition> registeredConditions;

	public ConditionRegisterEvent() {
		this.registeredConditions = new HashMap<>();
	}

	public ConditionRegisterEvent(Map<String, Condition> registeredConditions) {
		this.registeredConditions = new HashMap<>(registeredConditions);
	}

	public void register(String name, Condition condition) {
		registeredConditions.put(name, condition);
	}

	public void unregister(String name) {
		registeredConditions.remove(name);
	}

	public Map<String, Condition> getRegisteredConditions() {
		return Collections.unmodifiableMap(registeredConditions);
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

}
