package ee.ioc.cs.vsle.api;

/**
 * The Scheme interface that is exposed to the generated programs through the
 * {@code ProgramContext} class.
 * This interface declares methods for reading and modifying the scheme as well as
 * other useful method such as sending repaint requests to the canvas and running
 * the code generator.
 * @see ee.ioc.cs.vsle.api.ProgramContext
 * @see ee.ioc.cs.vsle.api.SchemeObject
 */
public interface Scheme {

    // TODO: Add other method declarations needed by daemons
    // TODO: Improve documentation to reflect threading issues

    /**
     * Returns the scheme object with the specified name {@code objectName}.
     * @param objectName the name of the requested scheme object
     * @return the scheme object if there exists an object with the specified name,
     * {@code} null otherwise.
     */
    public SchemeObject getObject(String objectName);

    /**
     * Gets the value from the specified scheme object field.
     * @param objectName the name of the object
     * @param fieldName the name of the field
     * @return the field value which can be null
     * @throws RuntimeException when there is no such class or field
     */
    public Object getFieldValue(String objectName, String fieldName);

    /**
     * Repaints the canvas as soon as possible.
     * This method is thread safe.
     */
    public void repaint();

    /**
     * Repaints the specified area of the canvas as soon as possible.
     * This method is thread safe.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width
     * @param height the height
     */
    public void repaint(int x, int y, int width, int height);
    
    /**
     * 
     * @return Package
     */
    public ee.ioc.cs.vsle.api.Package getPackage();
    
    /**
     * @param tableName
     * @param args
     * @return
     */
    public Object queryTable( String tableName, Object[] args );
    
    /**
     * Terminates the execution of a program from any thread
     * 
     * @param runnerId
     */
    public void terminate( long runnerId );
    
    /**
     * Terminates and reruns a program from any thread
     */
    public void rerun();
    
    /**
     * ProgramContext.computeModel delegate
     */
    public Object[] computeModel(
            Class<?> context, String[] inputNames,
            String[] outputNames, Object[] inputValues);
    
    public Object[] computeModel(
            String context, String[] inputNames,
            String[] outputNames, Object[] inputValues);
}
