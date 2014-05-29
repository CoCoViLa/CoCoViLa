package ee.ioc.cs.vsle.equations;

import ee.ioc.cs.vsle.equations.EquationSolver.*;
import ee.ioc.cs.vsle.util.*;

class ConstNode extends ExpNode {

    /**
     * Node number.
     */
    private String value;
    private EquationSolver solver;

    /**
     * Class constructor.
     * @param val String
     */
    ConstNode(String val, EquationSolver solver) {
        value = val;
        this.solver = solver;
    } // ee.ioc.cs.editor.equations.ConstNode

    @Override
    void getExpressions() {
     // getExpressions
    }

    @Override
    void getVars() {
        if (!Character.isDigit(value.charAt(0))) {
            solver.vars.add(value);
        }
    } // getVars

    @Override
    void getExpressions(String upper) {
        String rel = "";

        if (!Character.isDigit(value.charAt(0))) {
            String exp = value + "=" + upper;
            rel = exp + ":";
            for ( String a : solver.vars ) {
                //allow the following because later we need to check for variables appearing on both sides of equation
                if ( upper.indexOf(a) > 0 ) {
                    rel += a + " ";
                }
            }
            rel = (rel + ":" + value).replaceAll( "\\$", "" );
            solver.getRelations().add( new Relation( rel, exp ) );
        }
    } // getExpressions

    @Override
    void decorate() {
        // decorate
    }

    @Override
    String inFix() {
        return value;
    } // inFix

    void postFix() {
        db.p(value);
    } // postFix
}

