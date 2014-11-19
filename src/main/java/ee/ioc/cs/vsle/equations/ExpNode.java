package ee.ioc.cs.vsle.equations;

abstract class ExpNode {

    abstract String inFix();

    abstract void decorate();

    abstract void getExpressions(String a);

    abstract void getExpressions();

    abstract void getVars();
}

