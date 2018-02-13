package shape;

import math.Ray;

/**
 * Interface which should be implemented by all shapes.
 * 
 * @author 	CGRG
 * @version 4.0.0
 */
public interface Shape {
	
	/**
	 * Returns whether the given ray intersects this shape. Returns false when
	 * the given ray is null.
	 * 
	 * @param ray
	 *            the ray to intersect with.
	 * @return true when the given ray intersects this shape.
	 */
	public boolean intersect(Ray ray);
}
