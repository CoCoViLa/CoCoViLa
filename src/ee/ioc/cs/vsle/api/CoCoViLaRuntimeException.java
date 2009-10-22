/**
 * 
 */
package ee.ioc.cs.vsle.api;

/**
 * Base class for all API exceptions
 */
public abstract class CoCoViLaRuntimeException extends RuntimeException {

    /**
     * 
     */
    public CoCoViLaRuntimeException() {
    }

    /**
     * @param message
     */
    public CoCoViLaRuntimeException( String message ) {
        super( message );
    }

    /**
     * @param cause
     */
    public CoCoViLaRuntimeException( Throwable cause ) {
        super( cause );
    }

    /**
     * @param message
     * @param cause
     */
    public CoCoViLaRuntimeException( String message, Throwable cause ) {
        super( message, cause );
    }

}
