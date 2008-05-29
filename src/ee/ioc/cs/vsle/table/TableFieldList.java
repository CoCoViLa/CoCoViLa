/**
 * 
 */
package ee.ioc.cs.vsle.table;

import java.util.*;

/**
 * @author pavelg
 *
 * This list does not allow duplicate additions (by the id)
 */
public class TableFieldList extends ArrayList<TableField> {

    @Override
    public void add( int index, TableField element ) {
        checkField( element );
        super.add( index, element );
    }

    @Override
    public boolean add( TableField element ) {
        checkField( element );
        return super.add( element );
    }

    @Override
    public boolean addAll( Collection<? extends TableField> c ) {
        checkAllFields( c );
        return super.addAll( c );
    }

    @Override
    public boolean addAll( int index, Collection<? extends TableField> c ) {
        checkAllFields( c );
        return super.addAll( index, c );
    }

    private void checkAllFields( Collection<? extends TableField> c ) {
        
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
    
    public TableField getFieldByID( String id ) {
        
        for ( TableField field : this ) {
            if( field.getId().equals( id ) ) {
                return field;
            }
        }
        
        return null;
    }
}
