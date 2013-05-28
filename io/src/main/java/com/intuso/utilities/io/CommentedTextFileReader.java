package com.intuso.utilities.io;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Extends functionality of TextFileReader to skip over lines that start with a specified comment prefix
 * @author tclabon
 * @see TextFileReader
 */
public class CommentedTextFileReader extends TextFileReader {
	
	/**
	 * Prefix of commented lines
	 */
	private String m_comment_prefix;
	
	/**
	 * Create a commented text file reader
	 * @param filename - the file to open
	 * @param comment_prefix - prefix that commented lines will start with
	 * @throws FileNotFoundException
	 */
	public CommentedTextFileReader(String filename, String comment_prefix) throws FileNotFoundException {
		super(filename);
		m_comment_prefix = comment_prefix;
	}
	
	/**
	 * Reads the file and returns the next uncommented line, or null if there isn't one
	 * @return the next uncommented line or null if reached EOF
	 * @throws IOException - if underlying BufferedReader throws an exception
	 */
	public String readUsefulLine() throws IOException {
		String line;
		while((line = readLine()) != null && (line.startsWith(m_comment_prefix) || line.trim().length() == 0)) {
			// ignore comment and empty lines
		}
		return line;
	}
}
