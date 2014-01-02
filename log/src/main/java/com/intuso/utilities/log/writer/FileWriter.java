package com.intuso.utilities.log.writer;

import com.intuso.utilities.log.LogLevel;
import com.intuso.utilities.log.LogWriter;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Log writer that writes message to a file
 * @author tclabon
 *
 */
public class FileWriter extends LogWriter {
	
	/**
	 * Wrapper around the file's output stream
	 */
	private PrintWriter out;
	
	/**
	 * Create a new log file write
	 * @param level the level to filter at
	 * @param file_name the name of the file to write to
	 * @throws IOException if an error occurs opening the file
	 */
	public FileWriter(LogLevel level, String file_name) throws IOException {
		super(level);
		out = new PrintWriter(new java.io.FileWriter(file_name));
	}

	@Override
	public void _write(LogLevel level, String message, Throwable t) {
        out.write(message + "\n");
        if(t != null) {
            System.out.print(CAUSED_BY_MSG);
            t.printStackTrace(out);
        }
        out.flush();
	}
}