package ee.ioc.cs.vsle.synthesize;

/**
 */
public class ParseError
	extends Exception {
	// Represents a syntax error found in the user's input.
	ParseError(String message) {
		super(message);
	}
} // end nested class ee.ioc.cs.editor.synthesize.ParseError