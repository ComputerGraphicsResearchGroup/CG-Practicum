package camera;

import math.Ray;
import sampling.Sample;

/**
 * An interface which allows the generation of rays in a three-dimensional
 * space, corresponding to a sample in a two-dimensional image.
 * 
 * @author Niels Billen
 * @version 0.3
 */
public interface Camera {
	/**
	 * Generates a new ray from the given sample.
	 * 
	 * @param sample
	 *            sample to construct the ray from.
	 * @throws NullPointerException
	 *             when the given sample is null.
	 * @return a new ray from the given sample.
	 */
	public Ray generateRay(Sample sample) throws NullPointerException;
}
