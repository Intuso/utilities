package com.intuso.utilities.log.writer;

import com.intuso.utilities.log.LogLevel;
import com.intuso.utilities.log.LogWriter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Log writer that writes message to a file
 * @author tclabon
 *
 */
public class FileWriter extends LogWriter {

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	/**
	 * Wrapper around the file's output stream
	 */
	private PrintWriter out;
	
	/**
	 * Create a new log file write
	 * @param level the level to filter at
	 * @param fileName the name of the file to write to
	 * @throws IOException if an error occurs opening the file
	 */
	public FileWriter(LogLevel level, String fileName) throws IOException {
		super(level);
        File file = new File(fileName);
        File directory = file.getParentFile();
        if(!directory.exists() && !directory.mkdirs())
            throw new IOException("Cannot create log directory " + directory.getAbsolutePath());
		out = new PrintWriter(new java.io.FileWriter(fileName));
	}

	@Override
	public void _write(LogLevel level, String message, Throwable t) {
        out.println(DATE_FORMAT.format(new Date()) + SEPARATOR + level + SEPARATOR + message);
        if(t != null) {
            out.print(CAUSED_BY_MSG);
            t.printStackTrace(out);
        }
        out.flush();
	}
}