package ee.ioc.cs.vsle.util;

import java.lang.reflect.*;
import java.util.*;

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
	public static final String TYPE_STRING_FULL = "java.lang.String";
	
	public static final String TYPE_INT = "int";
	public static final String TYPE_DOUBLE = "double";
	public static final String TYPE_LONG = "long";
	public static final String TYPE_FLOAT = "float";
	public static final String TYPE_SHORT = "short";
	public static final String TYPE_BYTE = "byte";
	public static final String TYPE_BOOLEAN = "boolean";
	public static final String TYPE_CHAR = "char";
	
	public final static String ARRAY_TOKEN = "%%";
	
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
		return isString( type ) || isPrimitive( type );
	}
    
    public static boolean isString( String type ) {
        return type.equals(TYPE_STRING) || type.equals(TYPE_STRING_FULL);
    }
    
    public static boolean isIntegral( String type ) {
        return type.equals( TYPE_BYTE ) || type.equals( TYPE_CHAR )
                || type.equals( TYPE_SHORT ) || type.equals( TYPE_INT )
                || type.equals( TYPE_LONG );
    }
    
    public static boolean isFractional( String type ) {
        return type.equals( TYPE_FLOAT ) || type.equals( TYPE_DOUBLE );
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
     * @param isSpec
     * @param isStatic
     * @param value
     * @param prx
     * @return
     */
    private static String getDeclaration( String varName, String type, boolean isAlias, boolean isSpec, boolean isStatic,
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
    	} else if ( isSpec ) {
            return prx + type + " " + varName + " = new " + type + "();\n";
    	} else {
    		return prx + type + " " + varName /*+ " = new " + type + "()"*/ + ";\n";
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
        
        if( value == null ) {
            return null;
        }
        
        TypeToken token = TypeToken.getTypeToken( type );

        Class<?> clazz = token.getWrapperClass();

        if ( isString( type ) ) {
            return value;
        } else if ( clazz != null ) {
            Method meth = clazz.getMethod( "valueOf",
                    new Class[] { String.class } );
            Object o = meth.invoke( null, new Object[] { value } );
            return o;
        } else if ( isArray( type ) ) {
            String componentType = getArrayComponentType( type );
            String[] split = value.split( ARRAY_TOKEN );
            
            if( isPrimitiveOrString( componentType ) ) {

                token = TypeToken.getTypeToken( getArrayComponentType( type ) );
                clazz = token.getWrapperClass();
                System.out.println("clazz " + clazz);
                if ( clazz != null && token != TypeToken.TOKEN_STRING ) {

//                    Object primeArray = Array.newInstance( token.getPrimeClass(),
//                            split.length );
//
//                    for ( int j = 0; j < split.length; j++ ) {
//                        Method meth = clazz.getMethod( "valueOf",
//                                new Class[] { String.class } );
//                        System.out.println("array: " + Arrays.deepToString( split ));
//                        Object val = meth.invoke( null, new Object[] { split[j] } );
//                        Array.set( primeArray, j, val );
//                    }
                    return createObjectArray(
                            split,
                            token.getPrimeClass(),
                            clazz );
                }
                /* equals String[] */
                return split;
            } //object type
            TypeToken tt = TypeToken.getTypeTokenByObjectType( componentType );
            return createObjectArray(
                    split,
                    tt.getWrapperClass(),
                    tt.getWrapperClass() );
        }

        return null;
    }
    
    private static Object createObjectArray( String[] values, Class<?> componentType, Class<?> wrapperClass ) 
        throws Exception {
        
        Object arr = Array.newInstance( componentType, values.length );
        for ( int j = 0; j < values.length; j++ ) {
            Method meth = wrapperClass.getMethod( "valueOf",
                    new Class[] { String.class } );
            Object val = meth.invoke( null, new Object[] { values[j] } );
            Array.set( arr, j, val );
        }
        return arr;
    }
    
    /**
     * Creates a string from a given array with elements separated by ARRAY_TOKEN
     * 
     * @param array
     * @return
     */
    public static String toTokenString( Object array ) {
        return toString( array, "", "", ARRAY_TOKEN, "" );
    }
    
    /**
     * Universal toString() method
     * 
     * @param array
     * @return
     */
    public static String toString( Object array ) {
        return toString( array, "[", "]", ", ", "[]" );
    }
    
    /**
     * Private toString implementation used for different purposes
     * 
     * @param array
     * @param start
     * @param end
     * @param separator
     * @param empty
     * @return
     */
    private static String toString( Object array, String start, String end, String separator, String empty ) {
        
        if( array == null ) {
            return "null";
        } else if( !array.getClass().isArray() ) {
            return array.toString();
        }
        
        int maxLength = Array.getLength( array ) - 1;
        
        if( maxLength == -1 ) {
            return empty;
        }
        
        StringBuilder b = new StringBuilder();
        b.append(start);        
        for ( int i = 0;; i++ ) {
            
            b.append( String.valueOf( Array.get( array, i ) ) );
            if( i == maxLength ) {
                return b.append(end).toString();
            }
            b.append(separator);
        }
    }

}