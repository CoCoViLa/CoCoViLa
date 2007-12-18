package ee.ioc.cs.vsle.equations;

class UnaryOpNode extends ExpNode {
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
	    
		if ( Function.contains( meth ) ) {
			operand.getExpressions( Function.valueOf( meth ).getOpposite() + "(" + upper + ")");
		} else {
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

	String inFix() {
		String a = operand.inFix();

		if (!meth.equals("-")) {
			return Function.valueOf( meth ).getFunction() + "(" + a + ")";
		}
		return meth + "(" + a + ")";
	}

}
