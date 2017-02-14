package math;

import java.util.Comparator;
import java.util.Locale;

/**
 * Vector implementation in three dimensions.
 * 
 * @author Niels Billen
 * @version 0.3
 */
public class Vector implements Comparable<Vector> {
	/**
	 * x coordinate of this vector.
	 */
	public final double x;

	/**
	 * y coordinate of this vector.
	 */
	public final double y;

	/**
	 * z coordinate of this vector.
	 */
	public final double z;

	/**
	 * Creates a vector at the origin.
	 */
	public Vector() {
		this(0, 0, 0);
	}

	/**
	 * Creates a new vector at the given position.
	 * 
	 * @param x
	 *            the x coordinate.
	 * @param y
	 *            the y coordinate.
	 * @param z
	 *            the z coordinate.
	 */
	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Returns the coordinate of this vector along the given axis.
	 * 
	 * @param axis
	 *            axis to retrieve the coordinate of (0=x, 1=y, 2=z axis).
	 * @throws IllegalArgumentException
	 *             when the given axis is smaller than zero or larger than two.
	 * @return the coordinate of this vector along the given axis.
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
	 * Constructs a vector equal to this vector translated by the given
	 * coordinates.
	 * 
	 * @param x
	 *            the x coordinate.
	 * @param y
	 *            the y coordinate.
	 * @param z
	 *            the z coordinate.
	 * @return a new vector which is equal to this vector translated by the
	 *         given coordinates.
	 */
	public Vector add(double x, double y, double z) {
		return new Vector(this.x + x, this.y + y, this.z + z);
	}

	/**
	 * Constructs a vector equal to this vector translated by the given vector.
	 * 
	 * @param vector
	 *            the vector to add to this vector.
	 * @throws NullPointerException
	 *             when the given vector is null.
	 * @return a new vector which is equal to this vector translated by the
	 *         given vector .
	 */
	public Vector add(Vector vector) throws NullPointerException {
		return add(vector.x, vector.y, vector.z);
	}

	/**
	 * Constructs the vector spanned between this vector and the given
	 * coordinates.
	 * 
	 * @param x
	 *            the x coordinate.
	 * @param y
	 *            the y coordinate.
	 * @param z
	 *            the z coordinate.
	 * @return a new vector equal to the vector spanned between this vector and
	 *         the given coordinates.
	 */
	public Vector subtract(double x, double y, double z) {
		return new Vector(this.x - x, this.y - y, this.z - z);
	}

	/**
	 * Constructs the vector spanned between this vector and the given vector.
	 * 
	 * @param vector
	 *            the vector to subtract from this vector.
	 * @throws NullPointerException
	 *             when the given vector is null.
	 * @return a new vector equal to the vector spanned between this vector and
	 *         the given vector.
	 */
	public Vector subtract(Vector vector) throws NullPointerException {
		return subtract(vector.x, vector.y, vector.z);
	}

	/**
	 * Constructs the vector equal to this vector with its coordinates scaled by
	 * the given scalar.
	 * 
	 * @param scalar
	 *            the scalar to scale this vector with.
	 * @return a new vector equal to this vector with its coordinates scaled by
	 *         the given scalar.
	 */
	public Vector scale(double scalar) {
		return new Vector(x * scalar, y * scalar, z * scalar);
	}

	/**
	 * Constructs the vector equal to this vector with its coordinates divided
	 * by the given divisor.
	 * 
	 * @param divisor
	 *            the divisor to scale this vector with.
	 * @return a new vector equal to this vector with its coordinates divided by
	 *         the given divisor.
	 */
	public Vector divide(double divisor) {
		return scale(1.0 / divisor);
	}

	/**
	 * Returns a comparator to sort vector objects along the given axis.
	 * 
	 * @param axis
	 *            the axis to sort the vectors along.
	 * @throws IllegalArgumentException
	 *             when the given axis is smaller than zero or larger than two.
	 * @return a comparator to sort objects of vector along the given axis.
	 */
	public Comparator<Vector> getComparator(final int axis)
			throws IllegalArgumentException {
		return new Comparator<Vector>() {
			@Override
			public int compare(Vector o1, Vector o2) {
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
	 * Returns the dot product of this vector with the given vector.
	 * 
	 * @param vector
	 *            the vector to calculate the dot product with.
	 * @throws NullPointerException
	 *             when the given vector is null.
	 * @return the dot product of this vector with the given vector.
	 */
	public double dot(Vector vector) throws NullPointerException {
		return x * vector.x + y * vector.y + z * vector.z;
	}

	/**
	 * Returns the cross product of this vector with the given vector.
	 * 
	 * @param vector
	 *            the vector to calculate the cross product of.
	 * @throws NullPointerException
	 *             when the given vector is null.
	 * @return the cross product of this vector with the given vector.
	 */
	public Vector cross(Vector vector) throws NullPointerException {
		double xx = y * vector.z - z * vector.y;
		double yy = z * vector.x - x * vector.z;
		double zz = x * vector.y - y * vector.x;

		return new Vector(xx, yy, zz);
	}

	/**
	 * Returns the squared length of this vector.
	 * 
	 * @return the squared length of this vector.
	 */
	public double lengthSquared() {
		return x * x + y * y + z * z;
	}

	/**
	 * Returns the length of this vector.
	 * 
	 * @return the length of this vector.
	 */
	public double length() {
		return Math.sqrt(lengthSquared());
	}

	/**
	 * Converts this vector to a point.
	 * 
	 * @return this vector as a point.
	 */
	public Point toPoint() {
		return new Point(x, y, z);
	}

	/**
	 * Returns a normalized version of this vector.
	 * 
	 * @return a normalized version of this vector.
	 */
	public Vector normalize() {
		return divide(length());
	}

	/**
	 * Returns the inverse of this vector.
	 * 
	 * @return the inverse of this vector.
	 */
	public Vector invert() {
		return new Vector(-x, -y, -z);
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
		if (!(obj instanceof Vector))
			return false;
		Vector v = (Vector) obj;

		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(v.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(v.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(v.z))
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
	public int compareTo(Vector o) {
		if (x < o.x)
			return -1;
		else if (x > o.x)
			return 1;
		else if (y < o.y)
			return -1;
		else if (y > o.y)
			return 1;
		else if (z < o.z)
			return -1;
		else if (z > o.z)
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
