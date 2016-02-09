package film;

import java.util.Locale;

/**
 * An implementation of a {@link RGBSpectrum} which only stores a red, green and
 * blue component.
 * 
 * @author Niels Billen
 * @version 0.2
 */
public class RGBSpectrum {
	/**
	 * A black {@link RGBSpectrum}.
	 */
	public static final RGBSpectrum BLACK = new RGBSpectrum(0);

	/**
	 * The red color component (in radiance).
	 */
	public final double red;

	/**
	 * The green color component (in radiance).
	 */
	public final double green;

	/**
	 * The blue color component (in radiance).
	 */
	public final double blue;

	/**
	 * Creates a {@link RGBSpectrum} where the red, green and blue color
	 * components are equal to the given value.
	 * 
	 * @param value
	 *            the value for the red, green and blue color components (in
	 *            radiance).
	 * @throws IllegalArgumentException
	 *             when the given value is infinite or not a number.
	 */
	public RGBSpectrum(double value) throws IllegalArgumentException {
		this(value, value, value);
	}

	/**
	 * Creates a new {@link RGBSpectrum} from the given color components.
	 * 
	 * @param red
	 *            the red color component (in radiance).
	 * @param green
	 *            the green color component (in radiance).
	 * @param blue
	 *            the blue color component (in radiance).
	 * @throws IllegalArgumentException
	 *             when one of the color components is infinite or not a number.
	 */
	public RGBSpectrum(double red, double green, double blue) throws IllegalArgumentException {
		if (!isValidColorComponent(red))
			throw new IllegalArgumentException("the red component is not a valid number! " + red);
		if (!isValidColorComponent(green))
			throw new IllegalArgumentException("the green component is not a valid number!" + green);
		if (!isValidColorComponent(blue))
			throw new IllegalArgumentException("the blue component is not a valid number!" + blue);
		this.red = red;
		this.blue = blue;
		this.green = green;
	}

	/**
	 * Creates a copy of the given {@link RGBSpectrum}.
	 * 
	 * @param spectrum
	 *            the {@link RGBSpectrum} to copy.
	 * @throws NullPointerException
	 *             when the given spectrum is null.
	 */
	public RGBSpectrum(RGBSpectrum spectrum) throws NullPointerException {
		if (spectrum == null)
			throw new NullPointerException("the given spectrum is null!");
		this.red = spectrum.red;
		this.green = spectrum.green;
		this.blue = spectrum.blue;
	}

	/**
	 * Return true when the given value is not infinite and not NaN.
	 * 
	 * @param value
	 *            the value to test.
	 * @return true when the given value is not infinite and not NaN.
	 */
	public boolean isValidColorComponent(double value) {
		return !Double.isInfinite(value) && !Double.isNaN(value);
	}

	/**
	 * Returns a new {@link RGBSpectrum} which is the sum of this and the given
	 * {@link RGBSpectrum}.
	 * 
	 * @param spectrum
	 *            the {@link RGBSpectrum} to add to this {@link RGBSpectrum}.
	 * @throws NullPointerException
	 *             when the given {@link RGBSpectrum} is null.
	 * @return a new {@link RGBSpectrum} which is the sum of this and the given
	 *         {@link RGBSpectrum}.
	 */
	public RGBSpectrum add(RGBSpectrum spectrum) throws NullPointerException {
		return new RGBSpectrum(this.red + spectrum.red, this.green + spectrum.green, this.blue + spectrum.blue);
	}

	/**
	 * Returns a new {@link RGBSpectrum} which is the sum of this and the given
	 * color components.
	 * 
	 * @param red
	 *            the red color component to add to this {@link RGBSpectrum}.
	 * @param green
	 *            the green color component to add to this {@link RGBSpectrum}.
	 * @param blue
	 *            the blue color component to add to this {@link RGBSpectrum}.
	 * @throws IllegalArgumentException
	 *             when one of the given color components is either infinite or
	 *             not NaN.
	 * @return a new {@link RGBSpectrum} which is the sum of this and the given
	 *         {@link RGBSpectrum}.
	 */
	public RGBSpectrum add(double red, double green, double blue) throws IllegalArgumentException {
		if (!isValidColorComponent(red))
			throw new IllegalArgumentException("the given red color component is not a valid number!");
		if (!isValidColorComponent(green))
			throw new IllegalArgumentException("the given green color component is not a valid number!");
		if (!isValidColorComponent(blue))
			throw new IllegalArgumentException("the given blue color component is not a valid number!");
		return new RGBSpectrum(this.red + red, this.green + green, this.blue + blue);
	}

	/**
	 * Returns a new {@link RGBSpectrum} which is the subtraction of this and
	 * the given {@link RGBSpectrum}.
	 * 
	 * @param spectrum
	 *            the {@link RGBSpectrum} to subtract to this
	 *            {@link RGBSpectrum}.
	 * @throws NullPointerException
	 *             when the given {@link RGBSpectrum} is null.
	 * @return a new {@link RGBSpectrum} which is the sum of this and the given
	 *         {@link RGBSpectrum}.
	 */
	public RGBSpectrum subtract(RGBSpectrum spectrum) throws NullPointerException {
		return new RGBSpectrum(this.red - spectrum.red, this.green - spectrum.green, this.blue - spectrum.blue);
	}

	/**
	 * Returns a new {@link RGBSpectrum} which is the subtraction of this and
	 * the given color components.
	 * 
	 * @param red
	 *            the red color component to subtract from this
	 *            {@link RGBSpectrum}.
	 * @param green
	 *            the green color component to subtract from to this
	 *            {@link RGBSpectrum}.
	 * @param blue
	 *            the blue color component to subtract from to this
	 *            {@link RGBSpectrum}.
	 * @throws IllegalArgumentException
	 *             when one of the given color components is either infinite or
	 *             not NaN.
	 * @return a new {@link RGBSpectrum} which is the sum of this and the given
	 *         {@link RGBSpectrum}.
	 */
	public RGBSpectrum subtract(double red, double green, double blue) throws IllegalArgumentException {
		if (!isValidColorComponent(red))
			throw new IllegalArgumentException("the given red color component is not a valid number!");
		if (!isValidColorComponent(green))
			throw new IllegalArgumentException("the given green color component is not a valid number!");
		if (!isValidColorComponent(blue))
			throw new IllegalArgumentException("the given blue color component is not a valid number!");
		return new RGBSpectrum(this.red - red, this.green - green, this.blue - blue);
	}

	/**
	 * Returns a new {@link RGBSpectrum} which is equal to this
	 * {@link RGBSpectrum} scaled by the given scalar.
	 * 
	 * @param scalar
	 *            the scalar to scale this {@link RGBSpectrum} with.
	 * @throws IllegalArgumentException
	 *             when the given scalar is either infinite or not NaN.
	 * @return a new {@link RGBSpectrum} which is equal to this
	 *         {@link RGBSpectrum} scaled by the given scalar.
	 */
	public RGBSpectrum scale(double scalar) throws IllegalArgumentException {
		if (!isValidColorComponent(scalar))
			throw new IllegalArgumentException("the given scalar is not a valid number!");
		return new RGBSpectrum(scalar * red, scalar * green, scalar * blue);
	}

	/**
	 * Returns a new {@link RGBSpectrum} which is equal to this
	 * {@link RGBSpectrum} divided by the given divisor.
	 * 
	 * @param divisor
	 *            the divisor to divide this {@link RGBSpectrum} with.
	 * @throws IllegalArgumentException
	 *             when the given divisor is either zero, infinite or not NaN.
	 * @return a new {@link RGBSpectrum} which is equal to this
	 *         {@link RGBSpectrum} scaled by the given scalar.
	 */
	public RGBSpectrum divide(double divisor) throws IllegalArgumentException {
		if (divisor == 0.0)
			throw new IllegalArgumentException("the divisor cannot be equal to zero!");
		if (!isValidColorComponent(divisor))
			throw new IllegalArgumentException("the given scalar is not a valid number!");
		return scale(1.0 / divisor);
	}

	/**
	 * Returns a new {@link RGBSpectrum} where the components of this and the
	 * given {@link RGBSpectrum} are multiplied together.
	 * 
	 * @param spectrum
	 *            the {@link RGBSpectrum} to multiply with.
	 * @return a new {@link RGBSpectrum} where the components of this and the
	 *         given {@link RGBSpectrum} are multiplied together.
	 */
	public RGBSpectrum multiply(RGBSpectrum spectrum) {
		return new RGBSpectrum(red * spectrum.red, green * spectrum.green, blue * spectrum.blue);
	}

	/**
	 * Returns a new {@link RGBSpectrum} where all the color components of this
	 * {@link RGBSpectrum} are raised to the given power.
	 * 
	 * @param power
	 *            the power to raise the color components of this
	 *            {@link RGBSpectrum} to.
	 * @return a new {@link RGBSpectrum} where all the color components of this
	 *         {@link RGBSpectrum} are raised to the given power.
	 */
	public RGBSpectrum pow(double power) {
		if (power == 1.0)
			return this;
		return new RGBSpectrum(Math.pow(red, power), Math.pow(green, power), Math.pow(blue, power));
	}

	/**
	 * Returns a new {@link RGBSpectrum} which has the color spectra of this
	 * {@link RGBSpectrum} clamped to the given lower and upper bound.
	 * 
	 * @param low
	 *            the lower bound to clamp this {@link RGBSpectrum} to.
	 * @param high
	 *            the upper bound to clamp this {@link RGBSpectrum} to.
	 * @return a new {@link RGBSpectrum} which has the color spectra of this
	 *         {@link RGBSpectrum} clamped to the given lower and upper bound.
	 */
	public RGBSpectrum clamp(double low, double high) {
		double r = Math.min(high, Math.max(low, red));
		double g = Math.min(high, Math.max(low, green));
		double b = Math.min(high, Math.max(low, blue));
		return new RGBSpectrum(r, g, b);
	}

	/**
	 * Returns whether this {@link RGBSpectrum} is black.
	 * 
	 * @return true when all the color components are zero.
	 */
	public boolean isBlack() {
		return red == 0 && green == 0 && blue == 0;
	}

	/**
	 * Returns this {@link RGBSpectrum} as a 32-bit color.
	 * 
	 * To convert the radiance to a 32-bit displayable color, the components of
	 * this {@link RGBSpectrum} are clamped between 0 and 255 and rounded to the
	 * nearest integer.
	 * 
	 * @return this {@link RGBSpectrum} as a 32-bit color.
	 */
	public int toRGB() {
		int r = Math.min(255, Math.max(0, (int) Math.round(red)));
		int g = Math.min(255, Math.max(0, (int) Math.round(green)));
		int b = Math.min(255, Math.max(0, (int) Math.round(blue)));

		return (255 << 24) + (r << 16) + (g << 8) + b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		long temp = Double.doubleToLongBits(blue);
		int result = 31 + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(green);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(red);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RGBSpectrum other = (RGBSpectrum) obj;
		return red == other.red && green == other.green && blue == other.blue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "[%s]: (%.6f, %.6f, %.6f)", getClass().getName(), red, green, blue);
	}
}
