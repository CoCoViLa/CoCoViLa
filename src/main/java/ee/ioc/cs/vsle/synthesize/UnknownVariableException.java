package ee.ioc.cs.vsle.synthesize;

/**
 * Unknown Variable Exception
 */
public class UnknownVariableException extends SpecParseException {
    /**
     * Class constructor.
     * @param varName name of the unknown variable
     */
    public UnknownVariableException(String varName) {
        super(varName);
    }

    /**
     * Class constructor
     * @param varName name of the unknown variable
     * @param line the specification line where the exception occured$
     */
    public UnknownVariableException(String varName, String line) {
        super(varName);
        setLine(line);
    }
}
