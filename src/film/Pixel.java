package film;

import java.util.Locale;

/**
 * Representation of a {@link Pixel} which contains a weighted sum of
 * {@link RGBSpectrum}s.
 * 
 * @author Niels Billen
 * @version 0.2
 */
public class Pixel {
	/**
	 * The sum of the {@link RGBSpectrum}s.
	 */
	private RGBSpectrum color = RGBSpectrum.BLACK;

	/**
	 * Total sum of the weights.
	 */
	private double weightSum = 0;

	/**
	 * Creates a new black {@link Pixel}.
	 */
	public Pixel() {
	}

	/**
	 * Adds the given color values to this {@link Pixel}, weighted by the given
	 * weight parameter.
	 * 
	 * @param red
	 *            the red color component (in radiance).
	 * @param green
	 *            the green color component (in radiance).
	 * @param blue
	 *            the blue color component (in radiance).
	 * @param weight
	 *            the weight for the color components.
	 * @throws IllegalArgumentException
	 *             when one of the given color components or the weight is
	 *             either infinite or not NaN.
	 */
	public void add(double red, double green, double blue, double weight)
			throws IllegalArgumentException {
		this.color = color.add(red * weight, green * weight, blue * weight);
		this.weightSum += weight;
	}

	/**
	 * Adds the given color values to this {@link Pixel}, weighted by the given
	 * weight parameter.
	 * 
	 * @param red
	 *            the red color component (in radiance).
	 * @param green
	 *            the green color component (in radiance).
	 * @param blue
	 *            the blue color component (in radiance).
	 * @throws IllegalArgumentException
	 *             when one of the given color components is either infinite or
	 *             not NaN.
	 */
	public void add(double red, double green, double blue)
			throws IllegalArgumentException {
		add(red, green, blue, 1.0);
	}

	/**
	 * Adds the given {@link RGBSpectrum} to this {@link Pixel}, weighted by the
	 * given weight parameter.
	 * 
	 * @param spectrum
	 *            the {@link RGBSpectrum} to add to this {@link Pixel}.
	 * @param weight
	 *            the weight for the color components.
	 * @throws NullPointerException
	 *             when the given {@link RGBSpectrum} is equal to zero.
	 * @throws IllegalArgumentException
	 *             when the weight is either infinite or not NaN.
	 */
	public void add(RGBSpectrum spectrum, double weight)
			throws NullPointerException, IllegalArgumentException {
		if (spectrum == null)
			throw new NullPointerException("the given spectrum is null!");
		add(spectrum.red, spectrum.green, spectrum.blue, weight);
	}

	/**
	 * Adds the given {@link RGBSpectrum} to this {@link Pixel}.
	 * 
	 * @param spectrum
	 *            the {@link RGBSpectrum} to add to this {@link Pixel}.
	 * @throws NullPointerException
	 *             when the given {@link RGBSpectrum} is equal to zero.
	 */
	public void add(RGBSpectrum spectrum) throws NullPointerException {
		if (spectrum == null)
			throw new NullPointerException("the given spectrum is null!");
		add(spectrum.red, spectrum.green, spectrum.blue);
	}

	/**
	 * Returns the {@link RGBSpectrum} of this {@link Pixel}.
	 * 
	 * @return the {@link RGBSpectrum} of this {@link Pixel}.
	 */
	public RGBSpectrum getSpectrum() {
		if (weightSum == 0)
			return RGBSpectrum.BLACK;
		return color.divide(weightSum);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		RGBSpectrum spectrum = getSpectrum();
		return String.format(Locale.ENGLISH, "[%s] (%.6f, %.6f, %.6f)",
				getClass().getName(), spectrum.red, spectrum.green,
				spectrum.blue);
	}
}
