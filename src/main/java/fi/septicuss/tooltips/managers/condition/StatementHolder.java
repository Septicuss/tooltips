package fi.septicuss.tooltips.managers.condition;

import fi.septicuss.tooltips.managers.condition.composite.CompositeCondition;
import fi.septicuss.tooltips.managers.condition.parser.ParsedCondition;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatementHolder {

	private List<Statement> statements;

	public StatementHolder() {
		this.statements = new ArrayList<>();
	}

	public StatementHolder(List<Statement> statements) {
		this.statements = statements;
	}

	public void addStatement(Statement statement) {
		statements.add(statement);
	}

	public boolean evaluate(Player player, Context context) {

		for (var statement : statements) {
			if (statement == null || statement.getCompositeCondition() == null)
				continue;

			boolean conditionResult = statement.getCompositeCondition().check(player, context);

			if (statement.hasOutcome()) {
				final var outcome = statement.getOutcome();

				if (outcome == Statement.Outcome.SKIP) {
					this.writeSkippedContext(player, context, statement);
					continue;
				}

				final boolean required = statement.getOutcome().asBoolean();

				if (required) {
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

	private void writeSkippedContext(Player player, Context context, Statement statement) {

		final CompositeCondition compositeCondition = statement.getCompositeCondition();
		if (compositeCondition == null) return;

		writeSkippedContext(player, context, compositeCondition);

	}

	private void writeSkippedContext(Player player, Context context, CompositeCondition compositeCondition) {

		if (compositeCondition.hasLeft())
			writeSkippedContext(player, context, compositeCondition.getLeft());

		if (compositeCondition.hasRight())
			writeSkippedContext(player, context, compositeCondition.getRight());

		if (compositeCondition.hasCondition()) {
			final ParsedCondition parsedCondition = compositeCondition.getParsedCondition();
			if (parsedCondition == null) return;

			final Condition condition = parsedCondition.getCondition();
			if (condition == null) return;

			condition.writeContext(player, parsedCondition.getArgs(), context);
		}

	}


}
