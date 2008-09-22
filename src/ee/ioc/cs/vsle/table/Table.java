/**
 * 
 */
package ee.ioc.cs.vsle.table;

import java.util.*;

import ee.ioc.cs.vsle.table.event.*;
import ee.ioc.cs.vsle.util.*;

/**
 * 
 * Class representing Structural Table. All conditions that are verified 
 * for data selection in structural tables are represented in the form of
 * two lists: lists of conditions for choosing a row and a column from the table.
 * For each row and each column a set of conditions are marked to hold true in order
 * to select a value from the table. 
 * Rules bind table variables, values and conditions.
 * 
 * @author pavelg
 */
public final class Table implements IStructuralExpertTable {
    
    public static final String TABLE_KEYWORD = "@table";
    
    private String tableId;
    private TableFieldList inputList = new TableFieldList();
    private TableField output;
    private List<Rule> hrules = new ArrayList<Rule>();
    private List<Rule> vrules = new ArrayList<Rule>();
    private List<DataRow> data = new ArrayList<DataRow>();
    
    private int lastHorizontalId = -1;
    private int lastVerticalId = -1;
    
    /**
     * Constructor
     * 
     * @param id table name
     */
    Table( String id ) {
        
        assert id != null : "Table ID cannot be null";
        
        this.tableId = id;
    }
    
    /**
     * Creates an empty table with one row and one column
     * 
     * @param id
     * @return
     */
    public static Table createEmptyTable( String id ) {
        
        Table table = new Table( id );
        table.addEmptyRow( 0 );
        
        return table;
    }
    
    /**
     * @return the id
     */
    public String getTableId() {
        return tableId;
    }

    /**
     * Assumes that newTableId and the correctness of fields has been checked from the outside
     * 
     * If some input fields are removed or the type is changed, corresponding rules will be also removed
     * 
     * If type of an output is changed, will try to cast the data to new type, otherwise delete values
     * 
     * @param newTableId
     * @param inputFields
     * @param outputField
     */
    public void changePropertiesAndVerify( String newTableId, Collection<TableField> inputFields, TableField outputField ) {
        
        tableId = newTableId;
        
        int eventTypeMask = 0;
        
        //handle output
        if( !outputField.getType().equals( output.getType() ) ) {
            
            eventTypeMask |= TableEvent.DATA;
            
            String newType = outputField.getType();
            
            for ( DataRow row : data ) {
                for ( DataCell cell : row.cells ) {
                    Object value = cell.getValue();
                    
                    if( value != null ) {
                        Object newValue;
                        
                        try {
                            newValue = TypeUtil.createObjectFromString( newType, value.toString() );
                        } catch ( Exception e ) {
                            newValue = null;
                        }
                        cell.setValue( newValue );
                    }
                }
            }
        }
        
        output = outputField;
        
        //handle inputs
        TableFieldList oldInputs = new TableFieldList();
        oldInputs.addAll( inputList );
        
        inputList.clear();
        
        for ( TableField newInput : inputFields ) {
            
//            TableField oldInput;
            
            if( ( /*oldInput = */oldInputs.getFieldByID( newInput.getId() ) ) != null ) {
                //remove field that is totally equal (by type and by id)
                oldInputs.remove( newInput );
            }
            
            inputList.add( newInput );
        }

        //handle rules
        int count = hrules.size();
        
        //delete rules for fields that have been removed
        for ( Iterator<Rule> it =  hrules.iterator(); it.hasNext();  ) {
            
            Rule rule = it.next();
            
            if( oldInputs.contains( rule.getField() ) ) {
                it.remove();
            }
        }
        
        if( count != hrules.size() ) {
            eventTypeMask |= TableEvent.HRULES;
        }
        
        count = vrules.size();
        
        for ( Iterator<Rule> it =  vrules.iterator(); it.hasNext();  ) {
            
            Rule rule = it.next();
            
            if( oldInputs.contains( rule.getField() ) ) {
                it.remove();
            }
        }

        if( count != vrules.size() ) {
            eventTypeMask |= TableEvent.VRULES;
        }
        
        if( eventTypeMask > 0 ) {
            TableEvent.dispatchEvent( new TableEvent( this, eventTypeMask ) );
        }
    }

    /**
     * @param field
     */
    public void addInputFields( Collection<TableField> fields ) {
        
        inputList.addAll( fields );
    }

    /**
     * @return
     */
    public List<TableField> getInputFields() {
        return new ArrayList<TableField>( inputList );
    }
    
    /**
     * @param field
     */
    public void setOutputField( TableField field ) {
        output = field;
    }
    
    /**
     * @return
     */
    public TableField getOutputField() {
        return output;
    }
    
    /**
     * @param rules
     */
    void addHRules( Collection<Rule> rules ) {
        hrules.addAll( rules );
    }
    
    /**
     * @param position
     * @param rule
     */
    public void addHRule( int position, Rule rule ) {
        hrules.add( position, rule );
    }
    
    /**
     * @param from
     * @param to
     */
    public void moveHRule( int from, int to ) {
        hrules.add( to, hrules.remove( from ) );
    }
    
    /**
     * @param position
     */
    public void removeHRule( int position ) {
        hrules.remove( position );
    }
    
    /**
     * @param position
     * @param rule
     */
    public void addVRule( int position, Rule rule ) {
        vrules.add( position, rule );
    }

    /**
     * @param from
     * @param to
     */
    public void moveVRule( int from, int to ) {
        vrules.add( to, vrules.remove( from ) );
    }
    
    /**
     * @param position
     */
    public void removeVRule( int position ) {
        vrules.remove( position );
    }
    
    /**
     * @return
     */
    public List<Rule> getHRules() {
        return new ArrayList<Rule>( hrules );
    }
    
    /**
     * @param rules
     */
    void addVRules( Collection<Rule> rules ) {
        vrules.addAll( rules );
    }
    
    /**
     * @return
     */
    public List<Rule> getVRules() {
        return new ArrayList<Rule>( vrules );
    }
    
    /**
     * @param position
     * @return
     */
    public int addEmptyRow( int position ) {
        int id = getNextHorizontalId();
        DataRow anyRow = data.size() > 0 ? data.get( 0 ) : null;
        DataRow newRow = new DataRow( id );
        data.add( position, newRow );
        
        if( anyRow != null ) {
            for ( DataCell cell : anyRow.getCells() ) {
                newRow.addCell( new DataCell( cell.verticalId, null ) );
            }
        } else {//handle adding of the first row to the table
            newRow.addCell( new DataCell( getNextVerticalId(), null ) );
        }
            
        return id;
    }
    
    /**
     * @param position
     * @return
     */
    public int addEmptyColumn( int position ) {
        int id = getNextVerticalId();
        
        for ( DataRow row : data ) {
            row.cells.add( position, new DataCell( id, null ) );
        }
        return id;
    }
    
    /**
     * @param from
     * @param to
     */
    public void moveDataRow( int from, int to ) {
        data.add( to, data.remove( from ) );
    }
    
    /**
     * @param from
     */
    public void removeDataRow( int from ) {
        if( data.size() <= 1 )
            return;
        
        data.remove( from );
    }
    
    /**
     * @param from
     * @param to
     */
    public void moveDataColumn( int from, int to ) {
        
        for ( DataRow row : data ) {
            row.cells.add( to, row.cells.remove( from ) );
        }
    }
    
    /**
     * @param from
     */
    public void removeDataColumn( int from ) {
        
        for ( DataRow row : data ) {
            if( row.cells.size() <= 1 )
                return;
            
            row.cells.remove( from );
        }
    }

    /**
     * @param horizontalId
     * @param verticalId
     * @param value
     */
    void addDataCell( int horizontalId, int verticalId, String value ) {
        
        DataRow row;
        
        if( ( row = getDataRowById( horizontalId ) ) == null ) {
            data.add( row = new DataRow( horizontalId ) );
        }
        
        if( row.getCell( verticalId ) != null ) {
            throw new TableException( "Dublicate cell! row=" + horizontalId + ", col=" + verticalId );
        }
        
        row.addCell( new DataCell( verticalId, createDataObjectFromString( value ) ) );
        
        if( horizontalId > lastHorizontalId ) {
            lastHorizontalId = horizontalId;
        }
        
        if( verticalId > lastVerticalId ) {
            lastVerticalId = verticalId;
        }
    }
    
    /**
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    public Object getCellValueAt( int rowIndex, int columnIndex ) {
        return data.get( rowIndex ).getCells().get( columnIndex ).getValue();
    }
    
    /**
     * @param value
     * @param rowIndex
     * @param columnIndex
     */
    public void setCellValueAt( Object value, int rowIndex, int columnIndex ) {
        data.get( rowIndex ).getCells().get( columnIndex ).setValue( value );
    }
    
    /**
     * @param value
     * @return
     */
    public Object createDataObjectFromString( String value ) {
        try {
            return TypeUtil.createObjectFromString( output.getType(), value );
        } catch ( Exception e ) {
            throw new TableException( "Unable to create object from string, type=" + 
                    output.getType() + ", value=" + value, e );
        }
    }
    
    /**
     * @param id
     * @return
     */
    DataRow getDataRowById( int id ) {
        for ( DataRow row : data ) {
            if( id == row.getId() ) {
                return row;
            }
        }
        
        return null;
    }
    
    /**
     * @return
     */
    private int getNextHorizontalId() {
        return ++lastHorizontalId;
    }
    
    /**
     * @return
     */
    private int getNextVerticalId() {
        return ++lastVerticalId;
    }
    
    /**
     * @return
     */
    public List<Integer> getOrderedRowIds() {
        List<Integer> ids = new ArrayList<Integer>();
        
        for ( DataRow row : data ) {
            ids.add( row.getId() );
        }
        
        return ids;
    }
    
    /**
     * @return
     */
    public List<Integer> getOrderedColumnIds() {
        List<Integer> ids = new ArrayList<Integer>();
        
        Iterator<DataRow> it = data.iterator();
        
        if( it.hasNext() ) {
            for ( DataCell cell : it.next().getCells() ) {
                ids.add( cell.getVerticalId() );
            }
        }
        return ids;
    }
    
    /**
     * Returns input field by its id
     * 
     * @param id
     * @return
     */
    TableField getInput( String id ) {
        for ( TableField tableField : inputList ) {
            if( id.equals( tableField.getId() ) ) {
                return tableField;
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see ee.ioc.cs.vsle.table.IStructuralExpertTable#queryTable(java.lang.Object[])
     */
    public synchronized Object queryTable( Object[] args ) {
        
        //db.p( "queryTable(): " + Arrays.toString( args ) );
        
        int rowId = checkRules( getOrderedRowIds(), hrules, args );
        int colId = checkRules( getOrderedColumnIds(), vrules, args );
        
        if( rowId == -1 || colId == -1 ) {
            throw new TableException( "No valid rules for current input: " + Arrays.toString( args ) );
        }

        // Get row by rowId which is not always the same as array index
        DataRow row = null;
        for (DataRow r : data) {
            if (r.getId() == rowId) {
                row = r;
                break;
            }
        }

        if (row == null) {
            // should never happen, or checkRules returned invalid rowId
            throw new TableException("Cannot find row with id = " + rowId);
        }

        DataCell cell = row.getCell(colId);

        if( cell == null || cell.getValue() == null ) {
            throw new TableException( "Cell value not specified" );
        }
        
        return cell.getValue();
    }

    /**
     * Verifies conditions and returns corresponding index
     * 
     * @param ids
     * @param rules
     * @param args
     * @return
     */
    private int checkRules( List<Integer> ids, List<Rule> rules, Object[] args ) {
        outer: for ( Integer id : ids ) {
            
            //if a row does not contain any rule entries it is valid by default
            boolean isOK = true;
            
            for ( Rule rule : rules ) {
                if ( rule.getEntries().contains( id ) ) {
                    isOK &= rule.verifyCondition( args[ inputList.indexOf( rule.getField() )] );
                }
                
                if( !isOK ) 
                    continue outer;
            }
            
            return id;
        }
        
        return -1;
    }
    
    /**
     * @return
     */
    public int getRowCount() {
        return data.size();
    }
    
    /**
     * @return
     */
    public int getColumnCount() {
        if( data.size() > 0 ) {
            return data.get( 0 ).cells.size();
        }
        
        return 0;
    }
    
    /**
     * 
     */
    synchronized void destroy() {
        inputList.clear();
        output = null;
        hrules.clear();
        vrules.clear();
        data.clear();
    }
    
    /**
     * @author pavelg
     *
     */
    private class DataRow {
        
        private int horizontalId;
        private List<DataCell> cells = new ArrayList<DataCell>();
        
        /**
         * @param id
         */
        public DataRow( int id ) {
            horizontalId = id;
        }
        
        /**
         * @return
         */
        public int getId() {
            return horizontalId;
        }
        
        /**
         * @param cell
         */
        void addCell( DataCell cell ) {
            cells.add( cell );
        }
        
        /**
         * @param position
         * @param cell
         */
        void addCell( int position, DataCell cell ) {
            cells.add( position, cell );
        }
        
        /**
         * @param verticalId
         * @return
         */
        DataCell getCell( int verticalId ) {
            for ( DataCell cell : cells ) {
                if( verticalId == cell.getVerticalId() ) {
                    return cell;
                }
            }

            return null;
        }
        
        /**
         * @return copy
         */
        public List<DataCell> getCells() {
            return new ArrayList<DataCell>( cells );
        }
        
    }
    
    /**
     * Represents table cell. 
     * Contains value, row and column index.
     */
    private class DataCell {
        private int verticalId;
        private Object value;
        
        public DataCell( int verticalId, Object value ) {
            this.verticalId = verticalId;
            this.value = value;
        }

        /**
         * @return the col
         */
        public int getVerticalId() {
            return verticalId;
        }

        /**
         * @return the value
         */
        public Object getValue() {
            return value;
        }

        /**
         * @param value
         */
        public void setValue( Object value ) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Data cell: " /*+ "row=" + horizontalId + ", "+*/+"col=" + verticalId + ", value=" + value;
        }
    }
    
}
