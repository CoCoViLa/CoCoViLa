/**
 * 
 */
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

import java.text.*;
import java.util.*;

import ee.ioc.cs.vsle.table.exception.*;

/**
 * @author pavelg
 * Inputs can have constraints and a custom question
 */
public class InputTableField extends TableField {

    private String question;
    private List<TableFieldConstraint> constraints;
    
    public InputTableField( String id, String type ) {
        super( id, type );
    }

    /**
     * @return the question
     */
    public String getQuestion() {
        return question;
    }

    public String getQuestionText() {
        return MessageFormat.format( question, getId(), getType() );
    }
    
    /**
     * @param question the question to set
     */
    public void setQuestion( String question ) {
        this.question = ( question == null || question.trim().length() == 0 ) 
                          ? null 
                          : question;
    }

    /**
     * @return the constraints
     */
    public List<TableFieldConstraint> getConstraints() {
        if( constraints == null ) {
            constraints = new ArrayList<TableFieldConstraint>();
        }
        return constraints;
    }

    /**
     * @param constraints the constraints to set
     */
    public void setConstraints( List<TableFieldConstraint> constraints ) {
        this.constraints = constraints;
    }
    
    public void verifyConstraints( Object obj ) {
        for ( TableFieldConstraint constr : getConstraints() ) {
            if( !constr.verify( obj ) ) {
                throw new TableInputConstraintViolationException( MessageFormat.format( constr.printConstraint(), getId() ) + " violated with " + obj );
            }
        }
    }

}
