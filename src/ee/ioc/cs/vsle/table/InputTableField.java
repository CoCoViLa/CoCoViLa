/**
 * 
 */
package ee.ioc.cs.vsle.table;

import java.util.*;

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
        return constraints;
    }

    /**
     * @param constraints the constraints to set
     */
    public void setConstraints( List<TableFieldConstraint> constraints ) {
        this.constraints = constraints;
    }

}
