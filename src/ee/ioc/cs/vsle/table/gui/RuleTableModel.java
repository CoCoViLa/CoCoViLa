package ee.ioc.cs.vsle.table.gui;

import static ee.ioc.cs.vsle.table.gui.RuleTable.*;

import javax.swing.table.*;

import ee.ioc.cs.vsle.table.*;
import ee.ioc.cs.vsle.table.Table.*;

/**
 * @author pavelg
 *
 */
public class RuleTableModel extends AbstractTableModel {

    private int orientation = HORIZONTAL;
    private Table storage;
    private TableEventListener tableListener;
    
    /**
     * Constructor
     * 
     * @param orientation
     * @param tableStorage
     */
    public RuleTableModel( int orientation, Table tableStorage ) {
        
        if( orientation != HORIZONTAL && orientation != VERTICAL ) {
            throw new IllegalArgumentException( "Illegal orientation: " + orientation );
        }
        
        this.orientation = orientation;
        
        storage = tableStorage;
        
        initTableListener();
    }
    
    @Override
    public int getColumnCount() {
        return orientation == HORIZONTAL ? storage.getHRules().size() : storage.getColumnCount();
    }

    @Override
    public int getRowCount() {
        return orientation == HORIZONTAL ? storage.getRowCount() : storage.getVRules().size();
    }

    @Override
    public Boolean getValueAt( int rowIndex, int columnIndex ) {
        if( orientation == HORIZONTAL ) {
            
            int id = storage.getOrderedRowIds().get( rowIndex );
            
            return new Boolean( storage.getHRules().get( columnIndex ).getEntries().contains( new Integer( id ) ) );
        }
        
        int id = storage.getOrderedColumnIds().get( columnIndex );
        
        return new Boolean( storage.getVRules().get( rowIndex ).getEntries().contains( new Integer( id ) ) );
    }
    
    /**
     * Adds new data
     * @param position
     */
    public void addData( int position ) {
        
        if( orientation == HORIZONTAL ) {
            storage.addEmptyRow( position );
            fireTableRowsInserted( position, position + 1 );
        } else {
            storage.addEmptyColumn( position );
            fireTableStructureChanged();
        }
    }
    
    public void moveData( int from, int to ) {
        
        if( orientation == HORIZONTAL ) {
            storage.moveDataRow( from, to );
            fireTableRowsUpdated( from -1, from + 1 );
        } else {
            storage.moveDataColumn( from, to );
            fireTableStructureChanged();
        }
    }

    public void deleteData( int from ) {
        
        if( orientation == HORIZONTAL ) {
            storage.removeDataRow( from );
            fireTableRowsDeleted( from -1, from + 1 );
        } else {
            storage.removeDataColumn( from );
            fireTableStructureChanged();
        }
    }

    /**
     * @param position
     * @param rule
     */
    public void addRule( int position, Rule rule ) {
        
        if( orientation == HORIZONTAL ) {
            storage.addHRule( position, rule );
            
            fireTableStructureChanged();
        } else {
            storage.addVRule( position, rule );
            
            fireTableRowsInserted( position, position + 1 );
        }
    }
    
    public void moveRule( int from, int to ) {
        
        if( orientation == HORIZONTAL ) {
            storage.moveHRule( from, to );
            
            fireTableStructureChanged();
        } else {
            storage.moveVRule( from, to );
            
            fireTableRowsInserted( from - 1, from + 1 );
        }
    }

    public void deleteRule( int position ) {
        if( orientation == HORIZONTAL ) {
            storage.removeHRule( position );
            fireTableStructureChanged();
        } else {
            storage.removeVRule( position );
            
            fireTableRowsDeleted( position, position + 1 );
        }
    }
    
    /**
     * @param index
     * @return
     */
    public Rule getRule( int index ) {
        if( orientation == HORIZONTAL ) {
            return storage.getHRules().get( index );
        }
        
        return storage.getVRules().get( index );
    }
    
    @Override
    public Class<?> getColumnClass( int columnIndex ) {
        return Boolean.class;
    }

    @Override
    public boolean isCellEditable( int rowIndex, int columnIndex ) {
        return true;
    }

    @Override
    public void setValueAt( Object value, int rowIndex, int columnIndex ) {
        
        Boolean isSelected = (Boolean)value;
        Rule rule;
        int id;
        
        if( orientation == HORIZONTAL ) {
            id = storage.getOrderedRowIds().get( rowIndex );
            rule = storage.getHRules().get( columnIndex );
        } else {
            id = storage.getOrderedColumnIds().get( columnIndex );
            rule = storage.getVRules().get( rowIndex );
        }
        
        if( isSelected ) {
            rule.addEntry( id );
        } else {
            rule.removeEntry( id );
        }
    }
    
    private void initTableListener() {
        
        tableListener = new TableEventListener() {

            @Override
            public void tableChanged( TableEvent e ) {
                
                if( ( orientation == HORIZONTAL && ( e.getType() & TableEvent.HRULES ) > 0 )
                        || ( orientation == VERTICAL && ( e.getType() & TableEvent.VRULES ) > 0 ) ) {
                    
                    fireTableStructureChanged();
                }
            }
        };
        
        storage.addTableListener( tableListener );
    }
    
    /**
     * 
     */
    public void destroy() {
        
        storage.removeTableListener( tableListener );
        tableListener = null;
        storage = null;
    }
}
