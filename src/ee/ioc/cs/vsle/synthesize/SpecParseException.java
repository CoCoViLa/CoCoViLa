package ee.ioc.cs.vsle.synthesize;

public class SpecParseException
	extends Throwable {
	public String excDesc;
	public String line;
	SpecParseException(String s) {
		excDesc = s;
	}
	SpecParseException(String s, String line) {
        excDesc = s;
        setLine( line );
    }
    /**
     * @return the line
     */
    public String getLine() {
        return line;
    }
    /**
     * @param line the line to set
     */
    public void setLine( String line ) {
        this.line = line;
    }
}
