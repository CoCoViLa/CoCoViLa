/**
 * 
 */
package ee.ioc.cs.vsle.api;

/**
 * @author pavelg
 *
 */
public class ComputeModelException extends CoCoViLaRuntimeException {

    /**
     * @param message
     */
    public ComputeModelException( String message ) {
        super( message );
    }

    /**
     * @param message
     * @param cause
     */
    public ComputeModelException( String message, Throwable cause ) {
        super( message, cause );
    }

}
