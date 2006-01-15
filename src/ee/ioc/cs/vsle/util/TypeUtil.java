package ee.ioc.cs.vsle.util;

public class TypeUtil {

	public static boolean isPrimitive( String type ) {
		
		return type.equals("int") || type.equals("double") || type.equals("float")
			|| type.equals("long") || type.equals("short") || type.equals("byte")
			|| type.equals("boolean") || type.equals("char");
	}
	
	public static boolean isArray( String type ) {
        int length = type.length();

        return ( type.length() >= 2 && type.substring( length - 2, length ).equals( "[]" ) );
    }
    
    public static String getTypeWithoutArray( String type ) {
        return type.substring( 0, type.length() - 2 );
    }
    
    public static boolean isPrimitiveOrString( String type ) {
		return type.equals("String") || isPrimitive( type );
	}
}