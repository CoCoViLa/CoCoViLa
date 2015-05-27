package ee.ioc.cs.vsle.ccl;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;

import ee.ioc.cs.vsle.editor.RuntimeProperties;
import ee.ioc.cs.vsle.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Package classloader.  Loads classes from the package directory and
 * zip and jar archives in the top level package directory.  Source files
 * are compiled on demand when needed.
 */
public class PackageClassLoader extends CCL implements INameEnvironment {

    private static final Logger logger = LoggerFactory.getLogger(PackageClassLoader.class);

    public PackageClassLoader(File pkgDir) {
        super(createPackageClassPath(pkgDir),
                PackageClassLoader.class.getClassLoader());

        // initialize the environment field inherited from CCL
        initNameEnvironment();
    }

    private void initNameEnvironment() {
        ArrayList<String> fileNames = new ArrayList<String>();
        for (URL u : getURLs()) {
            try {
                fileNames.add(new File(u.toURI()).getAbsolutePath());
            } catch (URISyntaxException e) {
                logger.error(null, e);
            }
        }

        for (String s : getCompilerClassPath()) {
            if (!fileNames.contains(s)) {
                fileNames.add(s);
            }
        }
        environment = new FileSystem(
                fileNames.toArray(new String[fileNames.size()]),
                new String[] { }, null);
    }

    /**
     * Creates a URL array with paths required for package class loading.
     * The returned array contains the URLs of the package directory,
     * all the jar and zip archives found in the package top level directory
     * (the directory is NOT searched recursively) and the compilation
     * classpath set by the user.
     * @return the package classpath
     */
    private static URL[] createPackageClassPath(File packagePath) {
        ArrayList<URL> urls = new ArrayList<URL>();

        // (1) CoCoVila standard libraries
        // CoCoViLa standard libraries should be accessible using the parent
        // classloader, therefore we omit them here.

        // (2) The package directory
        try {
            urls.add(packagePath.toURI().toURL());
        } catch (MalformedURLException e) {
            logger.error(null, e);
        }

        // (3) jar and zip archives from the package top level directory
        File[] pkgLibs = getLibraryFiles(packagePath);
        if (pkgLibs != null) {
            for (File f : pkgLibs) {
                try {
                    urls.add(f.toURI().toURL());
                } catch (MalformedURLException e) {
                    logger.error(null, e);
                }
            }
        }

        // (4) user set classpath
        String[] paths = RuntimeProperties.getCompilationClasspaths();

        for (String path : paths) {
            File file = new File(path);
            if(file.exists()) {
                try {
                    urls.add(file.toURI().toURL());
                } catch (MalformedURLException e) {
                    logger.error(null, e);
                }
            }
        }

        if(RuntimeProperties.isFromWebstart())
        {
            urls.addAll( getWebStartClasspath() );
        }
        
        return urls.toArray(new URL[urls.size()]);
    }

    private static boolean isJarsLoadedLocal = false;
    private static List<URL> localJarsClassPath = null;
    
    /**
     * Prepares classpath if the application has been started 
     * from Java Web Start
     * @return
     */
    private static synchronized List<URL> getWebStartClasspath() {
        if (isJarsLoadedLocal) {
            return localJarsClassPath;
        }

        List<URL> classpathJars = new ArrayList<URL>();

        try {
            
            Set<String> systemLibs = new HashSet<String>();
            for ( File file : getSystemLibs() ) {
                systemLibs.add( file.getAbsolutePath() );
            }
            
            for (Enumeration<?> e = PackageClassLoader.class
                    .getClassLoader().getResources("META-INF/MANIFEST.MF"); e
                    .hasMoreElements();) {
                URL url = (URL) e.nextElement();
                
                URL localJarURL = getLocalJarURL(url, systemLibs);
                
                if (localJarURL != null) {
                    classpathJars.add(localJarURL);
                }
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        isJarsLoadedLocal = true;
        localJarsClassPath = classpathJars;

        System.err.println("localJarsClassPath: " + localJarsClassPath);
        
        return localJarsClassPath;
    }

    /**
     * Helper method for getWebStartClasspath() method.
     * Returns a local URL of a jar for the classpath.
     * If jar is a system lib, it is ignored.
     * @param url
     * @param systemLibs
     * @return
     */
    private static URL getLocalJarURL(URL url, Collection<String> systemLibs) {
        
        String urlStrJar = getJarPath( url );
        
        //ignore if it is a system lib
        if(systemLibs.contains( urlStrJar ))
            return null;
        
        InputStream inputStreamJar = null;
        File tempJar;
        try {
            //check if the file is already local
            if(url.getPath().startsWith( "file:" )) 
                return new File( urlStrJar ).toURI().toURL();
            
            JarFile jar = ( (JarURLConnection) url.openConnection() ).getJarFile();
            inputStreamJar = new FileInputStream( jar.getName() );
            
            String strippedName = urlStrJar;
            int dotIndex = strippedName.lastIndexOf('.');
            if (dotIndex >= 0) {
                strippedName = strippedName.substring(0, dotIndex);
                strippedName = strippedName.replace("/", File.separator);
                strippedName = strippedName.replace("\\", File.separator);
                int slashIndex = strippedName.lastIndexOf(File.separator);
                if (slashIndex >= 0) {
                    strippedName = strippedName.substring(slashIndex + 1);
                }
            }
            tempJar = File.createTempFile(strippedName, ".jar");
            tempJar.deleteOnExit();
            FileUtils.copyInputStreamToFile(inputStreamJar, tempJar);
            return tempJar.toURI().toURL();
        } catch (Exception ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (inputStreamJar != null) {
                    inputStreamJar.close();
                }
            } catch (IOException ioe) {
            }
        }
        return null;
    }

    /**
     * Returns a path for a jar from a given url.
     */
    private static String getJarPath( URL url )
    {
        String urlString = url.getFile();

        int idx;
        if ((idx = urlString.indexOf( "!/" )) >= 0) {
            urlString = urlString.substring(0, idx);
        }

        if (urlString.startsWith("file:")) {
            return urlString.substring(5);
        }

        return urlString;
    }

    @Override
    protected INameEnvironment getNameEnvironment() {
        return this;
    }

    public void cleanup() {
        if (environment != null) {
            environment.cleanup();
            environment = null;
        }
    }

    public NameEnvironmentAnswer findType(char[][] compoundTypeName) {
        NameEnvironmentAnswer rv = environment.findType(compoundTypeName);
        if (rv == null) {
            rv = findSourceAnswer(toClassName(compoundTypeName));
        }
        return rv;
    }

    private NameEnvironmentAnswer findSourceAnswer(String className) {
        String fileName = classToSourceResource(className);
        InputStream is = getResourceAsStream(fileName);
        if (is != null) {
            char[] source = FileFuncs.getCharStreamContents(is);
            if (source != null) {
                return new NameEnvironmentAnswer(
                        new CompilationUnit(source, fileName, null), null);
            }
        }
        return null;
    }

    public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName) {
        NameEnvironmentAnswer rv = environment.findType(typeName, packageName);
        if (rv == null) {
            rv = findSourceAnswer(toClassName(packageName, typeName)); 
        }
        return rv;
    }

    public boolean isPackage(char[][] parentPackageName, char[] packageName) {
        return environment.isPackage(parentPackageName, packageName);
    }
}
