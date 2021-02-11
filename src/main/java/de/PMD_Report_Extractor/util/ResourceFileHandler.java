package de.PMD_Report_Extractor.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourceFileHandler {
	private static final Logger LOG = LogManager.getLogger(ResourceFileHandler.class);

	private ResourceFileHandler() {
		super();
	}

	public static StringBuffer readFile(String path) throws IOException {
		StringBuffer sbIn = new StringBuffer();

		try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
			LOG.debug("--- reading file: " + path);

			String line;

			while ((line = br.readLine()) != null) {
				sbIn.append(line).append("\n");
			}

			return sbIn;
		}
	}

	public static void writeToFile(String path, StringBuffer sbOut) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path), false))) {
			LOG.debug("--- writing file to: " + path);

			bw.write(sbOut.toString());
		}
	}

	public static void writeToConsole(StringBuffer sbOut, String contentTitle) {
		LOG.info("--- begin of content " + contentTitle + ":\n" + sbOut.toString());
		LOG.info("--- end of content " + contentTitle);
	}
}
