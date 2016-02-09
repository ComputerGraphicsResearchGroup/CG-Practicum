package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

/**
 * A {@link JFrame} which displays the progress of a render as an image and as a
 * progress bar. Functionality to save the image is also supported.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class RenderFrame implements ProgressListener {
	private JFrame frame;
	private ImagePanel panel;
	private JProgressBar bar = new JProgressBar(0, 100);

	/**
	 * Creates a new {@link RenderFrame} displaying the given {@link Image} in a
	 * {@link JFrame} with the given title.
	 * 
	 * @param title
	 *            the title for the {@link JFrame}.
	 * @param panel
	 *            image to display in the {@link JFrame}.
	 * @throws NullPointerException
	 *             when the given title is null.
	 * @throws NullPointerException
	 *             when the given {@link ImagePanel} is null.
	 */
	public RenderFrame(String title, ImagePanel panel)
			throws NullPointerException {
		if (title == null)
			throw new NullPointerException("the given title is null!");
		if (panel == null)
			throw new NullPointerException("the given panel is null!");

		this.frame = new JFrame(title);
		this.panel = panel;

		// customize the progressbar
		bar.setPreferredSize(new Dimension(-1, 32));
		bar.setStringPainted(true);

		// add a file bar
		frame.setJMenuBar(createMenuBar());

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.add(RenderFrame.this.panel, BorderLayout.CENTER);
		frame.add(bar, BorderLayout.SOUTH);
		frame.pack();
		center();
		frame.setVisible(true);
	}

	/**
	 * Creates the {@link JMenuBar} for this {@link RenderFrame}.
	 * 
	 * @return the {@link JMenuBar} for this {@link RenderFrame}.
	 */
	private JMenuBar createMenuBar() {
		JMenuBar bar = new JMenuBar();
		bar.setPreferredSize(new Dimension(-1, 32));

		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);

		JMenuItem saveImage = new JMenuItem("Save image as...");
		KeyStroke controlShiftS = KeyStroke.getKeyStroke("control shift S");
		saveImage.setAccelerator(controlShiftS);

		saveImage.addActionListener(new ActionListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ActionListener#actionPerformed(java.awt.event.
			 * ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(new File("."));
				chooser.setMultiSelectionEnabled(false);
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

				FileFilter imageFilter = new FileFilter() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see javax.swing.filechooser.FileFilter#getDescription()
					 */
					@Override
					public String getDescription() {
						return "image file (*.png, *.jpeg *.bmp *.gif)";
					}

					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * javax.swing.filechooser.FileFilter#accept(java.io.File)
					 */
					@Override
					public boolean accept(File f) {
						String name = f.getName();

						for (String extension : ImageIO.getWriterFileSuffixes())
							if (name.endsWith(".".concat(extension)))
								return true;
						return false;
					}
				};

				chooser.setFileFilter(imageFilter);
				int returnValue = chooser.showSaveDialog(frame);

				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();

					if (imageFilter.accept(file)) {
						try {
							String extension = file.getName();
							extension = extension.substring(extension
									.lastIndexOf('.') + 1);
							ImageIO.write(panel.getImage(), extension, file);
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					} else {
						StringBuilder builder = new StringBuilder("The given "
								+ "filename:\n\n");
						builder.append(file.getAbsolutePath());
						builder.append("\n\ndoes not have a valid image "
								+ "extension! "
								+ "The supported image extensions are:\n");

						for (String extension : ImageIO.getWriterFileSuffixes())
							builder.append(" " + extension);

						JOptionPane.showMessageDialog(frame, builder.toString());
					}
				}
			}
		});

		JMenuItem close = new JMenuItem("Close");
		KeyStroke controlQ = KeyStroke.getKeyStroke("control Q");
		close.setAccelerator(controlQ);

		close.addActionListener(new ActionListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ActionListener#actionPerformed(java.awt.event.
			 * ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				RenderFrame.this.frame.dispose();
			}
		});

		fileMenu.add(saveImage);
		fileMenu.add(close);
		bar.add(fileMenu);

		return bar;
	}

	/**
	 * Returns the {@link ImagePanel} on which the render is drawn.
	 * 
	 * @return the {@link ImagePanel} on which the render is drawn.
	 */
	public ImagePanel getImagePanel() {
		return panel;
	}

	/**
	 * Sets the value of the progress bar.
	 * 
	 * @param progress
	 *            value for the progress bar (between 0 and 1).
	 */
	public void setProgress(double progress) {
		bar.setValue(Math.max(0, Math.min(100, (int) (100.0 * progress))));
	}

	/**
	 * Centers this {@link RenderFrame} on the first monitor encountered.
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
	 * Centers this {@link RenderFrame} on the given graphics device.
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

		int x = (r.width - frame.getWidth()) / 2;
		int y = (r.height - frame.getHeight()) / 2;

		frame.setLocation(x, y);
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
}
