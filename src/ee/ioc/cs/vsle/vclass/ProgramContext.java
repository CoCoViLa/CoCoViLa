package ee.ioc.cs.vsle.vclass;

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
 * 		(String) ProgramContext.getField(objectName, "k1"));
 * </pre>
 * The variable {@code objectName} is a special field that is always present
 * in all scheme objects and contains the name of this particular scheme
 * object instance. User code should never attempt to modify this field.</p>
 */
public final class ProgramContext {

	/**
	 * Reference to the scheme the program was generated from
	 */
	private static Scheme scheme;

	/**
	 * Sets the static scheme reference.
	 * @param scheme the scheme
	 */
	public static void setScheme(Scheme scheme) {
		ProgramContext.scheme = scheme;
	}

	/**
	 * Returns a reference to the scheme the program was generated from.
	 * @return the scheme reference
	 */
	public static Scheme getScheme() {
		return ProgramContext.scheme;
	}

	/**
	 * Reads a value object from the specified field.
	 * @param className the name of the object
	 * @param fieldName the name of the field
	 * @return the field value which can be null
	 */
	public static Object getField(String className, String fieldName) {
		GObj obj = scheme.getObjects().getByName(className);
		ClassField fld = obj.getField(fieldName);
		return fld.getValue();
	}
}
