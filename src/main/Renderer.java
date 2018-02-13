package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;
import sampling.Sample;
import shape.Shape;
import shape.Sphere;
import camera.PerspectiveCamera;
import film.FrameBuffer;
import film.Tile;
import gui.ProgressReporter;
import gui.RenderFrame;

/**
 * Entry point of your renderer.
 * 
 * @author 	CGRG
 * @version 4.0.0
 */
public class Renderer {
	
	/**
	 * Entry point of your renderer.
	 * 
	 * @param arguments
	 *            command line arguments.
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	public static void main(String[] arguments) {
		int width = 640;
		int height = 640;
		double sensitivity = 1.0;
		double gamma = 2.2;
		boolean gui = true;
		boolean quiet = false;
		Point origin = new Point(0, 0, 0);
		Point destination = new Point(0, 0, -1);
		Vector lookup = new Vector(0, 1, 0);
		double fov = 90;
		String filename = "output.png";

		/**********************************************************************
		 * Parse the command line arguments
		 *********************************************************************/

		for (int i = 0; i < arguments.length; ++i) {
			if (arguments[i].startsWith("-")) {
				String flag = arguments[i];

				try {
					if (flag.equals("-width"))
						width = Integer.parseInt(arguments[++i]);
					else if (flag.equals("-height"))
						height = Integer.parseInt(arguments[++i]);
					else if (flag.equals("-gui"))
						gui = Boolean.parseBoolean(arguments[++i]);
					else if (flag.equals("-quiet"))
						quiet = Boolean.parseBoolean(arguments[++i]);
					else if (flag.equals("-sensitivity"))
						sensitivity = Double.parseDouble(arguments[++i]);
					else if (flag.equals("-gamma"))
						gamma = Double.parseDouble(arguments[++i]);
					else if (flag.equals("-origin")) {
						double x = Double.parseDouble(arguments[++i]);
						double y = Double.parseDouble(arguments[++i]);
						double z = Double.parseDouble(arguments[++i]);
						origin = new Point(x, y, z);
					} else if (flag.equals("-destination")) {
						double x = Double.parseDouble(arguments[++i]);
						double y = Double.parseDouble(arguments[++i]);
						double z = Double.parseDouble(arguments[++i]);
						destination = new Point(x, y, z);
					} else if (flag.equals("-lookup")) {
						double x = Double.parseDouble(arguments[++i]);
						double y = Double.parseDouble(arguments[++i]);
						double z = Double.parseDouble(arguments[++i]);
						lookup = new Vector(x, y, z);
					} else if (flag.equals("-fov")) {
						fov = Double.parseDouble(arguments[++i]);
					} else if (flag.equals("-output")) {
						filename = arguments[++i];
					} else if (flag.equals("-help")) {
						System.out
								.println("usage: java -jar cgpracticum.jar\n"
										+ "  -width <integer>      width of the image\n"
										+ "  -height <integer>     height of the image\n"
										+ "  -sensitivity <double> scaling factor for the radiance\n"
										+ "  -gamma <double>       gamma correction factor\n"
										+ "  -origin <point>       origin for the camera\n"
										+ "  -destination <point>  destination for the camera\n"
										+ "  -lookup <vector>      up direction for the camera\n"
										+ "  -output <string>      filename for the image\n"
										+ "  -gui <boolean>        whether to start a graphical user interface\n"
										+ "  -quiet <boolean>      whether to print the progress bar");
						return;
					} else {
						System.err.format("unknown flag \"%s\" encountered!\n",
								flag);
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					System.err.format("could not find a value for "
							+ "flag \"%s\"\n!", flag);
				}
			} else
				System.err.format("unknown value \"%s\" encountered! "
						+ "This will be skipped!\n", arguments[i]);
		}

		/**********************************************************************
		 * Validate the input
		 *********************************************************************/

		if (width <= 0)
			throw new IllegalArgumentException("the given width cannot be "
					+ "smaller than or equal to zero!");
		if (height <= 0)
			throw new IllegalArgumentException("the given height cannot be "
					+ "smaller than or equal to zero!");
		if (gamma <= 0)
			throw new IllegalArgumentException("the gamma cannot be "
					+ "smaller than or equal to zero!");
		if (sensitivity <= 0)
			throw new IllegalArgumentException("the sensitivity cannot be "
					+ "smaller than or equal to zero!");
		if (fov <= 0)
			throw new IllegalArgumentException("the field of view cannot be "
					+ "smaller than or equal to zero!");
		if (fov >= 180)
			throw new IllegalArgumentException("the field of view cannot be "
					+ "larger than or equal to 180!");
		if (filename.isEmpty())
			throw new IllegalArgumentException("the filename cannot be the "
					+ "empty string!");

		/**********************************************************************
		 * Initialize the camera and graphical user interface
		 *********************************************************************/

		final PerspectiveCamera camera = new PerspectiveCamera(width, height,
				origin, destination, lookup, fov);

		// initialize the frame buffer
		final FrameBuffer buffer = new FrameBuffer(width, height);

		// initialize the progress reporter
		final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
				width * height, quiet);

		// initialize the graphical user interface
		RenderFrame userinterface;
		if (gui) {
			try {
				userinterface = RenderFrame.buildRenderFrame(buffer, gamma,
						sensitivity);
				reporter.addProgressListener(userinterface);
			} catch (Exception e) {
				userinterface = null;
			}
		} else
			userinterface = null;

		final RenderFrame frame = userinterface;

		/**********************************************************************
		 * Initialize the scene
		 *********************************************************************/

		Transformation t1 = Transformation.translate(0, 0, -10).append(
				Transformation.scale(5, 5, 5));
		Transformation t2 = Transformation.translate(4, -4, -12).append(
				Transformation.scale(4, 4, 4));
		Transformation t3 = Transformation.translate(-4, -4, -12).append(
				Transformation.scale(4, 4, 4));
		Transformation t4 = Transformation.translate(4, 4, -12).append(
				Transformation.scale(4, 4, 4));
		Transformation t5 = Transformation.translate(-4, 4, -12).append(
				Transformation.scale(4, 4, 4));

		final List<Shape> shapes = new ArrayList<Shape>();
		shapes.add(new Sphere(t1));
		shapes.add(new Sphere(t2));
		shapes.add(new Sphere(t3));
		shapes.add(new Sphere(t4));
		shapes.add(new Sphere(t5));

		/**********************************************************************
		 * Multi-threaded rendering of the scene
		 *********************************************************************/

		final ExecutorService service = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors());

		// subdivide the buffer in equal sized tiles
		for (final Tile tile : buffer.subdivide(64, 64)) {
			// create a thread which renders the specific tile
			Thread thread = new Thread() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Thread#run()
				 */
				@Override
				public void run() {
					try {
						// iterate over the contents of the tile
						for (int y = tile.yStart; y < tile.yEnd; ++y) {
							for (int x = tile.xStart; x < tile.xEnd; ++x) {
								// create a ray through the center of the
								// pixel.
								Ray ray = camera.generateRay(new Sample(
										x + 0.5, y + 0.5));

								// test the scene on intersections
								boolean hit = false;
								for (Shape shape : shapes)
									if (shape.intersect(ray)) {
										hit = true;
										break;
									}

								// add a color contribution to the pixel
								if (hit)
									buffer.getPixel(x, y).add(1, 0, 0);
								else
									buffer.getPixel(x, y).add(0, 0, 0);
							}
						}

						// update the graphical user interface
						if (frame != null)
							frame.panel.finished(tile);
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(1);
					} catch (StackOverflowError e) {
						e.printStackTrace();
						System.exit(1);
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
						System.exit(1);
					}

					// update the progress reporter
					reporter.update(tile.getWidth() * tile.getHeight());

				}
			};
			service.submit(thread);
		}

		// signal the reporter that rendering has started
		reporter.start();

		// execute the threads
		service.shutdown();

		// wait until the threads have finished
		try {
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// signal the reporter that the task is done
		reporter.done();

		/**********************************************************************
		 * Export the result
		 *********************************************************************/

		BufferedImage result = buffer.toBufferedImage(sensitivity, gamma);
		try {
			ImageIO.write(result, "png", new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
