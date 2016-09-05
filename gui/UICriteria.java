package gui;

import javax.swing.JProgressBar;

public class UICriteria {

	private String searchFolder;
	private String outputFolder;
	private String andTables;
	private String andColumns;
	private String orTables;
	private String orColumns;

	private boolean selectQuery;
	private boolean updateQuery;
	private boolean insertQuery;
	private boolean createQuery;
	private boolean alterQuery;
	private boolean deleteQuery;
	private boolean dropQuery;
	
	private JProgressBar progressBar;

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(JProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	public String getSearchFolder() {
		return searchFolder;
	}

	public void setSearchFolder(String searchFolder) {
		this.searchFolder = searchFolder;
	}

	public String getOutputFolder() {
		return outputFolder;
	}

	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}

	public String getAndTables() {
		return andTables;
	}

	public void setAndTables(String andTables) {
		this.andTables = andTables;
	}

	public String getAndColumns() {
		return andColumns;
	}

	public void setAndColumns(String andColumns) {
		this.andColumns = andColumns;
	}

	public String getOrTables() {
		return orTables;
	}

	public void setOrTables(String orTables) {
		this.orTables = orTables;
	}

	public String getOrColumns() {
		return orColumns;
	}

	public void setOrColumns(String orColumns) {
		this.orColumns = orColumns;
	}

	public boolean isSelectQuery() {
		return selectQuery;
	}

	public void setSelectQuery(boolean selectQuery) {
		this.selectQuery = selectQuery;
	}

	public boolean isUpdateQuery() {
		return updateQuery;
	}

	public void setUpdateQuery(boolean updateQuery) {
		this.updateQuery = updateQuery;
	}

	public boolean isInsertQuery() {
		return insertQuery;
	}

	public void setInsertQuery(boolean insertQuery) {
		this.insertQuery = insertQuery;
	}

	public boolean isCreateQuery() {
		return createQuery;
	}

	public void setCreateQuery(boolean createQuery) {
		this.createQuery = createQuery;
	}

	public boolean isAlterQuery() {
		return alterQuery;
	}

	public void setAlterQuery(boolean alterQuery) {
		this.alterQuery = alterQuery;
	}

	public boolean isDeleteQuery() {
		return deleteQuery;
	}

	public void setDeleteQuery(boolean deleteQuery) {
		this.deleteQuery = deleteQuery;
	}

	public boolean isDropQuery() {
		return dropQuery;
	}

	public void setDropQuery(boolean dropQuery) {
		this.dropQuery = dropQuery;
	}

}
