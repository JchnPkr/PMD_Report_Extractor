package de.pmd_report_extractor.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.shared.utils.StringUtils;
import org.w3c.dom.Node;

public class ReportFormatter {
	private static final Logger LOG = LogManager.getLogger(ReportFormatter.class);

	private ReportFormatter() {
		super();
	}

	/**
	 * Returns a StringBuilder with formatted entries from node list.
	 * 
	 * @param filteredNodes
	 *                          a set of nodes of the same rule is expected
	 * @return a StringBuilder with formatted and line break separated entries from
	 *         node list
	 */
	public static StringBuilder formatToExcludeStyle(Set<Node> filteredNodes) {
		LOG.debug("formatting node entries");

		Set<String> results = new TreeSet<>();
		int count = 0;

		for (Node n : filteredNodes) {
			StringBuilder sb = new StringBuilder();
			String packageAtr = n.getAttributes().getNamedItem("package").getNodeValue();
			sb.append(packageAtr).append('.');
			String classAtr = n.getAttributes().getNamedItem("class").getNodeValue();
			sb.append(classAtr).append('=');
			String ruleAtr = n.getAttributes().getNamedItem("rule").getNodeValue();
			sb.append(ruleAtr);
			results.add(sb.toString());
			count++;
		}

		LOG.debug("found {} entries, formatted {} exclude entries", count, results.size());

		return transformToStringBuilder(results);
	}

	/**
	 * Merges resource and exclude file entries by adding an existing path from
	 * exclude file with it's rules to the new exclude entries from resource or
	 * appending sbResource and sbExtract should be formatted the same way:
	 * pathToFile.filename.java=rule,anotherRule
	 * 
	 * @param sbExclude
	 *                      existing entries from exclude file
	 * @param sbExtract
	 *                      new extracted entries from report
	 * @return the merged exclude entries
	 */
	public static StringBuilder merge(StringBuilder sbExclude, StringBuilder sbExtract) {
		LOG.debug("merging excludes resource with report extracts");

		Set<String> excludeEntries = transformToSet(sbExclude);
		Set<String> extractEntries = transformToSet(sbExtract);

		extractEntries.forEach(extractEntry -> {
			if (!extractEntry.isEmpty()) {
				String path = extractEntry.split("=")[0];
				String newRule = extractEntry.split("=")[1];

				Optional<String> match = excludeEntries.stream().filter(r -> r.contains(path)).findFirst();
				updateOrAddExcludeEntry(excludeEntries, extractEntry, newRule, match);
			} else {
				LOG.debug("skipping empty line");
			}
		});

		return transformToStringBuilder(excludeEntries);
	}

	/**
	 * Returns a sorted set from entries separated by line break in buffer.
	 * 
	 * @param sb
	 *               a StringBuilder
	 * @return line break separated entries as set
	 */
	private static Set<String> transformToSet(StringBuilder sb) {
		String tmp = sb.toString().replaceAll("\r", "");

		if (!tmp.isEmpty()) {
			if (tmp.contains("\n")) {
				return new TreeSet<>(Arrays.asList(tmp.split("\n", 0)));
			} else {
				return new TreeSet<>(Arrays.asList(tmp));
			}
		} else {
			return new TreeSet<>();
		}
	}

	/**
	 * Adds new exclude entry or appends new rule to existing exclude path.
	 * 
	 * @param excludeEntries
	 *                           old exclude entries which get updated
	 * @param extractEntry
	 *                           new entry from extract with path and rule
	 * @param newRule
	 *                           the new rule to exclude
	 * @param match
	 *                           entry from exclude file with matching exclude path
	 *                           and old rules
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

	/**
	 * Creates a StringBuilder from the given set entries, separated by line break.
	 * 
	 * @param entries
	 *                    set of entries to
	 * @return a StringBuilder with the given entries
	 */
	private static StringBuilder transformToStringBuilder(Set<String> entries) {
		return new StringBuilder().append(StringUtils.join(entries.toArray(), "\n"));
	}
}
