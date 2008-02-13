package ee.ioc.cs.vsle.table;

import java.util.*;

import ee.ioc.cs.vsle.util.*;

/**
 * Rule contains a variable (table field), condition and a value 
 * to verify the condition against a given argument
 */
public class Rule {
    
    private TableField field;
    protected Object value;
    private Set<Integer> entries = new LinkedHashSet<Integer>();
    private Condition condition;
    private boolean negative;
    
    /**
     * Rules are created in the static method createRule()
     * 
     * @param field
     * @param value
     * @param condition
     * @param isNegative
     */
    private Rule( TableField field, Object value, Condition condition, boolean isNegative ) {
        this.field = field;
        this.value = value;
        this.condition = condition;
        this.negative = isNegative;
    }
    
    /**
     * @param index
     */
    void addEntry( int index ) {
        entries.add( index );
    }
    
    /**
     * Verifies the condition against an argument
     * 
     * @param against
     * @return
     */
    boolean verifyCondition( Object against ) {
        
        boolean cond = condition.verify( value, against );
        
        return negative ? !cond : cond;
    }

    /**
     * @return the entries
     */
    Set<Integer> getEntries() {
        return entries;
    }

    /**
     * @return the field
     */
    TableField getField() {
        return field;
    }
    
    /**
     * Method for rule creation
     * 
     * @param var
     * @param cond
     * @param svalue
     * @return
     * @throws Exception
     */
    static Rule createRule( TableField var, String cond, String svalue ) {
        
        //db.p( "createRule(): " + var + " " + cond + " " + svalue );
        
        String varType = var.getType();
        
        boolean negative = false;
        
        if( cond.startsWith( "!" ) ) {
            
            negative = true;
            cond = cond.substring( 1 );
        }
        
        Condition condition = Condition.Factory.getCondition( cond );
        
        if( condition == null ) {
            throw new TableException( "Invalid condition" );
        }
        
        //the following check is required in order to create an array from svalue
        if( condition == Condition.COND_IN_ARRAY ) {
            
            varType += "[]";
        }
        
        Object value;
        
        try {
            value = TypeUtil.createObjectFromString( varType, svalue );
        } catch ( Exception e ) {
            throw new TableException( "Unable to create an object from the string value " + 
                    svalue + ", " + var.toString(), e );
        }
        
        return new Rule( var, value, condition, negative );
    }
}
