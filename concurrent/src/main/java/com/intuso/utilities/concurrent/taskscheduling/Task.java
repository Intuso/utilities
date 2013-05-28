package com.intuso.utilities.concurrent.taskscheduling;

import java.util.Date;

/**
 * Class to abstractly represent a task
 * @author tclabon
 *
 */
public abstract class Task {
	/**
	 * Enum of all possible states that the task can be in
	 * @author tclabon
	 *
	 */
	public enum Status {
		/**
		 * The task is currently queued
		 */
		Queued,
		
		/**
		 * The task is currently running
		 */
		Running,
		
		/**
		 * The task is complete
		 */
		Complete
	}
	
	/**
	 * The tasks current state
	 */
	private Status m_status;
	
	/**
	 * The scheduler that will run the task
	 */
	private TaskScheduler m_scheduler;
	
	/**
	 * The time that the task was created
	 */
	private long m_created_time;
	
	/**
	 * The duration that the task ran for
	 */
	private long m_duration;
	
	/**
	 * Create a new task that will run on the scheduler
	 * @param scheduler
	 */
	public Task(TaskScheduler scheduler) {
		m_scheduler = scheduler;
		m_status = Status.Queued;
		m_created_time = new Date().getTime();
		m_duration = -1;
		m_scheduler.queue(this);
	}
	
	/**
	 * Complete the task
	 */
	public final void complete() {
		// note start time, set status and notify scheduler that we're running
		long start = new Date().getTime();
		m_status = Status.Running;
		m_scheduler.taskStarted(this);
		
		// do the task
		execute();
		
		// note the duration, set the status and notify that we're complete 
		m_duration = new Date().getTime() - start;
		m_status = Status.Complete;
		m_scheduler.taskCompleted(this);
	}
	
	/**
	 * Get the current status of the task
	 * @return - the current status of the task
	 */
	public Status getStatus() { return m_status; }
	
	/**
	 * Get the time when the task was created
	 * @return - the time when the task was created
	 */
	public long getCreatedTime() { return m_created_time; }
	
	/**
	 * Get the duration that the task ran for
	 * @return - the duration 
	 */
	public long getDuration() { return m_duration; }

	/**
	 * Execute the task
	 */
	protected abstract void execute();
}
