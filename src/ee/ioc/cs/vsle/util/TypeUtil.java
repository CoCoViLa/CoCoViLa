package ee.ioc.cs.vsle.util;

import ee.ioc.cs.vsle.vclass.*;

public class TypeUtil {

	public static final String TYPE_ALIAS = "alias";
	public static final String TYPE_VOID = "void";
	public static final String TYPE_OBJECT = "Object";
	public static final String TYPE_ANY = "any";
	public static final String TYPE_THIS = "this";
	public static final String TYPE_STRING = "String";
	
	public static final String TYPE_INT = "int";
	public static final String TYPE_DOUBLE = "double";
	public static final String TYPE_LONG = "long";
	public static final String TYPE_FLOAT = "float";
	public static final String TYPE_SHORT = "short";
	public static final String TYPE_BYTE = "byte";
	public static final String TYPE_BOOLEAN = "boolean";
	public static final String TYPE_CHAR = "char";
	
	public static boolean isPrimitive( String type ) {
		
		return type.equals(TYPE_INT) || type.equals(TYPE_DOUBLE) || type.equals(TYPE_LONG)
			|| type.equals(TYPE_FLOAT) || type.equals(TYPE_SHORT) || type.equals(TYPE_BYTE)
			|| type.equals(TYPE_BOOLEAN) || type.equals(TYPE_CHAR);
	}
	
	public static boolean isArray( String type ) {
        int length = type.length();

        return ( type.length() >= 2 && type.substring( length - 2, length ).equals( "[]" ) );
    }
    
    public static String getTypeWithoutArray( String type ) {
        return type.substring( 0, type.length() - 2 );
    }
    
    public static boolean isPrimitiveOrString( String type ) {
		return type.equals(TYPE_STRING) || isPrimitive( type );
	}
    
    public static String getDeclaration( ClassField field, String prx ) {

    	String value = field.isConstant() ? field.getValue() : "";
		return getDeclaration( field.getName(), field.getType(), field.isAlias(), 
				field.isSpecField(), field.isStatic(), value, prx );

    }
    
    public static String getDeclaration( String varName, String type, boolean isAlias, boolean isClass, boolean isStatic,
    									/*for constant*/String value, String prx ) {
    	
    	if( prx == null ) 
    		prx = "";
    	else if ( prx.length() > 0 )
    		prx = prx.trim().concat( " " );
    	
    	if( isStatic ) {
    		prx += "static ";
    	}
    	
    	if ( TypeUtil.TYPE_VOID.equals( type ) || TypeUtil.TYPE_ANY.equals( type ) 
    			|| isAlias ) {
    		return "";
    	} else if ( value != null && value.length() > 0 ) {
    		return prx + "final " + type + " " + varName + " = " + value + ";\n";
    	} else if ( TypeUtil.isPrimitive( type ) ) {
    		return prx + type + " " + varName + ";\n";
    	} else if ( TypeUtil.isArray( type ) ) {
    		return prx + type + " " + varName + ";\n";
    	} else if ( isClass ) {
    		return prx + type + " " + varName + " = new " + type + "();\n";
    	} else {
    		return prx + type + " " + varName + " = new " + type + "();\n";
    	}
    }
}