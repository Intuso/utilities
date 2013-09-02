package com.intuso.utilities.log.writer;

import com.intuso.utilities.log.LogLevel;
import com.intuso.utilities.log.LogWriter;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Log writer that writes message to a file
 * @author tclabon
 *
 */
public class FileWriter extends LogWriter {
	
	/**
	 * The file to write to
	 */
	private String fileName;
	
	/**
	 * Wrapper around the file's output stream
	 */
	private BufferedWriter out;
	
	/**
	 * Create a new log file write
	 * @param level the level to filter at
	 * @param file_name the name of the file to write to
	 * @throws IOException if an error occurs opening the file
	 */
	public FileWriter(LogLevel level, String file_name) throws IOException {
		super(level);
		fileName = file_name;
		out = new BufferedWriter(new java.io.FileWriter(file_name));
	}

	@Override
	public void _write(LogLevel level, String message) {
		try {
			out.write(message + "\n");
			out.flush();
		} catch(IOException e) {
			System.err.println("Unable to write message to log file \"" + fileName + "\"");
		}
	}
}