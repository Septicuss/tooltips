package fi.septicuss.tooltips.utils.validation;

public interface Validatable {

	public boolean isValid();
	
	default Validity validity() {
		return Validity.TRUE;
	}

}
