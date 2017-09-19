/**
 * 
 */
package ee.ioc.cs.vsle.table;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.*;

import ee.ioc.cs.vsle.util.*;

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
    
    public abstract String getSymbol();
    
    public abstract String getOppositeSymbol();
    
    public abstract boolean acceptsType( String type );
    
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

        @Override
        public String getOppositeSymbol() {
            return "\u2260";
        }

        @Override
        public String getSymbol() {
            return "=";
        }

        @Override
        public boolean acceptsType( String type ) {
            return true;
        }

    };

    /**
     * COND_LESS
     */
    public final static Condition COND_LESS = new Condition() {

        @SuppressWarnings( "unchecked" )
        @Override
        public <T> boolean verify( T obj1, T obj2 ) {
            return ((Comparable<T>)obj2).compareTo( obj1 ) == -1;
        }

        @Override
        public String getKeyword() {
            return "less";
        }
        
        @Override
        public String getOppositeSymbol() {
            return "\u2265";
        }

        @Override
        public String getSymbol() {
            return "<";
        }

        @Override
        public boolean acceptsType( String type ) {
            return !TypeUtil.isArray( type );
        }
    };

    /**
     * COND_LESS_OR_EQUAL
     */
    public final static Condition COND_LESS_OR_EQUAL = new Condition() {

        @SuppressWarnings( "unchecked" )
        @Override
        public <T> boolean verify( T obj1, T obj2 ) {
            int result = ((Comparable<T>)obj2).compareTo( obj1 ); 
            return result == -1 || result == 0;
        }

        @Override
        public String getKeyword() {
            return "leq";
        }
        
        @Override
        public String getOppositeSymbol() {
            return ">";
        }

        @Override
        public String getSymbol() {
            return "\u2264";
        }

        @Override
        public boolean acceptsType( String type ) {
            return !TypeUtil.isArray( type );
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
        
        @Override
        public String getOppositeSymbol() {
            return "\u2209";
        }

        @Override
        public String getSymbol() {
            return "\u2208";
        }

        @Override
        public boolean acceptsType( String type ) {
            return !TypeUtil.isArray( type );
        }
    };
    
    /**
     * COND_IN_INPUT_ARRAY
     */
    public final static Condition COND_IN_INPUT_ARRAY = new Condition() {

        @Override
        public boolean verify( Object elem, Object inputArray ) {
            
            int len = Array.getLength( inputArray );
            
            for ( int i = 0; i < len; i++ ) {
                if( Array.get( inputArray, i ).equals( elem ) ) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getKeyword() {
            return "in_input_array";
        }
        
        @Override
        public String getOppositeSymbol() {
            return "\u220C";
        }

        @Override
        public String getSymbol() {
            return "\u220B";
        }

        @Override
        public boolean acceptsType( String type ) {
            return TypeUtil.isArray( type );
        }
    };
    
    /**
     * REG_EXP_MATCH
     */
    public final static Condition REG_EXP_MATCH = new Condition() {

        @Override
        public <T> boolean verify( T obj1, T obj2 ) {
            Pattern p = Pattern.compile( (String)obj1 );
            return p.matcher( (String)obj2 ).matches();
        }

        @Override
        public String getKeyword() {
            return "regexp";
        }
        
        @Override
        public String getOppositeSymbol() {
            return "\u00AC^$";
        }

        @Override
        public String getSymbol() {
            return "^$";
        }

        @Override
        public boolean acceptsType( String type ) {
            return TypeUtil.isString( type );
        }
    };
    
    /**
     * SUBSTRING
     */
    public final static Condition SUBSTRING = new Condition() {

        @Override
        public <T> boolean verify( T obj1, T obj2 ) {
            return obj1.toString().indexOf( obj2.toString() ) >= 0;
        }

        @Override
        public String getKeyword() {
            return "substr";
        }
        
        @Override
        public String getOppositeSymbol() {
            return "\u22E2";
        }

        @Override
        public String getSymbol() {
            return "\u2291";
        }

        @Override
        public boolean acceptsType( String type ) {
            return TypeUtil.isString( type );
        }
    };
    
    /**
     * SUBSTRING of Input
     */
    public final static Condition SUBSTRING_Of_INPUT = new Condition() {

        @Override
        public <T> boolean verify( T obj1, T obj2 ) {
            return obj2.toString().indexOf( obj1.toString() ) >= 0;
        }

        @Override
        public String getKeyword() {
            return "substr_of_input";
        }
        
        @Override
        public String getOppositeSymbol() {
            return "\u22E3";
        }

        @Override
        public String getSymbol() {
            return "\u2292";
        }

        @Override
        public boolean acceptsType( String type ) {
            return TypeUtil.isString( type );
        }
    };
    
    /**
     * SUBSET
     */
    public final static Condition SUBSET = new Condition() {

        @SuppressWarnings( "unchecked" )
        @Override
        public <T> boolean verify( T obj1, T obj2 ) {
            return Arrays.asList( obj1 ).containsAll( Arrays.asList( obj2 ) );
        }

        @Override
        public String getKeyword() {
            return "subset";
        }
        
        @Override
        public String getOppositeSymbol() {
            return "\u2288";
        }

        @Override
        public String getSymbol() {
            return "\u2286";
        }

        @Override
        public boolean acceptsType( String type ) {
            return TypeUtil.isArray( type );
        }
    };
    
    /**
     * STRICT_SUBSET
     */
    public final static Condition STRICT_SUBSET = new Condition() {

        @SuppressWarnings( "unchecked" )
        @Override
        public <T> boolean verify( T obj1, T obj2 ) {
            List<?> l1 = Arrays.asList( obj1 );
            List<?> l2 = Arrays.asList( obj2 );
            return l1.containsAll( l2 )
                && l1.size() > l2.size();
        }

        @Override
        public String getKeyword() {
            return "strictsubset";
        }
        
        @Override
        public String getOppositeSymbol() {
            return "\u2284";
        }

        @Override
        public String getSymbol() {
            return "\u2282";
        }

        @Override
        public boolean acceptsType( String type ) {
            return TypeUtil.isArray( type );
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
            map.put( REG_EXP_MATCH.getKeyword(), REG_EXP_MATCH );
            map.put( SUBSTRING.getKeyword(), SUBSTRING );
            map.put( SUBSET.getKeyword(), SUBSET );
            map.put( STRICT_SUBSET.getKeyword(), STRICT_SUBSET );
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
