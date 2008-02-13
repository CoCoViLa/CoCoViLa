/**
 * 
 */
package ee.ioc.cs.vsle.table;

/**
 * Class Table Exception
 */
public class TableException extends RuntimeException {

    /**
     * 
     */
    public TableException() {
    }

    /**
     * @param message
     */
    public TableException( String message ) {
        super( message );
    }

    /**
     * @param cause
     */
    public TableException( Throwable cause ) {
        super( cause );
    }

    /**
     * @param message
     * @param cause
     */
    public TableException( String message, Throwable cause ) {
        super( message, cause );
    }

}
