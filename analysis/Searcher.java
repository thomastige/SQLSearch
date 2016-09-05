package analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constants.SQLExceptions;
import constants.SQLKeyWords;
import constants.SQLQueryTypes;
import parser.SimpleParser;

public class Searcher {

	private static final Map<String, String> typeMappings = new HashMap<>();

	SimpleParser parser;
	private List<String> andTableConstraints;
	private List<String> andColumnConstraints;
	private List<String> orTableConstraints;
	private List<String> orColumnConstraints;
	private List<String> queryTypeConstraints;

	private List<String> queryTypesFound;

	static {
		typeMappings.put(SQLQueryTypes.SET, SQLQueryTypes.SELECT);
	}

	public Searcher(String path) throws IOException {
		parser = new SimpleParser(path);
		orTableConstraints = new ArrayList<String>();
		orColumnConstraints = new ArrayList<String>();
		andColumnConstraints = new ArrayList<String>();
		andTableConstraints = new ArrayList<String>();
		queryTypeConstraints = new ArrayList<String>();
		queryTypesFound = new ArrayList<>();
	}

	public void setTableAndConstraints(List<String> andConstraints) {
		this.andTableConstraints = andConstraints;
	}

	public void setTableOrConstraints(List<String> orConstraints) {
		this.orTableConstraints = orConstraints;
	}

	public void setColumnAndConstraints(List<String> andConstraints) {
		this.andColumnConstraints = andConstraints;
	}

	public void setColumnOrConstraints(List<String> orConstraints) {
		this.orColumnConstraints = orConstraints;
	}

	public void setQueryTypeConstraints(List<String> queryTypeConstraints) {
		this.queryTypeConstraints = queryTypeConstraints;
	}

	public boolean analyze() {
		List<String> parseResults = parser.parse();
		Set<String> andSet = new HashSet<String>();
		boolean orConstraintValid = false;
		for (String entry : parseResults) {
			boolean validQuery = false;
			if (SQLExceptions.isException(entry)) {
				break;
			}
			String queryType = getQueryType(entry);
			if (queryTypeConstraints.isEmpty() || queryType != null) {
				String[] splitEntry = entry.split("\\(");
				if (splitEntry.length == 1) {
					splitEntry = entry.split(SQLKeyWords.FROM);
				}
				for (String andConstraint : andTableConstraints) {
					entry = splitEntry[0];
					entry = entry.replace("' " + andConstraint.toUpperCase() + " '", "");
					if (entry.contains(andConstraint.toUpperCase()) && !andSet.contains(entry)) {
						validQuery = true;
						andSet.add(andConstraint.toUpperCase());
					}
				}
				for (String andConstraint : andColumnConstraints) {
					if (splitEntry.length > 1) {
						StringBuilder entryRebuilder = new StringBuilder();
						for (int i = 1; i < splitEntry.length; ++i) {
							entryRebuilder.append(splitEntry[i]);
						}
						entry = entryRebuilder.toString();
						entry = entry.replace("' " + andConstraint.toUpperCase() + " '", "");
						if (entry.contains(andConstraint.toUpperCase()) && !andSet.contains(entry)) {
							validQuery = true;
							andSet.add(andConstraint.toUpperCase());
						}
					}
				}
				if (!orConstraintValid) {
					for (String orConstraint : orTableConstraints) {
						entry = splitEntry[0];
						entry = entry.replace("' " + orConstraint.toUpperCase() + " '", "");
						if (entry.contains(orConstraint.toUpperCase())) {
							orConstraintValid = true;
							validQuery = true;
							break;
						}
					}
					for (String orConstraint : orColumnConstraints) {
						if (splitEntry.length > 1) {
							StringBuilder entryRebuilder = new StringBuilder();
							for (int i = 1; i < splitEntry.length; ++i) {
								entryRebuilder.append(splitEntry[i]);
							}
							entry = entryRebuilder.toString();
							entry = entry.replace("' " + orConstraint.toUpperCase() + " '", "");
							if (entry.contains(orConstraint.toUpperCase())) {
								orConstraintValid = true;
								validQuery = true;
								break;
							}
						}
					}
				}
			}
			if (validQuery) {
				queryTypesFound.add(queryType);
			}
		}
		if (orTableConstraints.size() == 0 && orColumnConstraints.size() == 0) {
			orConstraintValid = true;
		}
		return validateAndSet(andSet) && orConstraintValid;
	}

	private String getQueryType(String entry) {
		for (String queryType : queryTypeConstraints) {
			if (entry.startsWith(queryType)) {
				String mapping = typeMappings.get(queryType);
				if (mapping != null) {
					queryType = mapping;
				}
				return queryType;
			}
		}
		return null;
	}

	private boolean validateAndSet(Set<String> set) {
		boolean result = true;
		for (String constraint : andTableConstraints) {
			if (!set.contains(constraint)) {
				result = false;
			}
		}
		return result;
	}

	public List<String> getQueryTypes() {
		return queryTypesFound;
	}

}
