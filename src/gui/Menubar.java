package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * Implementation of the menu bar of the graphical user interface.
 * 
 * @author 	CGRG
 * @version 4.0.0
 */
public class Menubar extends JMenuBar {
	
	/**
	 * A unique id required for serialization (required by the Serializable
	 * interface which JMenuBar implements).
	 */
	private static final long serialVersionUID = 7902340495914576436L;

	/**
	 * Creates a new menu bar which has the given frame as parent.
	 * 
	 * @param frame
	 *            the parent.
	 * @throws NullPointerException
	 *             when the given frame is null.
	 */
	public Menubar(final RenderFrame frame) throws NullPointerException {
		if (frame == null)
			throw new NullPointerException("the given frame is null!");

		// Creates the "File" menu
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);

		// Create the "save image" menu item
		JMenuItem saveImage = new JMenuItem("Save image as...");
		KeyStroke controlShiftS = KeyStroke.getKeyStroke("control S");
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
				frame.save();
			}
		});

		// Creates the "close" menu item
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
				frame.dispose();
			}
		});

		fileMenu.add(saveImage);
		fileMenu.add(close);

		// Create the "View" menu
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);

		// Create the "center" menu item
		JMenuItem center = new JMenuItem("Center image");
		center.setAccelerator(KeyStroke.getKeyStroke("C"));
		center.addActionListener(new ActionListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ActionListener#actionPerformed(java.awt.event.
			 * ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.panel.center();
			}
		});

		// Create the "center" menu item
		JMenuItem fit = new JMenuItem("Best fit");
		fit.setAccelerator(KeyStroke.getKeyStroke("F"));
		fit.addActionListener(new ActionListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ActionListener#actionPerformed(java.awt.event.
			 * ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.panel.fit();
			}
		});

		// Create the "zoom" menu item
		JMenuItem zoom = new JMenuItem("Zoom 100%");
		zoom.setAccelerator(KeyStroke.getKeyStroke("O"));
		zoom.addActionListener(new ActionListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ActionListener#actionPerformed(java.awt.event.
			 * ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.panel.setZoom(1);
				frame.panel.repaint();
			}
		});

		// Create the "zoom in" menu item
		JMenuItem zoomIn = new JMenuItem("Zoom in");
		zoomIn.setAccelerator(KeyStroke.getKeyStroke('+'));
		zoomIn.addActionListener(new ActionListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ActionListener#actionPerformed(java.awt.event.
			 * ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.panel.setZoom(frame.panel.getZoom() * 1.1);
				frame.panel.repaint();
			}
		});

		// Create the "zoom out" menu item
		JMenuItem zoomOut = new JMenuItem("Zoom out");
		zoomOut.setAccelerator(KeyStroke.getKeyStroke('-'));
		zoomOut.addActionListener(new ActionListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ActionListener#actionPerformed(java.awt.event.
			 * ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.panel.setZoom(frame.panel.getZoom() / 1.1);
				frame.panel.repaint();
			}
		});

		viewMenu.add(center);
		viewMenu.add(fit);
		viewMenu.add(zoom);
		viewMenu.add(zoomIn);
		viewMenu.add(zoomOut);

		// Add the menus
		add(fileMenu);
		add(viewMenu);
	}
}
