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
    
    /**
     * @param table
     * @return
     */
    public static InputTableField getFirstTableInput( Table table ) {
        
        List<Integer> rowIds = table.getOrderedRowIds();
        //first, try horisontal rules
        if(!rowIds.isEmpty()) {
            int id = rowIds.get( 0 );
            for ( Rule rule : table.getHRules() ) {
                if ( rule.getEntries().contains( id ) ) {
                    return rule.getField();
                }
            }
        }
        
        List<Integer> colIds = table.getOrderedColumnIds();
        //if nothing there, try vertical ones
        if(!colIds.isEmpty()) {
            int id = colIds.get( 0 );
            for ( Rule rule : table.getVRules() ) {
                if ( rule.getEntries().contains( id ) ) {
                    return rule.getField();
                }
            }
        }
        //otherwise no inputs are required in order to get a value from a table
        return null;
    }

    static class ProductionRule {
        int id;
        List<Rule> rules = new ArrayList<Rule>();
        TableFieldList<InputTableField> inputFields = new TableFieldList<InputTableField>();

        @Override
        public String toString() {
            return "ProductionRule [id=" + id + ", rules=" + rules
                    + ", inputFields=" + inputFields + "]";
        }
        
        
    }
    
    /**
     * @param input
     * @return
     */
    public static InputTableField getNextInputAndRelevantIds( List<Rule> rules, InputTableField input, 
            Map<InputTableField, Object> inputsToValues, List<Integer> all_ids, List<Integer> out_ids ) {
        
        assert inputsToValues.containsKey( input );
        
        //the ids that hold for current input
        Map<Integer, ProductionRule> productionRules = new LinkedHashMap<Integer, ProductionRule>();
        
        for ( Rule rule : rules ) {
            System.out.println("Rule: " + rule );
            InputTableField ruleInput = rule.getField();
            
            //if null, the rule does not match the input
            boolean matchesInput = ruleInput.equals( input );
            Boolean holds = matchesInput ? rule.verifyCondition( inputsToValues.get( input ) ) : null;
            
            for( Integer id : rule.getEntries() ) {
                System.out.println("id: " + id + " contains in all available ids: " + all_ids.contains( id ) );
                if( !all_ids.contains( id ) ) continue;
                //
                ProductionRule pr;
                if( ( pr = productionRules.get( id ) ) == null ) {
                    pr = new ProductionRule();
                    pr.id = id;
                    productionRules.put( id, pr );
                }
                pr.rules.add( rule );
                if( !matchesInput && !pr.inputFields.contains( ruleInput ) 
                        && !inputsToValues.containsKey( ruleInput ) )
                    //store only unknown rule's inputs here
                    pr.inputFields.add( ruleInput );
                //
                if( holds != null ) {
                    boolean contains;
                    if( ( contains = out_ids.contains( id ) ) && !holds ) {
                        out_ids.remove( id );
                    } else if( !contains && holds ) {
                        out_ids.add( id );
                    }
                }
            }
        }
        
        System.out.println(productionRules.size() + " - All production rules: " + productionRules );
        productionRules.keySet().retainAll( out_ids );
        System.out.println(productionRules.size() + " - Filtered production rules: " + productionRules );
        for ( Integer id : all_ids ) {//we need to iterate all ids because some of rows/cols may contain no rules!
            ProductionRule prodr = productionRules.get( id );
            if( prodr == null || prodr.inputFields.isEmpty() ) {
                break;//if no other inputs are needed, do not proceed and return null
            }
            return prodr.inputFields.get( 0 );
        }
        return null;
    }
    
    public static void getNextTableInput( TableFieldList<InputTableField> inputFields, List<Integer> ids, List<Rule> rules ) {
        
    }
}
