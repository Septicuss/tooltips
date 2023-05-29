package fi.septicuss.tooltips.object.preset.condition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

public class StatementHolder {

	private List<Statement> statements;

	public StatementHolder() {
		this.statements = new ArrayList<>();
	}

	public StatementHolder(List<Statement> statements) {
		this.statements = statements;
	}

	public void addStatement(Statement statement) {
		if (statement == null)
			return;
		statements.add(statement);
	}
	
	public boolean evaluate(Player player) {

		for (var statement : statements) {
			if (statement == null || statement.getCondition() == null)
				continue;

			boolean conditionResult = statement.getCondition().check(player);

			if (statement.hasOutcome()) {
				boolean outcome = statement.getOutcome().asBoolean();
				
				if (outcome) {
					if (!conditionResult) return false;
				} else {
					if (conditionResult) return false;
					else continue;
				}
				
			}

			if (!conditionResult) return false;
		}

		return true;

	}

	public List<Statement> getStatements() {
		return Collections.unmodifiableList(statements);
	}

}
