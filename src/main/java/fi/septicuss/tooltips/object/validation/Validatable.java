package fi.septicuss.tooltips.object.validation;

public interface Validatable {

	public boolean isValid();
	
	default Validity validity() {
		return Validity.TRUE;
	}

}
