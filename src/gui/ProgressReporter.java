package gui;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Utility class for reporting progress of rendering which can be safely shared
 * among threads.
 * 
 * When the quiet flag is set to false, this class will print a progress bar on
 * the command line. The progress bar is written on the same line using the \r
 * character to return to the head of the current output line.
 * 
 * @author Niels Billen
 * @version 0.2
 */
public class ProgressReporter {
	private final String title;
	private final int barLength;
	private final int totalWork;
	private int done = 0;
	private boolean quiet;

	private long startTime = System.currentTimeMillis();
	private ReentrantLock lock = new ReentrantLock();
	private Set<ProgressListener> listeners = new HashSet<ProgressListener>();
	private String plusses = "";
	private String spaces = "";
	private int maximumPrintLength = 0;

	/**
	 * Creates a new {@link ProgressReporter} for a task with the given amount
	 * of work. If the {@link ProgressReporter} is not quiet, a progress bar is
	 * drawn leading with the given title followed by an ASCII progress bar of
	 * the given size.
	 * 
	 * @param title
	 *            title for the progress bar.
	 * @param barLength
	 *            size for the progress bar.
	 * @param totalWork
	 *            total amount of work which needs to be done.
	 * @param quiet
	 *            whether the {@link ProgressReporter} should print or not.
	 */
	public ProgressReporter(String title, int barLength, int totalWork,
			boolean quiet) {
		this.title = title;
		this.barLength = barLength;
		this.totalWork = totalWork;
		this.quiet = quiet;

		// initialize the spaces string
		for (int i = 0; i < barLength; ++i)
			spaces = spaces.concat(" ");
	}

	/**
	 * Adds the given {@link ProgressListener} to this {@link ProgressReporter}.
	 * 
	 * When {@link ProgressReporter#update(int)} is called, all
	 * {@link ProgressListener}s will be notified of the progress.
	 * 
	 * @param listener
	 *            the listener to add to this {@link ProgressReporter}.
	 */
	public void addProgressListener(ProgressListener listener) {
		if (listener != null)
			listeners.add(listener);
	}

	/**
	 * Removes the given {@link ProgressListener} from this
	 * {@link ProgressReporter}.
	 * 
	 * @param listener
	 *            the listener to remove.
	 */
	public void removeProgressListener(ProgressListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Updates this {@link ProgressReporter} with the given amount of work.
	 * 
	 * @param work
	 *            the amount of work which is done.
	 */
	public void update(int work) {
		if (work <= 0)
			return;

		lock.lock();
		done = Math.min(totalWork, done + work);

		// calculate the progress
		double percent = (double) done / (double) totalWork;

		// report to the listeners
		for (ProgressListener listener : listeners)
			listener.update(percent);

		// print the progressbar if not quiet
		if (!quiet) {
			// calculate the required amount of plusses/spaces
			int plussesNeeded = (int) (barLength * percent);
			if (plussesNeeded > plusses.length()) {
				int spacesNeeded = barLength - plussesNeeded;
				for (int i = plusses.length(); i < plussesNeeded; ++i)
					plusses = plusses.concat("+");
				spaces = spaces.substring(0, spacesNeeded);
			}

			// print the progress bar
			System.out.format("\r%s [%s%s] ", title, plusses, spaces);

			// calculate the time
			long time = System.currentTimeMillis() - startTime;
			double remainingTime = (double) time * (1.0 - percent) / percent;

			// print the time string.
			String timeString = String.format(Locale.ENGLISH,
					"(%.2fs | %.2fs)", time * 0.001, remainingTime * 0.001);
			System.out.print(timeString);

			// print trailing spaces to overwrite previous printouts
			int timeStringLength = timeString.length();
			if (timeStringLength < maximumPrintLength)
				for (int i = 0; i < maximumPrintLength - timeStringLength; ++i)
					System.out.print(" ");
			maximumPrintLength = timeStringLength;

		}
		lock.unlock();
	}

	/**
	 * Indicates to this {@link ProgressReporter} that the work is done.
	 */
	public void done() {
		lock.lock();
		done = totalWork;

		for (ProgressListener listener : listeners)
			listener.update(1);

		if (!quiet) {
			if (plusses.length() != barLength)
				for (int i = plusses.length(); i != barLength; ++i)
					plusses = plusses.concat("+");

			System.out.format("\r%s [%s] ", title, plusses);

			// print the time string.
			String timeString = String.format(Locale.ENGLISH, "(%.2fs)",
					(System.currentTimeMillis() - startTime) * 0.001);
			System.out.print(timeString);

			// print trailing spaces to overwrite previous printouts
			int timeStringLength = timeString.length();
			if (timeStringLength < maximumPrintLength)
				for (int i = 0; i < maximumPrintLength - timeStringLength; ++i)
					System.out.print(" ");
			maximumPrintLength = Math.max(maximumPrintLength, timeStringLength);
			System.out.println();
		}
		lock.unlock();
	}
}