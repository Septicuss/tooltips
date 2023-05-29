package fi.septicuss.tooltips.object.preset.condition.type;

public enum Operation {

	GREATER_THAN(">"), 
	GREATER_THAN_OR_EQUAL(">=", "=>"), 
	LESS_THAN("<"), 
	LESS_THAN_OR_EQUAL("<=", "=<"),
	EQUAL("=", "==");

	private String[] aliases;

	private Operation(String... aliases) {
		this.aliases = aliases;
	}

	public String[] getAliases() {
		return aliases;
	}

	public static Operation parseOperation(String arg0) {
		for (var operation : values())
			for (var alias : operation.getAliases())
				if (arg0.equalsIgnoreCase(alias))
					return operation;
		return null;
	}

}
