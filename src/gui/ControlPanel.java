package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import film.RGBSpectrum;

/**
 * A panel which allows the user to control the gamma and sensitivity of the
 * image shown in the image panel.
 * 
 * @author 	Niels Billen, Matthias Moulin
 * @version 0.3.1
 */
public class ControlPanel extends JPanel {
	
	/**
	 * A unique id required for serialization (required by the Serializable
	 * interface which JPanel implements).
	 */
	private static final long serialVersionUID = 9209414504036141905L;

	/**
	 * Slider to control the sensitivity of the renderframe's image panel.
	 */
	private final JSlider sensitivity = new JSlider();

	/**
	 * Slider to control the gamma panel of the renderframe's image panel.
	 */
	private final JSlider gamma = new JSlider();

	/**
	 * The image panel which is controlled by this panel.
	 */
	private final ImagePanel panel;

	/**
	 * Creates a new control panel which has the given frame as parent.
	 * 
	 * @param frame
	 *            the frame which this control panel has as parent.
	 * @throws NullPointerException
	 *             when the given frame is null.
	 * @throws NullPointerException
	 *             when the given frame's image panel is null.
	 */
	public ControlPanel(final RenderFrame frame) throws NullPointerException {
		if (frame == null)
			throw new NullPointerException("the given frame is null!");
		if (frame.panel == null)
			throw new NullPointerException(
					"the image panel of the given frame is null!");
		this.panel = frame.panel;

		// Create the components
		JPanel sensitivityPanel = createSensitivity();
		JPanel gammaPanel = createGamma();
		JPanel radiancePanel = createRadiancePanel();

		// Specify the layout
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		// Build the constraints
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.PAGE_START;
		c.weightx = 1;
		c.weighty = 0;

		// Add the sensitivity panel
		c.gridy = 0;
		add(sensitivityPanel, c);

		// Add the gamma panel
		c.gridy++;
		add(gammaPanel, c);

		// Add the radiance panel
		c.gridy++;
		add(radiancePanel, c);

		// Add glue
		c.weighty = 1;
		c.gridy++;
		add(new JPanel(), c);

		// Add save image button
		c.weighty = 0;
		c.gridy++;

		JButton save = new JButton("Save image");
		add(save, c);
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
				frame.save();
			}
		});

		// Add a border
		setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 4));

	}

	/**
	 * Create the panel for the sensitivity slider.
	 * 
	 * @return the panel containing the sensitivity slider.
	 */
	private JPanel createSensitivity() {
		final JPanel result = new JPanel();

		// Read the initial sensitivity value
		double value = panel.getSensitivity();
		int sensitivityValue = (int) (Math.log10(value * 10.0));

		// Create the label for the exposure
		final JLabel exposureLabel = new JLabel(String.format(Locale.ENGLISH,
				"Sensitivity: %e", Math.pow(10, sensitivityValue)));

		// Set the thicks for the sensitivity
		sensitivity.setMinorTickSpacing(5);
		sensitivity.setMajorTickSpacing(10);
		sensitivity.setSnapToTicks(false);
		sensitivity.setPaintLabels(true);
		sensitivity.setPaintTicks(true);

		// Set the renge for the slider
		sensitivity.setMinimum(-100);
		sensitivity.setMaximum(100);
		sensitivity.setValue(sensitivityValue);

		// Create the tick labels
		Hashtable<Integer, JLabel> exposureLabels = new Hashtable<Integer, JLabel>();
		exposureLabels.put(-100, new JLabel("1e-10", JLabel.CENTER));
		exposureLabels.put(-50, new JLabel("1e-5", JLabel.CENTER));
		exposureLabels.put(0, new JLabel("1e0", JLabel.CENTER));
		exposureLabels.put(50, new JLabel("1e5", JLabel.CENTER));
		exposureLabels.put(100, new JLabel("1e10", JLabel.CENTER));
		sensitivity.setLabelTable(exposureLabels);

		// Listen for changes to the slider
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
				final double value = Math.pow(10.0,
						sensitivity.getValue() * 0.1);

				SwingUtilities.invokeLater(new Runnable() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see java.lang.Runnable#run()
					 */
					@Override
					public void run() {
						exposureLabel.setText(String.format(Locale.ENGLISH,
								"Sensitivity: %e", value));
					}
				});

				panel.setSensitivity(value);
			}
		});

		// Specify the layout
		GridBagLayout manager = new GridBagLayout();
		result.setLayout(manager);

		// Initialize the constraints
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.ipady = 4;

		// Add the slider
		result.add(sensitivity, c);

		// Add the label
		c.gridy = 1;
		c.fill = GridBagConstraints.NONE;
		result.add(exposureLabel, c);

		// Add a titled border
		TitledBorder border = BorderFactory.createTitledBorder("Sensitivity");
		result.setBorder(border);

		return result;
	}

	/**
	 * Create the panel for the sensitivity slider.
	 * 
	 * @return the panel containing the sensitivity slider.
	 */
	private JPanel createGamma() {
		JPanel result = new JPanel();

		// Read the initial sensitivity value
		double value = panel.getGamma();
		int gammaValue = (int) (value * 10.0);

		// Create the label for the gamma
		final JLabel gammaLabel = new JLabel(String.format(Locale.ENGLISH,
				"Gamma: %2.1f", gammaValue * 0.1));

		// Set the thicks for the sensitivity
		gamma.setMinorTickSpacing(1);
		gamma.setMajorTickSpacing(5);
		gamma.setSnapToTicks(true);
		gamma.setPaintLabels(true);
		gamma.setPaintTicks(true);

		// Set the renge for the slider
		gamma.setMinimum(1);
		gamma.setMaximum(40);
		gamma.setValue(gammaValue);

		// Create the tick labels
		Hashtable<Integer, JLabel> gammaLabels = new Hashtable<Integer, JLabel>();
		gammaLabels.put( 1, new JLabel("0.1", JLabel.CENTER));
		gammaLabels.put(10, new JLabel("1.0", JLabel.CENTER));
		gammaLabels.put(20, new JLabel("2.0", JLabel.CENTER));
		gammaLabels.put(30, new JLabel("3.0", JLabel.CENTER));
		gammaLabels.put(40, new JLabel("4.0", JLabel.CENTER));
		gamma.setLabelTable(gammaLabels);

		// Listen for changes to the slider
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
				final double gamma = ControlPanel.this.gamma.getValue() * 0.1;

				SwingUtilities.invokeLater(new Runnable() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see java.lang.Runnable#run()
					 */
					@Override
					public void run() {
						gammaLabel.setText(String.format(Locale.ENGLISH,
								"Gamma: %2.1f", gamma));
					}
				});

				if (panel != null)
					panel.setGamma(gamma);
			}
		});

		// Specify the layout
		GridBagLayout manager = new GridBagLayout();
		result.setLayout(manager);

		// Initialize the constraints
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.ipady = 4;

		// Add the slider
		result.add(gamma, c);

		// Add the label
		c.gridy = 1;
		c.fill = GridBagConstraints.NONE;
		result.add(gammaLabel, c);

		// Add a titled border
		TitledBorder border = BorderFactory.createTitledBorder("Gamma");
		result.setBorder(border);

		return result;
	}

	/**
	 * Creates a panel which shows the amount of radiance at the mouse position
	 * in the image panel.
	 * 
	 * @return a panel which shows the amount of radiance at the mouse position
	 *         in the image panel.
	 */
	private JPanel createRadiancePanel() {
		JPanel result = new JPanel();

		// Specify the layout
		GridBagLayout manager = new GridBagLayout();
		result.setLayout(manager);

		// Initialize the constraints
		GridBagConstraints labels = new GridBagConstraints();
		labels.weightx = 0;
		labels.weighty = 1;
		labels.gridx = 0;
		labels.ipadx = 4;
		labels.ipady = 4;
		labels.anchor = GridBagConstraints.LINE_START;

		GridBagConstraints values = new GridBagConstraints();
		values.weightx = 1;
		values.weighty = 1;
		values.gridx = 1;
		values.ipadx = 4;
		values.ipady = 4;
		values.anchor = GridBagConstraints.LINE_END;

		// Create the labels
		JLabel redLabel   = new JLabel("Red:");
		JLabel greenLabel = new JLabel("Green:");
		JLabel blueLabel  = new JLabel("Blue:");

		// Create the values
		final JLabel red   = new JLabel("<html>? W/(m<sup><small>2</small></sup>sr)</html>");
		final JLabel green = new JLabel("<html>? W/(m<sup><small>2</small></sup>sr)</html>");
		final JLabel blue  = new JLabel("<html>? W/(m<sup><small>2</small></sup>sr)</html>");

		// Add the components
		labels.gridy = 0;
		values.gridy = 0;
		result.add(redLabel, labels);
		result.add(red, values);

		labels.gridy = 1;
		values.gridy = 1;
		result.add(greenLabel, labels);
		result.add(green, values);

		labels.gridy = 2;
		values.gridy = 2;
		result.add(blueLabel, labels);
		result.add(blue, values);

		panel.addListener(new ImagePanelListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * gui.ImagePanelListener#spectrumAtMouseChanged(film.RGBSpectrum)
			 */
			@Override
			public void spectrumAtMouseChanged(final RGBSpectrum s) {
				SwingUtilities.invokeLater(new Runnable() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see java.lang.Runnable#run()
					 */
					@Override
					public void run() {
						if (s == null) {
							String text = "<html>? W/(m<sup><small>2</small></sup>sr)</html>";
							red.setText(text);
							green.setText(text);
							blue.setText(text);
						} else {
							String redText  = String
									.format("<html>%e W/(m<sup><small>2</small></sup>sr)</html>", 
											s.red);
							String greenText = String
									.format("<html>%e W/(m<sup><small>2</small></sup>sr)</html>", 
											s.green);
							String blueText  = String
									.format("<html>%e W/(m<sup><small>2</small></sup>sr)</html>", 
											s.blue);
							red.setText(redText);
							green.setText(greenText);
							blue.setText(blueText);
						}
					}
				});
			}
		});

		// Add a titled border
		TitledBorder border = BorderFactory.createTitledBorder("Radiance");
		result.setBorder(border);

		// Set a tooltip
		result.setToolTipText("The amount of radiance in the image "
				+ "at the mouse position.");
		return result;
	}
}
