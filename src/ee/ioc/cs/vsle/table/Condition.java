/**
 * 
 */
package ee.ioc.cs.vsle.table;

import java.lang.reflect.*;
import java.util.*;

/**
 * Condition predicates 
 */
public interface Condition {

    /**
     * Verifies condition between two objects
     * 
     * @param <T>
     * @param obj1
     * @param obj2
     * @return
     */
    public abstract <T> boolean verify( T obj1, T obj2 );
    
    /**
     * Keywords are used in rule definitions
     * 
     * @return
     */
    public abstract String getKeyword();
    
    /**
     * COND_EQUALS
     */
    public final static Condition COND_EQUALS = new Condition() {

        @Override
        public boolean verify( Object obj1, Object obj2 ) {
            return obj1.equals( obj2 );
        }

        @Override
        public String getKeyword() {
            return "eq";
        }
    };

    /**
     * COND_LESS
     */
    public final static Condition COND_LESS = new Condition() {

        @Override
        public <T> boolean verify( T obj1, T obj2 ) {
            return ((Comparable<T>)obj2).compareTo( (T)obj1 ) == -1;
        }

        @Override
        public String getKeyword() {
            return "less";
        }
    };

    /**
     * COND_LESS_OR_EQUAL
     */
    public final static Condition COND_LESS_OR_EQUAL = new Condition() {

        @Override
        public <T> boolean verify( T obj1, T obj2 ) {
            int result = ((Comparable<T>)obj2).compareTo( (T)obj1 ); 
            return result == -1 || result == 0;
        }

        @Override
        public String getKeyword() {
            return "leq";
        }
    };
    
    /**
     * COND_IN_ARRAY
     */
    public final static Condition COND_IN_ARRAY = new Condition() {

        @Override
        public boolean verify( Object arrayObj, Object elem ) {
            
            int len = Array.getLength( arrayObj );
            
            for ( int i = 0; i < len; i++ ) {
                if( Array.get( arrayObj, i ).equals( elem ) ) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getKeyword() {
            return "in";
        }
    };
    
    /**
     * Class Factory
     */
    static class Factory {
        private static Map<String, Condition> map = 
            new HashMap<String, Condition>();
        
        static {
            map.put( COND_EQUALS.getKeyword(), COND_EQUALS );
            map.put( COND_LESS.getKeyword(), COND_LESS );
            map.put( COND_LESS_OR_EQUAL.getKeyword(), COND_LESS_OR_EQUAL );
            map.put( COND_IN_ARRAY.getKeyword(), COND_IN_ARRAY );
        }
        
        /**
         * Takes condition keyword and returns its implementation
         * 
         * @param pred
         * @return
         */
        public static Condition getCondition( String pred ) {
            return map.get( pred );
        }
    }
}
