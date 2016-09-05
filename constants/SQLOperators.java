package constants;

import java.util.Arrays;
import java.util.List;

public class SQLOperators {
	

	public static final String AMPERSAND = "&";
	public static final String PIPE = "|";
	public static final String GREATERTHAN = ">";
	public static final String LESSTHAN = "<";
	public static final String GREATEREQUALTHAN = ">=";
	public static final String LESSEQUALTHAN = "<=";
	public static final String DIAMOND = "<>";
	public static final String LPAREN = "(";
	public static final String RPAREN = ")";
	public static final String PLUS = "+";
	public static final String MINUS = "-";
	public static final String DIVIDED = "/";
	public static final String MODULO = "%";
	public static final String APOSTROPHE = "'";
	public static final String QUOTE = "\"";
	public static final String OCTOTHORPE = "#";
	public static final String AROBAS = "@";
	public static final String EQUALS = "=";
	
	
	public static final List<String> OPERATORS = Arrays.asList(AMPERSAND, PIPE, GREATERTHAN, LESSTHAN,
			GREATEREQUALTHAN, LESSEQUALTHAN, DIAMOND, EQUALS, PLUS, MINUS, DIVIDED, MODULO, SQLKeyWords.IS);

}
