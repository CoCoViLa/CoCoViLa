package ee.ioc.cs.vsle.synthesize;

/**
 */
class LineType {
	int type; // 1-declaration, 2-assignment, 3-axiom, 4- equation, 5-alias, 10 -error
	String specLine;
	LineType(int i, String s) {
		type = i;
		specLine = s;
	}

	public String toString() {
		return (Integer.toString(type) + specLine);
	}
}