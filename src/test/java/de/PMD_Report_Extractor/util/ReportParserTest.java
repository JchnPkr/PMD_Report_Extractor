package de.PMD_Report_Extractor.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import builder.XMLStringBuilder;

class ReportParserTest {

	@Test
	void filterByRule_findAllThreeOccurencesTest() throws SAXException, IOException, ParserConfigurationException {
		String input = XMLStringBuilder.get4EntriesFrom2ClassesWith2Rules1ClassRuleDuplicate();
		String rule = "GuardLogStatement";

		Document doc = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.parse(new InputSource(new StringReader(input)));

		Set<Node> nodes = ReportParser.filterByRule(doc, rule);

		assertAll(
				() -> assertEquals(3, nodes.size()),
				() -> assertThat(nodes)
						.allMatch(n -> n.getAttributes().getNamedItem("rule").getNodeValue().equals(rule)),
				() -> assertThat(nodes)
						.anySatisfy(n -> {
							assertThat(n.getAttributes().getNamedItem("class").getNodeValue()).isEqualTo("App");
						}),
				() -> assertThat(nodes)
						.anySatisfy(n -> {
							assertThat(n.getAttributes().getNamedItem("class").getNodeValue())
									.isEqualTo("ResourceFileHandler");
						}));
	}

	@Test
	void parseToXmlTest() throws ParserConfigurationException, SAXException, IOException {
		StringBuilder sbIn = new StringBuilder()
				.append(XMLStringBuilder.get4EntriesFrom2ClassesWith2Rules1ClassRuleDuplicate());

		Document doc = ReportParser.parseToXml(sbIn);

		// All third party calls so nothing to check really
		assertNotNull(doc);
	}
}
