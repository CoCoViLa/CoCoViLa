package ee.ioc.cs.vsle.equations;

import java.util.HashMap;

abstract class ExpNode {
	abstract void postFix();

	abstract String inFix();

	abstract void decorate();

	abstract void getExpressions(String a);

	abstract void getExpressions();

	abstract double calcValue(HashMap table);

	abstract void getVars();
}