package com.intuso.utilities.concurrent.taskscheduling.schedulers;

import com.intuso.utilities.concurrent.taskscheduling.Task;
import com.intuso.utilities.concurrent.taskscheduling.TaskScheduler;
import com.intuso.utilities.io.CommentedTextFileReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Class that provides priority based scheduling of tasks. The number of different priorities
 * and workers are described in a text properties file of the following format (everything
 * between the 2 dashed lines)
 * ----------------
 * num_priority_levels=n
 * workers num=a max_priority=b
 * workers ...
 * etc
 * ----------------
 * where n is the number of priority levels we should use, for each worker line a is the number
 * of those workers to create and b is the maximum priority task that they can work on. By
 * specifying maximum priority we can ensure that we don't starve the lower priority queues of
 * workers if we don't want to. A value of b <= 0 means any priority. Comments start with \#
 * @author tom
 *
 */
public class PriorityScheduler extends TaskScheduler {
	
	/**
	 * Whether to run in debug mode or not
	 */
	private final static boolean DEBUG = false;
	
	/**
	 * A description of the format of the props file
	 */
	private static final String PROPS_DESCRIPTION = "\nShould be of the form\nnum_priority_levels=n\nworkers num_workers=a max_priority=b\nworkers ...\netc";
	
	/**
	 * The queues, 1 for each priority level
	 */
	private List<Queue<Task>> m_queues;
	
	/**
	 * A map of task to priority levels
	 */
	private Map<Task, Integer> m_task_priorities;
	
	/**
	 * A list of workers
	 */
	private List<Worker> m_workers;
	
	/**
	 * A lock to prevent concurrent access to the queues and priority map
	 */
	private Object m_lock = new Object();
	
	/**
	 * Create a new PriorityScheduler
	 * @param props_file - the file to load the properties from
	 * @throws FileNotFoundException - if props file is not found
	 * @throws InvalidPropertiesFormatException - if props file is invalid 
	 * @throws IOException - if the underlying CommentedTextFileReader throws it
	 */
	public PriorityScheduler(String props_file) throws FileNotFoundException, InvalidPropertiesFormatException, IOException {
		debug("Creating a PriorityScheduler");
		CommentedTextFileReader in = new CommentedTextFileReader(props_file, "#");
		
		debug("Opened file");
		// read num priority levels and create a queue for each level
		String line = in.readUsefulLine();
		if(!line.startsWith("num_priority_levels="))
			throw new InvalidPropertiesFormatException("Invalid first line: " + line + PROPS_DESCRIPTION);
		m_queues = new ArrayList<Queue<Task>>();
		for(int i = Integer.parseInt(line.substring(new String("num_priority_levels=").length())); i > 0; i--)
			m_queues.add(new ConcurrentLinkedQueue<Task>());
		
		// create priority map
		m_task_priorities = new HashMap<Task, Integer>();
		
		debug("First line ok - " + m_queues.size() + " priority levels");
		// create all the workers
		m_workers = new ArrayList<Worker>();
		String parts[], keyval[];
		int num_workers, max_priority;
		while((line = in.readLine()) != null) {
			
			// check starts with "workers" and only has 2 spaces
			if(!line.startsWith("workers"))
				throw new InvalidPropertiesFormatException("Invalid workers line - doesn't start with \"workers\": " + line + PROPS_DESCRIPTION);
			parts = line.split(" ");
			if(parts.length != 3)
				throw new InvalidPropertiesFormatException("Invalid workers line - contains > 2 spaces: " + line + PROPS_DESCRIPTION);
			
			// get num_workers
			keyval = parts[1].split("=");
			if(keyval.length != 2 || !keyval[0].equals("num_workers"))
				throw new InvalidPropertiesFormatException("Invalid workers line - num_workers is not first keyval or contains > 1 \"=\": " + line + PROPS_DESCRIPTION);
			num_workers = Integer.parseInt(keyval[1]);
			
			// get max_priority
			keyval = parts[2].split("=");
			if(keyval.length != 2 || !keyval[0].equals("max_priority"))
				throw new InvalidPropertiesFormatException("Invalid workers line - max_priority is not first keyval or contains > 1 \"=\": " + line + PROPS_DESCRIPTION);
			max_priority = Integer.parseInt(keyval[1]) - 1;
			
			debug("Creating " + num_workers + " workers at max priority of " + max_priority);
			// create the workers for this line
			for(int i = 0; i < num_workers; i++)
				m_workers.add(new Worker(max_priority));
		}
		
		debug("Starting a total of " + m_workers.size() + " workers");
		// start all the workers
		for(Worker w : m_workers)
			w.start();
	}
	
	/**
	 * Shutdown the scheduler
	 */
	public void shutdown() {
		debug("Shutting down PriorityScheduler");
		for(Worker w : m_workers)
			w.shutdown();
	}

	@Override
	protected void queue(Task task) {
		// obtain lock
		synchronized(m_lock) {
			
			// add it to the lowest priority queue
			m_queues.get(0).add(task);
			m_task_priorities.put(task, 0);
		}
	}

	/**
	 * Increase the priority of a task
	 * @param task - the task to increase the priority of
	 */
	public void increasePriority(Task task) {
		// if it's not queued then we can't increase priority (it's either running already or is complete)
		if(task.getStatus() == Task.Status.Queued) {
			// obtain lock
			synchronized(m_lock) {
				// get the current priority
				Integer priority = m_task_priorities.get(task);
				// if it's not already top priority then increase it
				if(priority < m_queues.size() - 1) {
					m_queues.get(priority).remove(task);
					m_queues.get(priority + 1).add(task);
					m_task_priorities.put(task, priority + 1);
				}
			}
		}
	}
	
	/**
	 * Used internally by the workers to get their next task
	 * @param max_priority - the maximum priority task the worker can do
	 * @return the next task that it should complete
	 */
	private Task getTask(int max_priority) {
		// if max_priority is < 0 or greater than num priority levels, set it to max, else as specified
		int priority = (max_priority < 0 || max_priority >= m_queues.size()) ? m_queues.size() - 1 : max_priority;
		while(!Thread.currentThread().isInterrupted()) {
			// get the lock
			synchronized(m_lock) {
				// for each priority level down to the lowest, starting at the max we can do
				for(; priority >= 0; priority--) {
					// if the queue is not empty return the task
					if(!m_queues.get(priority).isEmpty()) {
						Task task = m_queues.get(priority).remove();
						m_task_priorities.remove(task);
						return task;
					}
				}
			}
			
			// wait to prevent hogging
			try {
				Thread.sleep(10);
			}
			catch(InterruptedException e) {
				break;
			}
		}
		
		return null;
	}
	
	/**
	 * Print a debug message
	 * @param message the message to print
	 */
	private static void debug(String message) {
		if(DEBUG)
			System.out.println(message);
	}
	
	/**
	 * Internal class representing a "worker" - runs as a separate Thread
	 * @author tclabon
	 *
	 */
	private class Worker extends Thread {
		/**
		 * The maximum priority that this worker is allowed to work to
		 */
		private int m_max_priority;
		
		/**
		 * Set to shutdown this worker once it has finished the current task
		 */
		private boolean m_shutdown;
		
		/**
		 * Create a new worker
		 * @param max_priority - the maximum priority that the worker can work at
		 */
		public Worker(int max_priority) {
			super();
			m_max_priority = max_priority;
			m_shutdown = false;
			debug("Worker created with max priority of " + m_max_priority);
		}
		
		/**
		 * Call to shutdown the worker once it has finished it's current task
		 */
		public void shutdown() {
			debug("Shutting down worker");
			m_shutdown = true;
		}
		
		@Override
		public void run() {
			debug("Worker started");
			// while not waiting to shutdown
			while(!m_shutdown) {
				debug("Worker requesting work at max priority of " + m_max_priority);
				// get the next task of the highest priority we're allowed to run and complete it
				getTask(m_max_priority).complete();
				
				// wait to prevent hogging
				try {
					Thread.sleep(10);
				}
				catch(InterruptedException e) {
					break;
				}
			}
			debug("Worker stopped");
		}
	}
}
