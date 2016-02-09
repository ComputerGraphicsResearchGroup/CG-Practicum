package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Hashtable;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

/**
 * A {@link JFrame} which displays the progress of a render as an image and as a
 * progress bar. Functionality to save the image is also supported.
 * 
 * @author Niels Billen
 * @version 0.2
 */
public class RenderFrame implements ProgressListener {
	private JFrame frame;
	private ImagePanel panel;
	private JProgressBar bar = new JProgressBar(0, 100);
	private JSlider sensitivity = new JSlider();
	private JSlider gamma = new JSlider();

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
	public RenderFrame(final String title, final ImagePanel panel) throws NullPointerException {
		if (title == null)
			throw new NullPointerException("the given title is null!");
		if (panel == null)
			throw new NullPointerException("the given panel is null!");

		this.frame = new JFrame(title);
		this.panel = panel;

		// customize the progress bar
		bar.setPreferredSize(new Dimension(-1, 48));
		bar.setStringPainted(true);
		bar.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		// add a file bar
		frame.setJMenuBar(createMenuBar());
		frame.add(createControlPanel(), BorderLayout.EAST);
		frame.add(panel, BorderLayout.CENTER);
		frame.add(bar, BorderLayout.SOUTH);
		frame.pack();
		center();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	/**
	 * Creates the {@link JPanel} with the controls for the image exposure,
	 * gamma and save button.
	 * 
	 * @return the {@link JPanel} with the controls for the image exposure,
	 *         gamma and save button.
	 */
	private JPanel createControlPanel() {
		// create a wrapper for the control panel which will have a raised
		// border
		JPanel controlWrapper = new JPanel();
		controlWrapper.setLayout(new BoxLayout(controlWrapper, BoxLayout.Y_AXIS));
		controlWrapper.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		// create shared objects
		final Border border = BorderFactory.createEmptyBorder(8, 8, 8, 8);
		final Dimension sliderDimension = new Dimension(320, 64);

		/**********************************************************************
		 * Create the slider for the exposure
		 *********************************************************************/

		int sensitivityValue = (int) (Math.log10(panel.getSensitivity()) * 10.0);

		// create the label for the exposure
		final JLabel exposureLabel = new JLabel(
				String.format(Locale.ENGLISH, "Sensitivity: %e", Math.pow(10, sensitivityValue)));
		exposureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		exposureLabel.setBorder(border);

		// set the ticks
		sensitivity.setMinorTickSpacing(5);
		sensitivity.setMajorTickSpacing(10);
		sensitivity.setSnapToTicks(false);
		sensitivity.setPaintLabels(true);
		sensitivity.setPaintTicks(true);

		// set the range
		sensitivity.setMinimum(-100);
		sensitivity.setMaximum(100);
		sensitivity.setValue(sensitivityValue);

		// set the labels
		Hashtable<Integer, JLabel> exposureLabels = new Hashtable<Integer, JLabel>();
		exposureLabels.put(-100, new JLabel("1e-10", JLabel.CENTER));
		exposureLabels.put(-50, new JLabel("1e-5", JLabel.CENTER));
		exposureLabels.put(0, new JLabel("1e0", JLabel.CENTER));
		exposureLabels.put(50, new JLabel("1e5", JLabel.CENTER));
		exposureLabels.put(100, new JLabel("1e10", JLabel.CENTER));
		sensitivity.setLabelTable(exposureLabels);

		// set the size
		sensitivity.setMinimumSize(sliderDimension);
		sensitivity.setPreferredSize(sliderDimension);
		sensitivity.setMaximumSize(sliderDimension);
		sensitivity.setAlignmentX(Component.CENTER_ALIGNMENT);

		// listen to changes
		sensitivity.addChangeListener(new ChangeListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * javax.swing.event.ChangeListener#stateChanged(javax.swing.event
			 * .ChangeEvent)
			 */
			@Override
			public void stateChanged(ChangeEvent event) {
				double sensitivity = Math.pow(10.0, RenderFrame.this.sensitivity.getValue() * 0.1);

				exposureLabel.setText(String.format(Locale.ENGLISH, "Sensitivity: %e", sensitivity));

				if (RenderFrame.this.panel != null)
					RenderFrame.this.panel.setSensitivity(sensitivity);
			}
		});

		/**********************************************************************
		 * Create the slider for the gamma
		 *********************************************************************/

		int gammaValue = (int) (panel.getGamma() * 10.0);

		final JLabel gammaLabel = new JLabel(String.format(Locale.ENGLISH, "Gamma: %2.1f", gammaValue * 0.1));
		gammaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		gammaLabel.setBorder(border);

		// set the ticks
		gamma.setMinorTickSpacing(1);
		gamma.setMajorTickSpacing(5);
		gamma.setSnapToTicks(true);
		gamma.setPaintLabels(true);
		gamma.setPaintTicks(true);

		// set the range
		gamma.setMinimum(1);
		gamma.setMaximum(40);
		gamma.setValue(gammaValue);

		// set the labels
		Hashtable<Integer, JLabel> gammaLabels = new Hashtable<Integer, JLabel>();
		gammaLabels.put(1, new JLabel("0.1", JLabel.CENTER));
		gammaLabels.put(10, new JLabel("1.0", JLabel.CENTER));
		gammaLabels.put(20, new JLabel("2.0", JLabel.CENTER));
		gammaLabels.put(30, new JLabel("3.0", JLabel.CENTER));
		gammaLabels.put(40, new JLabel("4.0", JLabel.CENTER));
		gamma.setLabelTable(gammaLabels);

		// set the size
		gamma.setMinimumSize(sliderDimension);
		gamma.setPreferredSize(sliderDimension);
		gamma.setMaximumSize(sliderDimension);
		gamma.setAlignmentX(Component.CENTER_ALIGNMENT);

		// listen to changes
		gamma.addChangeListener(new ChangeListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * javax.swing.event.ChangeListener#stateChanged(javax.swing.event
			 * .ChangeEvent)
			 */
			@Override
			public void stateChanged(ChangeEvent event) {
				double gamma = RenderFrame.this.gamma.getValue() * 0.1;
				gammaLabel.setText(String.format(Locale.ENGLISH, "Gamma: %2.1f", gamma));

				if (RenderFrame.this.panel != null)
					RenderFrame.this.panel.setGamma(gamma);
			}
		});

		/**********************************************************************
		 * Create the save button
		 *********************************************************************/

		JButton save = new JButton("Save image");
		save.setAlignmentX(Component.CENTER_ALIGNMENT);
		save.addActionListener(new ActionListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ActionListener#actionPerformed(java.awt.event.
			 * ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveImage();
			}
		});

		/**********************************************************************
		 * Construct the panels
		 *********************************************************************/

		JPanel exposurePanel = new JPanel();
		exposurePanel.setLayout(new BoxLayout(exposurePanel, BoxLayout.Y_AXIS));
		exposurePanel.setBorder(BorderFactory.createTitledBorder("Sensitivity"));
		exposurePanel.add(sensitivity);
		exposurePanel.add(exposureLabel);

		JPanel gammaPanel = new JPanel();
		gammaPanel.setLayout(new BoxLayout(gammaPanel, BoxLayout.Y_AXIS));
		gammaPanel.setBorder(BorderFactory.createTitledBorder("Gamma"));
		gammaPanel.add(gamma);
		gammaPanel.add(gammaLabel);

		// create the resulting control panel which has a vertical layout
		JPanel controls = new JPanel();
		controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
		controls.setBorder(border);

		controls.add(exposurePanel);
		controls.add(gammaPanel);
		controls.add(Box.createVerticalGlue());
		controls.add(save);

		controlWrapper.add(controls);

		return controlWrapper;
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
				saveImage();
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
	 * 
	 */
	public void saveImage() {
		JFileChooser chooser = new JFileChooser(new File("."));
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		FileFilter imageFilter = new FileFilter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see javax.swing.filechooser.FileFilter#getDescription()
			 */
			public String getDescription() {
				return "image file (*.png, *.jpeg *.bmp *.gif)";
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
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
					extension = extension.substring(extension.lastIndexOf('.') + 1);
					ImageIO.write(panel.getImage(), extension, file);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			} else {
				StringBuilder builder = new StringBuilder("The given " + "filename:\n\n");
				builder.append(file.getAbsolutePath());
				builder.append(
						"\n\ndoes not have a valid image " + "extension! " + "The supported image extensions are:\n");

				for (String extension : ImageIO.getWriterFileSuffixes())
					builder.append(" " + extension);

				JOptionPane.showMessageDialog(frame, builder.toString());
			}
		}
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
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();

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
			throw new NullPointerException("the graphics device to center this image frame upon!");

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
