package de.wuespace.telestion.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * Utilities for file handling in Java.
 *
 * @author Ludwig Richter (@fussel178)
 * @see java.nio.file
 */
public class FileUtils {

	/**
	 * Reads the contents from a file.
	 *
	 * @param path the path to the file
	 * @return the contents
	 * @throws IOException if an I/O error occurs during opening the file
	 */
	public static String readFile(Path path) throws IOException {
		var lines = Files.lines(path);

		var content = lines.collect(Collectors.joining());
		lines.close();

		return content;
	}
}
