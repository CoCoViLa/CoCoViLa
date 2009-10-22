package ee.ioc.cs.vsle.api;

/**
 * The interface of scheme objects that is exposed to the generated programs through
 * the {@code ProgramContext} class.
 * Methods for accessing field and property values are declared by this interface.
 * @see ee.ioc.cs.vsle.api.ProgramContext
 * @see ee.ioc.cs.vsle.api.Scheme
 */
public interface SchemeObject {

    /**
     * Returns the absolute x coordinate of the upper left corner of the scheme object.
     * @return x coordinate of the object
     */
    public int getX();

    /**
     * Returns the absolute y coordinate of the upper left corner of the scheme object.
     * @return y coordinate of the object
     */
    public int getY();

    /**
     * Sets the x coordinate of the upper left corner of the object.
     * @param x the new x coordinate value
     */
    public void setX(int x);

    /**
     * Sets the y coordinate of the upper left corner of the object.
     * @param y the new y coordinate value
     */
    public void setY(int y);

    /**
     * Returns the real width (x resize factor times the nominal width)
     * of the scheme object in pixels.
     * @return the real width of the object
     */
    public int getRealWidth();

    /**
     * Returns the real height (y resize factor times the nominal height)
     * of the scheme object in pixels.
     * @return the real width of the object
     */
    public int getRealHeight();

    /**
     * Returns the name of the scheme object instance.
     * @return the name of the object
     */
    public String getName();

    /**
     * Returns the name of the visual class the instance belongs to.
     * @return the name of the class.
     */
    public String getClassName();

    /**
     * Returns the value of the specified field
     * @param fieldName the name of the field
     * @return the value of the field, possibly null
     * @throws RuntimeException when the field does not exist
     */
    public Object getFieldValue(String fieldName);

    /**
     * Sets the value of the specified field
     * @param fieldName the name of the field
     * @param value the new value of the field, can be null
     * @throws RuntimeException when the field does not exist
     */
    public void setFieldValue(String fieldName, String value);
}
