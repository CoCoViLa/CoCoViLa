package ee.ioc.cs.vsle.synthesize;

/**
 * Created by IntelliJ IDEA.
 * User: Aulo
 * Date: 14.10.2003
 * Time: 9:36:59
 * To change this template use Options | File Templates.
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