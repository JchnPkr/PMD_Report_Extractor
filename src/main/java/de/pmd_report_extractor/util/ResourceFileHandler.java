package de.pmd_report_extractor.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourceFileHandler {
	private static final Logger LOG = LogManager.getLogger(ResourceFileHandler.class);

	private ResourceFileHandler() {
		super();
	}

	/**
	 * Reads a file into a StringBuilder.
	 * 
	 * @param path
	 *                 path to file
	 * @return StringBuilder with file content
	 * @throws IOException
	 *                         Exception
	 */
	public static StringBuilder readFile(String path) throws IOException {
		StringBuilder sbIn = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new FileReader(new File(path), StandardCharsets.UTF_8))) {
			LOG.debug("reading file: {}", path);

			String line;

			while ((line = br.readLine()) != null) {
				sbIn.append(line).append("\n");
			}

			return sbIn;
		}
	}

	/**
	 * Writes the content from the StringBuilder to a file under the given path.
	 * 
	 * @param path
	 *                  path to file
	 * @param sbOut
	 *                  content to write
	 * @throws IOException
	 *                         Exception
	 */
	public static void writeToFile(String path, StringBuilder sbOut) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path), StandardCharsets.UTF_8, false))) {
			LOG.debug("writing file to: {}", path);

			bw.write(sbOut.toString());
		}
	}

	/**
	 * Writes the given StringBuilder content to the console, adding a title.
	 * 
	 * @param sbOut
	 *                         StringBuilder with content to write
	 * @param contentTitle
	 *                         a title for the output
	 */
	public static void writeToConsole(StringBuilder sbOut, String contentTitle) {
		LOG.info("begin of content {}:\n{}", contentTitle, sbOut);
		LOG.info("end of content {}", contentTitle);
	}
}
