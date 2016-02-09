package math;

import java.util.Comparator;
import java.util.Locale;

/**
 * Vector implementation in three dimensions.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class Vector implements Cloneable, Comparable<Vector> {
	/**
	 * x coordinate of this {@link Vector}.
	 */
	public final double x;

	/**
	 * y coordinate of this {@link Vector}.
	 */
	public final double y;

	/**
	 * z coordinate of this {@link Vector}.
	 */
	public final double z;

	/**
	 * Creates a {@link Vector} at the origin.
	 */
	public Vector() {
		this(0, 0, 0);
	}

	/**
	 * Creates a new {@link Vector} at the given position.
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
	 * Creates a new {@link Vector} at the given position, scaled by the given
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
	public Vector(double x, double y, double z, double w) {
		double inv_w = 1.0 / w;
		this.x = x * inv_w;
		this.y = y * inv_w;
		this.z = z * inv_w;
	}

	/**
	 * Creates a copy of the given {@link Vector}.
	 * 
	 * @param vector
	 *            the {@link Vector} to copy.
	 * @throws NullPointerException
	 *             when the given {@link Vector} is null.
	 */
	public Vector(Vector vector) throws NullPointerException {
		this(vector.x, vector.y, vector.z);
	}

	/**
	 * Returns the coordinate of this {@link Vector} along the given axis.
	 * 
	 * @param axis
	 *            axis to retrieve the coordinate of (0=x, 1=y, 2=z axis).
	 * @throws IllegalArgumentException
	 *             when the given axis is smaller than zero or larger than two.
	 * @return the coordinate of this {@link Vector} along the given axis.
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
	 * Constructs a {@link Vector} equal to this {@link Vector} translated by
	 * the given coordinates.
	 * 
	 * @param x
	 *            the x coordinate.
	 * @param y
	 *            the y coordinate.
	 * @param z
	 *            the z coordinate.
	 * @return a new {@link Vector} which is equal to this {@link Vector}
	 *         translated by the given coordinates.
	 */
	public Vector add(double x, double y, double z) {
		return new Vector(this.x + x, this.y + y, this.z + z);
	}

	/**
	 * Constructs a {@link Vector} equal to this {@link Vector} translated by
	 * the given {@link Vector}.
	 * 
	 * @param vector
	 *            the {@link Vector} to add to this {@link Vector}.
	 * @throws NullPointerException
	 *             when the given {@link Vector} is null.
	 * @return a new {@link Vector} which is equal to this {@link Vector}
	 *         translated by the given {@link Vector} .
	 */
	public Vector add(Vector vector) throws NullPointerException {
		return add(vector.x, vector.y, vector.z);
	}

	/**
	 * Constructs the {@link Vector} spanned between this {@link Vector} and the
	 * given coordinates.
	 * 
	 * @param x
	 *            the x coordinate.
	 * @param y
	 *            the y coordinate.
	 * @param z
	 *            the z coordinate.
	 * @return a new {@link Vector} equal to the {@link Vector} spanned between
	 *         this {@link Vector} and the given coordinates.
	 */
	public Vector subtract(double x, double y, double z) {
		return new Vector(this.x - x, this.y - y, this.z - z);
	}

	/**
	 * Constructs the {@link Vector} spanned between this {@link Vector} and the
	 * given {@link Vector}.
	 * 
	 * @param vector
	 *            the {@link Vector} to subtract from this {@link Vector}.
	 * @throws NullPointerException
	 *             when the given {@link Vector} is null.
	 * @return a new {@link Vector} equal to the {@link Vector} spanned between
	 *         this {@link Vector} and the given {@link Vector}.
	 */
	public Vector subtract(Vector vector) throws NullPointerException {
		return subtract(vector.x, vector.y, vector.z);
	}

	/**
	 * Constructs the {@link Vector} equal to this {@link Vector} with its
	 * coordinates scaled by the given scalar.
	 * 
	 * @param scalar
	 *            the scalar to scale this {@link Vector} with.
	 * @return a new {@link Vector} equal to this {@link Vector} with its
	 *         coordinates scaled by the given scalar.
	 */
	public Vector scale(double scalar) {
		return new Vector(x * scalar, y * scalar, z * scalar);
	}

	/**
	 * Constructs the {@link Vector} equal to this {@link Vector} with its
	 * coordinates divided by the given divisor.
	 * 
	 * @param divisor
	 *            the divisor to scale this {@link Vector} with.
	 * @return a new {@link Vector} equal to this {@link Vector} with its
	 *         coordinates divided by the given divisor.
	 */
	public Vector divide(double divisor) {
		return scale(1.0 / divisor);
	}

	/**
	 * Returns a comparator to sort {@link Vector} objects along the given axis.
	 * 
	 * @param axis
	 *            the axis to sort the {@link Vector}s along.
	 * @throws IllegalArgumentException
	 *             when the given axis is smaller than zero or larger than two.
	 * @return a comparator to sort objects of {@link Vector} along the given
	 *         axis.
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
	 * Converts this {@link Vector} to a three dimensional array.
	 * 
	 * @return this {@link Vector} as a three dimensional array.
	 */
	public double[] toArray() {
		return new double[] { x, y, z };
	}

	/**
	 * Returns this {@link Vector} as a four dimensional array with the fourth
	 * coordinate being the homogeneous coordinate.
	 * 
	 * @return this {@link Vector} as a four dimensional array with the fourth
	 *         coordinate being the homogeneous coordinate.
	 */
	public double[] toHomogenousArray() {
		return new double[] { x, y, z, 0.0 };
	}

	/**
	 * Returns the dot product of this {@link Vector} with the given
	 * {@link Vector}.
	 * 
	 * @param vector
	 *            the {@link Vector} to calculate the dot product with.
	 * @throws NullPointerException
	 *             when the given {@link Vector} is null.
	 * @return the dot product of this {@link Vector} with the given
	 *         {@link Vector}.
	 */
	public double dot(Vector vector) throws NullPointerException {
		return x * vector.x + y * vector.y + z * vector.z;
	}

	/**
	 * Returns the cross product of this {@link Vector} with the given
	 * {@link Vector}.
	 * 
	 * @param vector
	 *            the {@link Vector} to calculate the cross product of.
	 * @throws NullPointerException
	 *             when the given {@link Vector} is null.
	 * @return the cross product of this {@link Vector} with the given
	 *         {@link Vector}.
	 */
	public Vector cross(Vector vector) throws NullPointerException {
		double xx = y * vector.z - z * vector.y;
		double yy = z * vector.x - x * vector.z;
		double zz = x * vector.y - y * vector.x;

		return new Vector(xx, yy, zz);
	}

	/**
	 * Returns the squared length of this {@link Vector}.
	 * 
	 * @return the squared length of this {@link Vector}.
	 */
	public double lengthSquared() {
		return x * x + y * y + z * z;
	}

	/**
	 * Returns the length of this {@link Vector}.
	 * 
	 * @return the length of this {@link Vector}.
	 */
	public double length() {
		return Math.sqrt(lengthSquared());
	}

	/**
	 * Converts this {@link Vector} to a {@link Point}.
	 * 
	 * @return this {@link Vector} as a {@link Point}.
	 */
	public Point toPoint3D() {
		return new Point(x, y, z);
	}

	/**
	 * Returns a normalized version of this {@link Vector}.
	 * 
	 * @return a normalized version of this {@link Vector}.
	 */
	public Vector normalize() {
		return divide(length());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Vector(this);
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
