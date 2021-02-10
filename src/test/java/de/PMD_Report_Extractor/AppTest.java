package de.PMD_Report_Extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.SAXException;

import de.PMD_Report_Extractor.util.ReportFormatter;
import de.PMD_Report_Extractor.util.ReportParser;
import de.PMD_Report_Extractor.util.ResourceFileHandler;

@ExtendWith(MockitoExtension.class)
public class AppTest {
	@InjectMocks
	private App app;

	@Mock
	private ResourceFileHandler fileHandler;

	@Mock
	private ReportParser parser;

	@Mock
	private ReportFormatter formatter;

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

		fail("TODO - refactor classes");

		App.main(args);
	}
}
