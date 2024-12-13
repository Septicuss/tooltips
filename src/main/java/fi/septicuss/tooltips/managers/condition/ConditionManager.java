package fi.septicuss.tooltips.managers.condition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fi.septicuss.tooltips.api.TooltipsAPI;
import fi.septicuss.tooltips.managers.condition.parser.ArgumentParser;
import fi.septicuss.tooltips.managers.condition.parser.CompositeConditionParser;
import fi.septicuss.tooltips.managers.condition.parser.ConditionParser;
import fi.septicuss.tooltips.managers.condition.parser.StatementParser;

public class ConditionManager {

	private final Map<String, Condition> registeredConditions;

	private final ArgumentParser argumentParser;
	private final ConditionParser conditionParser;
	private final CompositeConditionParser compositeParser;
	private final StatementParser statementParser;

	public ConditionManager() {
		
		this.registeredConditions = new HashMap<>();

		this.argumentParser = new ArgumentParser();
		this.conditionParser = new ConditionParser(this, argumentParser);
		this.compositeParser = new CompositeConditionParser(conditionParser);
		this.statementParser = new StatementParser(compositeParser);

	}

	public void register(Condition condition) {
		registeredConditions.put(condition.id(), condition);
	}

	public void register(Condition... conditions) {
		for (var condition : conditions)
			this.register(condition);
	}

	public void unregister(String name) {
		registeredConditions.remove(name);
	}

	public Set<String> getConditions() {
		return this.registeredConditions.keySet();
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
