/**
 * 
 */
package ee.ioc.cs.vsle.api;

/**
 * This exception can the thrown in the running thread and handled either by the user 
 * or the system (in the latter case the program terminates)
 */
public class RunningProgramException extends RuntimeException {

    public RunningProgramException(Throwable e) {
        super(e);
    }
}
