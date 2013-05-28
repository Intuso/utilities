package com.intuso.utilities.concurrent.statemanager;

/**
 * Manager to control access to a resource based on the type of access required
 * @author tclabon
 *
 * @param <T> the possible states
 */
public abstract class StateManager<T extends Enum<T>> {
	
	/**
	 * The current state
	 */
	private Enum<T> m_current_state;
	
	/**
	 * object to use as a lock
	 */
	private final Object m_lock;
	
	/**
	 * The number of thread currently running
	 */
	private int m_threads_running;
	
	/**
	 * Create a new state manager
	 */
	public StateManager() {
		m_current_state = null;
		m_threads_running = 0;
		m_lock = new Object();
	}
	
	/**
	 * Request the manager to be in a certain state - blocking call!
	 * @param state
	 * @throws InterruptedException 
	 */
	public final void requestState(Enum<T> state) throws InterruptedException {
		synchronized(m_lock) {
			if(m_current_state == null)
				m_current_state = state;
			else if(m_current_state != state) {
				addStateRequest(state);
				while(m_current_state != state)
					m_lock.wait();
			}
			m_threads_running++;
		}
	}
	
	/**
	 * Notification that the current thread has finished with a state
	 */
	public final void finished() {
		synchronized(m_lock) {
			m_threads_running--;
			if(m_threads_running == 0) {
				m_current_state = pickNextState();
				m_lock.notifyAll();
			}
		}
	}
	
	/**
	 * add a request to be in a certain state
	 * @param state the state to request
	 */
	protected abstract void addStateRequest(Enum<T> state);
	
	/**
	 * Pick the next state to be in
	 * @return the next state to be in
	 */
	protected abstract Enum<T> pickNextState();
}
