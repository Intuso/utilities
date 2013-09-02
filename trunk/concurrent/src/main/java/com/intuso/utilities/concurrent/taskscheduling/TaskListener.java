package com.intuso.utilities.concurrent.taskscheduling;

/**
 * Interface for classes to implement if they are interested in the progress of tasks
 * @author tclabon
 *
 */
public interface TaskListener {
	/**
	 * Called as a task starts
	 * @param task - the task that has started
	 */
	public void taskStarted(Task task);
	
	/**
	 * Called when a task has completed
	 * @param task - the task that completed
	 */
	public void taskCompleted(Task task);
}
