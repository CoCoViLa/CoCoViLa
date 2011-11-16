/**
 * 
 */
package ee.ioc.cs.vsle.table;

import java.util.*;

import javax.swing.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.table.event.*;
import ee.ioc.cs.vsle.table.exception.*;
import ee.ioc.cs.vsle.table.gui.*;
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
    public static final String TABLE_WITH_INPUT_MAPPING_KEYWORD = "@tablewithinputmapping";
    
    private String tableId;
    private TableFieldList<InputTableField> inputList = new TableFieldList<InputTableField>();
    private TableFieldList<TableField> outputList = new TableFieldList<TableField>();
    private TableField aliasOutput;
    //this member indicates either a single output 
    //OR the current active element of an alias 
    //(values of which are shown in GUI)
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
    @Override
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
    public void changePropertiesAndVerify( String newTableId, TableFieldList<InputTableField> inputFields, TableFieldList<TableField> outputFields, TableField alias ) {
        
        tableId = newTableId;
        
        int eventTypeMask = 0;
        
        boolean outputsChanged = false;
        if( aliasOutput == null ) {

            if( alias == null ) {//single output remains
                //handle output
                TableField outputField = outputFields.iterator().next();

                if( !outputField.getType().equals( getOutputField().getType() ) ) {

                    outputsChanged = true;
                }
            } else {//change output to alias
                if( outputFields.size() == 1 && outputFields.iterator().next().equals( getOutputField() ) ) {
                    //if alias was created by the element remains the same, do nothing   
                } else {
                    outputsChanged = true;
                }
            }
        } else {//has alias output
            if( alias == null ) {//alias removed
                outputsChanged = true;
            } else {
                if( outputFields.size() == outputList.size() ) {
                    for ( int i = 0; i < outputFields.size(); i++ ) {
                        if( !outputFields.get( i ).equals( outputList.get( i ) ) ) {
                            outputsChanged = true;
                            break;
                        }
                    }
                } else {
                    outputsChanged = true;
                }
            }
        }
        
        if( outputsChanged ) {
            eventTypeMask |= TableEvent.DATA;
            for ( DataRow row : data ) {
                for ( DataCell cell : row.cells ) {
                    cell.update( outputList, outputFields );
                }
            }
        }
        
        aliasOutput = alias;
        outputList.clear();
        outputList.addAll( outputFields );
        output = null;
        
        //handle inputs
        TableFieldList<InputTableField> oldInputs = new TableFieldList<InputTableField>();
        oldInputs.addAll( inputList );
        
        inputList.clear();
        
        for ( InputTableField newInput : inputFields ) {
            
            if( ( oldInputs.getFieldByID( newInput.getId() ) ) != null ) {
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
    public void addInputFields( Collection<InputTableField> fields ) {
        
        inputList.addAll( fields );
    }

    /**
     * @return
     */
    public List<InputTableField> getInputFields() {
        return new ArrayList<InputTableField>( inputList );
    }
    
    public void setOutputField( TableField field, boolean notify ) {
        if( outputList.contains( field ) ) {
            if( output != field ) {
                output = field;
                if( notify ) {
                    TableEvent.dispatchEvent( new TableEvent( this, TableEvent.DATA ) );
                }
            }
        } else {
            throw new TableException( "No such output field: " + field );
        }
    }
    
    public void addOutputFields( Collection<TableField> fields ) {
        
        outputList.addAll( fields );
    }

    /**
     * @return
     */
    public TableField getOutputField() {
        if( output == null && outputList.size() > 0 )
            output = outputList.get( 0 );
        
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
    void addDataCell( int horizontalId, int verticalId, Map<String, String> values ) {
        
        DataRow row;
        
        if( ( row = getDataRowById( horizontalId ) ) == null ) {
            data.add( row = new DataRow( horizontalId ) );
        }
        
        if( row.getCell( verticalId ) != null ) {
            throw new TableException( "Dublicate cell! row=" + horizontalId + ", col=" + verticalId );
        }
        
        DataCell cell;
        row.addCell( cell = new DataCell( verticalId ) );
        
        if( values != null ) {
            for ( String outputId : values.keySet() ) {
                TableField out = outputList.getFieldByID( outputId );
                if( out == null )
                    throw new TableException( "Output " + outputId + " is not declared" );

                cell.setValue( out, createDataObjectFromString( out.getType(), values.get( outputId ) ) );
            }
        }
        
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
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    Map<TableField, Object> getCellValuesAt( int rowIndex, int columnIndex ) {
        return data.get( rowIndex ).getCells().get( columnIndex ).getValues();
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
     * @param type
     * @param value
     * @return
     */
    public static Object createDataObjectFromString( String type, String value ) {
        try {
            return TypeUtil.createObjectFromString( type, value );
        } catch ( Exception e ) {
            throw new TableException( "Unable to create object from string, type=" + 
                    type + ", value=" + value, e );
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
    InputTableField getInput( String id ) {
        for ( InputTableField tableField : inputList ) {
            if( id.equals( tableField.getId() ) ) {
                return tableField;
            }
        }
        return null;
    }
    
    public boolean isAliasOutput() {
        return aliasOutput != null;
    }
    
    public void setAliasOutput( TableField alias ) {
        aliasOutput = alias;
    }
    
    public TableField getAliasOutput() {
        return aliasOutput;
    }
    
    @Override
    public synchronized Object queryTable( Object[] args ) {
        return queryTable( args, true );
    }
    
    public synchronized Object queryTable( Object[] args, boolean verifyInputs ) {
        
        //TODO check the following case -- when no inputs are actually required in order to get a default value
        if( verifyInputs ) {
            if( inputList.size() != args.length )
                throw new TableException( "Number of table inputs is incorrect!" );
            TableInferenceEngine.verifyInputs( inputList, args );
        }
        
        int rowId = TableInferenceEngine.checkRules( inputList, getOrderedRowIds(), hrules, args );
        int colId = TableInferenceEngine.checkRules( inputList, getOrderedColumnIds(), vrules, args );
        
        return getOutputValue( rowId, colId );
    }

    @Override
    public synchronized Object queryTable( String[] inputIds, Object[] args ) {
        //inputIds may contain names in different order
        List<String> outerInputs = Arrays.asList( inputIds );
        List<InputTableField> missingInputs = new ArrayList<InputTableField>();
        Object[] newArgs = new Object[inputList.size()];
        final Map<InputTableField, Object> knownInputs = new LinkedHashMap<InputTableField, Object>();
        
        for ( int i = 0; i < inputList.size(); i++ ) {
            InputTableField input = inputList.get( i );
            int index = outerInputs.indexOf( input.getId() );
            if( index > -1 ) {
                newArgs[ i ] = args[ index ];
                knownInputs.put( input, newArgs[ i ] );
            } else {
                missingInputs.add( input );
            }
        }
        
        if( missingInputs.isEmpty() )
            return queryTable( newArgs, true );

        //return a value from Consultant
        final Object[] o = new Object[1];
        
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ExpertConsultant ec = new ExpertConsultant(
                        Editor.getInstance(), Table.this, knownInputs, false );
                
                //check if consultant is already in a finished state
                //and if true, there is no need to show it, just take the value
                if ( ec.isOk() ) {
                    o[0] = ec.getValue();
                    ec.dispose();
                } else {
                    ec.setModal( true );
                    ec.setVisible( true );
                    if ( ec.isOk() )
                        o[0] = ec.getValue();
                }
            }
        };
        
        if ( SwingUtilities.isEventDispatchThread() ) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait( runnable );
            } catch ( Exception e ) {
                db.p( e );
            }
        }
        
        if( o[0] == null ) {
            throw new TableCellValueUndefinedException( "Cell value not specified." );
        }
        return o[0];
    }
    
    public Object getOutputValue( int rowId, int colId ) {
     // Get row by rowId which is not always the same as array index
        DataRow row = getDataRowById( rowId );

        if (row == null) {
            // should never happen, or checkRules returned invalid rowId
            throw new TableException("Cannot find row with id = " + rowId);
        }

        DataCell cell = row.getCell(colId);

        if( cell == null ) {
            throw new TableException( "Cell is null (rowId=" + rowId + ", colId=" + colId );
        }
        
        if( isAliasOutput() ) {
            List<Object> res = new ArrayList<Object>();
            for ( TableField out : outputList ) {
                Object val = cell.getValue( out, true );
                if( val == null )
                    throw new TableException( "Cell value for output " + out.getId() + " not specified" );
                res.add( val );
            }
            
            return res.toArray();
        }
        
        Object res = cell.getValue();
        if( res == null ) {
            throw new TableCellValueUndefinedException( "Cell value not specified. Row: " + rowId + ", col: " + colId );
        }
        return res;
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
     * Contains map of values, row and column index.
     */
    private class DataCell {
        private int verticalId;
        private Map<TableField, Object> values = new LinkedHashMap<TableField, Object>();
        
        /**
         * @param verticalId
         */
        public DataCell( int verticalId ) {
            this.verticalId = verticalId;
        }
        
        /**
         * @param verticalId
         * @param value
         */
        public DataCell( int verticalId, Object value ) {
            this( verticalId );
            if( getOutputField() != null )
                values.put( getOutputField(), value );
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
            return getValue( getOutputField(), true );
        }

        /**
         * Returns the value and if it is null, 
         * returns the default value
         * 
         * @param field
         * @param checkDefault
         * @return
         */
        public Object getValue( TableField field, boolean checkDefault ) {
            Object value;
            return ( ( value = values.get( field ) ) != null
                     || !checkDefault )
                        ? value 
                        : field.getDefaultValue();
        }
        
        /**
         * @param value
         */
        public void setValue( Object value ) {
            setValue( getOutputField(), value );
        }
        
        /**
         * @param field
         * @param value
         */
        public void setValue( TableField field, Object value ) {
            values.put( field, value );
        }

        /**
         * Updates the values and tries to perform type conversion
         * 
         * @param oldOutputFields
         * @param newOutputFields
         */
        public void update( TableFieldList<TableField> oldOutputFields, TableFieldList<TableField> newOutputFields ) {
            Map<TableField, Object> oldValues = values;
            values = new LinkedHashMap<TableField, Object>();
            
            for ( TableField newTf : newOutputFields ) {
                TableField oldTf = oldOutputFields.getFieldByID( newTf.getId() );
                Object oldValue = oldValues.get( oldTf );
                
                if( oldTf != null && oldValue != null ) {
                    Object newValue;

                    if( oldTf.getType().equals( newTf.getType() ) )
                        //if types are equal
                        newValue = oldValue;
                    else//otherwise try to cast old type to new
                        try {
                            newValue = TypeUtil.createObjectFromString( newTf.getType(), oldValue.toString() );
                        } catch ( Exception e ) {
                            newValue = null;
                        }
                    setValue( newTf, newValue );
                }
            }
        }
        
        /**
         * Returns read-only mapping of outputs to values
         */
        public Map<TableField, Object> getValues() {
            return Collections.unmodifiableMap( values );
        }
        
        @Override
        public String toString() {
            return "Data cell: " +"col=" + verticalId + ", value=" + getValue();
        }
    }

    /**
     * Returns read-only list of outputs
     */
    public List<TableField> getOutputFields() {
        return Collections.unmodifiableList( outputList );
    }

    @Override
    public String[] getTableInputIds() {
        
        List<String> inputNames = new ArrayList<String>(getTableInputCount());
        for ( InputTableField input : inputList ) {
            inputNames.add( input.getId() );
        }
        return inputNames.toArray( new String[inputNames.size()] );
    }

    @Override
    public int getTableInputCount() {
        return inputList.size();
    }
    
}
