package fi.septicuss.tooltips.object.validation;

public class Validity {

	public static final Validity TRUE = Validity.of(true);
	public static final Validity FALSE = Validity.of(false);
	
	private boolean result;
	private String reason;

	protected Validity(boolean result, String reason) {
		this.result = result;
		this.reason = reason;
	}

	public boolean isValid() {
		return result;
	}

	public String getReason() {
		return reason;
	}
	
	public boolean hasReason() {
		return reason != null;
	}

	public static Validity of(boolean result) {
		return new Validity(result, null);
	}

	public static Validity of(boolean result, String reason) {
		return new Validity(result, reason);
	}

}
