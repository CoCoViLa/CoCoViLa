package ee.ioc.cs.vsle.vclass;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import ee.ioc.cs.vsle.ccl.PackageClassLoader;
import ee.ioc.cs.vsle.ccl.RunnerClassLoader;
import ee.ioc.cs.vsle.util.FileFuncs.GenStorage;

/**
 * <p>Title: ee.ioc.cs.editor.vclass.VPackage</p>
 * <p>Description: <description></p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Ando Saabas
 * @version 1.0
 */
public class VPackage {

    private String name;
    public String description;
    private String path;
    private String lastScheme;
    public ArrayList<PackageClass> classes = new ArrayList<PackageClass>();
    private PackageClassLoader classLoader;

    /**
     * Are there any classes with custom painters in this package?
     */
    private boolean painters;

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
	 * @param className name of the class to be retrieved from the package
	 * @return the specified class from the package.
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
            String packClassName = getName().substring( 0, 1 ).toUpperCase()
                                   .concat( getName().substring( 1, getName().length() ) );

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

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

        public boolean hasPainters() {
            return painters;
        }

        public void setPainters(boolean painters) {
            this.painters = painters;
        }

        /**
         * Returns the next serial number for the visual class used for
         * generating unique names for instances.
         * @param className the class name
         * @return the next serial number >= 0, -1 if there is no such class
         */
		public int getNextSerial(String className) {
			PackageClass pc = getClass(className);
			if (pc != null)
				return pc.getNextSerial();
			
			return -1;
		}

    /**
     * Creates and returns the package classloader for this package.
     * The same instance is returned on subsequent calls.
     * @return the package classloader
     */
    public PackageClassLoader getPackageClassLoader() {
        if (classLoader == null) {
            File p = new File(path);
            if (!p.isDirectory()) {
                p = p.getParentFile();
            }
            classLoader = new PackageClassLoader(p);
        }
            
        return classLoader;
    }

    /**
     * Creates and returns a new RunnerClassLoader on each call.
     * The returned runner classloader uses the package classloader to delegate
     * work to.
     * @param fs the storage for generated files
     * @return a fresh runner classloader
     */
    public ClassLoader newRunnerClassLoader(GenStorage fs) {
        return new RunnerClassLoader(fs, getPackageClassLoader());
    }
}
