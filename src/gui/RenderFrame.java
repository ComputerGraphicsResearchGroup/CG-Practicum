package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import film.FrameBuffer;

/**
 * A frame which display the progress of a render.
 * 
 * @author 	CGRG
 * @version 4.0.0
 */
public class RenderFrame extends JFrame implements ProgressListener {
	
	/**
	 * A unique id required for serialization (required by the Serializable
	 * interface which JFrame implements).
	 */
	private static final long serialVersionUID = -2141536191366207069L;

	/**
	 * The menu bar for the graphical user interface.
	 */
	private final Menubar menu;

	/**
	 * Progress bar indicating the percentage of completion of the render.
	 */
	private final JProgressBar bar;

	/**
	 * A panel which allows the user to manipulate the gamma and sensitivity.
	 */
	private final ControlPanel control;

	/**
	 * Panel which shows a preview of the render.
	 */
	public final ImagePanel panel;

	/**
	 * The frame buffer.
	 */
	private final FrameBuffer buffer;

	/**
	 * Creates a user interface which shows the progress of the render which is
	 * stored in the given frame buffer. The progress will be visualized as an
	 * image, tone mapped with the given sensitivity and gamma.
	 * 
	 * This method makes sure that all the components of the user interface are
	 * created and started on the AWT event dispatching thread.
	 * 
	 * @param buffer
	 *            the frame buffer which stores the rendered image.
	 * @param gamma
	 *            the gamma exponent to tone map the image with.
	 * @param sensitivity
	 *            the sensitivity to scale the image with.
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
	private RenderFrame(FrameBuffer buffer, double sensitivity, double gamma)
			throws NullPointerException {
		super("CG Project");

		if (buffer == null)
			throw new IllegalArgumentException(
					"the given frame buffer is null!");
		this.buffer = buffer;

		// Create the image panel
		panel = new ImagePanel(buffer, sensitivity, gamma);

		// Create the menu bar
		menu = new Menubar(this);
		setJMenuBar(menu);

		// Create the progress bar
		bar = new JProgressBar(0, 100);
		bar.setStringPainted(true);
		bar.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		// Create the control panel
		control = new ControlPanel(this);
		control.setPreferredSize(new Dimension(320, -1));

		// Add the components
		add(control, BorderLayout.EAST);
		add(panel, BorderLayout.CENTER);
		add(bar, BorderLayout.SOUTH);

		// Determine the size and center
		pack();
		center();

		// Show the usedr interface.
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Centers this frame on the first monitor encountered.
	 */
	public void center() {
		GraphicsEnvironment environment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();

		GraphicsDevice[] devices = environment.getScreenDevices();
		if (devices.length == 0)
			return;
		center(devices[0]);
	}

	/**
	 * Centers this frame on the given graphics device.
	 * 
	 * @param device
	 *            the device to center this {@link RenderFrame} on.
	 * @throws NullPointerException
	 *             when the given device is null.
	 */
	public void center(GraphicsDevice device) throws NullPointerException {
		if (device == null)
			throw new NullPointerException(
					"the graphics device to center this image frame upon!");

		Rectangle r = device.getDefaultConfiguration().getBounds();

		int x = r.x + (r.width - getWidth()) / 2;
		int y = r.y + (r.height - getHeight()) / 2;

		setLocation(x, y);
	}

	/**
	 * Sets the value of the progress bar. The given value must be in the
	 * interval [0,1] or are otherwise clamped to the interval.
	 * 
	 * @param progress
	 *            value for the progress bar (between 0 and 1).
	 */
	private void setProgress(double progress) {
		bar.setValue(Math.max(0, Math.min(100, (int) (100.0 * progress))));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.ProgressListener#update(double)
	 */
	@Override
	public void update(double progress) {
		setProgress(progress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.ProgressListener#finished()
	 */
	@Override
	public void finished() {
		setProgress(1.0);
	}

	/**
	 * Opens a file dialog with the request to save the current image stored in
	 * the frame buffer.
	 */
	public void save() {
		JFileChooser chooser = new JFileChooser(".");
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(new FileFilter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see javax.swing.filechooser.FileFilter#getDescription()
			 */
			@Override
			public String getDescription() {
				return "Portable Network Graphics (*.png)";
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
			 */
			@Override
			public boolean accept(File file) {
				return file.isDirectory() || file.getName().endsWith(".png");
			}
		});

		int result = chooser.showSaveDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			String filename = file.getName();

			if (!filename.endsWith(".png")) {
				JOptionPane.showMessageDialog(this,
						"The chosen filename does not end with '.png'!",
						"Invalid filename", JOptionPane.ERROR_MESSAGE);
			} else {
				try {
					double sensitivity = panel.getSensitivity();
					double gamma = panel.getGamma();
					BufferedImage image = buffer.toBufferedImage(sensitivity,
							gamma);
					ImageIO.write(image, "png", file);
				} catch (IOException e) {
					StringBuilder builder = new StringBuilder(e.toString());
					for (StackTraceElement element : e.getStackTrace())
						builder.append("\n\tat ").append(element);

					JOptionPane.showMessageDialog(this, builder.toString(),
							"IOException occured", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * Creates a user interface which shows the progress of the render which is
	 * stored in the given frame buffer. The progress will be visualized as an
	 * image, tone mapped with the given sensitivity and gamma.
	 * 
	 * This method makes sure that all the components of the user interface are
	 * created and started on the AWT event dispatching thread.
	 * 
	 * @param buffer
	 *            the frame buffer which stores the rendered image.
	 * @param gamma
	 *            the gamma exponent to tone map the image with.
	 * @param sensitivity
	 *            the sensitivity to scale the image with.
	 * @throws InvocationTargetException
	 *             when an exception occurs during the construction of the
	 *             graphical user interface.
	 * @throws InterruptedException
	 *             when an interruption occurs while we are waiting for the AWT
	 *             event dispatching thread to schedule the thread which creates
	 *             the graphical user interface.
	 * @return a graphical user interface which shows the progress of the
	 *         render.
	 */
	public static RenderFrame buildRenderFrame(FrameBuffer buffer,
			double gamma, double sensitivity) throws InvocationTargetException,
			InterruptedException {
		RenderFrameThread thread = new RenderFrameThread(buffer, gamma,
				sensitivity);
		SwingUtilities.invokeAndWait(thread);
		return thread.getRenderFrame();
	}

	/**
	 * A class which allows us to construct the user interface in a separate
	 * thread.
	 * 
	 * The Java Swing library requires us to build all the components of the
	 * graphical user interface on the AWT event dispatching thread.
	 * 
	 * The run method of this class constructs the graphical user interface. To
	 * create the user interface on the AWT event dispatching thread, an
	 * instance of this class has to be run using
	 * {@link SwingUtilities#invokeLater(Runnable)} or
	 * {@link SwingUtilities#invokeAndWait(Runnable)}).
	 * 
	 * @author Niels Billen
	 * @version 0.3
	 */
	private static class RenderFrameThread implements Runnable {
		/**
		 * Reference to the user interface which will be constructed in this
		 * {@link Runnable#run()} method.
		 */
		private RenderFrame frame;

		/**
		 * The frame buffer to construct the user interface for.
		 */
		private final FrameBuffer buffer;

		/**
		 * The initial gamma value to display the render with.
		 */
		private final double gamma;

		/**
		 * The initial sensitivity value to display the render with.
		 */
		private final double sensitivity;

		/**
		 * Constructs a new runnable which builds a graphical user interface
		 * which shows the progress of the render which is stored in the given
		 * frame buffer. The progress will be visualized as an image, tone
		 * mapped with the given sensitivity and gamma.
		 * 
		 * @param buffer
		 *            the frame buffer which stores the rendered image.
		 * @param gamma
		 *            the gamma exponent to tone map the image with.
		 * @param sensitivity
		 *            the sensitivity to scale the image with.
		 */
		public RenderFrameThread(FrameBuffer buffer, double gamma,
				double sensitivity) {
			this.buffer = buffer;
			this.gamma = gamma;
			this.sensitivity = sensitivity;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			frame = new RenderFrame(buffer, sensitivity, gamma);
		}

		/**
		 * Returns the user interface which has been constructed by this thread.
		 * 
		 * @return the user interface which has been constructed by this thread.
		 */
		public RenderFrame getRenderFrame() {
			return frame;
		}
	}
}
