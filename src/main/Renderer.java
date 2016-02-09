package main;

import gui.ImagePanel;
import gui.ProgressReporter;
import gui.RenderFrame;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;
import sampling.Sample;
import shape.Shape;
import shape.Sphere;
import camera.PerspectiveCamera;

/**
 * Entry point of your renderer.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class Renderer {
	/**
	 * Entry point of your renderer.
	 * 
	 * @param arguments
	 *            command line arguments.
	 */
	public static void main(String[] arguments) {
		int width = 640;
		int height = 640;

		// parse the command line arguments
		for (int i = 0; i < arguments.length; ++i) {
			if (arguments[i].startsWith("-")) {
				try {
					if (arguments[i].equals("-width"))
						width = Integer.parseInt(arguments[++i]);
					else if (arguments[i].equals("-height"))
						height = Integer.parseInt(arguments[++i]);
					else if (arguments[i].equals("-help")) {
						System.out.println("usage: "
								+ "[-width  width of the image] "
								+ "[-height  height of the image]");
						return;
					} else {
						System.err.format("unknown flag \"%s\" encountered!\n",
								arguments[i]);
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					System.err.format("could not find a value for "
							+ "flag \"%s\"\n!", arguments[i]);
				}
			} else
				System.err.format("unknown value \"%s\" encountered! "
						+ "This will be skipped!\n", arguments[i]);
		}

		// validate the input
		if (width <= 0)
			throw new IllegalArgumentException("the given width cannot be "
					+ "smaller than or equal to zero!");
		if (height <= 0)
			throw new IllegalArgumentException("the given height cannot be "
					+ "smaller than or equal to zero!");

		// initialize the camera
		PerspectiveCamera camera = new PerspectiveCamera(width, height,
				new Point(), new Vector(0, 0, 1), new Vector(0, 1, 0), 90);

		// initialize the graphical user interface
		ImagePanel panel = new ImagePanel(width, height);
		RenderFrame frame = new RenderFrame("Sphere", panel);

		// initialize the progress reporter
		ProgressReporter reporter = new ProgressReporter("Rendering", 40, width
				* height, false);
		reporter.addProgressListener(frame);
		
		// initialize the scene
		Transformation t1 = Transformation.createTranslation(0, 0, 10);
		Transformation t2 = Transformation.createTranslation(4, -4, 12);
		Transformation t3 = Transformation.createTranslation(-4, -4, 12);
		Transformation t4 = Transformation.createTranslation(4, 4, 12);
		Transformation t5 = Transformation.createTranslation(-4, 4, 12);

		List<Shape> shapes = new ArrayList<Shape>();
		shapes.add(new Sphere(t1, 5));
		shapes.add(new Sphere(t2, 4));
		shapes.add(new Sphere(t3, 4));
		shapes.add(new Sphere(t4, 4));
		shapes.add(new Sphere(t5, 4));

		// render the scene
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// create a ray through the center of the pixel.
				Ray ray = camera.generateRay(new Sample(x + 0.5, y + 0.5));

				boolean hit = false;
				for (Shape shape : shapes)
					if (shape.intersect(ray)) {
						hit = true;
						break;
					}
				panel.set(x, y, 255, hit ? 255 : 0, 0, 0);
			}
			reporter.update(height);
		}
		reporter.done();

		// save the output
		try {
			ImageIO.write(panel.getImage(), "png", new File("output.png"));
		} catch (IOException e) {
		}
	}
}
