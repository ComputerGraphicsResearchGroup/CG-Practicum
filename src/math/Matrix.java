package math;

import java.util.Arrays;
import java.util.Locale;

/**
 * Implementation of a 4 x 4 matrix.
 * 
 * @author 	CGRG
 * @version 4.0.0
 */
public class Matrix {
	
	private final double[][] matrix = new double[4][4];

	/**
	 * Reference to the identity matrix.
	 */
	// @formatter:off
	public static final Matrix IDENTITY = new Matrix(
			1, 0, 0, 0,
			0, 1, 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 1);
	// @formatter:on

	/**
	 * Creates a new matrix filled with zeros.
	 */
	public Matrix() {
	}

	/**
	 * Creates a new matrix containing the given elements.
	 * 
	 * The elements are put in the matrix in row-major order (i.e. the first 4
	 * elements of <code>elements</code> correspond to the first row in the
	 * matrix.
	 * 
	 * @param elements
	 *            the elements to fill the matrix with.
	 * @throws NullPointerException
	 *             when the given elements array is null.
	 * @throws ArrayIndexOutOfBoundsException
	 *             when the given array contains less than sixteen elements.
	 */
	public Matrix(double... elements) throws NullPointerException,
			ArrayIndexOutOfBoundsException {
		for (int i = 0; i < 16; ++i)
			set(i / 4, i % 4, elements[i]);
	}

	/**
	 * Constructs a copy of the given matrix.
	 * 
	 * @param matrix
	 *            the matrix to copy.
	 * @throws NullPointerException
	 *             when the given matrix is null.
	 */
	public Matrix(Matrix matrix) throws NullPointerException {
		for (int row = 0; row < 4; ++row)
			for (int column = 0; column < 4; ++column)
				set(row, column, matrix.get(row, column));
	}

	/**
	 * Returns the value at the given row and column.
	 * 
	 * @param row
	 *            the row to retrieve the value of.
	 * @param column
	 *            the column to retrieve the value of.
	 * @throws ArrayIndexOutOfBoundsException
	 *             when the given row or column is smaller than zero or larger
	 *             than the four.
	 * @return the value at the given row and column.
	 */
	public double get(int row, int column)
			throws ArrayIndexOutOfBoundsException {
		return matrix[row][column];
	}

	/**
	 * Sets the element at the given row and column to the given value.
	 * 
	 * @param row
	 *            the row to set the value of.
	 * @param column
	 *            the column to set the value of.
	 * @param value
	 *            the value to set.
	 * @throws ArrayIndexOutOfBoundsException
	 *             when the given row or column is smaller than zero or larger
	 *             than the four.
	 * @return the element at the given row and column.
	 */
	protected void set(int row, int column, double value)
			throws ArrayIndexOutOfBoundsException {
		matrix[row][column] = value;
	}

	/**
	 * Returns true when this matrix is exactly equal to the identity matrix.
	 * 
	 * @return true when this matrix is exactly equal to the identity matrix.
	 */
	public boolean isIdentity() {
		for (int row = 0; row < 4; ++row)
			for (int column = 0; column < 4; ++column)
				if (row == column && get(row, column) != 1.0)
					return false;
				else if (row != column && get(row, column) != 0.0)
					return false;
		return true;
	}

	/**
	 * Constructs the matrix which is the sum of this matrix and the given
	 * matrix.
	 * 
	 * @param matrix
	 *            the matrix to add to this matrix.
	 * @throws NullPointerException
	 *             when the given matrix is null.
	 * @return a matrix which is the sum of this matrix and the given matrix.
	 */
	public Matrix add(Matrix matrix) throws NullPointerException {
		Matrix result = new Matrix();
		for (int row = 0; row < 4; ++row)
			for (int column = 0; column < 4; ++column)
				result.set(row, column,
						get(row, column) + matrix.get(row, column));
		return result;
	}

	/**
	 * Constructs the matrix which is the difference of this matrix and the
	 * given matrix.
	 * 
	 * @param matrix
	 *            the matrix to subtract from this matrix.
	 * @throws NullPointerException
	 *             when the given matrix is null.
	 * @return a matrix which is the difference of this matrix and the given
	 *         matrix.
	 */
	public Matrix subtract(Matrix matrix) throws NullPointerException {
		Matrix result = new Matrix();
		for (int row = 0; row < 4; ++row)
			for (int column = 0; column < 4; ++column)
				result.set(row, column,
						get(row, column) - matrix.get(row, column));
		return result;
	}

	/**
	 * Constructs the matrix equal to this matrix with all elements multiplied
	 * by the given scalar.
	 * 
	 * @param scalar
	 *            the scalar to multiply with.
	 * @return a matrix which is the multiplication of this matrix and the given
	 *         matrix.
	 */
	public Matrix multiply(double scalar) {
		Matrix result = new Matrix();
		for (int row = 0; row < 4; ++row)
			for (int column = 0; column < 4; ++column)
				result.set(row, column, get(row, column) * scalar);
		return result;
	}

	/**
	 * Constructs the matrix which is the multiplication of this matrix with the
	 * given matrix.
	 * 
	 * @param matrix
	 *            the matrix to multiply this matrix with.
	 * @throws NullPointerException
	 *             when the given matrix is null.
	 * @return this matrix multiplied with this matrix.
	 */
	public Matrix multiply(Matrix matrix) throws NullPointerException {
		Matrix result = new Matrix();
		for (int i = 0; i < 4; ++i)
			for (int j = 0; j < 4; ++j) {
				double value = 0;
				for (int k = 0; k < 4; ++k)
					value += get(i, k) * matrix.get(k, j);
				result.set(i, j, value);
			}
		return result;
	}

	/**
	 * Returns the transpose of this matrix.
	 * 
	 * @return the transpose of this matrix.
	 */
	public Matrix transpose() {
		Matrix result = new Matrix();
		for (int row = 0; row < 4; ++row)
			for (int column = 0; column < 4; ++column)
				result.set(column, row, get(row, column));
		return result;
	}

	/**
	 * Transforms the given point by this matrix.
	 * 
	 * @param point
	 *            the point to transform.
	 * @throws NullPointerException
	 *             when the given point is null.
	 * @return the given point transformed by this matrix.
	 */
	public Point transform(Point point) throws NullPointerException {
		// @formatter:off
		double x = get(0, 0) * point.x + get(0, 1) * point.y +
				   get(0, 2) * point.z + get(0, 3);
		double y = get(1, 0) * point.x + get(1, 1) * point.y +
				   get(1, 2) * point.z + get(1, 3);
		double z = get(2, 0) * point.x + get(2, 1) * point.y +
				   get(2, 2) * point.z + get(2, 3);
		double w = get(3, 0) * point.x + get(3, 1) * point.y + 
				   get(3, 2) * point.z + get(3, 3);
		double invW = 1.0 / w;
		// @formatter:on

		return new Point(x * invW, y * invW, z * invW);
	}

	/**
	 * Transforms the given vector by this matrix.
	 * 
	 * @param vector
	 *            the point to transform.
	 * @throws NullPointerException
	 *             when the given point is null.
	 * @return the given point transformed by this matrix.
	 */
	public Vector transform(Vector vector) throws NullPointerException {
		// @formatter:off
		double x = get(0, 0) * vector.x + get(0, 1) * vector.y +
				   get(0, 2) * vector.z;
		double y = get(1, 0) * vector.x + get(1, 1) * vector.y +
				   get(1, 2) * vector.z;
		double z = get(2, 0) * vector.x + get(2, 1) * vector.y +
				   get(2, 2) * vector.z;
		// @formatter:on

		return new Vector(x, y, z);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Arrays.hashCode(matrix);
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
		Matrix other = (Matrix) obj;
		return Arrays.equals(matrix, other.matrix);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[");
		builder.append(getClass().getName());
		builder.append("]:\n");

		// determine the maximum length for each column of the matrix.
		int maximumLength = 0;
		String[][] strings = new String[4][4];
		for (int row = 0; row < 4; ++row)
			for (int column = 0; column < 4; ++column) {
				String string = String.format(Locale.ENGLISH, "%s",
						get(row, column));
				strings[row][column] = string;
				maximumLength = Math.max(string.length(), maximumLength);
			}

		// append each row with the appropriate amount of spaces for proper
		// formatting.
		for (int row = 0; row < 4; ++row) {
			builder.append("[ ");
			for (int column = 0; column < 4; ++column) {
				String string = strings[row][column];
				builder.append(string);

				for (int i = 0; i < maximumLength - string.length() + 1; ++i)
					builder.append(" ");
			}
			if (row < 3)
				builder.append("]\n");
			else
				builder.append("]");
		}

		return builder.toString();
	}
}
