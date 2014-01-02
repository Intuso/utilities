package com.intuso.utilities.log.writer;

import com.intuso.utilities.log.LogLevel;
import com.intuso.utilities.log.LogWriter;

/**
 * Log writer that writes messages to stdout
 * @author tclabon
 *
 */
public class StdOutWriter extends LogWriter {

	/**
	 * Create a new stdout writer
	 * @param level level to filter messages at
	 */
	public StdOutWriter(LogLevel level) {
		super(level);
	}
	
	@Override
	public void _write(LogLevel level, String message, Throwable t) {
		System.out.println(message);
        if(t != null) {
            System.out.print(CAUSED_BY_MSG);
            t.printStackTrace(System.out);
        }
	}
}