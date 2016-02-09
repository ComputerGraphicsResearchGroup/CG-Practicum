package math;

import java.util.Arrays;
import java.util.Locale;

/**
 * Implementation of a 4 x 4 matrix.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class Matrix implements Cloneable {
	private final double[][] matrix = new double[4][4];

	/**
	 * Reference to the identity {@link Matrix}.
	 */
	// @formatter:off
	public static final Matrix IDENTITY = new Matrix(
										1, 0, 0, 0, 
										0, 1, 0, 0, 
										0, 0, 1, 0, 
										0, 0, 0, 1);
	// @formatter:on

	/**
	 * Creates a new {@link Matrix} filled with zeros.
	 */
	public Matrix() {
	}

	/**
	 * Creates a new {@link Matrix} filled with the given value.
	 * 
	 * @param value
	 *            the value to fill the {@link Matrix} with.
	 */
	public Matrix(double value) {
		for (int i = 0; i < 4; ++i)
			Arrays.fill(matrix[i], value);
	}

	/**
	 * Creates a new {@link Matrix} containing the given elements.
	 * 
	 * The elements are put in the {@link Matrix} in row-major order (i.e. the
	 * first 4 elements of <code>elements</code> correspond to the first row in
	 * the {@link Matrix}.
	 * 
	 * @param elements
	 *            the elements to fill the {@link Matrix} with.
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
	 * Constructs a {@link Matrix} from the given double array.
	 * 
	 * @param data
	 *            the data to populate the {@link Matrix} with.
	 * @throws NullPointerException
	 *             when the given data is null.
	 * @throws ArrayIndexOutOfBoundsException
	 *             when the size of the given data is smaller than a 4x4 array.
	 */
	public Matrix(double[][] data) throws NullPointerException,
			ArrayIndexOutOfBoundsException {
		for (int row = 0; row < 4; ++row)
			matrix[row] = Arrays.copyOf(data[row], 4);
	}

	/**
	 * Constructs a copy of the given {@link Matrix}.
	 * 
	 * @param matrix
	 *            the {@link Matrix} to copy.
	 * @throws NullPointerException
	 *             when the given {@link Matrix} is null.
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
	 * Returns true when this {@link Matrix} is exactly equal to the identity
	 * {@link Matrix}.
	 * 
	 * @return true when this {@link Matrix} is exactly equal to the identity
	 *         {@link Matrix}.
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
	 * Constructs the {@link Matrix} which is the sum of this {@link Matrix} and
	 * the given {@link Matrix}.
	 * 
	 * @param matrix
	 *            the {@link Matrix} to add to this {@link Matrix}.
	 * @throws NullPointerException
	 *             when the given {@link Matrix} is null.
	 * @return a {@link Matrix} which is the sum of this {@link Matrix} and the
	 *         given {@link Matrix}.
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
	 * Constructs the {@link Matrix} which is the difference of this
	 * {@link Matrix} and the given {@link Matrix}.
	 * 
	 * @param matrix
	 *            the {@link Matrix} to subtract from this {@link Matrix}.
	 * @throws NullPointerException
	 *             when the given {@link Matrix} is null.
	 * @return a {@link Matrix} which is the difference of this {@link Matrix}
	 *         and the given {@link Matrix}.
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
	 * Constructs the {@link Matrix} equal to this {@link Matrix} with all
	 * elements multiplied by the given scalar.
	 * 
	 * @param scalar
	 *            the scalar to multiply with.
	 * @return a {@link Matrix} which is the multiplication of this
	 *         {@link Matrix} and the given {@link Matrix}.
	 */
	public Matrix multiply(double scalar) {
		Matrix result = new Matrix();
		for (int row = 0; row < 4; ++row)
			for (int column = 0; column < 4; ++column)
				result.set(row, column, get(row, column) * scalar);
		return result;
	}

	/**
	 * Constructs the {@link Matrix} which is the multiplication of this
	 * {@link Matrix} with the given {@link Matrix}.
	 * 
	 * @param matrix
	 *            the {@link Matrix} to multiply this {@link Matrix} with.
	 * @throws NullPointerException
	 *             when the given {@link Matrix} is null.
	 * @return this {@link Matrix} multiplied with this {@link Matrix}.
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
	 * Returns an array which is the multiplication of this {@link Matrix} and
	 * the given array.
	 * 
	 * @param array
	 *            the array to be multiplied by this {@link Matrix}.
	 * @throws NullPointerException
	 *             when the given array is null.
	 * @throws IllegalArgumentException
	 *             when the length of the array does not match the number of
	 *             columns of this matrix.
	 * @return an array which is the multiplication of this {@link Matrix} and
	 *         the given array.
	 */
	public double[] multiply(double[] array) throws NullPointerException,
			IllegalArgumentException {
		if (array.length != 4)
			throw new IllegalArgumentException("the length of the array does "
					+ "not match the number of columns of this matrix!");
		double[] result = new double[] { 0, 0, 0, 0 };
		for (int i = 0; i < 4; ++i)
			for (int k = 0; k < 4; ++k)
				result[i] += get(i, k) * array[k];
		return result;
	}

	/**
	 * Returns the transpose of this {@link Matrix}.
	 * 
	 * @return the transpose of this {@link Matrix}.
	 */
	public Matrix transpose() {
		Matrix result = new Matrix();
		for (int row = 0; row < 4; ++row)
			for (int column = 0; column < 4; ++column)
				result.set(column, row, get(row, column));
		return result;
	}

	/**
	 * Transforms the given {@link Point} using this {@link Matrix}.
	 * 
	 * @param point
	 *            the {@link Point} to transform.
	 * @throws NullPointerException
	 *             when the given {@link Point} is null.
	 * @return the given {@link Point} transformed by this {@link Matrix}.
	 */
	public Point transform(Point point) throws NullPointerException {
		double[] homogenous = point.toHomogenousArray();
		double[] transformed = multiply(homogenous);
		return new Point(transformed[0], transformed[1], transformed[2],
				transformed[3]);
	}

	/**
	 * Transforms the given {@link Vector} using this {@link Matrix}.
	 * 
	 * @param vector
	 *            the {@link Vector} to transform.
	 * @throws NullPointerException
	 *             when the given {@link Vector} is null.
	 * @return the given {@link Vector} transformed by this {@link Matrix}.
	 */
	public Vector transform(Vector vector) throws NullPointerException {
		double[] v = new double[3];
		for (int row = 0; row < 3; ++row)
			for (int column = 0; column < 3; ++column)
				v[row] += get(row, column) * vector.get(column);
		return new Vector(v[0], v[1], v[2]);
	}

	/**
	 * Returns this {@link Matrix} as a two dimensional array.
	 * 
	 * The resulting array is not backed by this {@link Matrix} so changes to
	 * the resulting array will not be reflected in this {@link Matrix} and vice
	 * versa.
	 * 
	 * @return this {@link Matrix} as a two dimensional array.
	 */
	public double[][] toArray() {
		double[][] result = new double[4][4];

		for (int row = 0; row < 4; ++row)
			result[row] = Arrays.copyOf(matrix[row], 4);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Matrix(this);
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
