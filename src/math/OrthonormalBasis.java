package math;

import java.util.Locale;

/**
 * Represents a basis in three dimensions consisting of three orthogonal vectors
 * of unit length.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class OrthonormalBasis implements Cloneable {
	/**
	 * First vector spanning the orthonormal basis.
	 */
	public final Vector u;

	/**
	 * Second vector spanning the orthonormal basis.
	 */
	public final Vector v;

	/**
	 * Third vector of spanning the orthonormal basis.
	 */
	public final Vector w;

	/**
	 * Creates an orthonormal basis around the given vector.
	 * 
	 * The <code>w</code> vector of this orthonormal basis basis will point in
	 * the same direction as the given vector <code>a</code>.
	 * 
	 * @param a
	 *            vector to construct the orthonormal basis about. The
	 *            <code>w</code> vector of this orthonormal basis will point in
	 *            the direction of this parameter.
	 * @throws NullPointerException
	 *             when the given vector is null.
	 * @throws IllegalArgumentException
	 *             when the length of the given vector is zero.
	 */
	public OrthonormalBasis(Vector a) throws NullPointerException {
		if (a == null)
			throw new NullPointerException("the given vector is null!");
		double length = a.length();

		if (length == 0)
			throw new IllegalArgumentException(
					"the given vector has zero length!");
		w = a.divide(length);

		if (w.x > w.y) {
			double inv_length = 1.0 / Math.sqrt(w.x * w.x + w.z * w.z);
			u = new Vector(-w.z * inv_length, 0.0, w.x * inv_length);
		} else {
			double inv_length = 1.0 / Math.sqrt(w.y * w.y + w.z * w.z);
			u = new Vector(0.f, w.z * inv_length, -w.y * inv_length);
		}
		v = w.cross(u);
	}

	/**
	 * Creates an orthonormal basis from the two given vectors.
	 * 
	 * The <code>w</code> vector of this orthonormal basis basis will point in
	 * the same direction as the given vector <code>a</code>. The constructor
	 * tries to force the <code>v</code> vector of this orthonormal basis to
	 * point roughly in the same direction as the given <code>b</code> vector.
	 * 
	 * @param a
	 *            the first vector. The <code>w</code> vector of this
	 *            orthonormal basis basis will point in the same direction as
	 *            the given vector <code>a</code>.
	 * @param b
	 *            the second vector. The <code>v</code> vector will point
	 *            roughly in the same direction as the given vector.
	 * @throws NullPointerException
	 *             when one of the two vectors is null.
	 * @throws IllegalArgumentException
	 *             when the two given vectors are colinear.
	 */
	public OrthonormalBasis(Vector a, Vector b) throws NullPointerException,
			IllegalArgumentException {
		if (a == null)
			throw new NullPointerException("the first vector is null!");
		if (b == null)
			throw new NullPointerException("the sceond vector is null!");

		Vector cross = b.cross(a);
		double length = cross.length();

		if (length == 0) {
			throw new IllegalArgumentException("the vectors are colinear!");
		} else if (length < 1e-8) {
			System.err.println("Warning: vector a and b are nearly colinear");
			System.err.println(a);
			System.err.println(b);
		}

		w = a.normalize();
		u = cross.divide(length);
		v = w.cross(u);
	}

	/**
	 * Constructs a copy of the given orthonormal basis.
	 * 
	 * @param basis
	 *            the orthonormal basis to copy.
	 * @throws NullPointerException
	 *             when the given orthonormal basis is null.
	 */
	public OrthonormalBasis(OrthonormalBasis basis) throws NullPointerException {
		this.u = basis.u;
		this.v = basis.v;
		this.w = basis.w;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new OrthonormalBasis(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		//@formatter:off
		return String.format(Locale.ENGLISH,
				"[%s %s %s]\n[%s %s %s]\n[%s %s %s]",
				u.x, u.y, u.z, v.x, v.y, v.z, w.x, w.y, w.z);
		//@formatter: on
	}
}
