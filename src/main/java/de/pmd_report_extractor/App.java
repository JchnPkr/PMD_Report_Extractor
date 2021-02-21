package de.pmd_report_extractor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.pmd_report_extractor.util.ReportFormatter;
import de.pmd_report_extractor.util.ReportParser;
import de.pmd_report_extractor.util.ResourceFileHandler;

public class App {
	private static final Logger LOG = LogManager.getLogger(App.class);

	/**
	 * Expects three arguments separated with a blank space. 1. the path to the
	 * report file, 2. the path to the exclude file, 3. the rule to add to the
	 * exclude file.
	 * 
	 * @param args
	 *                 takes the arguments from console
	 */
	public static void main(String[] args) {
		LOG.info("rule extraction started...");

		if (args.length == 3) {
			final String pathToPmdResource = args[0];
			final String pathToExcludeResource = args[1];
			final String rule = args[2];

			try {
				process(pathToPmdResource, pathToExcludeResource, rule);
				LOG.info("report extracted successfully");
			} catch (IOException e) {
				LOG.error("Error reading / writing file: {}", e.getMessage());
			} catch (ParserConfigurationException | SAXException e) {
				LOG.error("Error parsing XML: {}", e.getMessage());
			}
		} else {
			throw new IllegalArgumentException(
					"Wrong number of arguments. A path to a PMD xml report, a path to an exlude file "
							+ "and a PMD rule are mandatory.\n Arguments given: " + Arrays.toString(args));
		}

		LOG.info("...shutting down");
	}

	/**
	 * The real work get's done here.
	 * 
	 * @param pathToPmdResource
	 *                                  path to report file
	 * @param pathToExcludeResource
	 *                                  path to exclude file
	 * @param rule
	 *                                  rule to exclude
	 * @throws IOException
	 *                                          Exception
	 * @throws ParserConfigurationException
	 *                                          Exception
	 * @throws SAXException
	 *                                          Exception
	 */
	private static void process(final String pathToPmdResource, final String pathToExcludeResource, final String rule)
			throws IOException, ParserConfigurationException, SAXException {
		StringBuilder sbIn = ResourceFileHandler.readFile(pathToPmdResource);
		Document xml = ReportParser.parseToXml(sbIn);
		Set<Node> filteredNodes = ReportParser.filterByRule(xml, rule);
		StringBuilder sbExtract = ReportFormatter.formatToExcludeStyle(filteredNodes);
		ResourceFileHandler.writeToConsole(sbExtract, "result from extraction");

		StringBuilder sbExclude = ResourceFileHandler.readFile(pathToExcludeResource);
		StringBuilder sbMerge = ReportFormatter.merge(sbExclude, sbExtract);
		ResourceFileHandler.writeToConsole(sbMerge, "result from merge");
		ResourceFileHandler.writeToFile(pathToExcludeResource, sbMerge);
	}
}