/**
 * 
 */
package ee.ioc.cs.vsle.table;

import java.util.*;

/**
 * @author pavelg
 *
 */
public class TableInferenceEngine {

    /**
     * Verifies conditions and returns corresponding index
     * 
     * @param ids
     * @param rules
     * @param args
     * @return
     */
    public static int checkRules( TableFieldList<InputTableField> inputFields, List<Integer> ids, List<Rule> rules, Object[] args ) {
        outer: for ( Integer id : ids ) {
            
            //if a row does not contain any rule entries it is valid by default
            boolean isOK = true;
            
            for ( Rule rule : rules ) {
                if ( rule.getEntries().contains( id ) ) {
                    isOK &= rule.verifyCondition( args[ inputFields.indexOf( rule.getField() )] );
                }
                
                if( !isOK ) 
                    continue outer;
            }
            
            return id;
        }
        
        throw new TableException( "No valid rules for current input: " + Arrays.toString( args ) );
    }
    
    /**
     * If a constraint is violated, TableInputConstraintViolationException is thrown
     * 
     * @param inputFields
     * @param args
     */
    public static void verifyInputs( TableFieldList<InputTableField> inputFields, Object[] args ) {
        for( int i = 0; i < inputFields.size(); i++ ) {
            inputFields.get( i ).verifyConstraints( args[i] );
        }
    }
}
