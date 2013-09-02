package com.intuso.utilities.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Class to read a text file and count the number of lines read so far
 * @author tclabon
 *
 */
public class TextFileReader extends BufferedReader {
	/**
	 * Counter for the number of lines read
	 */
	private int m_lines_read;
	
	/**
	 * Create a text file reader
	 * @param filename - file to read
	 * @throws FileNotFoundException
	 */
	public TextFileReader(String filename) throws FileNotFoundException {
		super(new FileReader(filename));
		m_lines_read = 0;
	}
	
	@Override
	public String readLine() throws IOException {
		m_lines_read++;
		return super.readLine();
	}
	
	/**
	 * Return the number of lines read so far
	 * @return the number of lines read so far
	 */
	public int getNumLinesRead() { return m_lines_read; }
}