package camera;

import math.Ray;
import sampling.Sample;

/**
 * Interface which all {@link Camera} subclasses should implement.
 * 
 * @author Niels Billen
 * @version 0.2
 */
public interface Camera {
	/**
	 * Generates a new {@link Ray} from the given {@link Sample}.
	 * 
	 * @param sample
	 *            {@link Sample} to construct the {@link Ray} from.
	 * @throws NullPointerException
	 *             when the given {@link Sample} is null.
	 * @return a new {@link Ray} from the given {@link Sample}.
	 */
	public Ray generateRay(Sample sample) throws NullPointerException;
}
