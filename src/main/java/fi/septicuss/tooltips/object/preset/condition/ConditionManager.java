package fi.septicuss.tooltips.object.preset.condition;

import java.util.HashMap;
import java.util.Map;

import fi.septicuss.tooltips.api.TooltipsAPI;
import fi.septicuss.tooltips.object.preset.condition.parser.ArgumentParser;
import fi.septicuss.tooltips.object.preset.condition.parser.CompositeConditionParser;
import fi.septicuss.tooltips.object.preset.condition.parser.ConditionParser;
import fi.septicuss.tooltips.object.preset.condition.parser.StatementParser;

public class ConditionManager {

	private Map<String, Condition> registeredConditions;

	private ArgumentParser argumentParser;
	private ConditionParser conditionParser;
	private CompositeConditionParser compositeParser;
	private StatementParser statementParser;

	public ConditionManager() {
		
		this.registeredConditions = new HashMap<>();
		this.registeredConditions.putAll(TooltipsAPI.getRegisteredConditions());
		
		this.argumentParser = new ArgumentParser();
		this.conditionParser = new ConditionParser(this, argumentParser);
		this.compositeParser = new CompositeConditionParser(conditionParser);
		this.statementParser = new StatementParser(compositeParser);

	}

	public void register(String name, Condition condition) {
		registeredConditions.put(name, condition);
	}

	public boolean exists(String name) {
		return registeredConditions.containsKey(name);
	}

	public Condition get(String name) {
		return registeredConditions.get(name);
	}

	public ArgumentParser getArgumentParser() {
		return argumentParser;
	}

	public ConditionParser getConditionParser() {
		return conditionParser;
	}

	public CompositeConditionParser getCompositeParser() {
		return compositeParser;
	}

	public StatementParser getStatementParser() {
		return statementParser;
	}

}
