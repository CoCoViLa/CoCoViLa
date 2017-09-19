package ee.ioc.cs.vsle.vclass;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.*;
import java.util.*;

import ee.ioc.cs.vsle.ccl.*;
import ee.ioc.cs.vsle.editor.Editor;
import ee.ioc.cs.vsle.util.FileFuncs.*;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * <p>Title: ee.ioc.cs.editor.vclass.VPackage</p>
 * <p>Description: <description></p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Ando Saabas
 * @version 1.0
 */
public class VPackage implements ee.ioc.cs.vsle.api.Package {

  private static final Logger logger = LoggerFactory.getLogger(VPackage.class);

    private String name;
    private String description;
    private String pathToXml;
    private String packageDir;
    private ArrayList<PackageClass> classes = new ArrayList<PackageClass>();
    private PackageClassLoader classLoader;

    /**
     * Are there any classes with custom painters in this package?
     */
    private boolean painters;

  public VPackage(String pathToXml) {
    this.pathToXml = pathToXml;
    this.packageDir = FilenameUtils.getFullPath(pathToXml);
  }

  public VPackage(File xml) {
    this(xml.getAbsolutePath());
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
            return pathToXml;
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

  public void initPainters() {
    this.painters = true;
    createPainterPrototypes();
  }

  private boolean createPainterPrototypes() {
    boolean success = true;
        /* TODO Will be replaced by more general daemon stuff */
    PackageClassLoader pcl = null;

    for ( PackageClass pclass : getClasses() ) {
      if ( pclass.getPainterName() == null )
        continue;

      try {
        if (pcl == null) {
          pcl = getPackageClassLoader();
        }
        Class<?> painterClass = pcl.loadClass(pclass.getPainterName());
        pclass.setPainterPrototype((ClassPainter) painterClass.newInstance());
      }
      catch ( CompileException e ) {
        success = false;
        logger.error(null, e); // print compiler generated message
      }
      catch ( Exception e ) {
        success = false;
        logger.error(null, e);
      }
      finally {
        if (pcl != null && pcl.hasErrors()) {
          pcl.clearProblems();
        }
      }
    }

    if (!success) {
      JOptionPane.showMessageDialog(Editor.getInstance(), "One or more errors occured. See the error log for details.", "Error",
              JOptionPane.ERROR_MESSAGE);
    }

    return success;
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
            File p = new File(pathToXml);
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
        return pathToXml.equals( ((VPackage)obj).pathToXml);
    }

    @Override
    public int hashCode() {
        return pathToXml.hashCode();
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
                + ", pathToXml=" + pathToXml + "]";
    }

  public String getDir() {
    return packageDir;
  }
}
