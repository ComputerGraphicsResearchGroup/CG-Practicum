package math;

/**
 * Represents an ray in three dimensional space starting at a given point and
 * extending infinitely in a given direction.
 * 
 * @author 	CGRG
 * @version 4.0.0
 */
public class Ray implements Cloneable {
	
	/**
	 * The origin of the ray.
	 */
	public final Point origin;

	/**
	 * The direction the ray extends to.
	 */
	public final Vector direction;

	/**
	 * Creates a new ray starting at the given origin and propagating in the
	 * given direction.
	 * 
	 * @param origin
	 *            the origin of the ray.
	 * @param direction
	 *            the direction of the ray.
	 * @throws NullPointerException
	 *             when the given origin and/or direction is null.
	 */
	public Ray(Point origin, Vector direction) throws NullPointerException {
		if (origin == null)
			throw new NullPointerException("the given origin is null!");
		if (direction == null)
			throw new NullPointerException("the given direction is null!");
		this.origin = origin;
		this.direction = direction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("[Ray3D] from %s %s %s in direction %s %s %s",
				origin.x, origin.y, origin.z, direction.x, direction.y,
				direction.z);
	}
}
