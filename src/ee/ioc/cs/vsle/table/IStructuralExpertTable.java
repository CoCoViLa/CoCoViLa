package ee.ioc.cs.vsle.table;

import ee.ioc.cs.vsle.table.exception.*;

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
     * @return id of the table
     */
    public String getTableId();
}