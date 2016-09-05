package parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import constants.SQLPunctuation;
import constants.SQLQueryTypes;

public class SimpleParser {

	private List<String> queries;
	Tokenizer tokenizer;
	boolean commented = false;

	public SimpleParser(String path) throws IOException {
		this.tokenizer = new Tokenizer(path);
		queries = new ArrayList<String>();
	}

	public List<String> parse() {
		String currentToken = tokenizer.getNextToken();
		while (tokenizer.hasNext() && !SQLQueryTypes.QUERY_TYPES.contains(currentToken.toUpperCase())) {
			currentToken = getNext();
		}
		while (tokenizer.hasNext()) {
			StringBuilder currentQuery = new StringBuilder(currentToken.toUpperCase() + " ");
			currentToken = getNext();
			if (!commented) {
				while (tokenizer.hasNext() && !SQLQueryTypes.QUERY_TYPES.contains(currentToken.toUpperCase())) {
					if ("/*".equals(currentToken)) {
						commented = true;
					} else if ("*/".equals(currentToken)) {
						commented = false;
					}
					if (!commented) {
						currentQuery.append(currentToken.toUpperCase() + " ");
					}
					currentToken = getNext();
				}
				queries.add(currentQuery.toString());
			}
		}
		return queries;
	}

	private String getNext() {
		String result = null;
		while (result == null && tokenizer.hasNext()) {
			do {
				result = tokenizer.getNextToken();
			} while (result != null && "".equals(result.trim()));
			if (result != null) {
				if (result.startsWith(SQLPunctuation.LINE_COMMENT)) {
					tokenizer.skipLine();
					result = getNext();
				}
			}
		}
		return result;
	}

}
