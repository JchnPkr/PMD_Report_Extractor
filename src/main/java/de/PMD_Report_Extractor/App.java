package de.PMD_Report_Extractor;

import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.PMD_Report_Extractor.util.ReportFormatter;
import de.PMD_Report_Extractor.util.ReportParser;
import de.PMD_Report_Extractor.util.ResourceFileHandler;

public class App {
	private static final Logger LOG = LogManager.getLogger(App.class);

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		LOG.info("--- rule extraction started...");

		if (args.length == 3) {
			final String pathToPmdResource = args[0];
			final String pathToExcludeResource = args[1];
			final String rule = args[2];

			StringBuffer sbIn = ResourceFileHandler.readFile(pathToPmdResource);
			Document xml = ReportParser.parseToXml(sbIn);
			Set<Node> filteredNodes = ReportParser.filterByRule(xml, rule);
			StringBuffer sbExtract = ReportFormatter.formatToExcludeStyle(filteredNodes);
			ResourceFileHandler.writeToConsole(sbExtract, "result from extraction");

			StringBuffer sbExclude = ResourceFileHandler.readFile(pathToExcludeResource);
			StringBuffer sbMerge = ReportFormatter.merge(sbExclude, sbExtract);
			ResourceFileHandler.writeToConsole(sbMerge, "result from merge");
			ResourceFileHandler.writeToFile(pathToExcludeResource, sbMerge);
		} else {
			LOG.error("--- wrong number of arguments. A path to a PMD xml report, a path to an exlude file "
					+ "and a PMD rule are mandatory.\n Arguments given: " + args.toString());
			System.exit(1);
		}

		LOG.info("--- report extracted successfully");
	}
}