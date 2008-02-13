package ee.ioc.cs.vsle.util;

import java.lang.reflect.*;

import ee.ioc.cs.vsle.vclass.*;

/**
 * Utility class for manipulating types
 */
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
	
	/**
	 * @param type
	 * @return true if type is primitive, false otherwise
	 */
	public static boolean isPrimitive( String type ) {
		
		return type.equals(TYPE_INT) || type.equals(TYPE_DOUBLE) || type.equals(TYPE_LONG)
			|| type.equals(TYPE_FLOAT) || type.equals(TYPE_SHORT) || type.equals(TYPE_BYTE)
			|| type.equals(TYPE_BOOLEAN) || type.equals(TYPE_CHAR);
	}
	
	/**
	 * @param type
	 * @return true if type is array
	 */
	public static boolean isArray( String type ) {

        return type.endsWith( "[]" );
    }
    
    /**
     * @param type should be array
     * @return array component type
     */
    public static String getArrayComponentType( String type ) {
        return type.substring( 0, type.length() - 2 );
    }
    
    /**
     * @param type
     * @return true if type is primitive or String
     */
    public static boolean isPrimitiveOrString( String type ) {
		return type.equals(TYPE_STRING) || isPrimitive( type );
	}
    
    /**
     * Used in code generation
     * 
     * @param field
     * @param prx
     * @return
     */
    public static String getDeclaration( ClassField field, String prx ) {

    	String value = field.isConstant() ? field.getValue() : "";
		return getDeclaration( field.getName(), field.getType(), field.isAlias(), 
				field.isSpecField(), field.isStatic(), value, prx );

    }
    
    /**
     * Creates variable declaration string for Java language
     * 
     * @param varName
     * @param type
     * @param isAlias
     * @param isClass
     * @param isStatic
     * @param value
     * @param prx
     * @return
     */
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
    
    /**
     * This method takes String value and returns corresponding Object value 
     * of specified type. 
     * Supports primitive types, Strings and arrays.
     * Array elements should be separated by ClassField.ARRAY_TOKEN="%%"
     * 
     * Example: type="int", value="1" => returns Integer(1).
     * Example: type="double[]", value="1.0%%2.0%%3.0" => double[]{ 1.0, 2.0, 3.0 }
     * 
     * @param type name of the type
     * @param value String value
     * @return corresponding Object value
     * @throws Exception
     */
    public static Object createObjectFromString( String type, String value )
            throws Exception {
        TypeToken token = TypeToken.getTypeToken( type );

        Class<?> clazz = token.getWrapperClass();

        if ( clazz != null ) {
            Method meth = clazz.getMethod( "valueOf",
                    new Class[] { String.class } );
            Object o = meth.invoke( null, new Object[] { value } );
            return o;
        } else if ( type.equals( TYPE_STRING ) ) {
            return value;
        } else if ( isArray( type )
                && isPrimitiveOrString( getArrayComponentType( type ) ) ) {

            token = TypeToken.getTypeToken( getArrayComponentType( type ) );
            clazz = token.getWrapperClass();

            if ( clazz != null ) {

                String[] split = value.split( ClassField.ARRAY_TOKEN );
                Object primeArray = Array.newInstance( token.getPrimeClass(),
                        split.length );

                for ( int j = 0; j < split.length; j++ ) {
                    Method meth = clazz.getMethod( "valueOf",
                            new Class[] { String.class } );
                    Object val = meth.invoke( null, new Object[] { split[j] } );
                    Array.set( primeArray, j, val );
                }
                return primeArray;
            }
            /* equals String[] */
            return value.split( ClassField.ARRAY_TOKEN );

        }

        return null;
    }
    
}