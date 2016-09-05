package constants;

import java.util.Arrays;
import java.util.List;

public class SQLExceptions {

	// Contains the values that should not be taken into account. Hackish
	// solution, to be fixed later.

	public static final List<String> QUERY_EXCEPTIONS = (List<String>) Arrays.asList("INDEX", "TRIGGER", "' IVR '");

	public static boolean isException(String query) {
		for (String entry : QUERY_EXCEPTIONS) {
			if (query.contains(entry)) {
				return true;
			}
		}
		return false;

	}

}
