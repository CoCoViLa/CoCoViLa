package ee.ioc.cs.vsle.synthesize;

public class RelType {
    public static final int declaration = 1,
        javamethod = 2,
        equation = 3,
        alias = 4,
        subtask = 5,
        method_with_subtask = 6,
        unimplemented = 7;
//    * Type of the relation.
//    * 2 - javamethod
//    * 3 - equation(and assignment)
//    * 4 - alias
//    * 5 - subtask
//    * 6 - method with subtask
//    * 7 - unimplemented
    static int auxVarCounter = 0;

    final static String TAG_SUBTASK = "<<subtask>>";

    public final static int REL_HASH = "rel".hashCode();
    public final static int VAR_HASH = "var".hashCode();

    private RelType() {
    }
}
