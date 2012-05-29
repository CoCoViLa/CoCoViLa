package ee.ioc.cs.vsle.vclass;

import java.io.*;
import java.util.*;

import ee.ioc.cs.vsle.ccl.*;
import ee.ioc.cs.vsle.util.FileFuncs.*;

/**
 * <p>Title: ee.ioc.cs.editor.vclass.VPackage</p>
 * <p>Description: <description></p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Ando Saabas
 * @version 1.0
 */
public class VPackage implements ee.ioc.cs.vsle.api.Package {

    private String name;
    private String description;
    private String path;
    private ArrayList<PackageClass> classes = new ArrayList<PackageClass>();
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

		for (int i = 0; i < getClasses().size(); i++) {
			pClass = getClasses().get(i);
			if (pClass.getName().equals(className)) {
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

		for (PackageClass pClass : getClasses()) {
			if (pClass.getName().equals(className)) {
				return pClass;
			}
		}
		return null;
	} // getClass

        /**
         * Creates suitable Java class name for scheme's metaclass
         * 
         * @return package class name
         */
        public String getSchemeClassName( String schemeNameRef ) {
            //all non word chars are removed and then if the first char is digit or the string is empty it is replaced by "_"
            String className = ( getName() + ( schemeNameRef != null ? "_" + schemeNameRef : "") )
                    .replaceAll( "\\W+", "" ).replaceFirst( "^[0-9]|^$", "_" );

            // if first char is in lower case, make it upper case
            className = className.replaceFirst("^\\p{javaLowerCase}",
                    className.substring(0, 1).toUpperCase());

            // make the name unique
            int i = 0;
            while( hasClass( className ) ) {
                className = className + "_" + i++;
            }

            return className;
        }

        @Override
        public String getPath() {
            return path;
        }

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		@Override
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

    @Override
    public boolean equals( Object obj ) {
        return path.equals( ((VPackage)obj).path );
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    /**
     * @param description the description to set
     */
    public void setDescription( String description ) {
        this.description = description;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the classes
     */
    public ArrayList<PackageClass> getClasses() {
        return classes;
    }

    @Override
    public String toString() {
        return "VPackage [name=" + name + ", description=" + description
                + ", path=" + path + "]";
    }
}
