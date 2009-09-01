package ee.ioc.cs.vsle.api;

import java.io.InputStream;

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
    
    private static Thread thread;
    private static long runnerId;
    
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
     * @param thread the thread to set
     */
    public static void setThread( Thread thread ) {
        ProgramContext.thread = thread;
    }

    /**
     * @param runnerId the runnerId to set
     */
    public static void setRunnerId( long runnerId ) {
        ProgramContext.runnerId = runnerId;
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
    
    /**
     * Method for querying tables
     * 
     * @param tableName
     * @param args
     * @return value from the table if conditions hold for some row and column
     * @see ee.ioc.cs.vsle.table.IStructuralExpertTable#queryTable(Object[])
     */
    public static Object queryTable( String tableName, Object... args ) {
        return scheme.queryTable( tableName, args );
    }

    /**
     * Sets the value of a scheme object field.
     * @param objectName the name of the scheme object
     * @param fieldName the name of the field
     * @param value the value to set, null is also accepted
     * @throws RuntimeException when there is no such class or field
     */
    public static void setFieldValue(String objectName, String fieldName,
            String value) {
        scheme.getObject(objectName).setFieldValue(fieldName, value);
    }
    /**
     * Terminates the current execution of the generated program
     * and reruns the scheme.
     */
    public static final void rerun() {
        if( !isRunningThread() ) {
            scheme.rerun();
        } else {
            throw new RerunProgramException();
        }
    }

    /**
     * Terminates the current execution of the generated program.
     */
    public static final void terminate() {
        if( !isRunningThread() ) {
            scheme.terminate( runnerId );
        } else {
            throw new TerminateProgramException();
        }
    }
    
    private static boolean isRunningThread() {
        return Thread.currentThread() == thread;
    }

    /**
     * Computes a given model at runtime and executes a generated program
     * Static approach
     * 
     * @param context
     * @param inputNames
     * @param outputNames
     * @param inputValues
     * @return Object array of computed values
     */
    public static final Object[] computeModel(
            Class<?> context, String[] inputNames,
            String[] outputNames, Object[] inputValues) {
        return scheme.computeModel( context, inputNames, outputNames, inputValues );
    }

    /**
     * Computes a given model at runtime and executes a generated program
     * Dynamic approach
     * 
     * @param context
     * @param inputNames
     * @param outputNames
     * @param inputValues
     * @return Object array of computed values
     */
    public static final Object[] computeModel(
            String context, String[] inputNames,
            String[] outputNames, Object[] inputValues) {
        return scheme.computeModel( context, inputNames, outputNames, inputValues );
    }

    /**
     * Loads a scheme into the Scheme Editor.
     * The scheme description has to be in .syn format and belong to the
     * same package as the current scheme.
     * @param inputStream the source that should output a valid scheme
     * description
     * @see Scheme#load(InputStream)
     */
    public static final Scheme loadScheme(InputStream inputStream) {
        return scheme.load(inputStream);
    }
}
