/**
 * 
 */
package ee.ioc.cs.vsle.api;

/**
 * This exception wraps an exception thrown inside a subtask
 */
public class SubtaskExecutionException extends RunningProgramException {

    public SubtaskExecutionException(Throwable e)
    {
        super(e);
    }
}
