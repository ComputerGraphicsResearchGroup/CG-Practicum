package math;

import java.util.Comparator;
import java.util.Locale;

/**
 * A position in three-dimensional space.
 * 
 * @author Niels Billen
 * @version 0.3
 */
public class Point implements Comparable<Point> {
	/**
	 * x coordinate of this point.
	 */
	public final double x;

	/**
	 * y coordinate of this point.
	 */
	public final double y;

	/**
	 * z coordinate of this point.
	 */
	public final double z;

	/**
	 * Creates a point at the origin.
	 */
	public Point() {
		this(0, 0, 0);
	}

	/**
	 * Creates a new point at the given position.
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
	 * Creates a new point at the given position, scaled by the given
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
	 * Returns the coordinate of this point along the given axis.
	 * 
	 * @param axis
	 *            axis to retrieve the coordinate of (0=x, 1=y, 2=z axis).
	 * @throws IllegalArgumentException
	 *             when the given axis is smaller than zero or larger than two.
	 * @return the coordinate of this point along the given axis.
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
	 * Constructs a point equal to this point translated by the given
	 * coordinates.
	 * 
	 * @param x
	 *            the x coordinate.
	 * @param y
	 *            the y coordinate.
	 * @param z
	 *            the z coordinate.
	 * @return a new point which is equal to this point translated by the given
	 *         coordinates.
	 */
	public Point add(double x, double y, double z) {
		return new Point(this.x + x, this.y + y, this.z + z);
	}

	/**
	 * Constructs a point equal to this point translated by the given vector.
	 * 
	 * @param vector
	 *            the vector to add to this point.
	 * @throws NullPointerException
	 *             when the given vector is null.
	 * @return a new point which is equal to this point translated by the given
	 *         vector.
	 */
	public Point add(Vector vector) throws NullPointerException {
		return add(vector.x, vector.y, vector.z);
	}

	/**
	 * Constructs the vector spanned between this point and the given
	 * coordinates.
	 * 
	 * @param x
	 *            the x coordinate.
	 * @param y
	 *            the y coordinate.
	 * @param z
	 *            the z coordinate.
	 * @return a new vector equal to the vector spanned between this point and
	 *         the given coordinates.
	 */
	public Vector subtract(double x, double y, double z) {
		return new Vector(this.x - x, this.y - y, this.z - z);
	}

	/**
	 * Constructs the vector spanned between this point and the given point.
	 * 
	 * @param point
	 *            the point to span the vector with.
	 * @throws NullPointerException
	 *             when the given point is null.
	 * @return a new vector equal to the vector spanned between this point and
	 *         the given point.
	 */
	public Vector subtract(Point point) throws NullPointerException {
		return subtract(point.x, point.y, point.z);
	}

	/**
	 * Constructs the point equal to this point with its coordinates scaled by
	 * the given scalar.
	 * 
	 * @param scalar
	 *            the scalar to scale this point with.
	 * @return a new point equal to this point with its coordinates scaled by
	 *         the given scalar.
	 */
	public Point scale(double scalar) {
		return new Point(x * scalar, y * scalar, z * scalar);
	}

	/**
	 * Constructs the point equal to this point with its coordinates divided by
	 * the given divisor.
	 * 
	 * @param divisor
	 *            the divisor to scale this point with.
	 * @return a new point equal to this point with its coordinates divided by
	 *         the given divisor.
	 */
	public Point divide(double divisor) {
		return scale(1.0 / divisor);
	}

	/**
	 * Returns a comparator to sort point objects along the given axis.
	 * 
	 * @param axis
	 *            the axis to sort the points along.
	 * @throws IllegalArgumentException
	 *             when the given axis is smaller than zero or larger than two.
	 * @return a comparator to sort objects of point along the given axis.
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
	 * Converts this point to a vector.
	 * 
	 * @return this point as a vector.
	 */
	public Vector toVector() {
		return new Vector(x, y, z);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof Point))
			return false;
		Point p = (Point) obj;

		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(p.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(p.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(p.z))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = 1;
		long temp = Double.doubleToLongBits(x);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		return 31 * result + (int) (temp ^ (temp >>> 32));
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
