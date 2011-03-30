package ee.ioc.cs.vsle.api;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

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
     * Returns a copy of the list containing all scheme objects. Adding or
     * removing list elements does not get reflected on the scheme: use
     * {@code addObject} etc. to modify the scheme.
     * @return list of scheme objects
     */
    public List<SchemeObject> getObjects();

    /**
     * Returns a copy of the list containing all connections on the scheme.
     * @return list of all connections
     */
    //public List<Connection> getConnections();

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
     * @return value from the table if conditions hold for some row and column
     * @see ee.ioc.cs.vsle.table.IStructuralExpertTable#queryTable(Object[])
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
            String[] outputNames, Object[] inputValues, boolean cacheCompiledModel );

    /**
     * Closes the scheme in Scheme Editor.
     */
    public void close();

    /**
     * Writes the scheme XML description (.syn format) to the specified stream.
     * @param outputStream the destination of XML data
     * @see #load(InputStream)
     */
    public void save(OutputStream outputStream);

    /**
     * Loads a scheme into the Scheme Editor.
     * The scheme description has to be in .syn format and belong to the
     * same package as the current scheme.
     * This method can be called from any thread, therefore the implementation
     * has to be thread safe.
     * @param inputStream the source that should output a valid scheme
     * description
     */
    public Scheme load(InputStream inputStream);

    /**
     * Runs the scheme.
     * @return the runner id
     */
    public long run();
}
