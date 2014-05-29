package ee.ioc.cs.vsle.synthesize;

public class SpecParseException extends Throwable {

    private String line;

    public SpecParseException(String message) {
        super(message);
    }

    SpecParseException(String message, SpecParseException e) {
        super(message, e);
        setLine(e.getLine());
    }

    /**
     * Returns the specification line that caused the exception.
     * @return the specification line
     */
    public String getLine() {
        return line;
    }

    /**
     * Sets the specification line that caused the exception.
     * @param line the specification line that caused the exception
     */
    public void setLine(String line) {
        this.line = line;
    }
}
