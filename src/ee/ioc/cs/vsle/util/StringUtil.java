package ee.ioc.cs.vsle.util;

/**
 * Title: String Utility class.
 * Copyright: Copyright (c) 2004
 * @author Aulo Aasmaa
 * @version 1.0
 */

public class StringUtil {

	/*
	 * Constants for XML data types
	 */
	public static final String CDATA = "CDATA";

    public static String indent(int size) {
        StringBuffer sb = new StringBuffer();
		for (int i = 0; i < size; i++) {
			sb.append(' ');
		}
		return sb.toString();
	}
}