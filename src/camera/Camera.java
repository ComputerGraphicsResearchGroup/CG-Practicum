package camera;

import math.Ray;
import sampling.Sample;

/**
 * Interface which all {@link Camera} subclasses should implement.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public interface Camera {
	/**
	 * Generates a new {@link Ray} from the given {@link Sample}.
	 * 
	 * @param sample
	 *            sample to construct the {@link Ray} from.
	 * @throws NullPointerException
	 *             when the given {@link Sample} is null.
	 * @return a new {@link Ray} from the given {@link Sample}.
	 */
	public Ray generateRay(Sample sample) throws NullPointerException;
}
