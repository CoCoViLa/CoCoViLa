package ee.ioc.cs.vsle.equations;

class StringStack {
	String e;

	StringStack(String e) {
		this.e = e;
	}

	char peek() {
		if (e.length() > 0) {
			return e.charAt(0);
		}
		else {
			return '#';
		}
	}

	char getAnyChar() {
		char ch = e.charAt(0);

		e = e.substring(1, e.length());
		return ch;
	}

	double getString() {
		int i = 0;

		while (i < e.length() && Character.isDigit(e.charAt(i))) {
			i++;
		}
		double d = (double) (Integer.parseInt(e.substring(0, i)));

		e = e.substring(i, e.length());
		return d;
	}

	char getChar() {
		char ch = peek();

		while (ch == ' ' || ch == '\n') {
			getAnyChar();
			ch = peek();
		}
		return getAnyChar();
	}

}

;
