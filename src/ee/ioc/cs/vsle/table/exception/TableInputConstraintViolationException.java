/**
 * 
 */
package ee.ioc.cs.vsle.table.exception;

/**
 * @author pavelg
 *
 */
public class TableInputConstraintViolationException extends TableException {

    /**
     * @param message
     */
    public TableInputConstraintViolationException( String message ) {
        super( message );
    }

    /**
     * @param cause
     */
    public TableInputConstraintViolationException( Throwable cause ) {
        super( cause );
    }

    /**
     * @param message
     * @param cause
     */
    public TableInputConstraintViolationException( String message,
            Throwable cause ) {
        super( message, cause );
    }

}
