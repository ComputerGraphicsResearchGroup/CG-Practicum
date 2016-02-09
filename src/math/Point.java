package math;

import java.util.Comparator;
import java.util.Locale;

/**
 * Point implementation in three dimensions.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class Point implements Cloneable, Comparable<Point> {
	/**
	 * x coordinate of this {@link Point}.
	 */
	public final double x;

	/**
	 * y coordinate of this {@link Point}.
	 */
	public final double y;

	/**
	 * z coordinate of this {@link Point}.
	 */
	public final double z;

	/**
	 * Creates a {@link Point} at the origin.
	 */
	public Point() {
		this(0, 0, 0);
	}

	/**
	 * Creates a new {@link Point} at the given position.
	 * 
	 * @param x
	 *            the x coordinate.
	 * @param y
	 *            the y coordinate.
	 * @param z
	 *            the z coordinate.
	 */
	public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Creates a new {@link Point} at the given position, scaled by the given
	 * homogeneous coordinate.
	 * 
	 * @param x
	 *            the x coordinate.
	 * @param y
	 *            the y coordinate.
	 * @param z
	 *            the z coordinate.
	 * @param w
	 *            the homogeneous coordinate.
	 */
	public Point(double x, double y, double z, double w) {
		double inv_w = 1.0 / w;
		this.x = x * inv_w;
		this.y = y * inv_w;
		this.z = z * inv_w;
	}

	/**
	 * Creates a copy of the given {@link Point}.
	 * 
	 * @param point
	 *            the {@link Point} to copy.
	 * @throws NullPointerException
	 *             when the given {@link Point} is null.
	 */
	public Point(Point point) throws NullPointerException {
		this(point.x, point.y, point.z);
	}

	/**
	 * Returns the coordinate of this {@link Point} along the given axis.
	 * 
	 * @param axis
	 *            axis to retrieve the coordinate of (0=x, 1=y, 2=z axis).
	 * @throws IllegalArgumentException
	 *             when the given axis is smaller than zero or larger than two.
	 * @return the coordinate of this {@link Point} along the given axis.
	 */
	public double get(int axis) throws IllegalArgumentException {
		switch (axis) {
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		default:
			throw new IllegalArgumentException(
					"the given axis is out of bounds!");
		}
	}

	/**
	 * Constructs a {@link Point} equal to this {@link Point} translated by the
	 * given coordinates.
	 * 
	 * @param x
	 *            the x coordinate.
	 * @param y
	 *            the y coordinate.
	 * @param z
	 *            the z coordinate.
	 * @return a new {@link Point} which is equal to this {@link Point}
	 *         translated by the given coordinates.
	 */
	public Point add(double x, double y, double z) {
		return new Point(this.x + x, this.y + y, this.z + z);
	}

	/**
	 * Constructs a {@link Point} equal to this {@link Point} translated by the
	 * given {@link Vector}.
	 * 
	 * @param vector
	 *            the {@link Vector} to add to this {@link Point}.
	 * @throws NullPointerException
	 *             when the given {@link Vector} is null.
	 * @return a new {@link Point} which is equal to this {@link Point}
	 *         translated by the given {@link Vector}.
	 */
	public Point add(Vector vector) throws NullPointerException {
		return add(vector.x, vector.y, vector.z);
	}

	/**
	 * Constructs the {@link Vector} spanned between this {@link Point} and the
	 * given coordinates.
	 * 
	 * @param x
	 *            the x coordinate.
	 * @param y
	 *            the y coordinate.
	 * @param z
	 *            the z coordinate.
	 * @return a new {@link Vector} equal to the {@link Vector} spanned between
	 *         this {@link Point} and the given coordinates.
	 */
	public Vector subtract(double x, double y, double z) {
		return new Vector(this.x - x, this.y - y, this.z - z);
	}

	/**
	 * Constructs the {@link Vector} spanned between this {@link Point} and the
	 * given {@link Point}.
	 * 
	 * @param point
	 *            the {@link Point} to span the {@link Vector} with.
	 * @throws NullPointerException
	 *             when the given {@link Point} is null.
	 * @return a new {@link Vector} equal to the {@link Vector} spanned between
	 *         this {@link Point} and the given {@link Point}.
	 */
	public Vector subtract(Point point) throws NullPointerException {
		return subtract(point.x, point.y, point.z);
	}

	/**
	 * Constructs the {@link Point} equal to this {@link Point} with its
	 * coordinates scaled by the given scalar.
	 * 
	 * @param scalar
	 *            the scalar to scale this {@link Point} with.
	 * @return a new {@link Point} equal to this {@link Point} with its
	 *         coordinates scaled by the given scalar.
	 */
	public Point scale(double scalar) {
		return new Point(x * scalar, y * scalar, z * scalar);
	}

	/**
	 * Constructs the {@link Point} equal to this {@link Point} with its
	 * coordinates divided by the given divisor.
	 * 
	 * @param divisor
	 *            the divisor to scale this {@link Point} with.
	 * @return a new {@link Point} equal to this {@link Point} with its
	 *         coordinates divided by the given divisor.
	 */
	public Point divide(double divisor) {
		return scale(1.0 / divisor);
	}

	/**
	 * Returns a comparator to sort {@link Point} objects along the given axis.
	 * 
	 * @param axis
	 *            the axis to sort the {@link Point}s along.
	 * @throws IllegalArgumentException
	 *             when the given axis is smaller than zero or larger than two.
	 * @return a comparator to sort objects of {@link Point} along the given
	 *         axis.
	 */
	public Comparator<Point> getComparator(final int axis)
			throws IllegalArgumentException {
		return new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				if (o1.get(axis) < o2.get(axis))
					return -1;
				else if (o1.get(axis) > o2.get(axis))
					return 1;
				else
					return 0;
			}
		};
	}

	/**
	 * Converts this {@link Point} to a three dimensional array.
	 * 
	 * @return this {@link Point} as a three dimensional array.
	 */
	public double[] toArray() {
		return new double[] { x, y, z };
	}

	/**
	 * Returns this {@link Point} as a four dimensional array with the fourth
	 * coordinate being the homogeneous coordinate.
	 * 
	 * @return this {@link Point} as a four dimensional array with the fourth
	 *         coordinate being the homogeneous coordinate.
	 */
	public double[] toHomogenousArray() {
		return new double[] { x, y, z, 1.0 };
	}

	/**
	 * Converts this {@link Point} to a {@link Vector}.
	 * 
	 * @return this {@link Point} as a {@link Vector}.
	 */
	public Vector toVector() {
		return new Vector(x, y, z);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Point(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Point point) {
		if (x < point.x)
			return -1;
		else if (x > point.x)
			return 1;
		else if (y < point.y)
			return -1;
		else if (y > point.y)
			return 1;
		else if (z < point.z)
			return -1;
		else if (z > point.z)
			return 1;
		else
			return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "[%s]:\n%g %g %g", getClass()
				.getName(), x, y, z);
	};
}
