package ee.ioc.cs.vsle.equations;

import ee.ioc.cs.vsle.util.db;

import java.util.HashMap;

class BinOpNode
	extends ExpNode {

	char op; // The operator.
	ExpNode left; // The expression for its left operand.
	ExpNode right; // The expression for its right operand.
	String leftSub;
	String rightSub;

	/**
	 * Class constructor. Constructs a ee.ioc.cs.editor.equations.BinOpNode containing the specified data.
	 * @param op char
	 * @param left ee.ioc.cs.editor.Equations.ExpNode
	 * @param right ee.ioc.cs.editor.Equations.ExpNode
	 */BinOpNode(char op, ExpNode left, ExpNode right) {
		this.op = op;
		this.left = left;
		this.right = right;
	} // ee.ioc.cs.editor.equations.BinOpNode

	/**
	 * Returns an opposite value of the operand.
	 * @param op char - operand.
	 * @return char - opposite value of the operand.
	 */ char getOpposite(char op) {
		switch (op) {
			case '-':
				return '+';

			case '+':
				return '-';

			case '/':
				return '*';

			case '*':
				return '/';

			case '=':
				return ' ';

			default:
				return op;
		}
	} // getOpposite

	/**
	 * Gets variables for the left and right expression.
	 */
	void getVars() {
		left.getVars();
		right.getVars();
	} // getVars

	/**
	 * Reverses the left side of the expression.
	 */
	void leftReverse() {
		leftSub = getOpposite(op) + left.inFix();
	} // leftReverse

	/**
	 * Reverses the right side of the expression.
	 */
	void rightReverse() {
		rightSub = getOpposite(op) + right.inFix();
	} // rightReverse

	/**
	 * Reverses the left and right sides of the expression
	 * and decorates both sides.
	 */
	void decorate() {
		leftReverse();
		rightReverse();
		left.decorate();
		right.decorate();
	} // decorate

	/**
	 * <UNCOMMENTED>
	 */
	void getExpressions() {
		left.getExpressions(rightSub);
		right.getExpressions(leftSub);
	} // getExpression

	/**
	 * <UNCOMMENTED>
	 * @param table -
	 * @return double -
	 */
	double calcValue(HashMap table) {
		switch (op) {
			case '-':
				return left.calcValue(table) - right.calcValue(table);

			case '+':
				return left.calcValue(table) + right.calcValue(table);

			case '/':
				return left.calcValue(table) / right.calcValue(table);

			case '*':
				return left.calcValue(table) * right.calcValue(table);

			case '^':
				return Math.pow(left.calcValue(table), right.calcValue(table));

			default:
				return 0;
		}
	}

	void getExpressions(String upper) {
		if (op == '^') {
			left.getExpressions("Math.pow(" + upper + ", 1.0/(" + rightSub.substring(1, rightSub.length()) + "))");
		}
		else {
			left.getExpressions("(" + upper + ")" + rightSub);
			if (op == '-') {
				right.getExpressions("-(" + upper + ")" + "-" + leftSub.substring(1, leftSub.length()));
			}
			else if (op == '/') {
				right.getExpressions(leftSub.substring(1, leftSub.length()) + "/" + upper);
			}
			else {
				right.getExpressions("(" + upper + ")" + leftSub);
			}
		}
	} // getExpressions

	/**
	 * <UNCOMMENTED>
	 * @return String
	 */
	String inFix() {
		String l = "(" + left.inFix();
		String r = right.inFix() + ")";

		if (op == '^') {
			return ("Math.pow" + l + ", " + r + "");
		}
		else {
			return (l + " " + op + " " + r);
		}
	} // inFix

	/**
	 * <UNCOMMENTED>
	 */ void postFix() {
		left.postFix();
		right.postFix();
		db.p(op);
	} // postFix
}