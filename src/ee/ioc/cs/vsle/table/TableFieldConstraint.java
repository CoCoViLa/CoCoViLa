/**
 * 
 */
package ee.ioc.cs.vsle.table;

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

//    private static Map<String, TableConstraintFactory> registeredConstraintFactories
//                    = new LinkedHashMap<String, TableConstraintFactory>();
//    
//    static {
//        registeredConstraintFactories.put( "List", new List.Factory() );
//        registeredConstraintFactories.put( "Range", new Range.Factory() );
//    }
    
    //if this is null, then constraint accepts any type
    private java.util.List<String> acceptedTypes;
    private java.util.List<Class<? extends TableFieldConstraint>> compatibleConstraints;
    
    public boolean acceptType( String type ) {
        return acceptedTypes == null ? true : acceptedTypes.contains( type );
    }
    
//    public static String[] getRegisteredConstraintNames() {
//        Collection<String> names = registeredConstraintFactories.keySet();
//        return names.toArray( new String[names.size()] );
//    }
//    
//    public static TableConstraintFactory getConstraintFactory( String name ) {
//        return registeredConstraintFactories.get( name );
//    }
    
    public abstract String getName();
    
    public abstract <T> boolean verify( T obj );
    
    public abstract String printConstraint();
    
    public static abstract class TableConstraintFactory implements IFactory {
        public abstract <T> TableFieldConstraint getGenericInstance();
        
        @Override
        public String getInterfaceInstance() {
            return null;
        }
        
        @Override
        public String getDescription() {
            return null;
        }
    }
    
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
    }
    
}
