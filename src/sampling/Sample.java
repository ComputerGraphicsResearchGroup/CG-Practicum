package sampling;

/**
 * Encapsulates all the data necessary for a camera to generate rays on the
 * image plane.
 * 
 * @author Niels Billen
 * @version 0.3
 */
public class Sample {
	/**
	 * x coordinate of the sample in image space [0, horizontal resolution).
	 */
	public final double x;

	/**
	 * y coordinate of the sample in image space [0, horizontal resolution).
	 */
	public final double y;

	/**
	 * Creates a new sample for a camera at the given position of the image.
	 * 
	 * @param x
	 *            x coordinate of the sample in image space (between 0
	 *            (inclusive) and the horizontal resolution of the image
	 *            (exclusive))
	 * @param y
	 *            y coordinate of the sample in image space (between 0
	 *            (inclusive) and the vertical resolution of the image
	 *            (exclusive))
	 */
	public Sample(double x, double y) {
		this.x = x;
		this.y = y;
	}
}
