package ee.ioc.cs.vsle.table;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.*;

import ee.ioc.cs.vsle.table.exception.*;
import ee.ioc.cs.vsle.util.*;

/**
 * Rule contains a variable (table field), condition and a value 
 * to verify the condition against a given argument
 */
public class Rule {
    
    private InputTableField field;
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
    private Rule( InputTableField field, String value, Condition condition, boolean isNegative ) {
        this.field = field;
        this.condition = condition;
        this.negative = isNegative;
        setValueFromString( value );
    }
    
    /**
     * @param index
     */
    public void addEntry( int index ) {
        entries.add( index );
    }
    
    public void removeEntry( int index ) {
        entries.remove( index );
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
    public Set<Integer> getEntries() {
        return entries;
    }

    /**
     * @return the field
     */
    public InputTableField getField() {
        return field;
    }
    
    public boolean isNegative() {
        return negative;
    }

    public void setNegative( boolean negative ) {
        this.negative = negative;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition( Condition condition ) {
        this.condition = condition;
    }

    public Object getValue() {
        return value;
    }

    public String getConditionString() {
        return ( isNegative() ? "!" : "" ) + condition.getKeyword(); 
    }
    
    public void setValueFromString( String svalue ) {

        String varType = field.getType();

        //the following check is required in order to create an array from svalue
        if( condition == Condition.COND_IN_ARRAY ) {

            varType += "[]";
        }

        try {
            value = TypeUtil.createObjectFromString( varType, svalue );
        } catch ( Exception e ) {
            throw new TableException( "Unable to create an object from the string \"" + 
                    svalue + "\", " + field.toString(), e );
        }

    }

    @Override
    public String toString() {
        return field.getId()
                + " "
                + toStringCond()
                + " "
                + toStringValue();
    }
    
    public String toStringCond() {
        return ( isNegative() ? condition.getOppositeSymbol() : condition.getSymbol() );
    }
    
    public String toStringValue() {
        return value.getClass().isArray() 
                ? TypeUtil.toString( value ) 
                : value.toString();
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
    public static Rule createRule( InputTableField var, String cond, String svalue ) {
        
        boolean negative = false;
        
        if( cond.startsWith( "!" ) ) {
            
            negative = true;
            cond = cond.substring( 1 );
        }
        
        Condition condition = Condition.Factory.getCondition( cond );
        
        if( condition == null ) {
            throw new TableException( "Invalid condition: " + cond );
        }
        
        return new Rule( var, svalue, condition, negative );
    }
}
