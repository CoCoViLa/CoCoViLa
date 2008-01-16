package ee.ioc.cs.vsle.util;

import static ee.ioc.cs.vsle.util.TypeUtil.*;

public class TypeToken implements Comparable<TypeToken> {

    public static final TypeToken TOKEN_INT = new TypeToken( 3, TYPE_INT, Integer.class, int.class, "intValue" );
    public static final TypeToken TOKEN_DOUBLE = new TypeToken( 6, TYPE_DOUBLE, Double.class, double.class, "doubleValue" );
    public static final TypeToken TOKEN_FLOAT = new TypeToken( 5, TYPE_FLOAT, Float.class, float.class, "floatValue" );
    public static final TypeToken TOKEN_CHAR = new TypeToken( 0, TYPE_CHAR, Character.class, char.class, "charValue" );
    public static final TypeToken TOKEN_BYTE = new TypeToken( 1, TYPE_BYTE, Byte.class, byte.class, "byteValue" );
    public static final TypeToken TOKEN_SHORT = new TypeToken( 2, TYPE_SHORT, Short.class, short.class, "shortValue" );
    public static final TypeToken TOKEN_LONG = new TypeToken( 4, TYPE_LONG, Long.class, long.class, "longValue" );
    public static final TypeToken TOKEN_BOOLEAN = new TypeToken( 0, TYPE_BOOLEAN, Boolean.class, boolean.class, "booleanValue" );
    public static final TypeToken TOKEN_OBJECT = new TypeToken( 0, null, null, null, "" );
    
    private String m_type;
    private String m_objType;
    private String m_method;
    private Class<?>  m_wclass;
    private Class<?>  m_pclass;
    private int priority;
    
    private TypeToken( int priority, String type, Class<?> wrapperClass, Class<?> primeClass, String method ) {
    	this.priority = priority;
        m_type = type;
        m_objType = wrapperClass != null ? wrapperClass.getName() : "";
        m_method = method;
        m_wclass = wrapperClass;
        m_pclass = primeClass;
    }

    public String getType() {
        return m_type;
    }

    public String getObjType() {
        return m_objType;
    }

    public String getMethod() {
        return m_method;
    }
    
    public Class<?> getWrapperClass() {
    	return m_wclass;
    }
    
    public Class<?> getPrimeClass() {
    	return m_pclass;
    }

	public int compareTo(TypeToken o) {
		
		return new Integer( priority ).compareTo( new Integer( o.priority ) );
	}
	
	public static TypeToken getTypeToken( String varType ) {
    	
    	TypeToken token;
    	
    	if ( varType.equals( TypeToken.TOKEN_INT.getType() ) ) {
			token = TypeToken.TOKEN_INT;
		} else if ( varType.equals( TypeToken.TOKEN_DOUBLE.getType() ) ) {
			token = TypeToken.TOKEN_DOUBLE;
		} else if ( varType.equals( TypeToken.TOKEN_FLOAT.getType() ) ) {
			token = TypeToken.TOKEN_FLOAT;
		} /*else if ( varType.equals( TOKEN_CHAR.getType() ) ) {
			token = TOKEN_CHAR;
		} */else if ( varType.equals( TypeToken.TOKEN_BYTE.getType() ) ) {
			token = TypeToken.TOKEN_BYTE;
		} else if ( varType.equals( TypeToken.TOKEN_SHORT.getType() ) ) {
			token = TypeToken.TOKEN_SHORT;
		} else if ( varType.equals( TypeToken.TOKEN_LONG.getType() ) ) {
			token = TypeToken.TOKEN_LONG;
		} else if ( varType.equals( TypeToken.TOKEN_BOOLEAN.getType() ) ) {
			token = TypeToken.TOKEN_BOOLEAN;
		} else {
			token = TypeToken.TOKEN_OBJECT;
		}
    	
    	return token;
    }
}
