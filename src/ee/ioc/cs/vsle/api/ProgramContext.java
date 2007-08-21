package ee.ioc.cs.vsle.api;

/**
 * <p>This class is made available to each synthesized program instance.
 * Generated programs can use the static methods of this class to 
 * obtain the reference to the scheme instance the program was synthesized
 * from. In current implementation there are no guarantees about the 
 * actual contents of the scheme object.</p>
 * <p>Example usage</p>
 * <p>A scheme object can read the vale of its field "k1" by including the
 * following statement:
 * <pre>double k1 = Double.valueOf(
 *              (String) ProgramContext.getFieldValue(objectName, "k1"));
 * </pre>
 * There is a special specification variable {@code cocovilaSpecObjectName}
 * of class String that is declared and initialized automatically.
 * The value of this variable can be used to get hold of the corresponding
 * scheme name of Java objects.
 * <pre>
 * public class A {
 *     /&#42@ specification A {
 *         int x, input;
 *         cocovilaSpecObjectName, input -> x {getX};
 *     }&#42;/
 *     private int getX(String objectName, int in) {
 *         return Integer.valueOf(
 *                 (String) ProgramContext.getFieldValue(objectName, in));
 *     }
 * }
 */
public final class ProgramContext {

    /**
     * Reference to the scheme the program was generated from
     */
    private static Scheme scheme;

    /**
     * Sets the static scheme reference.
     * This method cannot be called by user code.
     * @param scheme the scheme
     */
    public static void setScheme(Scheme scheme) {
        if (ProgramContext.scheme == null)
            ProgramContext.scheme = scheme;
        else
            throw new RuntimeException("ProgramContext already initialized");
    }

    /**
     * Returns a reference to the scheme the program was generated from.
     * @return the scheme reference
     */
    public static Scheme getScheme() {
        return ProgramContext.scheme;
    }

    /**
     * Gets the value from the specified scheme object field.
     * This method is supplied for convenience.
     * @param objectName the name of the object
     * @param fieldName the name of the field
     * @return the field value which can be null
     * @throws RuntimeException when there is no such class or field
     */
    public static Object getFieldValue(String objectName, String fieldName) {
        return scheme.getFieldValue(objectName, fieldName);
    }
}
