package ee.ioc.cs.vsle.util;

import ee.ioc.cs.vsle.synthesize.*;
import ee.ioc.cs.vsle.vclass.*;

public class TypeUtil {

	public static final String TYPE_ALIAS = "alias";
	public static final String TYPE_VOID = "void";
	public static final String TYPE_OBJECT = "Object";
	public static final String TYPE_ANY = "any";
	
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
    
    public static String getDeclaration( ClassField field ) {

    	String value = field.isConstant() ? field.getValue() : "";
		return getDeclaration( field.getName(), field.getType(), field.isSpecField(), value );

    }
    
    public static String getDeclaration( String varName, String type, boolean isClass, 
    									/*for constant*/String value ) {
    	if ( TypeUtil.TYPE_VOID.equals( type ) || TypeUtil.TYPE_ANY.equals( type ) ) {
    		return "";
    	} else if ( value != null && value.length() > 0 ) {
    		return "final public " + type + " " + varName + " = " + value + ";\n";
    	} else if ( TypeUtil.isPrimitive( type ) ) {
    		return "public " + type + " " + varName + ";\n";
    	} else if ( TypeUtil.isArray( type ) ) {
    		return "public " + type + " " + varName + " ;\n";
    	} else if ( isClass ) {
    		return "public " + type + " " + varName + " = new " + type + "();\n";
    	} else {
    		return "public " + type + " " + varName + " = new " + type + "();\n";
    	}
    }
}