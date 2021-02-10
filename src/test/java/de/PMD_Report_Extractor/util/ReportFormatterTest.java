package de.PMD_Report_Extractor.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ReportFormatterTest {
	private static final Logger LOG = LogManager.getLogger(ReportFormatterTest.class);

	@Nested
	@DisplayName("Methode: merge")
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
}
