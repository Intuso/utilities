package com.intuso.utilities.concurrent.taskscheduling;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class that all task schedulers extend
 * @author tclabon
 *
 */
public abstract class TaskScheduler {
	/**
	 * The list of listeners
	 */
	private List<TaskListener> m_listeners;
	
	/**
	 * Default constructor
	 */
	protected TaskScheduler() {
		m_listeners = new ArrayList<TaskListener>();
	}
	
	/**
	 * Add a listener
	 * @param listener - the listener to add
	 */
	public void addListener(TaskListener listener) {
		m_listeners.add(listener);
	}
	
	/**
	 * Remove a listener
	 * @param listener - the listened to remove
	 */
	public void removeListener(TaskListener listener) {
		m_listeners.add(listener);
	}
	
	/**
	 * Queue a task
	 * @param task - the task to queue
	 */
	protected abstract void queue(Task task);
	
	/**
	 * Called by a task when it has been started by the extended TaskScheduler
	 * @param task - the task that has started
	 */
	protected final void taskStarted(Task task) {
		for(TaskListener l : m_listeners)
			l.taskStarted(task);
	}
	
	/**
	 * Called by a task when it has completed
	 * @param task
	 */
	protected final void taskCompleted(Task task) {
		for(TaskListener l : m_listeners) 
			l.taskCompleted(task);
	}
}
