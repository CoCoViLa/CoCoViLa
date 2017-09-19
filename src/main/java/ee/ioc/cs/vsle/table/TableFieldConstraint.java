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

import java.util.*;

import ee.ioc.cs.vsle.factoryStorage.*;
import ee.ioc.cs.vsle.util.*;

/**
 * @author pavelg
 * A constraint on a field
 * 
 * Kinds of constraints:
 * Interval: between two values (implemented with spinner)
 * List: only values from the list
 * 
 */
public abstract class TableFieldConstraint {

    //if this is null, then constraint accepts any type
    private java.util.List<String> acceptedTypes;
    private java.util.List<Class<? extends TableFieldConstraint>> compatibleConstraints;
    
    public boolean acceptType( String type ) {
        return acceptedTypes == null ? true : acceptedTypes.contains( type );
    }
    
    public abstract String getName();
    
    public abstract <T> boolean verify( T obj );
    
    public abstract String printConstraint();
    
    public abstract void setValuesFromString( String type, String... args ); 
    
    public abstract boolean isCompatibleWith( java.util.List<TableFieldConstraint> constraints );
    
    public abstract boolean isCorrect();
    
    public static class List extends TableFieldConstraint {
        
        private Set<Object> valueList = new LinkedHashSet<Object>();
        
        @Override
        public String toString() {
            return valueList.getClass().getName();
        }

        @Override
        public <T> boolean verify( T obj ) {
            return valueList.contains( obj );
        }

        /**
         * @return the valueList
         */
        public Object[] getValueList() {
            return valueList.toArray();
        }

        /**
         * @param valueList the valueList to set
         */
        public void setValueList( Object[] valueList ) {
            this.valueList.clear();
            this.valueList.addAll( Arrays.asList( valueList ) );
        }

        @Override
        public String printConstraint() {
            return "List constraint: " + valueList;
        }

        @Override
        public String getName() {
            return "List";
        }
        
        @Override
        public boolean acceptType( String type ) {
            return TypeUtil.isPrimitiveOrString( type );
        }

        @Override
        public void setValuesFromString( String type, String... args ) {
            try {
                String compType = type;
                TypeToken tt = TypeToken.getTypeToken( type );
                if( tt != null && tt != TypeToken.TOKEN_OBJECT ) {
                    compType = tt.getObjType();
                }
                //TODO should avoid number format exception here
                Object o = TypeUtil.createObjectFromString( compType + "[]", args[0] );
                setValueList( (Object[]) o );
            } catch ( Exception e ) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean isCompatibleWith(
                java.util.List<TableFieldConstraint> constraints ) {
            
            for ( TableFieldConstraint tableFieldConstraint : constraints ) {
                if( tableFieldConstraint instanceof List 
                        && this != tableFieldConstraint ) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean isCorrect() {
            return valueList.size() > 0;
        }
    }
    
    public static class Range extends TableFieldConstraint {
        
        private Object min, max;
        
        @SuppressWarnings( "unchecked" )
        @Override
        public <T> boolean verify( T obj ) {
            Comparable<T> comp = (Comparable<T>)obj;
            
            boolean minOk = min == null;
            if( !minOk ) {
                minOk = comp.compareTo( (T)min ) >= 0;
            }
            
            boolean maxOk = max == null;
            if( !maxOk ) {
                maxOk = comp.compareTo( (T)max ) <= 0;
            }
//            System.out.println("Verify " + min + " <= " + obj + " <= " + max + " ");
            return minOk && maxOk;
        }

        /**
         * @return the min
         */
        public Object getMin() {
            return min;
        }

        /**
         * @param min the min to set
         */
        public void setMin( Object min ) {
            this.min = min;
        }

        /**
         * @return the max
         */
        public Object getMax() {
            return max;
        }

        /**
         * @param max the max to set
         */
        public void setMax( Object max ) {
            this.max = max;
        }

        @Override
        public void setValuesFromString( String type, String... args ) {
            String minS = args[0];
            if( minS != null && minS.trim().length() > 0 ) {
                try {
                    setMin( TypeUtil.createObjectFromString( type, minS ) );
                } catch ( Exception e ) {
                    e.printStackTrace();
                   throw new RuntimeException(e);
                }
            } else {
                setMin( null );
            }
            
            String maxS = args[1];
            if( maxS != null && maxS.trim().length() > 0 ) {
                try {
                    setMax( TypeUtil.createObjectFromString( type, maxS ) );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            } else {
                setMax( null );
            }
        }
        
        @Override
        public String printConstraint() {
            return "Range constraint: " 
                    + ( min != null ? min + " <= " : "" ) 
                    + "{0}" 
                    + ( max != null ? " <= " + max : "" );
        }

        @Override
        public String getName() {
            return "Range";
        }
        
        @Override
        public boolean acceptType( String type ) {
            return TypeUtil.isPrimitiveOrString( type );
        }

        @Override
        public boolean isCompatibleWith(
                java.util.List<TableFieldConstraint> constraints ) {
            
            for ( TableFieldConstraint tableFieldConstraint : constraints ) {
                if( tableFieldConstraint instanceof Range 
                        && this != tableFieldConstraint ) {
                    return false;
                }
            }
            return true;
        }

        @SuppressWarnings( { "unchecked", "rawtypes" } )
        @Override
        public boolean isCorrect() {
            if ( min == null && max == null )
                return false;
            else if ( min != null && max != null ) 
                return ((Comparable)min).compareTo( max ) == -1;
            
            return true;
        }
    }
    
}
