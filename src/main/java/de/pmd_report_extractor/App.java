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
	 * exclude file
	 * 
	 * @param args
	 *                 takes the arguments from console
	 * @throws IOException
	 *                                          Exception
	 * @throws ParserConfigurationException
	 *                                          Exception
	 * @throws SAXException
	 *                                          Exception
	 */
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		LOG.info("rule extraction started...");

		if (args.length == 3) {
			final String pathToPmdResource = args[0];
			final String pathToExcludeResource = args[1];
			final String rule = args[2];

			StringBuilder sbIn = ResourceFileHandler.readFile(pathToPmdResource);
			Document xml = ReportParser.parseToXml(sbIn);
			Set<Node> filteredNodes = ReportParser.filterByRule(xml, rule);
			StringBuilder sbExtract = ReportFormatter.formatToExcludeStyle(filteredNodes);
			ResourceFileHandler.writeToConsole(sbExtract, "result from extraction");

			StringBuilder sbExclude = ResourceFileHandler.readFile(pathToExcludeResource);
			StringBuilder sbMerge = ReportFormatter.merge(sbExclude, sbExtract);
			ResourceFileHandler.writeToConsole(sbMerge, "result from merge");
			ResourceFileHandler.writeToFile(pathToExcludeResource, sbMerge);
		} else {
			throw new IllegalArgumentException(
					"Wrong number of arguments. A path to a PMD xml report, a path to an exlude file "
							+ "and a PMD rule are mandatory.\n Arguments given: " + Arrays.toString(args));
		}

		LOG.info("report extracted successfully");
	}
}