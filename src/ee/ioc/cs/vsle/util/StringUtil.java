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

    /**
     * Checks if the specified string is a valid Java identifier.
     * @param name the string to validate
     * @return true if the name is a valid Java identifier, false otherwise
     */
    public static boolean isJavaIdentifier(String name) {
    	if (name == null || name.length() < 1)
    		return false;
    	
    	if (!Character.isJavaIdentifierStart(name.charAt(0)))
    		return false;
    	
    	for (int i = 1; i < name.length(); i++)
    		if (!Character.isJavaIdentifierPart(name.charAt(i)))
    			return false;
    	
    	return true;
    }
}