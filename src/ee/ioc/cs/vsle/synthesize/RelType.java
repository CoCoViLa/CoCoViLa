package ee.ioc.cs.vsle.synthesize;

public class RelType {

    public static final int
            TYPE_DECLARATION = 1,
            TYPE_JAVAMETHOD = 2,
            TYPE_EQUATION = 3,
            TYPE_ALIAS = 4,
            TYPE_SUBTASK = 5,
            TYPE_METHOD_WITH_SUBTASK = 6,
            TYPE_UNIMPLEMENTED = 7;

    static int auxVarCounter = 0;
    static int relCounter = 0;
    static int varCounter = 0;

    final static String TAG_SUBTASK = "<<subtask>>";

    public final static int REL_HASH = "rel".hashCode();
    public final static int VAR_HASH = "var".hashCode();

    private RelType() {
    }
}
