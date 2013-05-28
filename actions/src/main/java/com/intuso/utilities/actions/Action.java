package com.intuso.utilities.actions;

import java.io.Serializable;

/**
 * Class that all actions should extend
 * @author tclabon
 *
 */
public abstract class Action implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3693122503529595717L;
	
	/**
	 * The exception that was thrown when performing the action
	 */
	private Exception m_error = null;
	
	/**
	 * Get the error thrown when performing the action
	 * @return the error thrown when performing the action
	 */
	public final Exception getError() { return m_error; }
	
	/**
	 * Set the error that was thrown while performing the action
	 * @param error
	 */
	public final void setException(Exception error) {
		m_error = error;
	}
}
