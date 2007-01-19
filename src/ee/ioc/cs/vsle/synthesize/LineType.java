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
            TYPE_CONST = 7,
            TYPE_SUPERCLASSES = 8,
            TYPE_ERROR = 10;

	private int type;
	private String specLine;
	private String origSpecLine;

	LineType(int i, String s, String o) {
		type = i;
		specLine = s;
		origSpecLine = o;
	}

	public String toString() {
		return ( "Type=" + Integer.toString(type) + " Line=" + specLine);
	}

        int getType() {
            return type;
        }

        String getSpecLine() {
            return specLine;
        }

		public String getOrigSpecLine() {
			return origSpecLine;
		}
}
