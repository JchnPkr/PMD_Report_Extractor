package de.PMD_Report_Extractor.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import builder.XMLStringBuilder;

class ReportFormatterTest {
	private static final Logger LOG = LogManager.getLogger(ReportFormatterTest.class);

	@Nested
	@DisplayName("Method: merge(StringBuffer sbExclude, StringBuffer sbExtract)")
	class MergeExcludesTest {
		@Test
		void merge_nonExisting_existing_nonrelated_new_Test() {
			StringBuffer expected = new StringBuffer(
					"de.anyName.service.impl.Service=AvoidLiteralsInIfCondition,AvoidCatchingGenericException\n"
							+ "de.zivit.kista.anfrage.service.impl.FuServiceService=AvoidCatchingGenericException,ExcessiveImports\n"
							+ "de.zivit.kista.antwort.service.impl.BarServiceService=MoreThanOneLogger\n"
							+ "de.zivit.kista.dataaccess.AnotherService=AvoidCatchingGenericException");

			StringBuffer sbExtract = new StringBuffer(
					"de.anyName.service.impl.Service=AvoidCatchingGenericException\r\n"
							+ "de.zivit.kista.anfrage.service.impl.FuServiceService=AvoidCatchingGenericException\r\n"
							+ "de.zivit.kista.dataaccess.AnotherService=AvoidCatchingGenericException\r\n");

			StringBuffer sbResource = new StringBuffer("de.anyName.service.impl.Service=AvoidLiteralsInIfCondition\r\n"
					+ "de.zivit.kista.anfrage.service.impl.FuServiceService=AvoidCatchingGenericException,ExcessiveImports\r\n"
					+ "de.zivit.kista.antwort.service.impl.BarServiceService=MoreThanOneLogger\r\n");

			StringBuffer result = ReportFormatter.merge(sbResource, sbExtract);

			LOG.info("\n" + result.toString());
			assertEquals(expected.toString(), result.toString());
		}

		@Test
		void merge_existing_Test() {
			StringBuffer expected = new StringBuffer(
					"de.anyName.service.impl.Service=AvoidCatchingGenericException,AvoidLiteralsInIfCondition");

			StringBuffer sbExtract = new StringBuffer(
					"de.anyName.service.impl.Service=AvoidCatchingGenericException\r\n");

			StringBuffer sbResource = new StringBuffer(
					"de.anyName.service.impl.Service=AvoidCatchingGenericException,AvoidLiteralsInIfCondition\r\n");

			StringBuffer result = ReportFormatter.merge(sbResource, sbExtract);

			LOG.info("\n" + result.toString());
			assertEquals(expected.toString(), result.toString());
		}

		@Test
		void merge_nonexisting_Test() {
			StringBuffer expected = new StringBuffer(
					"de.anyName.service.impl.Service=AvoidLiteralsInIfCondition,AvoidCatchingGenericException");

			StringBuffer sbExtract = new StringBuffer(
					"de.anyName.service.impl.Service=AvoidCatchingGenericException\r\n");

			StringBuffer sbResource = new StringBuffer(
					"de.anyName.service.impl.Service=AvoidLiteralsInIfCondition\r\n");

			StringBuffer result = ReportFormatter.merge(sbResource, sbExtract);

			LOG.info("\n" + result.toString());
			assertEquals(expected.toString(), result.toString());
		}

		@Test
		void merge_new_Test() {
			StringBuffer expected = new StringBuffer("de.anyName.service.impl.Service=AvoidCatchingGenericException");

			StringBuffer sbExtract = new StringBuffer(
					"de.anyName.service.impl.Service=AvoidCatchingGenericException\r\n");

			StringBuffer sbResource = new StringBuffer();

			StringBuffer result = ReportFormatter.merge(sbResource, sbExtract);

			LOG.info("\n" + result.toString() + "!!");
			assertEquals(expected.toString(), result.toString());
		}

		@Test
		void merge_nonrelated_Test() {
			StringBuffer expected = new StringBuffer(
					"de.anyName.service.impl.AnotherService=AvoidLiteralsInIfCondition\n"
							+ "de.anyName.service.impl.Service=AvoidCatchingGenericException");

			StringBuffer sbExtract = new StringBuffer(
					"de.anyName.service.impl.Service=AvoidCatchingGenericException\r\n");

			StringBuffer sbResource = new StringBuffer(
					"de.anyName.service.impl.AnotherService=AvoidLiteralsInIfCondition\r\n");

			StringBuffer result = ReportFormatter.merge(sbResource, sbExtract);

			LOG.info("\n" + result.toString());
			assertEquals(expected.toString(), result.toString());
		}
	}

	@Nested
	@DisplayName("Methode: formatToExcludeStyle(Set<Node> filteredNodes)")
	class FormatToExcludeStyle {
		@Test
		void findsTwoClassesAndRemovesDuplicatesTest()
				throws ParserConfigurationException, SAXException, IOException {
			String input = XMLStringBuilder.get3EntriesFrom2ClassesSameRule1ClassRuleDuplicate();

			String expected = "de.PMD_Report_Extractor.App=GuardLogStatement\n" +
					"de.PMD_Report_Extractor.util.ResourceFileHandler=GuardLogStatement";

			NodeList violationTags = DocumentBuilderFactory
					.newInstance()
					.newDocumentBuilder()
					.parse(new InputSource(new StringReader(input)))
					.getElementsByTagName("violation");
			Set<Node> nodes = IntStream
					.range(0, violationTags.getLength())
					.mapToObj(violationTags::item)
					.collect(Collectors.toSet());

			StringBuffer result = ReportFormatter.formatToExcludeStyle(nodes);

			assertEquals(expected, result.toString());
		}
	}
}
