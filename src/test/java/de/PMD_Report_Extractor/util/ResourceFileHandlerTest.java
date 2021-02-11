package de.PMD_Report_Extractor.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResourceFileHandlerTest {
	private static final String RESOURCE_PATH = "src/test/resources/";
	private static final String TESTFILE = "testFile.txt";

	@Mock
	private Appender mockedAppender;

	@Captor
	private ArgumentCaptor<LogEvent> loggingEventCaptor;

	@Test
	void readFileTest() throws IOException {
		StringBuffer sb = ResourceFileHandler.readFile(RESOURCE_PATH + TESTFILE);

		assertEquals("test content\n", sb.toString());
	}

	@Test
	void writeToFileTest() throws IOException {
		String fileName = "out.txt";
		String content = "test text";
		StringBuffer sbOut = new StringBuffer().append(content);

		ResourceFileHandler.writeToFile(RESOURCE_PATH + fileName, sbOut);

		String result = new String(Files.readAllBytes(Paths.get(RESOURCE_PATH + fileName)));

		Files.delete(Paths.get(RESOURCE_PATH + fileName));
		assertEquals(content, result);
	}

	@Test
	void writeToConsoleTest() {
		String title = "test titel";
		String msg = "test message";

		StringBuffer sbOut = new StringBuffer().append(msg);

		String expectedMsg1 = "--- begin of content " + title + ":\n" + sbOut.toString();
		String expectedMsg2 = "--- end of content " + title;

		when(mockedAppender.getName()).thenReturn("mockedAppender");
		when(mockedAppender.isStarted()).thenReturn(true);

		Logger root = (Logger) LogManager.getRootLogger();
		root.addAppender(mockedAppender);
		root.setLevel(Level.INFO);

		ResourceFileHandler.writeToConsole(sbOut, title);

		verify(mockedAppender, times(2)).append(loggingEventCaptor.capture());

		LogEvent logEvent1 = loggingEventCaptor.getAllValues().get(0);
		LogEvent logEvent2 = loggingEventCaptor.getAllValues().get(1);

		assertAll(() -> assertEquals(expectedMsg1, logEvent1.getMessage().getFormattedMessage()),
				() -> assertEquals(Level.INFO, logEvent1.getLevel()),
				() -> assertEquals(expectedMsg2, logEvent2.getMessage().getFormattedMessage()),
				() -> assertEquals(Level.INFO, logEvent1.getLevel()));
	}
}
