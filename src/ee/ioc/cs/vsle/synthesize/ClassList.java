package ee.ioc.cs.vsle.synthesize;

import java.util.ArrayList;

/**
 * <p>Title: ee.ioc.cs.editor.synthesize.ClassList</p>
 * <p>Description: <description></p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Ando Saabas
 * @version 1.0
 */
public class ClassList
	extends ArrayList<AnnotatedClass> {

	/**
	 * Class constructor.
	 */
	ClassList() {
		super();
	} // ee.ioc.cs.editor.synthesize.ClassList

	/**
	 * Returns the Annotated Class of a specified type.
	 * @param type String - type of the Annotated Class to be returned.
	 * @return AnnotatedClass - AnnotatedClass of a specified type.
	 */

	public AnnotatedClass getType(String type) {
		for (AnnotatedClass ac : this ) {

			if (ac.getName().equals(type) && !ac.isOnlyForSuperclassGeneration() ) {
				return ac;
			}
		}
		return null;
	} // getType

	public boolean containsType( String type ) {
		return true;
	}
	
	public ArrayList<AnnotatedClass> getSuperClasses() {
		ArrayList<AnnotatedClass> a = new ArrayList<AnnotatedClass>();
		
		for (AnnotatedClass ac : this ) {

			if ( ac.isOnlyForSuperclassGeneration() ) {
				a.add( ac );
			}
		}
		return a;
	} // getType
}
