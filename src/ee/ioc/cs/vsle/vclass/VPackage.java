package ee.ioc.cs.vsle.vclass;

import java.util.ArrayList;

/**
 * <p>Title: ee.ioc.cs.editor.vclass.VPackage</p>
 * <p>Description: <description></p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Aulo Aasmaa, Ando Saabas
 * @version 1.0
 */
public class VPackage {

	public String name;
	public String description;
	public ArrayList classes = new ArrayList();

	/**
	 * Detects whether a package includes a specified class or not.
	 * @param className String - the class to be searched for.
	 * @return boolean - indicator indicating whether a package includes a specified
	 *                   class or not.
	 */
	public boolean hasClass(String className) {
		PackageClass pClass;

		for (int i = 0; i < classes.size(); i++) {
			pClass = (PackageClass) classes.get(i);
			if (pClass.name.equals(className)) {
				return true;
			}
		}
		return false;
	} // hasClass

	/**
	 * Get a specified class from the package.
	 * @param className String - name of the class to be retrieved from the package.
	 * @return ee.ioc.cs.editor.vclass.PackageClass - specified class returned from the package.
	 */
	public PackageClass getClass(String className) {
		PackageClass pClass;

		for (int i = 0; i < classes.size(); i++) {
			pClass = (PackageClass) classes.get(i);
			if (pClass.name.equals(className)) {
				return pClass;
			}
		}
		return null;
	} // getClass

}
