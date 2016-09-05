package parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import constants.SQLKeyWords;
import constants.SQLOperators;
import constants.SQLPunctuation;
import constants.SQLQueryTypes;
import sqloutput.JoinTypes;
import sqloutput.QueryActions;
import sqloutput.QueryList;
import sqloutput.SQLQuery;

public class SQLParser {



	private QueryList queryList;
	private SQLQuery currentQuery;

	private Tokenizer tokenizer;
	private String currentToken;

	public SQLParser(String filePath) throws IOException {
		tokenizer = new Tokenizer(filePath);
	}

	public QueryList getQueries() {
		currentQuery = new SQLQuery();
		queryList = new QueryList();
		getNext();
		parse();
		return queryList;
	}

	private void parse() {
		while (tokenizer.hasNext() && (!SQLQueryTypes.SUPPORTED_QUERY_TYPES.contains(currentToken.toUpperCase()))) {
			getNext();
		}
		if (currentToken != null && SQLQueryTypes.SUPPORTED_QUERY_TYPES.contains(currentToken.toUpperCase())) {
			try {
				String queryType = currentToken.toUpperCase();
				currentQuery.setQueryType(queryType);
				currentQuery.setLineFound(tokenizer.getCurrentLine());
				getNext();

				switch (queryType) {
				case SQLQueryTypes.SELECT:
					if (!QueryActions.INTO.equals(currentToken)) {
						expectDistinct();
						expectTop();
						List<String> columns = expectColumnNames();
						currentQuery.setColumns(columns);
						expectInto();
						expectFrom();
						expectJoin();
						expectWhere();
						expectGroupBy();
						expectOrderBy();
						break;
					}
					// if select into, roll in next case;

				case SQLQueryTypes.SELECT_INTO:
					break;
				//
				// case QueryTypes.CREATE:
				// // expectTableName();
				// break;
				//
				// case QueryTypes.DELETE:
				// // expectFrom();
				// // expectTableName();
				// break;
				//
				// case QueryTypes.INSERT:
				// if (!QueryActions.INTO.equals(currentToken)) {
				//
				// break;
				// }
				// // if insert into, roll in next case;
				// case QueryTypes.INSERT_INTO:
				// break;
				}
				// expectInto();
				// expectColumnNames();
				// expectFrom();
				// expectTableName();
				// expectFrom();
				// expectWhere();
				// expectGroupBy();
				// expectOrderBy();

				// temporary
				if (SQLQueryTypes.SELECT.equals(currentQuery.getQueryType()))
					queryList.add(currentQuery);
				currentQuery = new SQLQuery();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			if (tokenizer.hasNext()) {
				parse();
			}
		}
	}

	/*
	 * Do nothing for now
	 */
	private void expectInto() {
		if (currentToken.equals(QueryActions.INTO)) {
			getNext();
			getNext();
		}
	}

	/*
	 * Don't do anything for now, maybe eventually
	 */
	private void expectTop() {
		if (SQLKeyWords.TOP.equals(currentToken.toUpperCase())) {
			// value
			getNext();
			// actual next
			getNext();
		}
	}

	private void expectJoin() {
		while (JoinTypes.JOIN_TYPES.contains(currentToken.toUpperCase())) {
			StringBuilder join = new StringBuilder();
			while (JoinTypes.JOIN_TYPES.contains(currentToken.toUpperCase())) {
				join.append(currentToken + " ");
				getNext();
			}
			// current token is table name
			join.append(currentToken + " ");
			getNext();
			expectWithNolock();
			// current token is ON
			do {
				join.append(currentToken + " ");
				getNext();
				// add table and column names
				// possibly table name
				join.append(expectColumnNames());
				if (JoinTypes.JOIN_TYPES.contains(currentToken)) {
					continue;
					// This is hideous, but it's due to the expectColumnNames
					// expecting columns and matching operations.
				} else if (SQLKeyWords.WHERE.equals(currentToken)) {
					break;
				}
				// Equal sign
				join.append(currentToken + " ");
				getNext();
				join.append(expectColumnNames());
			} while (logicalComparison());
			// Other table
			while (SQLOperators.RPAREN.equals(currentToken.toUpperCase())) {
				getNext();
			}
			currentQuery.addJoin(join.toString());
		}

	}

	private boolean logicalComparison() {
		return SQLKeyWords.AND.equalsIgnoreCase(currentToken) || SQLKeyWords.OR.equalsIgnoreCase(currentToken)
				|| SQLOperators.OPERATORS.contains(currentToken);
	}

	private void expectDistinct() {
		if (SQLKeyWords.DISTINCT.equalsIgnoreCase(currentToken)) {
			getNext();
		}
	}

	private void expectGroupBy() {

	}

	private void expectOrderBy() {

	}

	private void expectWhere() {
		if (currentToken != null && currentToken.toUpperCase().equals(SQLKeyWords.WHERE)) {
			do {
				getNext();
				while (SQLOperators.LPAREN.equalsIgnoreCase(currentToken)) {
					getNext();
				}
				StringBuilder whereClause = new StringBuilder();
				whereClause.append(expectColumnNames().get(0));
				while (SQLOperators.OPERATORS.contains(currentToken)) {
					whereClause.append(currentToken + " ");
					getNext();
					whereClause.append(currentToken + " ");
					getNext();
				}
				if (SQLOperators.LPAREN.equals(currentToken)) {
					whereClause.append(currentToken);
					while (!SQLOperators.RPAREN.equals(currentToken)) {
						getNext();
						whereClause.append(currentToken);
					}
					getNext();
				}
				currentQuery.addWhereCondition(whereClause.toString());
				while (SQLOperators.RPAREN.equalsIgnoreCase(currentToken)) {
					getNext();
				}
			} while (logicalComparison());
		}

	}

	private String expectTableName() {
		while (currentToken != null && !isAlphaNumeric(currentToken)) {
			getNext();
		}
		StringBuilder tableName = new StringBuilder();
		tableName.append(currentToken);
		getNext();
		if (SQLPunctuation.DOT.equals(currentToken)) {
			tableName.append(currentToken);
			getNext();
			tableName.append(currentToken);
			getNext();
		}
		String alias = expectAlias();
		if (alias != null) {
			tableName.append(" " + alias);
		}
		expectWithNolock();
		return tableName.toString();
	}

	private String expectAlias() {
		String result = null;
		if (SQLKeyWords.AS.equalsIgnoreCase(currentToken)) {
			getNext();
		}
		if (isAlphaNumeric(currentToken) && !currentToken.equalsIgnoreCase(SQLKeyWords.ON)
				&& !currentToken.equalsIgnoreCase(SQLKeyWords.WITH) && !currentToken.equalsIgnoreCase(SQLKeyWords.FROM)
				&& !JoinTypes.JOIN_TYPES.contains(currentToken.toUpperCase())
				&& !SQLQueryTypes.QUERY_TYPES.contains(currentToken.toUpperCase())
				&& !SQLKeyWords.WHERE.equalsIgnoreCase(currentToken) && !SQLKeyWords.AND.equalsIgnoreCase(currentToken)
				&& !QueryActions.INTO.equalsIgnoreCase(currentToken) && !SQLKeyWords.IS.equalsIgnoreCase(currentToken)
				&& !SQLKeyWords.OR.equalsIgnoreCase(currentToken)
				&& !SQLKeyWords.KEYWORDS.contains(currentToken.toUpperCase())) {
			result = currentToken;
			getNext();
		}
		return result;
	}

	private void expectWithNolock() {
		if (currentToken != null && SQLKeyWords.WITH.equals(currentToken.toUpperCase())) {
			getNext();
			getNext();
			getNext();
			// getNext();
		}
	}

	private void expectFrom() {
		if (currentToken.toUpperCase().equals(SQLKeyWords.FROM)) {
			getNext();
			currentQuery.setTableQueried(expectTableName());
		}
	}

	private void expectSelectInto() {
		if (QueryActions.INTO.equals(currentToken)) {
			currentQuery.setQueryType(SQLQueryTypes.SELECT_INTO);
		}
	}

	private List<String> expectColumnNames() {
		List<String> list = new ArrayList<>();
		StringBuilder name = new StringBuilder();
		if (isAlphaNumeric(currentToken)) {
			name.append(currentToken);
			getNext();
		}
		if (SQLOperators.LPAREN.equals(currentToken)) {
			while (!SQLOperators.RPAREN.equals(currentToken)) {
				name.append(currentToken + " ");
				getNext();
			}
			name.append(currentToken);
			getNext();
		}
		while (SQLOperators.EQUALS.equalsIgnoreCase(currentToken)
				|| SQLOperators.APOSTROPHE.equalsIgnoreCase(currentToken)) {
			if (SQLOperators.EQUALS.equalsIgnoreCase(currentToken)) {
				name.append(currentToken);
				getNext();
			}
			boolean apostrophe = SQLOperators.APOSTROPHE.equalsIgnoreCase(currentToken);
			if (apostrophe) {
				name.append(": ");
				getNext();
				while (apostrophe) {
					if (SQLOperators.APOSTROPHE.equalsIgnoreCase(currentToken)) {
						apostrophe = !apostrophe;
						getNext();
					}
					while (apostrophe && !SQLOperators.APOSTROPHE.equals(currentToken)) {
						name.append(currentToken + " ");
						getNext();
					}
				}
			} else {
				name.append(currentToken);
				getNext();
			}

		}
		while (SQLPunctuation.DOT.equals(currentToken) || SQLOperators.OPERATORS.contains(currentToken)) {
			if (SQLPunctuation.DOT.equals(currentToken)) {
				name.append(currentToken);
				name.append(getNext());
				getNext();
			} else if (SQLOperators.EQUALS.equalsIgnoreCase(currentToken)) {
				int line = tokenizer.getCurrentLine();
				while (!SQLKeyWords.KEYWORDS.contains(currentToken.toUpperCase())
						&& !SQLQueryTypes.QUERY_TYPES.contains(currentToken.toUpperCase())
						&& tokenizer.getCurrentLine() == line) {
					name.append(currentToken);
					getNext();
				}
			} else {
				name.append(currentToken);
				name.append(getNext());
				getNext();
				if (SQLOperators.LPAREN.equals(currentToken)) {
					while (!SQLOperators.RPAREN.equals(currentToken)) {
						name.append(currentToken + " ");
						getNext();
					}
					name.append(currentToken);
					getNext();
				}
			}
		}
		String alias = expectAlias();
		if (alias != null) {
			name.append(alias + " ");
		}
		list.add(name.toString());
		if (SQLPunctuation.COMMA.equals(currentToken) || SQLOperators.OPERATORS.contains(currentToken)) {
			getNext();
			list.addAll(expectColumnNames());
		}
		expectWithNolock();
		return list;
	}

	private boolean isAlphaNumeric(String str) {
		boolean result = false;
		if (str != null) {
			result = str.matches("^[\\#\\pL\\pN\\p{Pc}]*$") || SQLPunctuation.STAR.equals(str) || str.startsWith(SQLOperators.AROBAS)
					|| str.startsWith(SQLOperators.OCTOTHORPE);
		}
		return result;
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
		currentToken = result;
		return result;
	}

	private String getNextAlphaNumeric() {
		String result = getNext();
		if (result != null && !isAlphaNumeric(result)) {
			result = getNextAlphaNumeric();
		}
		currentToken = result;
		return result;
	}

}
