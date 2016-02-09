package sampling;

import camera.Camera;

/**
 * Encapsulates all the data necessary for a {@link Camera} to generate
 * {@link Ray}s.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class Sample {
	/**
	 * x coordinate of the sample in image space.
	 */
	public final double x;

	/**
	 * y coordinate of the sample in image space.
	 */
	public final double y;

	/**
	 * Creates a new {@link Sample} for a {@link Camera} at the given position
	 * of the image.
	 * 
	 * @param x
	 *            x coordinate of the sample in image space.
	 * @param y
	 *            y coordinate of the sample in image space.
	 */
	public Sample(double x, double y) {
		this.x = x;
		this.y = y;
	}
}
