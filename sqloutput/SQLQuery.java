package sqloutput;

import java.util.ArrayList;
import java.util.List;

public class SQLQuery {

	private String queryType;
	private String tableQueried;
	private List<String> joins;
	private List<String> wheres;
	private List<String> columnsQueried;

	private int lineFound;

	public int getLineFound() {
		return lineFound;
	}

	public void setLineFound(int lineFound) {
		this.lineFound = lineFound;
	}

	public SQLQuery() {
		columnsQueried = new ArrayList<>();
		joins = new ArrayList<>();
		wheres = new ArrayList<>();
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getTableQueried() {
		return tableQueried;
	}

	public void setTableQueried(String tableQueried) {
		this.tableQueried = tableQueried;
	}

	public void addColumn(String column) {
		columnsQueried.add(column);
	}
	
	public void setColumns(List<String> columns){
		this.columnsQueried = columns;
	}

	public List<String> getColumnsQueried() {
		return columnsQueried;
	}

	public void addJoin(String join) {
		joins.add(join);
	}

	public List<String> getJoins() {
		return joins;
	}

	public List<String> getWhereCondition() {
		return wheres;
	}

	public void addWhereCondition(String whereCondition) {
		this.wheres.add(whereCondition);
	}

	public String toString() {
		StringBuilder result = new StringBuilder(
				queryType + " from " + tableQueried);
		for (String entry : columnsQueried) {
			result.append("\ncolumn: " + entry);
		}
		for (String entry : joins) {
			result.append("\njoin: " + entry);
		}
		for (String entry : wheres) {
			result.append("\nwhere: " + entry);
		}
		result.append("\nfound on line:" + lineFound + "\n");
		
		return result.toString();
	}

}
