package film;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implementation of a {@link FrameBuffer} which is a two dimensional grid of
 * {@link Pixel}s.
 * 
 * @author Niels Billen
 * @version 0.2
 */
public class FrameBuffer {
	/**
	 * Two-dimensional array of {@link Pixel}s. The {@link Pixel}s are stored in
	 * row order. When iterating over the {@link Pixel}s, one should first
	 * iterate over the y coordinates, followed by the x coordinates for optimal
	 * performance.
	 */
	private final Pixel[][] frameBuffer;

	/**
	 * The horizontal resolution of this {@link FrameBuffer}.
	 */
	public final int xResolution;

	/**
	 * The vertical resolution of this {@link FrameBuffer}.
	 */
	public final int yResolution;

	/**
	 * Creates a new black {@link FrameBuffer} with the given dimension.
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
	 * Returns the {@link Pixel} at the given coordinates.
	 * 
	 * @param x
	 *            the x coordinate.
	 * @param y
	 *            the y coordinate.
	 * @return the {@link Pixel} at the given coordinates.
	 */
	public Pixel getPixel(int x, int y) throws ArrayIndexOutOfBoundsException {
		return frameBuffer[y][x];
	}

	/**
	 * Subdivides this {@link FrameBuffer} in {@link Tile}s with the given width
	 * and height.
	 * 
	 * @param width
	 *            the preferred width of the {@link Tile}s.
	 * @param height
	 *            the preferred height of the {@link Tile}s.
	 * @return a {@link Collection} of non-overlapping {@link Tile}s covering
	 *         the entire size of this {@link FrameBuffer}.
	 */
	public Collection<Tile> subdivide(int width, int height) {
		if (width <= 0)
			throw new IllegalArgumentException(
					"the width of a tile must be larger than zero!");
		if (height <= 0)
			throw new IllegalArgumentException(
					"the height of a tile must be larger than zero!");

		List<Tile> result = new ArrayList<Tile>();
		for (int y = 0; y < yResolution; y += height) {
			for (int x = 0; x < xResolution; x += width) {
				int xEnd = Math.min(xResolution, x + width);
				int yEnd = Math.min(yResolution, y + height);
				result.add(new Tile(this, x, y, xEnd, yEnd));
			}
		}
		return result;
	}

	/**
	 * Converts this {@link FrameBuffer} object to a {@link BufferedImage}.
	 * 
	 * The contents of the {@link Pixel}s in this {@link FrameBuffer} have
	 * radiance units which can have arbitrarily large values. To display these
	 * radiance values on a display, they have to be <i>tone-mapped</i> within
	 * to values within the range [0, 255].
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
			for (int x = 0; x < xResolution; ++x) {
				Pixel pixel = getPixel(x, y);
				RGBSpectrum spectrum = pixel.getSpectrum();

				int rgb = spectrum.clamp(0, invSensitivity).scale(sensitivity)
						.pow(invGamma).scale(255).toRGB();

				rasterData[x + (yResolution - y - 1) * xResolution] = rgb;
			}
		}

		return image;
	}
}
