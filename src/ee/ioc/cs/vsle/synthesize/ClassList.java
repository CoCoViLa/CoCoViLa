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
public class ClassList<E>
	extends ArrayList<E> {

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
		for (int i = 0; i < this.size(); i++) {
			AnnotatedClass ac = (AnnotatedClass)this.get(i);

			if (ac.name.equals(type)) {
				return ac;
			}
		}
		return null;
	} // getType


    /** @link dependency */
    /*# AnnotatedClass lnkAnnotatedClass; */
}
