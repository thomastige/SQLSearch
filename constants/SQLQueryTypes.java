package constants;

import java.util.Arrays;
import java.util.List;

public class SQLQueryTypes {

	public static final String SELECT = "SELECT";
	public static final String SELECT_INTO = "SELECT INTO";
	public static final String UPDATE = "UPDATE";
	public static final String INSERT = "INSERT";
	public static final String INSERT_INTO = "INSERT INTO";
	public static final String CREATE = "CREATE";
	public static final String ALTER = "ALTER";
	public static final String DELETE = "DELETE";
	public static final String DROP = "DROP";
	public static final String SET = "SET";

	public static final List<String> QUERY_TYPES = (List<String>) Arrays.asList(SELECT_INTO, INSERT_INTO, SELECT, ALTER, CREATE, DELETE, INSERT,
			UPDATE, DROP, SET);

	/*
	 * Not used by SimpleParser
	 */
	public static final List<String> SUPPORTED_QUERY_TYPES = (List<String>) Arrays.asList(SELECT);

}
