package film;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.Collection;

/**
 * A wrapper for a two-dimensional array of pixels.
 * 
 * @author Niels Billen
 * @version 0.3
 */
public class FrameBuffer {
	/**
	 * Two-dimensional array of pixels. The pixels are stored in row order. When
	 * iterating over the pixels, one should first iterate over the y
	 * coordinates, followed by the x coordinates for optimal performance.
	 */
	private final Pixel[][] frameBuffer;

	/**
	 * The horizontal resolution of this frame buffer.
	 */
	public final int xResolution;

	/**
	 * The vertical resolution of this frame buffer.
	 */
	public final int yResolution;

	/**
	 * Creates a new black frame buffer with the given dimension initialized
	 * with black pixels.
	 * 
	 * @param xResolution
	 *            the horizontal resolution.
	 * @param yResolution
	 *            the vertical resolution.
	 * @throws IllegalArgumentException
	 *             when either resolution is smaller than or equal to zero.
	 */
	public FrameBuffer(int xResolution, int yResolution)
			throws IllegalArgumentException {
		if (xResolution <= 0)
			throw new IllegalArgumentException(
					"the horizontal resolution must be larger than zero!");
		if (yResolution <= 0)
			throw new IllegalArgumentException(
					"the vertical resolution must be larger than zero!");
		this.xResolution = xResolution;
		this.yResolution = yResolution;
		this.frameBuffer = new Pixel[yResolution][xResolution];

		for (int y = 0; y < yResolution; ++y)
			for (int x = 0; x < xResolution; ++x)
				this.frameBuffer[y][x] = new Pixel();
	}

	/**
	 * Returns the pixel at the given coordinates.
	 * 
	 * Note that when iterating over the pixels in this buffer, one should first
	 * iterate over the y coordinates for optimal performance.<br>
	 * 
	 * <pre>
	 * {@code
	 * 		// good
	 * 		for(int y = 0; y < yResolution; ++y)
	 * 			for(int x = 0; x < yResolution; ++y)
	 * 				// do stuff
	 * 
	 * 		// bad
	 * 		for(int x = 0; x < xResolution; ++x)
	 * 			for(int y = 0; y < yResolution; ++y)
	 * 				// do stuff
	 * }
	 * </pre>
	 * 
	 * @param x
	 *            the x coordinate.
	 * @param y
	 *            the y coordinate.
	 * @throws ArrayIndexOutOfBoundsException
	 *             when the given x coordinate or y coordinate is smaller than
	 *             zero.
	 * @throws ArrayIndexOutOfBoundsException
	 *             when the given x coordinate is larger than or equal to the
	 *             horizontal resolution of the image.
	 * @throws ArrayIndexOutOfBoundsException
	 *             when the given y coordinate is larger than or equal to the
	 *             vertical resolution of the image.
	 * @return the {@link Pixel} at the given coordinates.
	 */
	public Pixel getPixel(int x, int y) throws ArrayIndexOutOfBoundsException {
		return frameBuffer[y][x];
	}

	/**
	 * Returns a collection of tiles which completely cover this frame buffer.
	 * 
	 * The size of the tiles will never exceed the given maximum width and
	 * height.
	 * 
	 * @param width
	 *            the maximum width of the tiles.
	 * @param height
	 *            the maximum height of the tiles.
	 * @return a collection of non-overlapping tiles covering the entire size of
	 *         this frame buffer.
	 */
	public Collection<Tile> subdivide(int width, int height) {
		return new Tile(0, 0, xResolution, yResolution)
				.subdivide(width, height);
	}

	/**
	 * Converts this frame buffer object to a buffered image.
	 * 
	 * The contents of the pixels in this frame buffer have radiance as unit,
	 * which can have arbitrarily large values. To display these radiance values
	 * on a display, they have to be <i>tone-mapped</i> within to values within
	 * the range [0, 255].
	 * 
	 * The radiance values are first clamped between zero and the inverse of the
	 * given sensitivity. The radiance values are then divided by the given
	 * sensitivity in order for all values to be within the range of [0,1].
	 * Gamma correction is applied to these values before being multiplied and
	 * rounded to the nearest integer in the range [0, 255].
	 * 
	 * @param sensitivity
	 *            the sensitivity value to apply to the radiance values stored
	 *            in this {@link FrameBuffer}. The radiance values are first
	 *            clamped to this inverse of the given sensitivity before being
	 *            divided by the sensitivity. Therefore, higher sensitivity
	 *            means a higher contribution of the lower radiance values, but
	 *            results in high radiance values being clamped.
	 * @param gamma
	 *            the gamma correction to apply.
	 * @throws IllegalArgumentException
	 *             when either the given exposure or gamma is smaller than or
	 *             equal to zero.
	 * @throws IllegalArgumentException
	 *             when either the given exposure or gamma is infinite or NaN.
	 * @return this {@link FrameBuffer} converted to a {@link BufferedImage}.
	 */
	public BufferedImage toBufferedImage(double sensitivity, double gamma)
			throws IllegalArgumentException {
		if (sensitivity <= 0)
			throw new IllegalArgumentException(
					"the sensitivity must be larger than zero!");
		if (Double.isInfinite(sensitivity))
			throw new IllegalArgumentException(
					"the sensitivity cannot be infinite!");
		if (Double.isNaN(sensitivity))
			throw new IllegalArgumentException("the sensitivity cannot be NaN!");

		if (gamma <= 0)
			throw new IllegalArgumentException(
					"the gamma must be larger than zero!");
		if (Double.isInfinite(gamma))
			throw new IllegalArgumentException("the gamma cannot be infinite!");
		if (Double.isNaN(gamma))
			throw new IllegalArgumentException("the gamma cannot be NaN!");

		double invSensitivity = 1.0 / sensitivity;
		double invGamma = 1.0 / gamma;

		BufferedImage image = new BufferedImage(xResolution, yResolution,
				BufferedImage.TYPE_INT_ARGB);

		WritableRaster raster = image.getRaster();
		DataBufferInt rasterBuffer = (DataBufferInt) raster.getDataBuffer();
		int[] rasterData = rasterBuffer.getData();

		for (int y = 0; y < yResolution; ++y) {
			int yOffset = (yResolution - y - 1) * xResolution;

			for (int x = 0; x < xResolution; ++x) {
				Pixel pixel = getPixel(x, y);
				RGBSpectrum spectrum = pixel.getSpectrum();

				int rgb = spectrum.clamp(0, invSensitivity).scale(sensitivity)
						.pow(invGamma).scale(255).toRGB();

				rasterData[x + yOffset] = rgb;
			}
		}

		return image;
	}
}
