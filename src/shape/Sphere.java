package shape;

import math.Ray;
import math.Transformation;
import math.Vector;

/**
 * Represents a three-dimensional sphere with radius one, centered at the
 * origin, which is transformed by a transformation.
 * 
 * @author 	CGRG
 * @version 4.0.0
 */
public class Sphere implements Shape {
	
	/**
	 * The transformation which is applied to the sphere to place it in the
	 * scene.
	 */
	public final Transformation transformation;

	/**
	 * Creates a new unit sphere at the origin, transformed by the given
	 * transformation.
	 * 
	 * @param transformation
	 *            the transformation applied to this sphere.
	 * @throws NullPointerException
	 *             when the transformation is null.
	 */
	public Sphere(Transformation transformation) throws NullPointerException {
		if (transformation == null)
			throw new NullPointerException("the given transformation is null!");
		this.transformation = transformation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see shape.Shape#intersect(geometry3d.Ray3D)
	 */
	@Override
	public boolean intersect(Ray ray) {
		if (ray == null)
			return false;
		Ray transformed = transformation.transformInverse(ray);

		Vector o = transformed.origin.toVector();

		double a = transformed.direction.lengthSquared();
		double b = 2.0 * (transformed.direction.dot(o));
		double c = o.dot(o) - 1.0;

		double d = b * b - 4.0 * a * c;

		if (d < 0)
			return false;
		double dr = Math.sqrt(d);

		// numerically solve the equation a*t^2 + b * t + c = 0
		double q = -0.5 * (b < 0 ? (b - dr) : (b + dr));

		double t0 = q / a;
		double t1 = c / q;

		return t0 >= 0 || t1 >= 0;
	}
}
