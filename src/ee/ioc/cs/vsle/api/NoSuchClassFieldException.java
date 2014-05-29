/**
 * 
 */
package ee.ioc.cs.vsle.api;

/**
 * @author pavelg
 *
 */
public class NoSuchClassFieldException extends CoCoViLaRuntimeException {

    /**
     * 
     */
    public NoSuchClassFieldException() {
    }

    /**
     * @param message
     */
    public NoSuchClassFieldException( String message ) {
        super( message );
    }

    /**
     * @param cause
     */
    public NoSuchClassFieldException( Throwable cause ) {
        super( cause );
    }

    /**
     * @param message
     * @param cause
     */
    public NoSuchClassFieldException( String message, Throwable cause ) {
        super( message, cause );
    }

}
