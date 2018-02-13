package gui;

/**
 * A listener interface for all objects that which to listen to progress on a
 * computation task.
 * 
 * @author 	CGRG
 * @version 4.0.0
 */
public interface ProgressListener {
	
	/**
	 * Called when a task has progressed up to the given percentage of progress.
	 * 
	 * @param progress
	 *            the amount of progress on a task (between 0 and 1).
	 */
	public void update(double progress);

	/**
	 * Called when a task has been finished.
	 */
	public void finished();
}
