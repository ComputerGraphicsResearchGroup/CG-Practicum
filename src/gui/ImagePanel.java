package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JPanel;

/**
 * A {@link JPanel} which allows thread safe drawing.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class ImagePanel extends JPanel implements Runnable {
	private static final long serialVersionUID = 1597050597345753691L;
	private final BufferedImage image;
	private final ReentrantLock lock = new ReentrantLock();
	private long refreshRate = 16;
	private boolean dirty = true;

	/**
	 * Creates a new {@link ImagePanel} on which can be drawn.
	 * 
	 * @param xResolution
	 *            the horizontal resolution.
	 * @param yResolution
	 *            the vertical resolution.
	 * @throws IllegalArgumentException
	 *             when the x resolution and/or the y resolution is smaller than
	 *             one.
	 */
	public ImagePanel(int xResolution, int yResolution)
			throws IllegalArgumentException {
		if (xResolution < 1)
			throw new IllegalArgumentException(
					"the given horizontal resolution is smaller than one!");
		if (yResolution < 1)
			throw new IllegalArgumentException(
					"the given vertical resolution is smaller than one!");
		image = new BufferedImage(xResolution, yResolution,
				BufferedImage.TYPE_INT_ARGB);

		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.setName("ImagePanel Repaint Thread");
		thread.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(Math.max(128, image.getWidth()), Math.max(128,
				image.getHeight()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Rectangle size = getBounds();
		int x = (size.width - image.getWidth()) / 2;
		int y = (size.height - image.getHeight()) / 2;

		lock.lock();
		try {
			g.drawImage(image, x, y, null);
			dirty = false;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Sets the pixel at the given position to the given color.
	 * 
	 * @param x
	 *            the x coordinate of the pixel.
	 * @param y
	 *            the y coordinate of the pixel.
	 * @param color
	 *            the color stored in an integer where the first byte is the
	 *            alpha value and subsequent bytes are the red, green and blue
	 *            component.
	 * @throws ArrayIndexOutOfBoundsException
	 *             when the given x and y coordinate are out of bounds.
	 */
	public void set(int x, int y, int color)
			throws ArrayIndexOutOfBoundsException {
		WritableRaster raster = image.getRaster();
		DataBufferInt buffer = (DataBufferInt) raster.getDataBuffer();
		int[] data = buffer.getData();

		lock.lock();
		try {
			dirty = true;
			data[x + image.getWidth() * (image.getHeight() - 1 - y)] = color;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Sets the pixel at the given position to the given color. The frame of
	 * reference is located at the bottom left corner of the panel.
	 * 
	 * @param x
	 *            the x coordinate of the pixel.
	 * @param y
	 *            the y coordinate of the pixel.
	 * @param alpha
	 *            the transparency of the color (0 is transparent, 255 is
	 *            opaque).
	 * @param red
	 *            the red color component (will be clamped between 0-255).
	 * @param green
	 *            the green color component (will be clamped between 0-255).
	 * @param blue
	 *            the blue color component (will be clamped between 0-255).
	 * @throws ArrayIndexOutOfBoundsException
	 *             when the given x and y coordinate are out of bounds.
	 */
	public void set(int x, int y, int alpha, int red, int green, int blue)
			throws ArrayIndexOutOfBoundsException {
		int color = ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16)
				| ((green & 0xFF) << 8) | (blue & 0xFF);

		set(x, y, color);
	}

	/**
	 * Sets the pixel at the given position to the given color. The frame of
	 * reference is located at the bottom left corner of the panel.
	 * 
	 * @param x
	 *            the x coordinate of the pixel.
	 * @param y
	 *            the y coordinate of the pixel.
	 * @param alpha
	 *            the transparency of the color (0 is transparent, 1 is opaque).
	 * @param red
	 *            the red color component (between 0 and 1).
	 * @param green
	 *            the green color component (between 0 and 1).
	 * @param blue
	 *            the blue color component (between 0 and 1).
	 * @throws ArrayIndexOutOfBoundsException
	 *             when the given x and y coordinate are out of bounds.
	 */
	public void set(int x, int y, float alpha, float red, float green,
			float blue) throws ArrayIndexOutOfBoundsException {
		int alpha_i = Math.max(0, Math.min(255, (int) (alpha * 255.f)));
		int red_i = Math.max(0, Math.min(255, (int) (red * 255.f)));
		int green_i = Math.max(0, Math.min(255, (int) (green * 255.f)));
		int blue_i = Math.max(0, Math.min(255, (int) (blue * 255.f)));

		int color = ((alpha_i & 0xFF) << 24) | ((red_i & 0xFF) << 16)
				| ((green_i & 0xFF) << 8) | (blue_i & 0xFF);

		set(x, y, color);
	}

	/**
	 * Sets the pixel at the given position to the given color. The frame of
	 * reference is located at the bottom left corner of the panel.
	 * 
	 * @param x
	 *            the x coordinate of the pixel.
	 * @param y
	 *            the y coordinate of the pixel.
	 * @param color
	 *            the color for the pixel.
	 * @throws NullPointerException
	 *             when the given color is null.
	 * @throws ArrayIndexOutOfBoundsException
	 *             when the given x and y coordinate are out of bounds.
	 */
	public void set(int x, int y, Color color)
			throws ArrayIndexOutOfBoundsException, NullPointerException {
		set(x, y, color.getRGB());
	}

	/**
	 * Returns a copy of the image drawn on this {@link ImagePanel}.
	 * 
	 * Changes to the returned image do not result in changes to the image drawn
	 * on this {@link ImagePanel} and vice versa.
	 * 
	 * @return a copy of the image drawn on this {@link ImagePanel}.
	 */
	public BufferedImage getImage() {
		BufferedImage result = new BufferedImage(image.getWidth(),
				image.getHeight(), image.getType());
		lock.lock();
		try {
			result.getGraphics().drawImage(image, 0, 0, null);
		} finally {
			lock.unlock();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (true) {
			lock.lock();
			try {
				if (dirty)
					repaint();
			} finally {
				lock.unlock();
			}
			try {
				Thread.sleep(refreshRate);
			} catch (InterruptedException e) {
			}
		}
	}
}
