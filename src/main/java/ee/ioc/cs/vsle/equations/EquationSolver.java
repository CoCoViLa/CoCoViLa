package ee.ioc.cs.vsle.equations;

import java.util.LinkedHashSet;
import java.util.Set;

/*
 <expression>  ::=  [ "-" ] <term> [ [ "+" | "-" ] <term> ]...

 <term>  ::=  <factor> [ [ "*" | "/" ] <factor> ]...

 <factor>  ::=  <number>  |  "(" <expression> ")"

 */
// ------------------------------------------------------------------------
import ee.ioc.cs.vsle.synthesize.EquationException;

public class EquationSolver {
    Set<String> vars = new LinkedHashSet<String>();
    private Set<Relation> relations = new LinkedHashSet<Relation>();
    String equation;

    public void solve(String eq) throws EquationException {
        equation = eq;
        StringStack expString = new StringStack(eq);
        vars.clear();
        relations.clear();
        ExpNode exp = upperTree(expString);

        expString.skipBlanks();
        exp.getVars();
        exp.decorate();
        exp.getExpressions();
    }

    private ExpNode upperTree(StringStack expString) throws EquationException {
        expString.skipBlanks();
        ExpNode exp = expressionTree(expString);

        expString.skipBlanks();
        if (expString.peek() == '=') {
            expString.getAnyChar();
            ExpNode nextExp = expressionTree(expString);

            exp = new BinOpNode('=', exp, nextExp);
            expString.skipBlanks();
        }
        return exp;
    }

    private ExpNode expressionTree(StringStack expString) throws EquationException {
        // Read an expression from the current line of input and
        // return an expression tree representing the expression.
        expString.skipBlanks();
        boolean negative; // True if there is a leading minus sign.

        negative = false;
        if (expString.peek() == '-') {
            expString.getAnyChar();
            negative = true;
        }
        ExpNode exp; // The expression tree for the expression.

        exp = termTree(expString); // Start with the first term.
        if (negative) {
            exp = new UnaryOpNode(exp, "-");
        }
        expString.skipBlanks();
        while (expString.peek() == '+' || expString.peek() == '-') {
            // Read the next term and combine it with the
            // previous terms into a bigger expression tree.
            char op = expString.getAnyChar();
            ExpNode nextTerm = termTree(expString);

            exp = new BinOpNode(op, exp, nextTerm);
            expString.skipBlanks();
        }
        return exp;
    }

    private ExpNode termTree(StringStack expString) throws EquationException {
        // Read a term from the current line of input and
        // return an expression tree representing the term.
        expString.skipBlanks();
        ExpNode term; // The expression tree representing the term.

        term = factorTree(expString);
        expString.skipBlanks();
        while (expString.peek() == '*' || expString.peek() == '/') {
            // Read the next factor, and combine it with the
            // previous factors into a bigger expression tree.
            char op = expString.getAnyChar();
            ExpNode nextFactor = factorTree(expString);

            term = new BinOpNode(op, term, nextFactor);
            expString.skipBlanks();
        }
        return term;
    }

    private ExpNode factorTree(StringStack expString) throws EquationException {
        expString.skipBlanks();
        ExpNode factor;

        factor = primaryTree(expString);
        expString.skipBlanks();
        while (expString.peek() == '^') {
            // Read the next primary, and exponentiate
            // the postFix-so-far by the postFix of this primary.
            char op = expString.getAnyChar();
            ExpNode nextPrimary = primaryTree(expString);

            factor = new BinOpNode(op, factor, nextPrimary);
            expString.skipBlanks();
        }
        return factor;
    }

    private ExpNode primaryTree(StringStack expString) throws EquationException {
        expString.skipBlanks();
        char ch = expString.peek();

        if (Character.isDigit(ch) || Character.isLetter(ch)) {
            String val = readWord(expString);

            if (Function.contains( val )) {
                expString.skipBlanks();
                if (expString.peek() != '(') {
                    // The function name should have been followed
                    // by the argument of the function, in parentheses.
                    throw new EquationException(
                            "Error in equation '" + equation
                            + "'. Missing left parenthesis after function name.");
                }
                expString.getAnyChar(); // Read the '('
                ExpNode exp = expressionTree(expString);

                expString.skipBlanks();
                if (expString.peek() != ')') {
                    // There must be a right parenthesis after the argument.
                    throw new EquationException(
                            "Error in equation '" + equation
                            + "'. Missing right parenthesis after function argument.");
                }
                expString.getAnyChar(); // Read the ')'

                return new UnaryOpNode(exp, val);

            } 
            if( Character.isDigit(val.charAt(0)) ) {
                return new ConstNode(val, this);
            }
            //we need this later to be able to distinguish vars from other stuff
            return new ConstNode("$" + val + "$", this);

        } else if (ch == '(') {
            expString.getAnyChar(); // Read the "("
            ExpNode exp = expressionTree(expString);

            expString.skipBlanks();
            if (expString.peek() != ')') {
                throw new EquationException(
                        "Error in equation '" + equation
                        + "'. Missing right parenthesis.");
            }
            expString.getAnyChar(); // Read the ")"
            return exp;
        } else if (ch == '\n') {
            throw new EquationException(
                    "Error in equation '" + equation
                    + "'. End-of-line encountered in the middle of an expression.");
        } else if (ch == ')') {
            throw new EquationException(
                    "Error in equation '" + equation + "'. Extra right parenthesis.");
        } else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
            throw new EquationException(
                    "Error in equation '" + equation + "'. Misplaced operator.");
        } else {
            throw new EquationException(
                    "Error in equation '" + equation + "'. Unexpected character \""
                    + ch + "\" encountered.");
        }

    }

    private String readWord(StringStack expString) throws EquationException {
        // Reads a word from input.  A word is any sequence of
        // letters and digits, starting with a letter.  When
        // this subroutine is called, it should already be
        // known that the next character in the input is
        // a letter.
        StringBuilder word = new StringBuilder(); // The word.
        char ch = expString.peek();
        boolean digit = Character.isDigit(ch);
        boolean exponent = false;
        boolean shouldStop = false;
        boolean isHex = false;

        while (Character.isLetter(ch) || Character.isDigit(ch)
                || ch == '.' || ch == '_') {

            word.append( expString.getChar() ); // Add the character to the word.
            ch = expString.peek();

            if(expString.isEmpty()) break;

            if(digit) {
                // Sci notation? See Double.toString() docs for details
                // Double.toString() generates only capital 'E'-s but users might
                // want to use 'e' also.
                if ( !exponent && ( !isHex && (LIT_EXP.indexOf( ch ) > -1) 
                        //support for hexadecimal exponent
                        || isHex && (LIT_EXPh.indexOf( ch ) > -1))) {
                    exponent = true;
                    word.append( expString.getChar() );

                    // Signed exponent? Agan, '+' sign is never generated
                    // by toString() but accept it anyway.
                    ch = expString.peek();
                    if ((LIT_EXPs.indexOf( ch ) > -1))
                        word.append( expString.getChar() );

                    // Exponent must contain at least one digit
                    if ( !Character.isDigit( ch = expString.peek() ) ) {
                        throw new EquationException(
                                "Error in equation '" + equation
                                + "'. Not a valid exponent, beginning with: "
                                + word);
                    }
                } else if ((LIT_HEXs.indexOf( ch ) > -1) && !exponent && !isHex) {
                    isHex = true;
                    word.append( expString.getChar() );
                    // Hexadecimal must start with 0x 
                    //and contain at least one digit after x
                    if ( !LIT_OX.equalsIgnoreCase( word.toString() )
                            || (!Character.isDigit( ch = expString.peek() ) 
                                    && LIT_HEX.indexOf( ch ) < 0 ) ) {
                        throw new EquationException(
                                "Error in equation '" + equation
                                + "'. Not a valid hexadecimal, beginning with: "
                                + word);
                    }

                } else if (isHex && (LIT_HEX.indexOf( ch ) > -1) && !exponent) {
                    continue;
                } else if ((LIT_FLOAT.indexOf( ch ) > -1)
                        || (LIT_LONG.indexOf( ch ) > -1) && !exponent) {
                    word.append( expString.getChar() );
                    ch = expString.peek();

                    if(shouldStop && !expString.isEmpty()) {
                        throw new EquationException(
                                "Error in equation '" + equation
                                + "'. Wrong literal format, beginning with: " + word);
                    }	                
                } else if (Character.isLetter(ch)) {
                    throw new EquationException(
                            "Error in equation '" + equation
                            + "'. Not a number or variable, beginning with: " + word);
                }
            }
        }
        return word.toString();
    }

    private static final String LIT_EXP = "eE";
    private static final String LIT_EXPh = "pP";
    private static final String LIT_EXPs = "+-";
    private static final String LIT_FLOAT = "fFdD";
    private static final String LIT_LONG = "lL";
    private static final String LIT_OX = "0x";
    private static final String LIT_HEXs = "xX";
    private static final String LIT_HEX = "abcdefABCDEF";

    public static void main(String[] args) throws EquationException {

        Function f;

        if( (f = Function.valueOf( "sin" )) != null ) {
            System.err.println( f.getFunction() + ", vs " + f.getOpposite() );
        }
        EquationSolver solver = new EquationSolver();

        String[] literals = new String[] {
                "0xeeee"
                ,"1d"
                ,"0x2L"
                ,"3.0e-1f"
                ,"0xFF"
                ,"0XeFp-5f"
        };

        for ( String string : literals ) {
            System.out.println(solver.readWord( new StringStack(string) ));
        }

//        StringStack expString = new StringStack("  r = (a+b+c+f)/(d+e+l) + (z+x+v)/(g+w) ");
        StringStack expString = new StringStack("a = - a + 2 *(sin(b)  + c)");
        expString.skipBlanks();
        ExpNode exp = solver.upperTree(expString);
        System.out.println(exp);
        expString.skipBlanks();
        System.out.println(exp.inFix());

        exp.decorate();
        exp.getExpressions();
        //		HashMap m = new HashMap();
        //		m.put("x", new Double(4));
        //		System.out.println(exp.calcValue(m));

        for ( Relation rr : solver.getRelations() ) {
            System.out.println(rr);
        }
    }

    public Set<Relation> getRelations() {
        return relations;
    }

    public static class Relation {

        private String rel, exp;

        Relation( String rel, String exp ) {
            this.rel = rel;
            this.exp = exp;
        }

        public String getExp() {
            return exp;
        }

        public String getRel() {
            return rel;
        }

        @Override
        public String toString() {
            return "Rel: " + rel + ", exp: " + exp;
        }
    }
}

