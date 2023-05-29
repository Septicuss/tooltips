package fi.septicuss.tooltips.pack;

public interface Generator {

	/**
	 * Method to generate required files by this generator in the generated
	 * directory.
	 */
	public void generate();

	/**
	 * Get the simple name of this generator. Used in error logging.
	 * 
	 * @return Name of the generator
	 */
	public String getName();

}
