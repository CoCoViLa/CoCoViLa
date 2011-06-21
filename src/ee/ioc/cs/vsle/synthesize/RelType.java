package ee.ioc.cs.vsle.synthesize;

public enum RelType {

            TYPE_DECLARATION,
            TYPE_JAVAMETHOD,
            TYPE_EQUATION,
            TYPE_ALIAS,
            TYPE_SUBTASK,
            TYPE_METHOD_WITH_SUBTASK,
            TYPE_UNIMPLEMENTED;

    static int auxVarCounter = 0;
    static int relCounter = 0;
    static int varCounter = 0;

    final static String TAG_SUBTASK = "<<subtask>>";

    public final static int REL_HASH = "rel".hashCode();
    public final static int VAR_HASH = "var".hashCode();

}
