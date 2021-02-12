package de.pmd_report_extractor;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.pmd_report_extractor.util.ReportFormatter;
import de.pmd_report_extractor.util.ReportParser;
import de.pmd_report_extractor.util.ResourceFileHandler;

@ExtendWith(MockitoExtension.class)
public class AppTest {
	private static final String ERRMSG = "test message";

	@Mock
	private Appender mockedAppender;

	@Captor
	private ArgumentCaptor<LogEvent> loggingEventCaptor;

	@BeforeEach
	void setup() {
		when(mockedAppender.getName()).thenReturn("mockedAppender");
		when(mockedAppender.isStarted()).thenReturn(true);

		Logger root = (Logger) LogManager.getRootLogger();
		root.addAppender(mockedAppender);
		root.setLevel(Level.ERROR);
	}

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
			fileHandlerMock.verify(() -> ResourceFileHandler.writeToConsole(sbExtract, "result from extraction"));
			fileHandlerMock.verify(() -> ResourceFileHandler.writeToConsole(sbMerge, "result from merge"));
		}
	}

	@Test
	void main_IOExceptionTest() {
		String expectedMsg1 = "Error reading / writing file: " + ERRMSG;

		try (MockedStatic<ResourceFileHandler> fileHandlerMock = Mockito.mockStatic(ResourceFileHandler.class)) {
			fileHandlerMock.when(() -> ResourceFileHandler.readFile(any())).thenThrow(new IOException(ERRMSG));

			App.main(new String[] { "", "", "" });

			verify(mockedAppender, times(3)).append(loggingEventCaptor.capture());

			LogEvent logEvent2 = loggingEventCaptor.getAllValues().get(1);

			assertAll(
					() -> assertEquals(expectedMsg1, logEvent2.getMessage().getFormattedMessage()),
					() -> assertEquals(Level.ERROR, logEvent2.getLevel()));
		}
	}

	@ParameterizedTest
	@MethodSource("exceptionProvider")
	void main_XMLExceptionTest(Exception e) {
		String expectedMsg1 = "Error parsing XML: " + ERRMSG;

		try (MockedStatic<ResourceFileHandler> fileHandlerMock = Mockito.mockStatic(ResourceFileHandler.class);
				MockedStatic<ReportParser> parserMock = Mockito.mockStatic(ReportParser.class)) {
			fileHandlerMock.when(() -> ResourceFileHandler.readFile(any())).thenReturn(new StringBuilder());
			parserMock.when(() -> ReportParser.parseToXml(any(StringBuilder.class))).thenThrow(e);

			App.main(new String[] { "", "", "" });

			verify(mockedAppender, times(3)).append(loggingEventCaptor.capture());

			LogEvent logEvent2 = loggingEventCaptor.getAllValues().get(1);

			assertAll(
					() -> assertEquals(expectedMsg1, logEvent2.getMessage().getFormattedMessage()),
					() -> assertEquals(Level.ERROR, logEvent2.getLevel()));
		}
	}

	private static Stream<Exception> exceptionProvider() {
		return Stream.of(new ParserConfigurationException(ERRMSG), new SAXException(ERRMSG));
	}
}
