package com.intuso.utilities.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Log manager
 * @author tclabon
 *
 */
public class Log {

    private String name;

	/**
	 * List of message writers
	 */
	private List<LogWriter> writers;
	
	/**
	 * The minimum level of a writer
	 */
	private LogLevel minLevel;
	
	/**
	 * Create a new log manager
	 */
	public Log(String name) {
		this(name, new ArrayList<LogWriter>());
	}

    /**
     * Create a new log manager
     */
    public Log(String name, LogWriter ... writers) {
        this(name, Arrays.asList(writers));
    }
	
	/**
	 * Create a new log manager
	 * @param writers writers to write messages to
	 */
	public Log(String name, List<LogWriter> writers) {
        this.name = name;
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
	private synchronized final void write(LogLevel level, String message) {
		
		// check if anything will log this message. If not, return
		if(level.isLowerThan(minLevel))
			return;

        /*Date now = new Date();
		String to_write = now.getDate() + // yyyy/MM/dd HH:mm:ss.SSS
                "/" + (now.getMonth() + 1) +
                "/" + (now.getYear() + 1900) +
                " " + now.getHours() +
                ":" + now.getMinutes() +
                ":" + now.getSeconds() +
                "." + (now.getTime() % 1000) +*/
        String to_write = new Date().toString() +
            ": " + name +
			": " + level +
			": " + message;
		for(LogWriter writer : writers)
			writer.write(level, to_write);
	}
	
	/**
	 * Log a fatal error message
	 * @param message the message to log
	 */
	public final void f(String message) {
		write(LogLevel.FATAL, message);
	}
	
	/**
	 * Log an error message
	 * @param message the message to log
	 */
	public final void e(String message) {
		write(LogLevel.ERROR, message);
	}
	
	/**
	 * Log a warning message
	 * @param message the message to log
	 */
	public final void w(String message) {
		write(LogLevel.WARN, message);
	}
	
	/**
	 * Log a debug message
	 * @param message the message to log
	 */
	public final void d(String message) {
		write(LogLevel.DEBUG, message);
	}
	
	/**
	 * Log an info message
	 * @param message the message to log
	 */
	public final void i(String message) {
		write(LogLevel.INFO, message);
	}
	
	/**
	 * Print the stacktrace of an exception to the log. NB the stack trace is logged at debug level
	 * so writers must be filtered at debug for the stack trace to show
	 * @param t the exception to log
	 */
	public final void st(Throwable t) {
        String space = " ";
        String newLine = "\n";
        boolean first = true;
        StringBuffer sb = new StringBuffer();
        while(t != null) {
            sb.append((first ? "" : "Caused by: ") + t.getMessage()).append(newLine);
            StackTraceElement[] st = t.getStackTrace();
            for(int i = 0; i < st.length; i++) {
                sb.append("\t")
                        .append(st[i].getClassName())
                        .append(space)
                        .append(st[i].getMethodName())
                        .append(space)
                        .append(st[i].getLineNumber())
                        .append(newLine);
            }
            t = t.getCause();
        }
		d(sb.toString());
	}
}
