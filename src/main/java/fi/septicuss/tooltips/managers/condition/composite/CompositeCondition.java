package fi.septicuss.tooltips.managers.condition.composite;

import fi.septicuss.tooltips.managers.condition.Context;
import fi.septicuss.tooltips.managers.condition.parser.ParsedCondition;
import org.bukkit.entity.Player;

public class CompositeCondition {

	private static final boolean DEFAULT = false;

	// Leaf
	private ParsedCondition condition;

	private CompositeCondition left;
	private CompositeCondition right;
	private Operator operator;

	public CompositeCondition(ParsedCondition condition) {
		this.condition = condition;
	}

	public ParsedCondition getCondition() {
		return condition;
	}

	public boolean hasCondition() {
		return condition != null;
	}

	public CompositeCondition getLeft() {
		return left;
	}

	public void setLeft(CompositeCondition left) {
		this.left = left;
	}

	public boolean hasLeft() {
		return this.left != null;
	}

	public CompositeCondition getRight() {
		return right;
	}

	public void setRight(CompositeCondition right) {
		this.right = right;
	}

	public boolean hasRight() {
		return this.right != null;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public boolean check(Player player, Context context) {
		if (context == null)
			context = new Context();

		if (!hasLeft() && !hasRight())
			if (hasCondition()) {
				return condition.check(player, context);
			} else
				return DEFAULT;

		if (hasLeft() && !hasRight())
			return getLeft().check(player, context);

		if (!hasLeft() && hasRight())
			return getRight().check(player, context);

		switch (operator) {
		case AND:
			return (left.check(player, context) && right.check(player, context));
		case OR:
			return (left.check(player, context) || right.check(player, context));
		default:
			return DEFAULT;
		}

	}

	public boolean check(Player player) {
		return this.check(player, new Context());
	}

	@Override
	public String toString() {
		return "CompositeCondition [condition=" + (condition == null ? condition : condition.toString()) + ", left="
				+ (left == null ? left : left.toString()) + ", right=" + (right == null ? right : right.toString())
				+ ", operator=" + (operator == null ? operator : operator.toString()) + "]";
	}

	public enum Operator {

		AND("&&"), OR("||");

		private String stringOperator;

		private Operator(String stringOperator) {
			this.stringOperator = stringOperator;
		}

		public String getStringOperator() {
			return stringOperator;
		}

		public static Operator parseOperator(String string) {
			for (var operator : values())
				if (operator.toString().equalsIgnoreCase(string) || operator.getStringOperator().equals(string))
					return operator;
			return null;
		}

		public static boolean isOperator(String string) {
			for (var operator : values())
				if (operator.toString().equalsIgnoreCase(string) || operator.getStringOperator().equals(string))
					return true;
			return false;
		}

	}

}
