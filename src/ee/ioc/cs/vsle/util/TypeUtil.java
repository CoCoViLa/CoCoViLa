package ee.ioc.cs.vsle.util;

public class TypeUtil {
	public static boolean isArray(String type) {
		int length = type.length();

		if (type.substring(length - 2, length).equals("[]")) {
			return true;
		} else {
			return false;
		}
	} // isArray

};