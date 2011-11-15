package ee.ioc.cs.vsle.table;

public interface IStructuralExpertTable {

    /**
     * Method for querying table
     * 
     * @param args array of values of input variables (order is strict!)
     * @return value from the table if conditions hold for some row and column
     * @throws TableException if proper value cannot be returned 
     */
    public Object queryTable( Object[] args );

    /**
     * Method for querying table with possibly incomplete number of inputs.
     * The Consultant will ask for missing inputs, if required.
     * The order of inputs may differ from the table, but inputIds and args
     * must be in sync.
     * Input ids (names) in inputIds must precisely match ids of a table.
     * 
     * @param inputIds
     * @param args
     * @return
     */
    public Object queryTable( String[] inputIds, Object[] args );
    
    /**
     * @return id of the table
     */
    public String getTableId();
    
    /**
     * @return Array of table's input ids
     */
    public String[] getTableInputIds();
    
    /**
     * @return number of table inputs
     */
    public int getTableInputCount();
}