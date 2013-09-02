package com.intuso.utilities.misc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Miscellaneous utility functions.
 * @author tclabon
 *
 */
public final class Utils {
	
	/**
	 * All functions should be static, otherwise they should be in their own class
	 */
	private Utils() {
		// do nothing, defined solely to make it private so no instance of this class can be created
	}
	
	/**
	 * Convert an exception stacktrace to a String
	 * @param e - the exception
	 * @return - e's stacktrace as a string
	 */
	public static String getExceptionStackTrace(Throwable e) {
		Writer w = new StringWriter();
	    PrintWriter pw = new PrintWriter(w);
        e.printStackTrace(pw);
	    return w.toString();
	}
}
