package ee.ioc.cs.vsle.equations;

import ee.ioc.cs.vsle.util.db;

import java.util.HashMap;

class UnaryOpNode
	extends ExpNode {
	// An expression node to represent a unary minus operator + sin, cos, log,  abs, tan.
	ExpNode operand;
	String meth;
	String sub;
	UnaryOpNode(ExpNode operand, String meth) {
		this.operand = operand;
		this.meth = meth;
	}

	void getExpressions() {
		operand.getExpressions(sub);
	}

	void getExpressions(String upper) {
		if (EquationSolver.isFunction(meth)) {
			operand.getExpressions(getOpposite(meth) + "(" + upper + ")");
		}
		else {
			operand.getExpressions(meth + "(" + upper + ")");
		}
	}

	void getVars() {
		operand.getVars();
	}

	void reverse() {
		sub = operand.inFix();
	}

	void decorate() {
		reverse();
		operand.decorate();
	}

	void postFix() {
		operand.postFix();
		db.p(meth);
	}

	String inFix() {
		String a = operand.inFix();

		if (!meth.equals("-")) {
			return "Math." + meth + "(" + a + ")";
		}
		return meth + "(" + a + ")";
	}

	double calcValue(HashMap table) {
		if (meth.equals("sin")) {
			return Math.sin(operand.calcValue(table));
		}
		else if (meth.equals("cos")) {
			return Math.cos(operand.calcValue(table));
		}
		else if (meth.equals("tan")) {
			return Math.tan(operand.calcValue(table));
		}
		else if (meth.equals("log")) {
			return Math.log(operand.calcValue(table));
		}
		else if (meth.equals("abs")) {
			return Math.abs(operand.calcValue(table));
		}
		else if (meth.equals("-")) {
			return -1 * (operand.calcValue(table));
		}
		else {
			return 0;
		}

	}

	String getOpposite(String s) {
		if (s.equals("sin")) {
			return "Math.asin";
		}
		else if (s.equals("cos")) {
			return "Math.acos";
		}
		else if (s.equals("tan")) {
			return "Math.atan";
		}
		else if (s.equals("log")) {
			return "Math.exp";
		}
		else if (s.equals("abs")) {
			return "Math.abs";
		}
		return null;
	}
}
