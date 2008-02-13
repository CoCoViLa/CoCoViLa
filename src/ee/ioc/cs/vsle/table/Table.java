/**
 * 
 */
package ee.ioc.cs.vsle.table;

import java.util.*;

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
public class Table {
    
    private static final String VALUE_UNDEFINED = "%null%";

    public static final String TABLE_KEYWORD = "@table";
    
    private String id;
    private List<TableField> inputList = new ArrayList<TableField>();
    private Map<String, TableField> inputsById = new HashMap<String, TableField>();
    private TableField output;
    private Set<Rule> hrules = new LinkedHashSet<Rule>();
    private Set<Rule> vrules = new LinkedHashSet<Rule>();
    private Map<Integer, Map<Integer, DataCell>> data = new LinkedHashMap<Integer, Map<Integer,DataCell>>();
    
    /**
     * Constructor
     * 
     * @param id table name
     */
    public Table( String id ) {
        this.id = id;
    }
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    void addInputFields( Collection<TableField> field ) {
        
        for ( TableField tableField : field ) {
            inputList.add( tableField );
            inputsById.put( tableField.getId(), tableField );
        }
    }
    
    void setOutputField( TableField field ) {
        output = field;
    }
    
    void addHRules( Collection<Rule> rules ) {
        hrules.addAll( rules );
    }
    
    void addVRules( Collection<Rule> rules ) {
        vrules.addAll( rules );
    }
    
    void addDataCell( DataCell cell ) {
        
        Map<Integer, DataCell> row;
        
        if( ( row = data.get( cell.getRow() ) ) == null ) {
            row = new LinkedHashMap<Integer, DataCell>();
            data.put( cell.getRow(), row );
        }
        
        if( row.put( cell.getCol(), cell ) != null ) {
            throw new TableException( "Dublicate " + cell );
        }
    }
    
    /**
     * Returns input field by its id
     * 
     * @param id
     * @return
     */
    TableField getInput( String id ) {
        return inputsById.get( id );
    }
    
    /**
     * Method for querying table
     * 
     * @param args array of values of input variables (order is strict!)
     * @return value from the table if conditions hold for some row and column
     * @throws TableException if proper value cannot be returned 
     */
    public synchronized Object queryTable( Object[] args ) {
        
        //db.p( "queryTable(): " + Arrays.toString( args ) );
        
        int rowId = checkRules( data.keySet(), hrules, args );
        int colId = checkRules( data.values().iterator().next().keySet(), vrules, args );
        
        if( rowId == -1 || colId == -1 ) {
            throw new TableException( "No valid rules for current input: " + Arrays.toString( args ) );
        }
        
        DataCell cell = data.get( rowId ).get( colId );
        
        if( cell == null || cell.getValue().equals( VALUE_UNDEFINED ) ) {
            throw new TableException( "Cell value not specified" );
        }
        
        try {
            return TypeUtil.createObjectFromString( output.getType(), cell.getValue() );
        } catch ( Exception e ) {
            throw new TableException( "Cannot return object value from the table, type=" + 
                    output.getType() + ", value=" + cell.getValue(), e );
        }
    }

    /**
     * Verifies conditions and returns corresponding index
     * 
     * @param ids
     * @param rules
     * @param args
     * @return
     */
    private int checkRules( Set<Integer> ids, Set<Rule> rules, Object[] args ) {
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
    
    synchronized void destroy() {
        inputList.clear();
        inputsById.clear();
        output = null;
        hrules.clear();
        vrules.clear();
        data.clear();
    }
    
    /**
     * Represents table cell. 
     * Contains value, row and column index.
     */
    static class DataCell {
        private int row;
        private int col;
        private String value;
        
        public DataCell( int row, int col, String value ) {
            this.row = row;
            this.col = col;
            this.value = value;
        }

        /**
         * @return the row
         */
        public int getRow() {
            return row;
        }

        /**
         * @return the col
         */
        public int getCol() {
            return col;
        }

        /**
         * @return the value
         */
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Data cell: row=" + row + ", col=" + col + ", value=" + value;
        }
    }
    
}
