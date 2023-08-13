package fi.septicuss.tooltips.pack;

import java.io.File;

public interface Generator {

	/**
	 * Method to generate required files by this generator in the generated
	 * directory.
	 * 
	 * @param directory Directory where to generate the files
	 */
	public void generate(final PackData packData, final File assetsDirectory, final File targetDirectory);

	/**
	 * Get the simple name of this generator. Used in error logging.
	 * 
	 * @return Name of the generator
	 */
	public String getName();

}
