package ee.ioc.cs.vsle.synthesize;

/**
 */
public class LineType {
    public static final int
            TYPE_DECLARATION = 1,
            TYPE_ASSIGNMENT = 2,
            TYPE_AXIOM = 3,
            TYPE_EQUATION = 4,
            TYPE_ALIAS = 5,
            TYPE_SPECAXIOM = 6,
            TYPE_ERROR = 10;

	private int type;
	private String specLine;

	LineType(int i, String s) {
		type = i;
		specLine = s;
	}

	public String toString() {
		return (Integer.toString(type) + specLine);
	}

        int getType() {
            return type;
        }

        String getSpecLine() {
            return specLine;
        }
}
