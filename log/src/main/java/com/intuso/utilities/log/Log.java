package com.intuso.utilities.log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Log manager
 * @author tclabon
 *
 */
public class Log {

	/**
	 * List of message writers
	 */
	private Set<LogWriter> writers;
	
	/**
	 * The minimum level of a writer
	 */
	private LogLevel minLevel;
	
	/**
	 * Create a new log manager
	 */
	public Log() {
		this(new HashSet<LogWriter>());
	}

    /**
     * Create a new log manager
     */
    public Log(LogWriter ... writers) {
        this(new HashSet<LogWriter>(Arrays.asList(writers)));
    }
	
	/**
	 * Create a new log manager
	 * @param writers writers to write messages to
	 */
	public Log(Set<LogWriter> writers) {
		this.writers = writers;
		minLevel = LogLevel.INFO;
		for(LogWriter writer : this.writers)
			if(writer.getLevel().isLowerThan(minLevel))
				minLevel = writer.getLevel();
	}
	
	/**
	 * Add a single writer
	 * @param writer writer to add
	 */
	public final void addWriter(LogWriter writer) {
		writers.add(writer);
		if(writer.getLevel().isLowerThan(minLevel))
			minLevel = writer.getLevel();
	}
	
	/**
	 * Add writers
	 * @param writers writers to add
	 */
	public final void addWriters(List<LogWriter> writers) {
		for(LogWriter writer : writers)
			addWriter(writer);
	}
	
	/**
	 * Write a log message
	 * @param level the level of the message
	 * @param message the message to log
	 */
    @SuppressWarnings("deprecation")
	private synchronized final void write(LogLevel level, String message, Throwable t) {
		
		// check if anything will log this message. If not, return
		if(level.isLowerThan(minLevel))
			return;

		for(LogWriter writer : writers)
			writer.write(level, message, t);
	}
	
	/**
	 * Log a fatal error message
	 * @param message the message to log
	 */
	public final void f(String message) {
		write(LogLevel.FATAL, message, null);
	}

    /**
     * Log a fatal message
     * @param message the message to log
     * @param t the throwable to log with the message
     */
    public final void f(String message, Throwable t) {
        write(LogLevel.FATAL, message, t);
    }
	
	/**
	 * Log an error message
	 * @param message the message to log
	 */
	public final void e(String message) {
		write(LogLevel.ERROR, message, null);
	}

    /**
     * Log an error message
     * @param message the message to log
     * @param t the throwable to log with the message
     */
    public final void e(String message, Throwable t) {
        write(LogLevel.ERROR, message, t);
    }
	
	/**
	 * Log a warning message
	 * @param message the message to log
	 */
	public final void w(String message) {
		write(LogLevel.WARN, message, null);
	}

    /**
     * Log a warning message
     * @param message the message to log
     * @param t the throwable to log with the message
     */
    public final void w(String message, Throwable t) {
        write(LogLevel.WARN, message, t);
    }
	
	/**
	 * Log a debug message
	 * @param message the message to log
	 */
	public final void d(String message) {
		write(LogLevel.DEBUG, message, null);
	}

    /**
     * Log a debug message
     * @param message the message to log
     * @param t the throwable to log with the message
     */
    public final void d(String message, Throwable t) {
        write(LogLevel.DEBUG, message, t);
    }

    /**
     * Log an info message
     * @param message the message to log
     */
    public final void i(String message) {
        write(LogLevel.INFO, message, null);
    }
	
	/**
	 * Log an info message
	 * @param message the message to log
     * @param t the throwable to log with the message
	 */
	public final void i(String message, Throwable t) {
		write(LogLevel.INFO, message, t);
	}
}
