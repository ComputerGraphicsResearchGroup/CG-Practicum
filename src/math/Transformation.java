package math;

/**
 * A wrapper for transformation matrix which allows to apply transformation and
 * inverse transformation on three-dimensional objects.
 * 
 * @author Niels Billen
 * @version 0.3
 */
public class Transformation {
	/**
	 * The transformation matrix.
	 */
	private final Matrix matrix;

	/**
	 * The inverse of the transformation matrix.
	 */
	private final Matrix inverse;

	/**
	 * Reference to the identity transformation.
	 */
	public static final Transformation IDENTITY = new Transformation(
			Matrix.IDENTITY, Matrix.IDENTITY);

	/**
	 * Creates a new transformation for three dimensional objects.
	 * 
	 * @param matrix
	 *            the matrix transformation.
	 * @param inverse
	 *            the inverse of the given transformation.
	 */
	private Transformation(Matrix matrix, Matrix inverse) {
		this.matrix = matrix;
		this.inverse = inverse;
	}

	/**
	 * Returns the matrix containing the transformation.
	 * 
	 * @return the matrix containing the transformation.
	 */
	public Matrix getTransformationMatrix() {
		return matrix;
	}

	/**
	 * Returns the inverse of the matrix containing the transformation.
	 * 
	 * @return the inverse of the matrix containing the transformation.
	 */
	public Matrix getInverseTransformationMatrix() {
		return inverse;
	}

	/**
	 * Returns the inverse of this transformation.
	 * 
	 * @return the inverse of this transformation.
	 */
	public Transformation invert() {
		return new Transformation(inverse, matrix);
	}

	/**
	 * Appends the given transformation to this transformation.
	 * 
	 * @param transformation
	 *            the transformation to append.
	 * @throws NullPointerException
	 *             when the given transformation is null.
	 * @return this transformation concatenated with the given transformation.
	 */
	public Transformation append(Transformation transformation)
			throws NullPointerException {
		if (transformation == null)
			throw new NullPointerException("the given transformation is null!");
		return new Transformation(matrix.multiply(transformation.matrix),
				transformation.inverse.multiply(inverse));
	}

	/**
	 * Transforms the given point with this transformation.
	 * 
	 * @param point
	 *            the point to transform.
	 * @throws NullPointerException
	 *             when the given point is null.
	 * @return the given point transformed by this transformation.
	 */
	public Point transform(Point point) throws NullPointerException {
		return matrix.transform(point);
	}

	/**
	 * Transforms the given point with the inverse of this transformation.
	 * 
	 * @param point
	 *            the point to transform.
	 * @throws NullPointerException
	 *             when the given point is null.
	 * @return the given point transformed by the inverse of this
	 *         transformation.
	 */
	public Point transformInverse(Point point) throws NullPointerException {
		return inverse.transform(point);
	}

	/**
	 * Transforms the given vector with this transformation.
	 * 
	 * @param vector
	 *            the vector to transform.
	 * @throws NullPointerException
	 *             when the given vector is null.
	 * @return the given vector transformed by this transformation.
	 */
	public Vector transform(Vector vector) throws NullPointerException {
		return matrix.transform(vector);
	}

	/**
	 * Transforms the given vector with the inverse of this transformation.
	 * 
	 * @param vector
	 *            the vector to transform.
	 * @throws NullPointerException
	 *             when the given vector is null.
	 * @return the given vector transformed by the inverse of this
	 *         transformation.
	 */
	public Vector transformInverse(Vector vector) throws NullPointerException {
		return inverse.transform(vector);
	}

	/**
	 * Transforms the given ray with this transformation.
	 * 
	 * @param ray
	 *            the ray to transform.
	 * @throws NullPointerException
	 *             when the given ray is null.
	 * @return the given ray Ray} transformed by this transformation.
	 */
	public Ray transform(Ray ray) throws NullPointerException {
		Point point = transform(ray.origin);
		Vector direction = transform(ray.direction);
		return new Ray(point, direction);
	}

	/**
	 * Transforms the given ray with the inverse of this transformation.
	 * 
	 * @param ray
	 *            the ray to transform.
	 * @throws NullPointerException
	 *             when the given ray is null.
	 * @return the given ray transformed by the inverse of this transformation.
	 */
	public Ray transformInverse(Ray ray) throws NullPointerException {
		Point point = transformInverse(ray.origin);
		Vector direction = transformInverse(ray.direction);
		return new Ray(point, direction);
	}

	/**
	 * Creates a new translation transformation.
	 * 
	 * @param x
	 *            the x translation for this transformation.
	 * @param y
	 *            the y translation for this transformation.
	 * @param z
	 *            the z translation for this transformation.
	 * @return a new translation transformation.
	 */
	public static Transformation translate(double x, double y, double z) {
		// @formatter:off
		Matrix transformation = new Matrix(	1,	0,	0,	x,
											0,	1,	0,	y,
											0,	0,	1,	z,
											0,	0,	0,	1);
		Matrix inverse = new Matrix(1,	0,	0,	-x,
									0,	1,	0,	-y,
									0,	0,	1,	-z,
									0,	0,	0,	1);
		// @formatter:on
		return new Transformation(transformation, inverse);
	}

	/**
	 * Creates a new scale transformation.
	 * 
	 * @param x
	 *            the x scale for this scale transformation.
	 * @param y
	 *            the y scale for this scale transformation.
	 * @param z
	 *            the z scale for this scale transformation.
	 * @return a new transformation which scales three dimensional objects.
	 */
	public static Transformation scale(double x, double y, double z) {
		// @formatter:off
		Matrix transformation = new Matrix(	x,	0,	0,	0,
											0,	y,	0,	0,
											0,	0,	z,	0,
											0,	0,	0,	1);
		Matrix inverse = new Matrix(1/x,	0,		0,		0,
									0,		1/y,	0,		0,
									0,		0,		1/z,	0,
									0,		0,		0,		1);
		// @formatter:on
		return new Transformation(transformation, inverse);
	}

	/**
	 * Creates a new rotation transformation about the x axis in a counter
	 * clockwise direction.
	 * 
	 * @param angle
	 *            the angle to rotate about (in degrees).
	 * @return a new rotation transformation about the x axis.
	 */
	public static Transformation rotateX(double angle) {
		double rad = Math.toRadians(angle);
		double sin = Math.sin(rad);
		double cos = Math.cos(rad);

		// @formatter:off
		Matrix transformation = new Matrix(	1,		0,		0,		0,
											0,		cos,	-sin,	0,
											0,		sin,	cos,	0,
											0,		0,		0,		1);
		Matrix inverse = transformation.transpose();
		// @formatter:on
		return new Transformation(transformation, inverse);
	}

	/**
	 * Creates a new rotation transformation about the y axis in a counter
	 * clockwise direction.
	 * 
	 * @param angle
	 *            the angle to rotate about (in degrees).
	 * @return a new rotation transformation about the y axis.
	 */
	public static Transformation rotateY(double angle) {
		double rad = Math.toRadians(angle);
		double sin = Math.sin(rad);
		double cos = Math.cos(rad);

		// @formatter:off
		Matrix transformation = new Matrix(	cos,	0,		sin,	0,
											0,		1,		0,		0,
											-sin,	0,		cos,	0,
											0,		0,		0,		1);
		Matrix inverse = transformation.transpose();
		// @formatter:on
		return new Transformation(transformation, inverse);
	}

	/**
	 * Creates a new rotation transformation about the z axis in a counter
	 * clockwise direction.
	 * 
	 * @param angle
	 *            the angle to rotate about (in degrees).
	 * @return a new rotation transformation about the z axis.
	 */
	public static Transformation rotateZ(double angle) {
		double rad = Math.toRadians(angle);
		double sin = Math.sin(rad);
		double cos = Math.cos(rad);

		// @formatter:off
		Matrix transformation = new Matrix(	cos,	-sin,	0,		0,
											sin,	cos,	0,		0,
											0,		0,		1,		0,
											0,		0,		0,		1);
		Matrix inverse = transformation.transpose();
		// @formatter:on
		return new Transformation(transformation, inverse);
	}

	/**
	 * Creates a rotation around of the given angle around the specified
	 * rotation axis.
	 * 
	 * @param vector
	 *            the vector to rotate about.
	 * @param angle
	 *            the angle to rotate about (in degrees).
	 * @return a new rotation transformation about the z axis.
	 */
	public static Transformation rotate(Vector vector, double angle) {
		if (vector == null)
			throw new NullPointerException("the given vector is null!");
		double length = vector.length();
		if (length == 0)
			throw new IllegalArgumentException(
					"the given vector is degenerate (length is zero)!");
		Vector n = vector.divide(length);

		double rad = Math.toRadians(angle);
		double sin = Math.sin(rad);
		double cos = Math.cos(rad);
		double ncos = 1.0 - cos;

		// @formatter:off
		Matrix transformation = new Matrix(	
				n.x * n.x * ncos + cos,
					n.y * n.x * ncos - n.z * sin,
					n.z * n.x * ncos + n.y * sin, 0,
				n.x * n.y * ncos + n.z * sin,
					n.y * n.y * ncos + cos,
					n.z * n.y * ncos - n.x * sin, 0,
				n.x * n.z * ncos - n.y * sin,
					n.y * n.z * ncos + n.x * sin,
					n.z * n.z * ncos + cos, 0,
				0, 0, 0, 1);
		Matrix inverse = transformation.transpose();
		
		// @formatter:on
		return new Transformation(transformation, inverse);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getTransformationMatrix().hashCode();
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
		Transformation t = (Transformation) obj;
		return getTransformationMatrix().equals(t.getTransformationMatrix());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder("[").append(getClass().getName() + "]\n")
				.append(matrix.toString()).toString();
	}
}
