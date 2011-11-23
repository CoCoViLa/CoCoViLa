/**
 * 
 */
package ee.ioc.cs.vsle.table;

import java.util.*;
import java.util.Map.Entry;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.table.exception.*;
import ee.ioc.cs.vsle.util.*;

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
        TableFieldList<InputTableField> allInputFields = new TableFieldList<InputTableField>();
        TableFieldList<InputTableField> unknownInputFields = new TableFieldList<InputTableField>();

        @Override
        public String toString() {
            return "ProductionRule [id=" + id + ", rules=" + rules
                    + ", unknownInputFields=" + unknownInputFields + ", allInputFields=" + allInputFields + "]";
        }
        
        
    }
    
    public static class InputFieldAndSelectedId {
        
        private InputTableField input;
        private Integer selectedId;
        
        private InputFieldAndSelectedId() {}
        
        /**
         * @param input
         * @param selectedId
         */
        InputFieldAndSelectedId( InputTableField input,
                Integer selectedId ) {
            this();
            
            this.input = input;
            this.selectedId = selectedId;
        }
        /**
         * @return the input
         */
        public InputTableField getInput() {
            return input;
        }
        /**
         * @return the selectedId
         */
        public Integer getSelectedId() {
            return selectedId;
        }

        @Override
        public String toString() {
            return "InputFieldAndSelectedId [input=" + input + ", selectedId="
                    + selectedId + "]";
        }
        
    }
    
    /**
     * @param input
     * @return
     */
    public static InputFieldAndSelectedId getNextInputAndRelevantIds( List<Rule> rules, InputTableField input, 
            Map<InputTableField, Object> inputsToValues, List<Integer> allIds, List<Integer> outIds ) {
        
        assert inputsToValues.containsKey( input );
        
        //the ids that hold for current input
        Map<Integer, ProductionRule> productionRules = new LinkedHashMap<Integer, ProductionRule>();
        
        for ( Rule rule : rules ) {
            InputTableField ruleInput = rule.getField();
            
            //if null, the rule does not match the input
            boolean matchesInput = ruleInput.equals( input );
            Boolean holds = matchesInput ? rule.verifyCondition( inputsToValues.get( input ) ) : null;
            
            if (RuntimeProperties.isLogDebugEnabled()) {
                if( matchesInput )
                    db.p("Rule: " + rule + " holds: " + holds + " for input " + input.getId() + " = " + inputsToValues.get( input ) );
                else
                    db.p("Rule: " + rule + " does not match the input " + input.getId() + " = " + inputsToValues.get( input ) );
            }
            for( Integer id : rule.getEntries() ) {
                if (RuntimeProperties.isLogDebugEnabled()) db.p("Entry id: " + id + " contains in all available ids: " + allIds.contains( id ) );
                if( !allIds.contains( id ) ) continue;
                //
                ProductionRule pr;
                if( ( pr = productionRules.get( id ) ) == null ) {
                    pr = new ProductionRule();
                    pr.id = id;
                    productionRules.put( id, pr );
                }
                pr.rules.add( rule );
                if( !matchesInput && !pr.unknownInputFields.contains( ruleInput ) 
                        && !inputsToValues.containsKey( ruleInput ) ) {
                    //store only unknown rule's inputs here
                    pr.unknownInputFields.add( ruleInput );
                }
                if( !pr.allInputFields.contains( ruleInput ) ) {
                    pr.allInputFields.add( ruleInput );
                }
                //
                if( holds != null ) {
                    boolean contains;
                    if( ( contains = outIds.contains( id ) ) && !holds ) {
                        outIds.remove( id );//this id is a candidate for removal
                    } else if( !contains && holds ) {
                        outIds.add( id );
                    }
                }
//                else if( !pr.allInputFields.contains( input ) ){//does not contain current input, so could be useful later
//                    outIds.add( id );//this does not work correctly
//                }
                if (RuntimeProperties.isLogDebugEnabled()) db.p( pr );
            }
        }
        
        if (RuntimeProperties.isLogDebugEnabled())  {
            db.p( "All production rules: " + productionRules.size() );
            for ( Entry<Integer, ProductionRule> entry : productionRules.entrySet() ) {
                System.out.println("Id: " + entry.getKey() + ", prod: " + entry.getValue() );
            }
        }
        if (RuntimeProperties.isLogDebugEnabled()) db.p("All available ids: " + allIds );
        if (RuntimeProperties.isLogDebugEnabled()) db.p("Current out ids: " + outIds );
        InputFieldAndSelectedId result = null;//the result will be created only once but the iteration will continue until all ids are examined
        for ( Integer id : allIds ) {//we need to iterate all ids because some of rows/cols may contain no rules!
            ProductionRule prodr = productionRules.get( id );
            
            if( prodr == null//null means empty row that should be considered
                    //non null and not empty should be among out_ids
                    || ( prodr.unknownInputFields.isEmpty() && outIds.contains( prodr.id ) ) ) {
                if( !outIds.contains( id ) )//TODO
                    outIds.add( id );//this is required in order to keep empty rows/cols for the future use
                //seems that this id could be used
                if( result == null ) {
                    if (RuntimeProperties.isLogDebugEnabled()) db.p( "seems that this id could be used " + id );
                    result = new InputFieldAndSelectedId( null, id );
                }
                continue;
            } else if ( !prodr.allInputFields.contains( input ) ) { 
                outIds.add( id );//if a row/col does not contain input, it could be useful later
            } else if( !outIds.contains( prodr.id ) ) {
                continue;
            }
            //if (RuntimeProperties.isLogDebugEnabled()) db.p( "returning input " + prodr.unknownInputFields.get( 0 ) );
            if( result == null )
                result = new InputFieldAndSelectedId( 
                        prodr.unknownInputFields.isEmpty() 
                            ? null
                            : prodr.unknownInputFields.get( 0 ), 
                        null );
        }
        if (RuntimeProperties.isLogDebugEnabled()) db.p("Final out ids: " + outIds );
        
        if( result == null && outIds.size() != 0 ) {//there is still some hope left!
            if (RuntimeProperties.isLogDebugEnabled()) db.p("there is still some hope left! " + result );
            int id = outIds.get( 0 );
            ProductionRule prodr = productionRules.get( id );
            if( prodr != null ) {
                if( !prodr.unknownInputFields.isEmpty() )
                    result = new InputFieldAndSelectedId( prodr.unknownInputFields.get( 0 ), null );
            } else {
                result = new InputFieldAndSelectedId( null, id );
            }
        }
        if (RuntimeProperties.isLogDebugEnabled()) db.p( "returning " + result );
        return result == null ? new InputFieldAndSelectedId() : result;
    }
    
}
