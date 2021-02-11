package de.PMD_Report_Extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.PMD_Report_Extractor.util.ReportFormatter;
import de.PMD_Report_Extractor.util.ReportParser;
import de.PMD_Report_Extractor.util.ResourceFileHandler;

@ExtendWith(MockitoExtension.class)
public class AppTest {
	@Test
	void main_no_argsTest() {
		String[] args = {};
		String msg = "Wrong number of arguments. A path to a PMD xml report, a path to an exlude file "
				+ "and a PMD rule are mandatory.\n Arguments given: " + Arrays.toString(args);

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> App.main(args));

		assertEquals(msg, ex.getMessage());
	}

	@Test
	void main_argsTest() throws IOException, ParserConfigurationException, SAXException {
		String[] args = { "reportPath", "excludePath", "rule" };
		Document xml = Mockito.mock(Document.class);
		Set<Node> filteredNodes = Collections.emptySet();
		StringBuilder sbExtract = new StringBuilder().append("extract");
		StringBuilder sbExclude = new StringBuilder().append("exclude");
		StringBuilder sbMerge = new StringBuilder().append("merge");

		try (MockedStatic<ResourceFileHandler> fileHandlerMock = Mockito.mockStatic(ResourceFileHandler.class);
				MockedStatic<ReportParser> parserMock = Mockito.mockStatic(ReportParser.class);
				MockedStatic<ReportFormatter> fomatterMock = Mockito.mockStatic(ReportFormatter.class)) {
			fileHandlerMock.when(() -> ResourceFileHandler.readFile(args[0])).thenReturn(new StringBuilder());
			parserMock.when(() -> ReportParser.parseToXml(any(StringBuilder.class))).thenReturn(xml);
			parserMock.when(() -> ReportParser.filterByRule(xml, args[2])).thenReturn(filteredNodes);
			fomatterMock.when(() -> ReportFormatter.formatToExcludeStyle(filteredNodes)).thenReturn(sbExtract);
			fileHandlerMock.when(() -> ResourceFileHandler.readFile(args[1])).thenReturn(sbExclude);
			fomatterMock.when(() -> ReportFormatter.merge(sbExclude, sbExtract)).thenReturn(sbMerge);

			App.main(args);

			fileHandlerMock.verify(() -> ResourceFileHandler.writeToFile(args[1], sbMerge));
		}
	}
}
