package de.pmd_report_extractor.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ReportParser {
	private static final Logger LOG = LogManager.getLogger(ReportParser.class);

	private ReportParser() {
		super();
	}

	/**
	 * Parses xml content into a document.
	 * 
	 * @param sbIn
	 *                 StringBuilder containing the xml file content
	 * @return the doc from input
	 * @throws ParserConfigurationException
	 *                                          Exception
	 * @throws SAXException
	 *                                          Exception
	 * @throws IOException
	 *                                          Exception
	 */
	public static Document parseToXml(StringBuilder sbIn)
			throws ParserConfigurationException, SAXException, IOException {
		LOG.debug("parsing input");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(sbIn.toString()));

		return builder.parse(is);
	}

	/**
	 * Filters all nodes from xml doc matching the given rule.
	 * 
	 * @param xml
	 *                 the document containing the pmd report
	 * @param rule
	 *                 the rule to exclude
	 * @return nodes matching rule
	 */
	public static Set<Node> filterByRule(Document xml, String rule) {
		LOG.debug("filtering input");

		NodeList violationTags = xml.getElementsByTagName("violation");
		Stream<Node> nodeStream = IntStream.range(0, violationTags.getLength()).mapToObj(violationTags::item);

		return nodeStream
				.filter(t -> t.getAttributes().getNamedItem("rule").getNodeValue().equals(rule))
				.collect(Collectors.toSet());
	}
}
