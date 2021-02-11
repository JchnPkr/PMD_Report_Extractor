package de.pmd_report_extractor.util;

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

	public static StringBuilder readFile(String path) throws IOException {
		StringBuilder sbIn = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
			LOG.debug("--- reading file: {}", path);

			String line;

			while ((line = br.readLine()) != null) {
				sbIn.append(line).append("\n");
			}

			return sbIn;
		}
	}

	public static void writeToFile(String path, StringBuilder sbOut) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path), false))) {
			LOG.debug("--- writing file to: {}", path);

			bw.write(sbOut.toString());
		}
	}

	public static void writeToConsole(StringBuilder sbOut, String contentTitle) {
		LOG.info("--- begin of content {}:\n{}", contentTitle, sbOut);
		LOG.info("--- end of content {}", contentTitle);
	}
}
