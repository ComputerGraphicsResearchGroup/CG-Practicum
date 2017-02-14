package film;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A rectangular tile.
 * 
 * @author Niels Billen
 * @version 0.3
 */
public class Tile {
	/**
	 * The horizontal starting coordinate of this tile (inclusive).
	 */
	public final int xStart;

	/**
	 * The vertical starting coordinate of this tile (inclusive).
	 */
	public final int yStart;

	/**
	 * The horizontal end coordinate of this tile (exclusive).
	 */
	public final int xEnd;

	/**
	 * The vertical end coordinate of this tile (exclusive).
	 */
	public final int yEnd;

	/**
	 * Creates a new tile containing with the given size.
	 * 
	 * @param xStart
	 *            the minimum x coordinate of this tile (inclusive)
	 * @param yStart
	 *            the minimum y coordinate of this tile (inclusive)
	 * @param xEnd
	 *            the maximum x coordinate of this tile (exclusive)
	 * @param yEnd
	 *            the maximum y coordinate of this tile (exclusive)
	 * @throws IllegalArgumentException
	 *             when the minimum x coordinate is larger than the maximum x
	 *             coordinate.
	 * @throws IllegalArgumentException
	 *             when the minimum y coordinate is larger than the maximum y
	 *             coordinate.
	 */
	public Tile(int xStart, int yStart, int xEnd, int yEnd)
			throws IllegalArgumentException {
		if (xStart > xEnd)
			throw new IllegalArgumentException("the minimum x coordinate is "
					+ "larger than the maximum x coordinate!");
		if (yStart > yEnd)
			throw new IllegalArgumentException("the minimum y coordinate is "
					+ "larger than the maximum y coordinate!");
		this.xStart = xStart;
		this.yStart = yStart;
		this.xEnd = xEnd;
		this.yEnd = yEnd;
	}

	/**
	 * Returns the width of this tile.
	 * 
	 * @return the width of this tile.
	 */
	public int getWidth() {
		return xEnd - xStart;
	}

	/**
	 * Returns the height of this tile.
	 * 
	 * @return the height of this tile.
	 */
	public int getHeight() {
		return yEnd - yStart;
	}

	/**
	 * Subdivides this tile in tiles which have the preferred width and height.
	 * 
	 * The resulting list of tiles will completely cover this tile.
	 * 
	 * @param preferredWidth
	 *            the preferred with of the resulting tiles.
	 * @param preferredHeight
	 *            the preferred height of the resulting tiles.
	 * @throws IllegalArgumentException
	 *             when the given preferred width and/or height is smaller than
	 *             zero.
	 * @return a collection of tiles which cover this tile.
	 */
	public Collection<Tile> subdivide(int preferredWidth, int preferredHeight)
			throws IllegalArgumentException {
		if (preferredWidth <= 0)
			throw new IllegalArgumentException(
					"the width of a tile must be larger than zero!");
		if (preferredHeight <= 0)
			throw new IllegalArgumentException(
					"the height of a tile must be larger than zero!");
		final List<Tile> result = new ArrayList<Tile>();
		for (int y = this.yStart; y < this.yEnd; y += preferredHeight) {
			for (int x = this.xStart; x < this.xEnd; x += preferredWidth) {
				int xEnd = Math.min(this.xEnd, x + preferredWidth);
				int yEnd = Math.min(this.yEnd, y + preferredHeight);
				result.add(new Tile(x, y, xEnd, yEnd));
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("[Tile]: %d %d %d %d", xStart, yStart, xEnd, yEnd);
	}
}
