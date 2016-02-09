package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JPanel;

import film.Pixel;
import film.RGBSpectrum;
import film.Tile;

/**
 * A {@link JPanel} in which a preview of the render is shown, tone-mapped with
 * a given sensitivity and gamma.
 * 
 * @author Niels Billen
 * @version 0.2
 */
public class ImagePanel extends JPanel {
	private static final long serialVersionUID = 1597050597345753691L;

	private final LinkedList<Tile> tiles = new LinkedList<Tile>();
	private final BufferedImage image;
	private final ReentrantLock lock = new ReentrantLock();

	private double sensitivity = 1.0;
	private double gamma = 2.2;

	/**
	 * Creates a new {@link ImagePanel} on which can be drawn.
	 * 
	 * @param xResolution
	 *            the horizontal resolution.
	 * @param yResolution
	 *            the vertical resolution.
	 * @param sensitivity
	 *            inverse scaling factor for the radiance. (@see
	 *            {@link ImagePanel#setSensitivity(double)}.
	 * @param gamma
	 *            the gamma correction to apply to the shown image. (@see
	 *            {@link ImagePanel#setGamma(double)}.
	 * @throws IllegalArgumentException
	 *             when the x resolution and/or the y resolution is smaller than
	 *             one.
	 */
	public ImagePanel(int xResolution, int yResolution, double sensitivity,
			double gamma) throws IllegalArgumentException {
		if (xResolution <= 0)
			throw new IllegalArgumentException(
					"the horizontal resolution must be larger than zero!");
		if (yResolution <= 0)
			throw new IllegalArgumentException(
					"the vertical resolution must be larger than zero!");

		setSensitivity(sensitivity);
		setGamma(gamma);

		image = new BufferedImage(xResolution, yResolution,
				BufferedImage.TYPE_INT_ARGB);

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
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Updates this {@link ImagePanel} with the content in the given
	 * {@link Tile}.
	 * 
	 * @param tile
	 *            the {@link Tile} indicating the part of this
	 *            {@link ImagePanel} which should be updated.
	 * @throws NullPointerException
	 *             when the given {@link Tile} is null.
	 */
	public void update(Tile tile) {
		lock.lock();

		try {
			tiles.add(tile);
			updateImage(tile);
			repaint();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Sets the sensitivity to the given value.<br>
	 * <br>
	 * Since the radiance in the pixels of the rendered images can have
	 * arbitrarily large values, we have to scale them within the range [0, 255]
	 * which can be displayed on the computer screen.<br>
	 * <br>
	 * The radiance is converted to the range [0,255] by first clamping the
	 * radiance to <code>[0, 1.0 / sensitivity]</code> before multiplying the
	 * resulting radiance with the given <code>sensitivity</code> parameter.
	 * This results in radiance values within the range <code>[0, 1]</code>,
	 * which are gamma corrected and multiplied by 255, to fit within the
	 * displayable range of [0, 255].
	 * 
	 * @param sensitivity
	 *            the sensitivity for the shown image.
	 * @throws IllegalArgumentException
	 *             when the sensitivity is smaller than or equal to zero.
	 * @throws IllegalArgumentException
	 *             when the sensitivity is either infinite or NaN.
	 */
	public void setSensitivity(double sensitivity) {
		if (sensitivity <= 0)
			throw new IllegalArgumentException(
					"the sensitivity should be larger than zero!");
		if (Double.isInfinite(sensitivity))
			throw new IllegalArgumentException(
					"the sensitivity value cannot be infinite!");
		if (Double.isNaN(sensitivity))
			throw new IllegalArgumentException(
					"the sensitivity value cannot be NaN!");

		if (sensitivity == this.sensitivity)
			return;

		this.lock.lock();

		this.sensitivity = sensitivity;
		this.update();

		this.lock.unlock();
	}

	/**
	 * Returns the sensitivity applied to the image shown by this
	 * {@link ImagePanel}.
	 * 
	 * @return the sensitivity applied to the image shown by this
	 *         {@link ImagePanel}.
	 */
	public double getSensitivity() {
		return sensitivity;
	}

	/**
	 * Sets the gamma to the given gamma value.
	 * 
	 * @param gamma
	 *            the gamma value.
	 * @throws IllegalArgumentException
	 *             when the gamma value is smaller than or equal to zero.
	 * @throws IllegalArgumentException
	 *             when the gamma value is either infinite or not a number.
	 */
	public void setGamma(double gamma) {
		if (gamma <= 0)
			throw new IllegalArgumentException(
					"the gamma should be larger than zero!");
		if (Double.isInfinite(gamma))
			throw new IllegalArgumentException(
					"the gamma value cannot be infinite!");
		if (Double.isNaN(gamma))
			throw new IllegalArgumentException("the gamma value cannot be NaN!");

		if (gamma == this.gamma)
			return;

		this.lock.lock();

		this.gamma = gamma;
		this.update();

		this.lock.unlock();
	}

	/**
	 * The gamma applied to the image shown by this {@link ImagePanel}.
	 * 
	 * @return the gamma applied to the image shown by this {@link ImagePanel}.
	 */
	public double getGamma() {
		return gamma;
	}

	/**
	 * Copies the entire contents of the {@link ImagePanel#tiles} to the
	 * {@link ImagePanel#image} with the current {@link ImagePanel#sensitivity}
	 * and {@link ImagePanel#gamma} settings.
	 * 
	 * Note that this method is not thread safe and should be synchronized to
	 * avoid concurrent modification to the {@link #image}.
	 */
	private void update() {
		// update the image with several concurrent threads
		ExecutorService service = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors());

		// create a thread for each tile
		for (final Tile tile : tiles) {
			Thread thread = new Thread() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Thread#run()
				 */
				@Override
				public void run() {
					updateImage(tile);
				};
			};
			service.submit(thread);
		}

		service.shutdown();
		try {
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
			repaint();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Copies the contents of the given {@link Tile} to the
	 * {@link ImagePanel#image} with the current {@link ImagePanel#sensitivity}
	 * and {@link ImagePanel#gamma} settings.
	 * 
	 * Note that this method is not thread safe and should be synchronized to
	 * avoid concurrent modification to the {@link ImagePanel#image}.
	 * 
	 * @param tile
	 *            the {@link Tile} of which will bec
	 * @throws NullPointerException
	 *             when the given {@link Tile} is null.
	 */
	private void updateImage(Tile tile) throws NullPointerException {
		if (tile == null)
			throw new NullPointerException("the given tile is null!");

		double invSensitivity = 1.0 / sensitivity;
		double invGamma = 1.0 / gamma;

		WritableRaster raster = image.getRaster();
		DataBufferInt rasterBuffer = (DataBufferInt) raster.getDataBuffer();
		int[] rasterData = rasterBuffer.getData();

		for (int y = tile.yStart; y < tile.yEnd; ++y) {
			for (int x = tile.xStart; x < tile.xEnd; ++x) {
				Pixel pixel = tile.buffer.getPixel(x, y);
				RGBSpectrum spectrum = pixel.getSpectrum();

				int rgb = spectrum.clamp(0, invSensitivity).scale(sensitivity)
						.pow(invGamma).scale(255).toRGB();

				rasterData[x + (image.getHeight() - y - 1) * image.getWidth()] = rgb;
			}
		}
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
}
