package sqloutput;

import java.util.Arrays;
import java.util.List;

public class JoinTypes {

	public static final String JOIN = "JOIN";
	public static final String FULL = "FULL";
	public static final String OUTER = "OUTER";
	public static final String INNER = "INNER";
	public static final String LEFT = "LEFT";
	public static final String RIGHT = "RIGHT";

	public static final List<String> JOIN_TYPES = Arrays.asList(JoinTypes.JOIN, JoinTypes.FULL, JoinTypes.INNER,
			JoinTypes.OUTER, JoinTypes.LEFT, JoinTypes.RIGHT);
	
}
