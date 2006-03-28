package ee.ioc.cs.vsle.vclass;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * <p>Title: ee.ioc.cs.editor.vclass.VPackage</p>
 * <p>Description: <description></p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Ando Saabas
 * @version 1.0
 */
public class VPackage {

    public String name;
	public String description;
	private String path;
	private String lastScheme;
	public ArrayList<PackageClass> classes = new ArrayList<PackageClass>();

	public VPackage(String path) {
        this.path = path;
    }

	/**
	 * Detects whether a package includes a specified class or not.
	 * @param className String - the class to be searched for.
	 * @return boolean - indicator indicating whether a package includes a specified
	 *                   class or not.
	 */
	public boolean hasClass(String className) {
		PackageClass pClass;

		for (int i = 0; i < classes.size(); i++) {
			pClass = classes.get(i);
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
			pClass = classes.get(i);
			if (pClass.name.equals(className)) {
				return pClass;
			}
		}
		return null;
	} // getClass

        public String getPackageClassName() {
            String packClassName = name.substring( 0, 1 ).toUpperCase()
                                   .concat( name.substring( 1, name.length() ) );

            Pattern pattern = Pattern.compile( "[ \t]+" );
            Matcher matcher = pattern.matcher( packClassName );
            packClassName = matcher.replaceAll( "" );

            int i = 0;

            while( hasClass( packClassName ) ) {
                packClassName = packClassName + "_" + i++;
            }

            return packClassName;
        }

        public String getPath() {
            return path;
        }

		public String getLastScheme() {
			return lastScheme;
		}

		public void setLastScheme(String lastScheme) {
			this.lastScheme = lastScheme;
		}
}
