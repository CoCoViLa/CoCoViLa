/**
 * 
 */
package ee.ioc.cs.vsle.table;

import java.util.*;

import ee.ioc.cs.vsle.table.exception.*;

/**
 * @author pavelg
 *
 * This list does not allow duplicate additions (by the id)
 */
public class TableFieldList <T extends TableField> extends ArrayList<T> {

    @Override
    public void add( int index, T element ) {
        checkField( element );
        super.add( index, element );
    }

    @Override
    public boolean add( T element ) {
        checkField( element );
        return super.add( element );
    }

    @Override
    public boolean addAll( Collection<? extends T> c ) {
        checkAllFields( c );
        return super.addAll( c );
    }

    @Override
    public boolean addAll( int index, Collection<? extends T> c ) {
        checkAllFields( c );
        return super.addAll( index, c );
    }

    private void checkAllFields( Collection<? extends T> c ) {
        
        for ( TableField tableField : c ) {
            checkField( tableField );
        }
    }
    
    private void checkField( TableField f ) {
        if( containsByID( f ) ) {
            throw new TableException( "Fields with the same ID are not allowed!\n" + f + "\nList: " + toString() );
        }
    }
    
    public boolean containsByID( TableField f ) {
        
        return getFieldByID( f.getId() ) != null;
    }
    
    public T getFieldByID( String id ) {
        
        for ( T field : this ) {
            if( field.getId().equals( id ) ) {
                return field;
            }
        }
        
        return null;
    }
}
