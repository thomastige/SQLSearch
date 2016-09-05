package analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import constants.SQLQueryTypes;
import gui.UICriteria;

public class Processor {

	private static final String SEPARATOR = ";";

	public String performSearch(UICriteria criteria) throws FileNotFoundException {
		SearchThreadManager manager = new SearchThreadManager(criteria.getSearchFolder());
		List<String> andColumnConstraints = buildList(criteria.getAndColumns());
		List<String> andTableConstraints = (buildList(criteria.getAndTables()));
		List<String> orColumnConstraints = buildList(criteria.getOrColumns());
		List<String> orTableConstraints = (buildList(criteria.getOrTables()));
		List<String> queryTypeConstraints = buildQueryConstraints(criteria);
		manager.setProgressBar(criteria.getProgressBar());
		manager.search(andTableConstraints, orTableConstraints, andColumnConstraints, orColumnConstraints, queryTypeConstraints);
		return saveOutput(manager.getSearchResults(), criteria);
	}

	private List<String> buildQueryConstraints(UICriteria criteria) {
		List<String> queryConstraints = new ArrayList<>();
		if (criteria.isSelectQuery()) {
			queryConstraints.add(SQLQueryTypes.SELECT);
			queryConstraints.add(SQLQueryTypes.SET);
		}
		if (criteria.isAlterQuery()) {
			queryConstraints.add(SQLQueryTypes.ALTER);
		}
		if (criteria.isCreateQuery()) {
			queryConstraints.add(SQLQueryTypes.CREATE);
		}
		if (criteria.isDeleteQuery()) {
			queryConstraints.add(SQLQueryTypes.DELETE);
		}
		if (criteria.isDropQuery()) {
			queryConstraints.add(SQLQueryTypes.DROP);
		}
		if (criteria.isInsertQuery()) {
			queryConstraints.add(SQLQueryTypes.INSERT);
		}
		if (criteria.isUpdateQuery()) {
			queryConstraints.add(SQLQueryTypes.UPDATE);
		}
		return queryConstraints;
	}

	private List<String> buildList(String values) {
		List<String> result = new ArrayList<>(Arrays.asList(values.split(SEPARATOR)));
		Iterator<String> it = result.iterator();
		while (it.hasNext()) {
			String entry = it.next();
			if ("".equals(entry.trim())) {
				it.remove();
			}
		}
		for (ListIterator<String> i = result.listIterator(); i.hasNext();) {
			String element = i.next();
			i.set(" " + element.trim() + " ");
		}

		return result;
	}

	private String saveOutput(Map<String, Set<String>> searchOutput, UICriteria criteria) throws FileNotFoundException {
		StringBuilder outputBuilder = new StringBuilder();
		String path = criteria.getOutputFolder() + File.separator + "Search Output.txt";
		for (Entry<String, Set<String>> entry : searchOutput.entrySet()) {
			outputBuilder.append(entry.getKey() + "\n");
			for (String file : entry.getValue()) {
				outputBuilder.append(file + "\n");
			}
			outputBuilder.append("\n");

			PrintWriter writer = new PrintWriter(path);
			writer.println(outputBuilder.toString());
			writer.close();
		}
		return path;

	}

}
