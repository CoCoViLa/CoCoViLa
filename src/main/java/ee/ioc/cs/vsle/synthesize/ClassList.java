package ee.ioc.cs.vsle.synthesize;

import java.util.*;

/**
 * <p>Title: ee.ioc.cs.editor.synthesize.ClassList</p>
 * <p>Description: <description></p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Ando Saabas
 * @version 1.0
 */
public class ClassList
	extends LinkedHashSet<AnnotatedClass> {

	/**
	 * Class constructor.
	 */
	public ClassList() {
		super();
	} // ee.ioc.cs.editor.synthesize.ClassList

	public ClassList(Collection<AnnotatedClass> acs) {
		super(acs);
	}

	/**
	 * Returns the Annotated Class of a specified type.
	 * @param type String - type of the Annotated Class to be returned.
	 * @return AnnotatedClass - AnnotatedClass of a specified type.
	 */

	public AnnotatedClass getType(String type) {
		for (AnnotatedClass ac : this ) {

			if ( ac.getName().equals(type) ) {
				return ac;
			}
		}
		return null;
	} // getType

	public boolean containsType( String type ) {
		return getType( type ) != null;
	}

}
