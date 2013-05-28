package com.intuso.utilities.concurrent.statemanager;

import java.util.LinkedList;
import java.util.Queue;

/**
 * State manager that queues state requests
 * @author tclabon
 *
 * @param <T>
 */
public class QueueStateManager<T extends Enum<T>> extends StateManager<T> {

	/**
	 * The requested states queue
	 */
	private final Queue<Enum<T>> m_queue;
	
	/**
	 * Create a new queued state manager
	 */
	public QueueStateManager() {
		super();
		m_queue = new LinkedList<Enum<T>>();
	}
	
	@Override
	public void addStateRequest(Enum<T> state) {
		if(!m_queue.contains(state))
			m_queue.add(state);
	}

	@Override
	public Enum<T> pickNextState() {
		return (m_queue.size() > 0 ? m_queue.remove() : null);
	}
}
