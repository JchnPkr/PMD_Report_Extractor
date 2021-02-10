package de.PMD_Report_Extractor.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Node;

public class ReportFormatter {
	private static final Logger LOG = LogManager.getLogger(ReportFormatter.class);

	private ReportFormatter() {
		super();
	}

	public static StringBuffer formatToExcludeStyle(Set<Node> filteredNodes) {
		LOG.debug("--- formatting node entries");

		Set<String> results = new TreeSet<>();
		int singleCount = 0;

		for (Node n : filteredNodes) {
			StringBuffer sb = new StringBuffer();
			String packageAtr = n.getAttributes().getNamedItem("package").getNodeValue();
			sb.append(packageAtr).append('.');
			String classAtr = n.getAttributes().getNamedItem("class").getNodeValue();
			sb.append(classAtr).append('=');
			String ruleAtr = n.getAttributes().getNamedItem("rule").getNodeValue();
			sb.append(ruleAtr).append('\n');
			results.add(sb.toString());
			singleCount++;
		}

		StringBuffer formattedResult = new StringBuffer();
		results.forEach(r -> formattedResult.append(r));

		LOG.debug("--- found " + singleCount + " entries, formatted " + results.size() + " exclude entries");

		return formattedResult.deleteCharAt(formattedResult.lastIndexOf("\n"));
	}

	/**
	 * Merges resource and exclude file entries by adding an existing path from
	 * exclude file with it's rules to the new exclude entries from resource or
	 * appending
	 * 
	 * sbResource and sbExtract should be formatted the same way:
	 * pathToFile/filename.java=rule
	 * 
	 * @param sbExclude existing entries from exclude file
	 * @param sbExtract new extracted entries from report
	 * @return the merged exclude entries
	 */
	public static StringBuffer merge(StringBuffer sbExclude, StringBuffer sbExtract) {
		LOG.debug("--- merging excludes resource with report extracts");

		Set<String> excludeEntries = transformToSet(sbExclude);
		Set<String> extractEntries = transformToSet(sbExtract);

		extractEntries.forEach(extractEntry -> {
			if (!extractEntry.isEmpty()) {
				String path = extractEntry.split("=")[0];
				String newRule = extractEntry.split("=")[1];

				Optional<String> match = excludeEntries.stream().filter(r -> r.contains(path)).findFirst();
				updateOrAddExcludeEntry(excludeEntries, extractEntry, newRule, match);
			}
		});

		return transformToBuffer(excludeEntries);
	}

	/**
	 * return a sorted set from entries separated by line break in buffer
	 * 
	 * @param sb
	 * @return line break separated entries as set
	 */
	private static Set<String> transformToSet(StringBuffer sb) {
		String tmp = sb.toString().replaceAll("\r", "");
		sb = new StringBuffer(tmp);

		if (!sb.toString().isEmpty()) {
			if (sb.toString().contains("\n")) {
				return new TreeSet<String>(Arrays.asList(sb.toString().split("\n", 0)));
			} else {
				return new TreeSet<String>(Arrays.asList(sb.toString()));
			}
		} else {
			return new TreeSet<>();
		}
	}

	/**
	 * adds new exclude entry or appends new rule to existing exclude path
	 * 
	 * @param excludeEntries old exclude entries which get updated
	 * @param extractEntry   new entry from extract with path and rule
	 * @param newRule        the new rule to exclude
	 * @param match          entry from exclude file with matching exclude path and
	 *                       old rules
	 */
	private static void updateOrAddExcludeEntry(Set<String> excludeEntries, String extractEntry, String newRule,
			Optional<String> match) {
		if (match.isPresent()) {
			String ruleOnly = newRule.replace("\n", "");

			if (!match.get().contains(ruleOnly)) {
				String updatedEntry = match.get().replace("\n", "");
				updatedEntry = updatedEntry + "," + newRule;
				excludeEntries.remove(match.get());
				excludeEntries.add(updatedEntry);
			}
		} else {
			excludeEntries.add(extractEntry);
		}
	}

	private static StringBuffer transformToBuffer(Set<String> excludeEntries) {
		StringBuffer result = new StringBuffer();
		excludeEntries.forEach(e -> result.append(e).append('\n'));

		return result.deleteCharAt(result.lastIndexOf("\n"));
	}
}
