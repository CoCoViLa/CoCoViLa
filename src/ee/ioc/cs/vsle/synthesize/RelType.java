package ee.ioc.cs.vsle.synthesize;

public enum RelType {

            TYPE_DECLARATION,
            TYPE_JAVAMETHOD,
            TYPE_EQUATION,
            TYPE_ALIAS,
            TYPE_SUBTASK,
            TYPE_METHOD_WITH_SUBTASK,
            TYPE_UNIMPLEMENTED;

    private static int auxVarCounter = 0;
    private static int relCounter = 0;
    private static int varCounter = 0;

    final static String TAG_SUBTASK = "<<subtask>>";

    public final static int REL_HASH = "rel".hashCode();
    public final static int VAR_HASH = "var".hashCode();

    public static int tmpVarNr() {
        return auxVarCounter;
    }
    
    public static int nextTmpVarNr() {
        return auxVarCounter++;
    }
    
    public static int nextRelNr() {
        return relCounter++;
    }
    
    public static int nextVarNr() {
        return varCounter++;
    }
}
