package com.intuso.utilities.log;

/**
 * Base class for all log writers
 * @author tclabon
 *
 */
public abstract class LogWriter {
	
	/**
	 * The level to filter messages at
	 */
	private LogLevel level;
	
	/**
	 * Create a new log writer
	 * @param level the level to filter at
	 */
	protected LogWriter(LogLevel level) {
		this.level = level;
	}
	
	/**
	 * Get the level this writer will log at
	 * @return the level this writer will log at
	 */
	public final LogLevel getLevel() {
		return level;
	}
	
	/**
	 * Write a log message
	 * @param level the level of the log message
	 * @param message the message to write
	 */
	public final void write(LogLevel level, String message) {
		if(this.level.shouldLog(level))
			_write(level, message);
	}
	
	/**
	 * Actually write the log message
	 * @param level the level of the message
	 * @param message the message
	 */
	protected abstract void _write(LogLevel level, String message);
}