package com.intuso.utilities.log.writer;

import com.intuso.utilities.log.LogLevel;
import com.intuso.utilities.log.LogWriter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Log writer that writes messages to stdout
 * @author tclabon
 *
 */
public class StdOutWriter extends LogWriter {

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	/**
	 * Create a new stdout writer
	 * @param level level to filter messages at
	 */
	public StdOutWriter(LogLevel level) {
		super(level);
	}
	
	@Override
	public void _write(LogLevel level, String message, Throwable t) {
		System.out.println(DATE_FORMAT.format(new Date()) + SEPARATOR + level + SEPARATOR + message);
        if(t != null) {
            System.out.print(CAUSED_BY_MSG);
            t.printStackTrace(System.out);
        }
	}
}