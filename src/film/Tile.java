package film;

/**
 * Represents a rectangular {@link Tile} in a {@link FrameBuffer}.
 * 
 * @author Niels Billen
 * @version 0.2
 */
public class Tile {
	/**
	 * The {@link FrameBuffer} from which this {@link Tile} originates.
	 */
	public final FrameBuffer buffer;

	/**
	 * The horizontal starting coordinate of this {@link Tile} (inclusive).
	 */
	public final int xStart;

	/**
	 * The vertical starting coordinate of this {@link Tile} (inclusive).
	 */
	public final int yStart;

	/**
	 * The horizontal end coordinate of this {@link Tile} (exclusive).
	 */
	public final int xEnd;

	/**
	 * The vertical end coordinate of this {@link Tile} (exclusive).
	 */
	public final int yEnd;

	/**
	 * Creates a new {@link Tile} containing {@link Pixel}s of the given
	 * {@link FrameBuffer} object.
	 * 
	 * @param buffer
	 *            the {@link FrameBuffer} to create this {@link Tile} for.
	 * @param xStart
	 *            the minimum x coordinate of this {@link Tile} (inclusive)
	 * @param yStart
	 *            the minimum y coordinate of this {@link Tile} (inclusive)
	 * @param xEnd
	 *            the maximum x coordinate of this {@link Tile} (exclusive)
	 * @param yEnd
	 *            the maximum y coordinate of this {@link Tile} (exclusive)
	 */
	public Tile(FrameBuffer buffer, int xStart, int yStart, int xEnd, int yEnd) {
		this.buffer = buffer;
		this.xStart = xStart;
		this.yStart = yStart;
		this.xEnd = xEnd;
		this.yEnd = yEnd;
	}

	/**
	 * Returns the width of this {@link Tile}.
	 * 
	 * @return the width of this {@link Tile}.
	 */
	public int getWidth() {
		return xEnd - xStart;
	}

	/**
	 * Returns the height of this {@link Tile}.
	 * 
	 * @return the height of this {@link Tile}.
	 */
	public int getHeight() {
		return yEnd - yStart;
	}
}
