package de.PMD_Report_Extractor.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
	@Mock
	private Appender mockedAppender;

	@Captor
	private ArgumentCaptor<LogEvent> loggingEventCaptor;

	@Test
	void readFileTest() {
//		ResourceFileHandler.readFile(String path);
		fail("Not yet implemented");
	}

	@Test
	void writeToFileTest() {
//		ResourceFileHandler.writeToFile(String path, StringBuffer sbOut);
		fail("Not yet implemented");
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
