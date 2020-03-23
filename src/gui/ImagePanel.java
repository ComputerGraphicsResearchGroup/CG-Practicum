package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.beans.Transient;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JPanel;

import film.FrameBuffer;
import film.Pixel;
import film.RGBSpectrum;
import film.Tile;

/**
 * A panel which shows the progress of the rendered image.
 * 
 * @author 	CGRG
 * @version 4.0.0
 */
public class ImagePanel extends JPanel implements ComponentListener,
		MouseMotionListener, MouseListener, MouseWheelListener {
			
	/**
	 * A unique id required for serialization (required by the Serializable
	 * interface which JPanel implements).
	 */
	private static final long serialVersionUID = -5931426239657239552L;

	/**
	 * The frame buffer containing the image which should be displayed in this
	 * panel.
	 */
	private final FrameBuffer buffer;

	/**
	 * The exponent of the gamma curve for viewing the contents of the frame
	 * buffer.
	 */
	private double gamma;

	/**
	 * The sensitivity of viewing the contents of the frame buffer.
	 */
	private double sensitivity;

	/**
	 * The amount of zoom to draw the image.
	 */
	private double zoom = 1.0;

	/**
	 * The offset to draw the image with.
	 */
	private Point2D.Double offset = new Point2D.Double(0.0, 0.0);

	/**
	 * Two dimensional array which stores explicitly whether a certain pixel has
	 * already been rendered.been rendered.
	 * 
	 * When the corresponding value for a pixel is false, it could still be
	 * updated concurrently. However, when the value is true, we assume that the
	 * pixel does not change in the future.
	 */
	private final boolean[][] finished;

	/**
	 * An image which stores the rendered parts of the frame buffer, tone mapped
	 * with the sensitivity and gamma.
	 */
	private BufferedImage image;

	/**
	 * List of finished tiles.
	 */
	private List<Tile> tiles = new LinkedList<Tile>();

	/**
	 * List of finished tiles.
	 */
	private List<ReentrantLock> locks = new LinkedList<ReentrantLock>();

	/**
	 * A lock to use for concurrent modification of this panels internal state.
	 */
	private ReentrantLock lock = new ReentrantLock(true);

	/**
	 * The current position of the mouse in the panel.
	 */
	private Point currentMousePosition;

	/**
	 * A collection of listeners.
	 */
	private final Set<ImagePanelListener> listeners = new HashSet<ImagePanelListener>();

	/**
	 * Creates an panel which displays the result of the render which is stored
	 * in the given frame buffer. The contents of the frame buffer is tone
	 * mapped by the given sensitivity and gamma.
	 * 
	 * @param buffer
	 *            the frame buffer which contains the rendered image.
	 * @param sensitivity
	 *            the sensitivity to tone map the frame buffer's contents with.
	 * @param gamma
	 *            the gamma to tone map the frame buffer's contents with.
	 * @throws NullPointerException
	 *             when the given frame buffer is null.
	 * @throws IllegalArgumentException
	 *             when the sensitivity is smaller than or equal to zero.
	 * @throws IllegalArgumentException
	 *             when the sensitivity is either infinite or NaN.
	 * @throws IllegalArgumentException
	 *             when the gamma is smaller than or equal to zero.
	 * @throws IllegalArgumentException
	 *             when the gamma is either infinite or NaN.
	 */
	public ImagePanel(FrameBuffer buffer, double sensitivity, double gamma) {
		if (buffer == null)
			throw new NullPointerException("the given framebuffer is null!");
		this.buffer = buffer;
		this.finished = new boolean[buffer.yResolution][buffer.xResolution];
		this.image = new BufferedImage(buffer.xResolution, buffer.yResolution,
				BufferedImage.TYPE_INT_ARGB);

		setSensitivity(sensitivity);
		setGamma(gamma);

		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	@Transient
	public Dimension getPreferredSize() {
		int width = Math.max(128, Math.min(1024, buffer.xResolution));
		int height = Math.max(128, Math.min(768, buffer.yResolution));
		return new Dimension(width, height);
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

		lock.lock();
		try {
			this.sensitivity = sensitivity;
			update();
		} finally {
			lock.unlock();
		}

		repaint();
	}

	/**
	 * Returns the sensitivity applied to the image shown by this image panel.
	 * 
	 * @return the sensitivity applied to the image shown by this image panel.
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

		lock.lock();
		try {
			this.gamma = gamma;
			update();
		} finally {
			lock.unlock();
		}

		repaint();
	}

	/**
	 * The gamma applied to the image shown by this image panel.
	 * 
	 * @return the gamma applied to the image shown by this image panel.
	 */
	public double getGamma() {
		return gamma;
	}

	/**
	 * Returns the amount of zoom of the shown image.
	 * 
	 * @return the amount of zoom of the shown image.
	 */
	public double getZoom() {
		return zoom;
	}

	/**
	 * Sets the amount of zoom of the shown image.
	 * 
	 * @param zoom
	 *            the amount of zoom.
	 */
	public void setZoom(double zoom) {
		lock.lock();
		try {
			// clip the zoom within reasonable bounds
			if (zoom < 0.1)
				zoom = 0.1;
			else if (zoom > 10)
				zoom = 10;

			// check whether zoom remains the same
			if (zoom == this.zoom)
				return;

			// zoom on the last mouse position when available
			if (currentMousePosition != null) {
				double mx = currentMousePosition.getX();
				double my = currentMousePosition.getY();
				double ratio = zoom / this.zoom;

				double nx = mx - (mx - getDrawX()) * ratio;
				double ny = my - (my - getDrawY()) * ratio;

				this.zoom = zoom;

				setDrawX(nx);
				setDrawY(ny);
			} else {
				this.zoom = zoom;
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Sets the horizontal offset for drawing the image.
	 * 
	 * @param x
	 *            the horizontal offset for drawing the image.
	 */
	private void setDrawX(double x) {
		Dimension size = getSize();
		double max = size.getWidth() - image.getWidth() * getZoom();

		lock.lock();
		try {
			if (max > 0)
				offset.x = Math.max(0, Math.min(max, x));
			else
				offset.x = Math.min(0, Math.max(max, x));
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Returns the horizontal offset for drawing the image.
	 * 
	 * @return the horizontal offset for drawing the image.
	 */
	private double getDrawX() {
		return offset.x;
	}

	/**
	 * Sets the vertical offset for drawing the image.
	 * 
	 * @param y
	 *            the vertical offset for drawing the image.
	 */
	private void setDrawY(double y) {
		Dimension size = getSize();
		double max = size.getHeight() - image.getHeight() * getZoom();

		lock.lock();
		try {
			if (max > 0)
				offset.y = Math.max(0, Math.min(max, y));
			else
				offset.y = Math.min(0, Math.max(max, y));
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Returns the vertical offset for drawing the image.
	 * 
	 * @return the vertical offset for drawing the image.
	 */
	private double getDrawY() {
		return offset.y;
	}

	/**
	 * Applies the correct zoom and translation to the image to show it as large
	 * as possible within the constraints of this panel.
	 */
	public void fit() {
		Rectangle bounds = getBounds();

		double panelRatio = bounds.getWidth() / bounds.getHeight();
		double imageRatio = image.getWidth() / image.getHeight();

		if (imageRatio > panelRatio) {
			setZoom(bounds.getWidth() / image.getWidth());
		} else {
			setZoom(bounds.getHeight() / image.getHeight());
		}

		setDrawX((bounds.getWidth() - image.getWidth() * getZoom()) * 0.5);
		setDrawY((bounds.getHeight() - image.getHeight() * getZoom()) * 0.5);

		repaint();

	}

	/**
	 * Centers the image in this panel.
	 */
	public void center() {
		Rectangle bounds = getBounds();

		setDrawX(bounds.getCenterX() - image.getWidth() * getZoom() * 0.5);
		setDrawY(bounds.getCenterY() - image.getHeight() * getZoom() * 0.5);

		repaint();
	}

	/**
	 * Notifies this panel that the contents of the frame buffer within the
	 * given tile has been finished.
	 * 
	 * @note we assume none of the tiles passed along as arguments to this
	 *       function overlap. Overlapping tiles can possibly result in
	 *       concurrent modification of resources.
	 * 
	 * @param tile
	 */
	public void finished(Tile tile) {
		if (tile == null)
			throw new NullPointerException("the given tile is null!");

		// avoid concurrent access to the tiles
		lock.lock();
		ReentrantLock tileLock = new ReentrantLock();
		tiles.add(tile);
		locks.add(tileLock);
		lock.unlock();

		// update the tile
		tileLock.lock();
		try {
			// indicate that the pixels contained in the tile are finished
			for (int y = tile.yStart; y < tile.yEnd; ++y)
				for (int x = tile.xStart; x < tile.xEnd; ++x)
					finished[y][x] = true;

			update(tile);
		} finally {
			tileLock.unlock();
		}

		// determine the region of the panel which has to be repainted
		int x = (int) (tile.xStart * getZoom() + getDrawX());
		int y = (int) ((image.getHeight() - tile.yEnd) * getZoom() + getDrawY());
		int width = (int) Math.ceil(tile.getWidth() * getZoom() + 1.0);
		int height = (int) Math.ceil(tile.getHeight() * getZoom() + 1.0);

		repaint(x, y, width, height);

		// notify the listeners if mouse is in updated region
		if (currentMousePosition != null && currentMousePosition.x >= x
				&& currentMousePosition.x <= x + width
				&& currentMousePosition.y >= y
				&& currentMousePosition.y <= y + height) {
			notifyListeners();
		}

	}

	/**
	 * 
	 * @param listener
	 */
	public void addListener(ImagePanelListener listener) {
		if (listener != null)
			listeners.add(listener);
	}

	/**
	 * 
	 * @param listener
	 */
	public void removeListener(ImagePanelListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Updates the contents of all finished tiles.
	 */
	private void update() {
		// update the image with several concurrent threads
		ExecutorService service = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors());

		// create a thread for each tile
		for (int i = 0; i < tiles.size(); ++i) {
			final Tile tile = tiles.get(i);
			final ReentrantLock lock = locks.get(i);

			Thread thread = new Thread() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Thread#run()
				 */
				@Override
				public void run() {
					lock.lock();
					try {
						update(tile);
					} finally {
						lock.unlock();
					}
				};
			};
			service.submit(thread);
		}

		service.shutdown();
		try {
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		repaint();
	}

	/**
	 * Copies the contents of the framebuffer contained in the given tile to the
	 * buffered image used to draw on this panel with the appropriate tone
	 * mapping.
	 * 
	 * @param tile
	 *            the tile to update.
	 */
	private void update(Tile tile) throws NullPointerException {
		if (tile == null)
			throw new NullPointerException("the given tile is null!");
		double invSensitivity = 1.0 / sensitivity;
		double invGamma = 1.0 / gamma;

		WritableRaster raster = image.getRaster();
		DataBufferInt rasterBuffer = (DataBufferInt) raster.getDataBuffer();
		int[] data = rasterBuffer.getData();

		// update the finished pixels
		for (int y = tile.yStart; y < tile.yEnd; ++y) {
			int yOffset = (image.getHeight() - y - 1) * image.getWidth();

			for (int x = tile.xStart; x < tile.xEnd; ++x) {
				Pixel pixel = buffer.getPixel(x, y);
				RGBSpectrum spectrum = pixel.getSpectrum();

				int rgb = spectrum.clamp(0, invSensitivity).scale(sensitivity)
						.pow(invGamma).scale(255).toRGB();

				data[x + yOffset] = rgb;
			}
		}
	}

	/**
	 * Notifies the listeners that the spectrum at the mouse position has
	 * changed.
	 */
	private void notifyListeners() {
		double invZoom = 1.0 / getZoom();
		int x = (int) ((currentMousePosition.getX() - getDrawX()) * invZoom);
		int y = (int) ((currentMousePosition.getY() - getDrawY()) * invZoom);

		int yy = buffer.yResolution - y - 1;
		
		if (x >= 0 && x < buffer.xResolution && yy >= 0
				&& yy < buffer.yResolution && finished[yy][x]) {
			RGBSpectrum spectrum = buffer.getPixel(x, yy).getSpectrum();

			for (ImagePanelListener listener : listeners)
				listener.spectrumAtMouseChanged(x, yy, spectrum);
		} else {
			for (ImagePanelListener listener : listeners)
				listener.spectrumAtMouseChanged(x, yy, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		int x = (int) getDrawX();
		int y = (int) getDrawY();
		int width = (int) Math.ceil(getZoom() * image.getWidth());
		int height = (int) Math.ceil(getZoom() * image.getHeight());

		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(image, x, y, width, height, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.
	 * ComponentEvent)
	 */
	@Override
	public void componentResized(ComponentEvent e) {
		fit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent
	 * )
	 */
	@Override
	public void componentMoved(ComponentEvent e) {
		// no-op
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent
	 * )
	 */
	@Override
	public void componentShown(ComponentEvent e) {
		// no-op
	}

	/*
	 * s(non-Javadoc)
	 * 
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.
	 * ComponentEvent)
	 */
	@Override
	public void componentHidden(ComponentEvent e) {
		// no-op
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent
	 * )
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		Point p = e.getPoint();

		if (currentMousePosition != null) {
			int dx = p.x - currentMousePosition.x;
			int dy = p.y - currentMousePosition.y;

			setDrawX(getDrawX() + dx);
			setDrawY(getDrawY() + dy);

			repaint();
		}

		currentMousePosition = p;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		currentMousePosition = e.getPoint();

		notifyListeners();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// no-op
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// no-op
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// no-op
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// no-op
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		double invZoom = 1.0 / getZoom();
		int x = (int) ((currentMousePosition.getX() - getDrawX()) * invZoom);
		int y = (int) ((currentMousePosition.getY() - getDrawY()) * invZoom);

		int yy = buffer.yResolution - y - 1;

		for (ImagePanelListener listener : listeners)
			listener.spectrumAtMouseChanged(x, yy,null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.
	 * MouseWheelEvent)
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int amount = e.getWheelRotation();

		if (amount > 0) {
			setZoom(getZoom() / 1.1);
		} else if (amount < 0) {
			setZoom(getZoom() * 1.1);
		}

		repaint();
	}
}
